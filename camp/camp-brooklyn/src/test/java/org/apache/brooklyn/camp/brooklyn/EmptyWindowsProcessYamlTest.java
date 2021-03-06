/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.brooklyn.camp.brooklyn;

import org.apache.brooklyn.api.entity.Entity;
import org.apache.brooklyn.core.entity.Attributes;
import org.apache.brooklyn.core.entity.Entities;
import org.apache.brooklyn.core.entity.EntityAsserts;
import org.apache.brooklyn.entity.software.base.EmptyWindowsProcess;
import org.apache.brooklyn.location.winrm.WinRmMachineLocation;
import org.testng.annotations.Test;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

@Test
public class EmptyWindowsProcessYamlTest extends AbstractYamlTest {

    // takes couple of seconds unfortunately (why?!), but our only unit-test coverage of 
    // EmptyWindowsProcess so including in unit tests anyway.
    @Test
    public void testNoWinrm() throws Exception {
        Entity app = createAndStartApplication(
                "location: byon:(hosts=\"1.2.3.4\",osFamily=windows)",
                "services:",
                "- type: "+EmptyWindowsProcess.class.getName(),
                "  brooklyn.config:",
                "    winrmMonitoring.enabled: false",
                "    onbox.base.dir.skipResolution: true");
        waitForApplicationTasks(app);

        EmptyWindowsProcess entity = Iterables.getOnlyElement(Entities.descendants(app, EmptyWindowsProcess.class));
        EntityAsserts.assertAttributeEqualsEventually(entity, Attributes.SERVICE_UP, true);
        EntityAsserts.assertAttributeEqualsContinually(entity, Attributes.SERVICE_UP, true);
        
        Iterables.find(entity.getLocations(), Predicates.instanceOf(WinRmMachineLocation.class));
    }
}
