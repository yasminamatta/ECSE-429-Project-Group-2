package com.software.validation.ECSE429.stressTests;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

public class Resource {

    public static void main(String[] args) {

        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

        //Get system CPU usage
        System.out.println("System CPU usage: " + osBean.getSystemCpuLoad() + " %");

        //Get process CPU usage
        System.out.println("Process CPU usage: " + osBean.getProcessCpuLoad() + " %");

        //Get total physical memory size
        long totalMemorySize = osBean.getTotalPhysicalMemorySize() / (1024 * 1024);
        System.out.println("Total memory size: " + totalMemorySize + " MB");

        //Get free physical memory size
        long freeMemorySize = osBean.getFreePhysicalMemorySize() / (1024 * 1024);
        System.out.println("Free memory size: " + freeMemorySize + " MB");

        //Get used physical memory size
        long usedMemorySize = (osBean.getTotalPhysicalMemorySize() - osBean.getFreePhysicalMemorySize()) / (1024 * 1024);
        System.out.println("Used memory size: " + usedMemorySize + " MB");
    }
}
