// Path: car/src/main/java/com/example/car/utils/EnergyMonitor.java
package com.example.car.utils;

import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;

public class EnergyMonitor {
    private final OperatingSystemMXBean osBean;
    private long startTime;
    private double startCPULoad;

    // Estimated watts per CPU load unit (can be calibrated)
    private static final double WATTS_PER_CPU_LOAD = 15.0;

    public EnergyMonitor() {
        this.osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    }

    public void start() {
        startTime = System.currentTimeMillis();
        startCPULoad = osBean.getCpuLoad();
    }

    public EnergyMetrics stop() {
        long endTime = System.currentTimeMillis();
        double endCPULoad = osBean.getCpuLoad();

        double durationInHours = (endTime - startTime) / (1000.0 * 3600.0); // Convert ms to hours
        double averageCPULoad = (startCPULoad + endCPULoad) / 2.0;

        // Calculate power and energy
        double powerWatts = averageCPULoad * WATTS_PER_CPU_LOAD * osBean.getAvailableProcessors();
        double energyKWh = (powerWatts * durationInHours) / 1000.0; // Convert Wh to kWh

        return new EnergyMetrics(powerWatts, energyKWh);
    }

    public static class EnergyMetrics {
        private final double powerConsumptionWatts;
        private final double energyConsumptionKWh;

        public EnergyMetrics(double powerConsumptionWatts, double energyConsumptionKWh) {
            this.powerConsumptionWatts = powerConsumptionWatts;
            this.energyConsumptionKWh = energyConsumptionKWh;
        }

        public double getPowerConsumptionWatts() {
            return powerConsumptionWatts;
        }

        public double getEnergyConsumptionKWh() {
            return energyConsumptionKWh;
        }
    }
}