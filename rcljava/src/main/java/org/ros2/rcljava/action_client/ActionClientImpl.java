package org.ros2.rcljava.action_client;

import org.ros2.rcljava.common.JNIUtils;
import org.ros2.rcljava.concurrent.RCLFuture;
import org.ros2.rcljava.consumers.Consumer;
import org.ros2.rcljava.interfaces.ActionDefinition;
import org.ros2.rcljava.interfaces.MessageDefinition;
import org.ros2.rcljava.node.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * @author Niels Tiben
 */
public class ActionClientImpl<T extends ActionDefinition> implements ActionClient<T> {
    private static final Logger logger = LoggerFactory.getLogger(ActionClientImpl.class);

    static {
        try {
            JNIUtils.loadImplementation(ActionClientImpl.class);
        } catch (UnsatisfiedLinkError ule) {
            logger.error("Native code library failed to load.\n" + ule);
            System.exit(1);
        }
    }

    private final WeakReference<Node> nodeReference;
    private long handle;
    private final String actionName;
    private long sequenceNumber = 0;
    private Map<Long, Map.Entry<Consumer, RCLFuture>> pendingGoalRequests;


    private final Class<MessageDefinition> goalType;
    private final Class<MessageDefinition> resultType;
    private final Class<MessageDefinition> feedbackType;


    public ActionClientImpl(final WeakReference<Node> nodeReference,
                            final long handle,
                            final String actionName,
                            final Class<MessageDefinition> goalType,
                            final Class<MessageDefinition> resultType,
                            final Class<MessageDefinition> feedbackType) {
        this.nodeReference = nodeReference;
        this.handle = handle;
        this.actionName = actionName;

        this.goalType = goalType;
        this.resultType = resultType;
        this.feedbackType = feedbackType;
        this.pendingGoalRequests = new HashMap<Long, Map.Entry<Consumer, RCLFuture>>();
    }

    /**
     * ================================ SEND GOAL ================================
     */

    public final <U extends MessageDefinition, V extends MessageDefinition> Future<V>
    asyncSendGoalRequest(final U actionGoalMessage) {
        return asyncSendGoalRequest(actionGoalMessage, new Consumer<Future<V>>() {
            public void accept(Future<V> input) {
            }
        });
    }

    public final <U extends MessageDefinition, V extends MessageDefinition> Future<V>
    asyncSendGoalRequest(
            final U actionGoalMessage,
            final Consumer<Future<V>> callback
    ) {
        synchronized (this.pendingGoalRequests) {
            sequenceNumber++;
            nativeSendGoalRequest(
                    handle,
                    sequenceNumber,
                    actionGoalMessage.getFromJavaConverterInstance(),
                    actionGoalMessage.getToJavaConverterInstance(),
                    actionGoalMessage.getDestructorInstance(),
                    actionGoalMessage
            );
            RCLFuture<V> future = new RCLFuture<V>(this.nodeReference);

            Map.Entry<Consumer, RCLFuture> entry =
                    new AbstractMap.SimpleEntry<Consumer, RCLFuture>(callback, future);
            pendingGoalRequests.put(sequenceNumber, entry);
            return future;
        }
    }

    public static native void nativeSendGoalRequest(
            long actionClientHandle,
            long sequenceNumber,
            long actionGoalMsgFromJavaConverterHandle,
            long actionGoalMsgToJavaConverterHandle,
            long actionGoalMsgDestructorHandle,
            MessageDefinition goalRequestMsg
    );

    /**
     * ================================ GETTERS ================================
     */

    public final Class<MessageDefinition> getGoalType() {
        return this.goalType;
    }

    public final Class<MessageDefinition> getResultType() {
        return this.resultType;
    }

    public final Class<MessageDefinition> getFeedbackType() {
        return this.feedbackType;
    }

    /**
     * ================================ OTHER STUFF ================================
     */

    @Override
    public void cancelGoal(ActionDefinition actionGoalMessage) {

    }

    @Override
    public void executeResultCallback(MessageDefinition resultMessage) {

    }

    @Override
    public void executeFeedbackCallback(MessageDefinition feedbackMessage) {

    }

    @Override
    public void dispose() {

    }

    @Override
    public long getHandle() {
        return 0;
    }
}
