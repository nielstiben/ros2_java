package org.ros2.rcljava.action_client;

import org.ros2.rcljava.consumers.Consumer;
import org.ros2.rcljava.interfaces.ActionDefinition;
import org.ros2.rcljava.interfaces.Disposable;
import org.ros2.rcljava.interfaces.MessageDefinition;

import java.util.concurrent.Future;

/**
 * @author Niels Tiben
 */
public interface ActionClient<T extends ActionDefinition> extends Disposable {
    <U extends MessageDefinition> Class<U> getGoalType();
    <U extends MessageDefinition> Class<U> getResultType();
    <U extends MessageDefinition> Class<U> getFeedbackType();

    /**
     * Send a goal to the action server.
     *
     * @param actionGoalMessage An instance of the action goal message.
     */
    <U extends MessageDefinition, V extends MessageDefinition> Future<V> asyncSendGoalRequest(
            final U actionGoalMessage);

    <U extends MessageDefinition, V extends MessageDefinition> Future<V> asyncSendGoalRequest(
            final U actionGoalMessage, Consumer<Future<V>> callback);

    /**
     * Cancel a goal.
     *
     * @param actionGoalMessage An instance of the action goal cancel message.
     */
    void cancelGoal(final T actionGoalMessage);

    void executeResultCallback(MessageDefinition resultMessage);

    void executeFeedbackCallback(MessageDefinition feedbackMessage);
}
