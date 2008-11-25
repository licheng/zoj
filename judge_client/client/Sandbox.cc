/*
 * Copyright 2007 Xu, Chuan <xuchuan@gmail.com>
 *
 * This file is part of ZOJ.
 *
 * ZOJ is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * ZOJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ZOJ. if not, see <http://www.gnu.org/licenses/>.
 */

#include <jni.h>
#include "Sandbox.h"

#include <string>

using namespace std;

#include <sys/resource.h>

#include "logging.h"
#include "runner.h"
#include "util.h"

JNIEXPORT jint JNICALL Java_Sandbox_setLimits(
        JNIEnv* env, jclass jcls, jint time_limit, jint output_limit, jint file_limit, jint uid, jint gid) {
    if (time_limit && SetLimit(RLIMIT_CPU, time_limit) < 0 ||
        output_limit && SetLimit(RLIMIT_FSIZE, output_limit) < 0 ||
        file_limit && SetLimit(RLIMIT_NOFILE, file_limit) ||
        gid && setgid(gid) == -1 ||
        uid && setuid(uid) == -1) {
        return -1;
    }
    return 0;
}

JNIEXPORT void JNICALL Java_Sandbox_closeLog(JNIEnv *, jclass) {
    Log::Close();
}

JNIEXPORT void JNICALL Java_Sandbox_logError(JNIEnv* env, jclass jcls, jstring message) {
    const char* buf = env->GetStringUTFChars(message, 0);
    LOG(ERROR)<<buf;
    env->ReleaseStringUTFChars(message, buf);
}
