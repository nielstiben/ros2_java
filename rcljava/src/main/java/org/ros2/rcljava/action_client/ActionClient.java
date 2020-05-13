package org.ros2.rcljava.action_client;

import org.ros2.rcljava.interfaces.ActionDefinition;
import org.ros2.rcljava.interfaces.Disposable;
import org.ros2.rcljava.interfaces.MessageDefinition;

/**
 * @author Niels Tiben
 */
public interface ActionClient<T extends ActionDefinition> extends Disposable {
    /**
     * Send a goal to the action server.
     *
     * @param actionGoalMessage An instance of the action goal message.
     */
    void sendGoal(final MessageDefinition actionGoalMessage);

    /**
     * Cancel a goal.
     *
     * @param actionGoalMessage An instance of the action goal cancel message.
     */
    void cancelGoal(final T actionGoalMessage);

    void executeResultCallback(MessageDefinition resultMessage);

    void executeFeedbackCallback(MessageDefinition feedbackMessage);
}
