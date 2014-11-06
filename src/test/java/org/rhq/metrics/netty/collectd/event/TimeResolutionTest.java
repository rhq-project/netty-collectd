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

import static org.junit.Assert.assertEquals;
import static org.rhq.metrics.netty.collectd.event.TimeResolution.HIGH_RES;
import static org.rhq.metrics.netty.collectd.event.TimeResolution.SECONDS;
import static org.rhq.metrics.netty.collectd.event.TimeResolution.toMillis;

import org.junit.Test;

public class TimeResolutionTest {

    @Test
    public void testToMillis() throws Exception {
        assertEquals(123000, toMillis(123, SECONDS));
        assertEquals(745000, toMillis(745 * (long) Math.pow(2, 30), HIGH_RES));
    }
}