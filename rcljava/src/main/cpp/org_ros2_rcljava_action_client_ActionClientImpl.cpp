// Copyright 2016-2018 Niels Tiben <nielstiben@outlook.com>
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

#include <jni.h>

#include <cassert>
#include <string>

#include "rcl/error_handling.h"
#include "rcl/node.h"
#include "rcl_action/rcl_action.h"
#include "rmw/rmw.h"

#include "rcljava_common/exceptions.hpp"
#include "rcljava_common/signatures.hpp"

#include "org_ros2_rcljava_action_client_ActionClientImpl.h"
#include "test_msgs/action/fibonacci.h"
#include <iostream>


using rcljava_common::exceptions::rcljava_throw_rclexception;
using rcljava_common::signatures::convert_from_java_signature;
using rcljava_common::signatures::destroy_ros_message_signature;

JNIEXPORT void JNICALL
Java_org_ros2_rcljava_action_1client_ActionClientImpl_nativeSendGoalRequest(
        JNIEnv * env, jclass,
        jlong action_client_handle,
        jlong sequence_number,
        jlong jaction_goal_msg_from_java_converter_handle,
        jlong jaction_goal_msg_to_java_converter_handle,
        jlong jaction_goal_msg_destructor_handle,
        jobject jaction_goal)
{
    assert(action_client_handle != 0);
    assert(jaction_goal_msg_from_java_converter_handle != 0);
    assert(jaction_goal_msg_to_java_converter_handle != 0);
    assert(jaction_goal_msg_destructor_handle != 0);
    assert(jaction_goal != nullptr);

    // Get Action client
    rcl_action_client_t * action_client = reinterpret_cast<rcl_action_client_t *>(action_client_handle);

    convert_from_java_signature convert_from_java =
            reinterpret_cast<convert_from_java_signature>(jaction_goal_msg_from_java_converter_handle);

    // Get Goal
    void * action_goal = convert_from_java(jaction_goal, nullptr);
    std::cout << "Assume we receive a Fibonacci Goal, show its data:" << std::endl;
    test_msgs__action__Fibonacci_Goal * abc = reinterpret_cast<test_msgs__action__Fibonacci_Goal *>(action_goal);
    std::cout << "Order " << abc->order << std::endl;

    // Generate Goal Request and insert Goal
    auto action_goal_request_msg = test_msgs__action__Fibonacci_SendGoal_Request__create();
    action_goal_request_msg->goal = *abc;

    // Send Goal Request
    rcl_ret_t ret = rcl_action_send_goal_request(action_client, action_goal_request_msg, &sequence_number);

    destroy_ros_message_signature destroy_ros_message =
            reinterpret_cast<destroy_ros_message_signature>(jaction_goal_msg_destructor_handle);
    destroy_ros_message(action_goal);

    if (ret != RCL_RET_OK) {
        std::string msg = "Failed to send action goal: " + std::string(rcl_get_error_string().str);
        rcl_reset_error();
        rcljava_throw_rclexception(env, ret, msg);
    }
}