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
#include <cstdio>
#include <cstdlib>
#include <string>

#include "rcl/error_handling.h"
#include "rcl/node.h"
#include "rcl/rcl.h"
#include "rcl_action/rcl_action.h"
#include "rmw/rmw.h"

#include "rcljava_common/exceptions.hpp"
#include "rcljava_common/signatures.hpp"

#include "org_ros2_rcljava_action_client_ActionClientImpl.h"
#include <iostream> // TODO: Remove this line, it's not needed!


using rcljava_common::exceptions::rcljava_throw_rclexception;
using rcljava_common::signatures::convert_from_java_signature;
using rcljava_common::signatures::destroy_ros_message_signature;

JNIEXPORT void JNICALL
Java_org_ros2_rcljava_action_1client_ActionClientImpl_nativeSendGoalRequest(
        JNIEnv * env, jclass, jlong action_client_handle, jlong jaction_msg_destructor_handle,
        jobject jgoal_request_msg)
{
    // Get Action client
    rcl_action_client_t * action_client = reinterpret_cast<rcl_action_client_t *>(action_client_handle);
    // Goal message class
    jclass jaction_message_class = env->GetObjectClass(jgoal_request_msg);
    jmethodID mid = env->GetStaticMethodID(jaction_message_class, "getFromJavaConverter", "()J");
    jlong jfrom_java_converter = env->CallStaticLongMethod(jaction_message_class, mid);

    convert_from_java_signature convert_from_java =
            reinterpret_cast<convert_from_java_signature>(jfrom_java_converter);

    void * raw_ros_message = convert_from_java(jgoal_request_msg, nullptr);

    int64_t sequence_number;

    rcl_ret_t ret = rcl_action_send_goal_request(action_client, raw_ros_message, &sequence_number);
    destroy_ros_message_signature destroy_ros_message =
            reinterpret_cast<destroy_ros_message_signature>(jaction_msg_destructor_handle);
    destroy_ros_message(raw_ros_message);

    if (ret != RCL_RET_OK) {
        std::string msg = "Failed to send action goal: " + std::string(rcl_get_error_string().str);
        rcl_reset_error();
        rcljava_throw_rclexception(env, ret, msg);
    }
//    rcljava_throw_rclexception(env, 0, "not implemented");
}