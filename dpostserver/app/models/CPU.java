package models;

import com.sun.management.OperatingSystemMXBean;
import java.lang.management.*;
/**
 * Copyright (C) 2013 Peter Kovgan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Main class, here the business starts
 * FIXME: create graceful interruption
*/
public class CPU { 
    
    private int  availableProcessors = ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();
    
    private long lastSystemTime = 0;
    private long lastProcessCpuTime = 0;

    public synchronized double getCpuUsage() {
        if(lastSystemTime == 0) {
            baselineCounters();
            return 0;
        }

        long systemTime = System.nanoTime();
        long processCpuTime = 0;

        if(ManagementFactory.getOperatingSystemMXBean() instanceof OperatingSystemMXBean) {
            processCpuTime = ((OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean()).getProcessCpuTime();
        }

        double cpuUsage = (double) (processCpuTime - lastProcessCpuTime) / (systemTime - lastSystemTime);

        lastSystemTime = systemTime;
        lastProcessCpuTime = processCpuTime;

        return cpuUsage / availableProcessors;
    }

    private void baselineCounters() {
        lastSystemTime = System.nanoTime();

        if (ManagementFactory.getOperatingSystemMXBean() instanceof OperatingSystemMXBean) {
            lastProcessCpuTime = ( (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean() ).getProcessCpuTime();
        }
    }
    
}