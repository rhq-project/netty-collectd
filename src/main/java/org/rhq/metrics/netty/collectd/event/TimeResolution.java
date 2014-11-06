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

package org.rhq.metrics.netty.collectd.event;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author Thomas Segismont
 */
public enum TimeResolution {
    SECONDS, HIGH_RES;

    public static long toMillis(TimeSpan timeSpan) {
        return toMillis(timeSpan.getValue(), timeSpan.getResolution());
    }

    public static long toMillis(long val, TimeResolution resolution) {
        if (resolution == SECONDS) {
            return TimeUnit.MILLISECONDS.convert(val, TimeUnit.SECONDS);
        }
        return (long) (((double) (val)) / 1073741.824);
    }

    public static Date toDate(TimeSpan timeSpan) {
        return toDate(timeSpan.getValue(), timeSpan.getResolution());
    }

    public static Date toDate(long timestamp, TimeResolution resolution) {
        return new Date(toMillis(timestamp, resolution));
    }
}
