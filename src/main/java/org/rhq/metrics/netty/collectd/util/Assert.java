/*
 * Copyright 2014 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.rhq.metrics.netty.collectd.util;

/**
 * @author Thomas Segismont
 */
public class Assert {

    public static void assertNotNull(Object o, String msg, Object... params) {
        if (o == null) {
            throwIllegalArgumentException(msg, params);
        }
    }

    public static void assertEquals(int a, int b, String msg, Object... params) {
        if (a != b) {
            throwIllegalArgumentException(msg, params);
        }
    }

    private static void throwIllegalArgumentException(String msg, Object[] params) {
        if (params != null && params.length > 0) {
            throw new IllegalArgumentException(String.format(msg, params));
        }
        throw new IllegalArgumentException(msg);
    }

    private Assert() {
        // Utility class
    }
}
