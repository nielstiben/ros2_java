package org.ros2.rcljava.action_client;

import org.ros2.rcljava.common.JNIUtils;
import org.ros2.rcljava.interfaces.ActionDefinition;
import org.ros2.rcljava.interfaces.MessageDefinition;
import org.ros2.rcljava.node.Node;
import org.ros2.rcljava.publisher.PublisherImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;

/**
 * @author Niels Tiben
 */
public class ActionClientImpl<T extends ActionDefinition> implements ActionClient {
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

    /**
     * An integer that represents a pointer to the underlying ROS2 publisher
     * structure (rcl_publisher_t).
     */
    private long handle;

    /**
     * The topic to which this publisher will publish messages.
     */
    private final String topic;

    public ActionClientImpl(final WeakReference<Node> nodeReference, final long handle, final String topic) {
        this.nodeReference = nodeReference;
        this.handle = handle;
        this.topic = topic;
    }

    public static native void nativeHello();

    public final void hello(){
        nativeHello();
    }
    // =====================================================
    // OVERRIDE METHODS
    // =====================================================

    @Override
    public void sendGoal(ActionDefinition actionGoalMessage) {

    }

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
