/*package com.example.car.controllers;

import com.example.car.models.CarResponse;
import com.example.car.services.CarFeignClientService;
import com.example.car.services.CarService;
import com.example.car.services.CarWebClientService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@RestController
@RequestMapping("/api/performance")
public class PerformanceTestController {
    private final CarService restTemplateService;
    private final CarFeignClientService feignClientService;

    private final CarWebClientService webClientService;

    public PerformanceTestController(
            CarService restTemplateService,
            CarFeignClientService feignClientService,
            CarWebClientService webClientService) {
        this.restTemplateService = restTemplateService;
        this.feignClientService = feignClientService;
        this.webClientService = webClientService;
    }

    @GetMapping("/test/{numberOfRequests}")
    public Map<String, Map<String, Object>> runPerformanceTest(@PathVariable int numberOfRequests) {
        Map<String, Map<String, Object>> results = new HashMap<>();

        // Test RestTemplate
        results.put("RestTemplate", testPerformance(() -> restTemplateService.findAll(), numberOfRequests));

        // Test Feign Client
        results.put("FeignClient", testPerformance(() -> feignClientService.findAll(), numberOfRequests));

        // Test WebClient
        results.put("WebClient", testPerformance(() -> webClientService.findAll(), numberOfRequests));

        return results;
    }

    private Map<String, Object> testPerformance(Supplier<List<CarResponse>> operation, int numberOfRequests) {
        Map<String, Object> metrics = new HashMap<>();

        long startTime = System.currentTimeMillis();
        long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        long startCPUTime = threadMXBean.getCurrentThreadCpuTime();

        // Execute requests
        for (int i = 0; i < numberOfRequests; i++) {
            operation.get();
        }

        long endTime = System.currentTimeMillis();
        long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long endCPUTime = threadMXBean.getCurrentThreadCpuTime();

        // Calculate metrics
        metrics.put("averageResponseTime", (endTime - startTime) / (double) numberOfRequests);
        metrics.put("throughput", numberOfRequests / ((endTime - startTime) / 1000.0));
        metrics.put("memoryUsed", (endMemory - startMemory) / (1024.0 * 1024.0)); // in MB
        metrics.put("cpuTime", (endCPUTime - startCPUTime) / 1_000_000.0); // in ms

        return metrics;
    }
}*/

/*
package com.example.car.controllers;

import com.example.car.models.CarResponse;
import com.example.car.services.CarFeignClientService;
import com.example.car.services.CarService;
import com.example.car.services.CarWebClientService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/performance")
public class PerformanceTestController {
    private final CarService restTemplateService;
    private final CarFeignClientService feignClientService;
    private final CarWebClientService webClientService;
    private final OperatingSystemMXBean osBean;

    public PerformanceTestController(
            CarService restTemplateService,
            CarFeignClientService feignClientService,
            CarWebClientService webClientService) {
        this.restTemplateService = restTemplateService;
        this.feignClientService = feignClientService;
        this.webClientService = webClientService;
        this.osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
    }

    @GetMapping("/test/{numberOfRequests}")
    public Map<String, Map<String, Object>> runPerformanceTest(@PathVariable int numberOfRequests) {
        Map<String, Map<String, Object>> results = new HashMap<>();

        // Test RestTemplate with warm-up
        warmUp(() -> restTemplateService.findAll());
        results.put("RestTemplate", testPerformance(() -> restTemplateService.findAll(), numberOfRequests));

        // Test Feign Client with warm-up
        warmUp(() -> feignClientService.findAll());
        results.put("FeignClient", testPerformance(() -> feignClientService.findAll(), numberOfRequests));

        // Test WebClient with warm-up
        warmUp(() -> webClientService.findAll());
        results.put("WebClient", testPerformance(() -> webClientService.findAll(), numberOfRequests));

        return results;
    }

    private void warmUp(Supplier<List<CarResponse>> operation) {
        for (int i = 0; i < 5; i++) {
            operation.get();
        }
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private Map<String, Object> testPerformance(Supplier<List<CarResponse>> operation, int numberOfRequests) {
        Map<String, Object> metrics = new HashMap<>();

        long startTime = System.currentTimeMillis();
        long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        long startCPUTime = threadMXBean.getCurrentThreadCpuTime();
        double startSystemCpuLoad = osBean.getSystemLoadAverage();

        int successCount = 0;
        int failureCount = 0;
        long totalResponseSize = 0;
        long maxResponseTime = Long.MIN_VALUE;
        long minResponseTime = Long.MAX_VALUE;

        // Execute requests
        for (int i = 0; i < numberOfRequests; i++) {
            long requestStart = System.currentTimeMillis();
            try {
                List<CarResponse> response = operation.get();
                successCount++;
                totalResponseSize += response != null ? response.size() : 0;

                long requestDuration = System.currentTimeMillis() - requestStart;
                maxResponseTime = Math.max(maxResponseTime, requestDuration);
                minResponseTime = Math.min(minResponseTime, requestDuration);

            } catch (Exception e) {
                failureCount++;
            }
        }

        long endTime = System.currentTimeMillis();
        long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long endCPUTime = threadMXBean.getCurrentThreadCpuTime();
        double endSystemCpuLoad = osBean.getSystemLoadAverage();

        // Basic Metrics
        double totalDuration = (endTime - startTime);
        double avgResponseTime = totalDuration / numberOfRequests;
        metrics.put("averageResponseTime", avgResponseTime); // ms
        metrics.put("minResponseTime", minResponseTime); // ms
        metrics.put("maxResponseTime", maxResponseTime); // ms
        metrics.put("throughput", numberOfRequests / (totalDuration / 1000.0)); // requests/second

        // Resource Usage Metrics
        metrics.put("memoryUsed", (endMemory - startMemory) / (1024.0 * 1024.0)); // MB
        metrics.put("cpuTime", (endCPUTime - startCPUTime) / 1_000_000.0); // ms
        metrics.put("cpuLoadDelta", endSystemCpuLoad - startSystemCpuLoad);
        metrics.put("systemCpuLoad", osBean.getSystemLoadAverage());

        // Success/Failure Metrics
        metrics.put("successRate", (successCount * 100.0) / numberOfRequests); // percentage
        metrics.put("failureRate", (failureCount * 100.0) / numberOfRequests); // percentage
        metrics.put("successCount", successCount);
        metrics.put("failureCount", failureCount);

        // Data Metrics
        metrics.put("totalResponseSize", totalResponseSize);
        metrics.put("averageResponseSize", totalResponseSize / (double) successCount);

        // Resource Efficiency
        double requestsPerMB = numberOfRequests / (metrics.get("memoryUsed") instanceof Double ? (Double) metrics.get("memoryUsed") : 1.0);
        metrics.put("requestsPerMB", requestsPerMB);

        // Estimated Power Consumption (rough estimation based on CPU usage)
        double estimatedPowerWatts = osBean.getSystemLoadAverage() * 15; // Rough estimate: 15W per CPU load unit
        metrics.put("estimatedPowerConsumption", estimatedPowerWatts); // Watts
        metrics.put("estimatedEnergyConsumption", (estimatedPowerWatts * totalDuration) / (1000.0 * 3600.0)); // kWh

        return metrics;
    }

    @GetMapping("/system-info")
    public Map<String, Object> getSystemInfo() {
        Map<String, Object> systemInfo = new HashMap<>();
        Runtime runtime = Runtime.getRuntime();

        systemInfo.put("availableProcessors", runtime.availableProcessors());
        systemInfo.put("totalMemory", runtime.totalMemory() / (1024.0 * 1024.0));
        systemInfo.put("maxMemory", runtime.maxMemory() / (1024.0 * 1024.0));
        systemInfo.put("freeMemory", runtime.freeMemory() / (1024.0 * 1024.0));
        systemInfo.put("systemLoadAverage", osBean.getSystemLoadAverage());
        systemInfo.put("systemCpuLoad", osBean.getSystemLoadAverage());

        return systemInfo;
    }
}*/


// PerformanceTestController.java
package com.example.car.controllers;

import com.example.car.models.CarResponse;
import com.example.car.services.CarFeignClientService;
import com.example.car.services.CarService;
import com.example.car.services.CarWebClientService;
import com.example.car.utils.EnergyMonitor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/performance")
public class PerformanceTestController {
    private final EnergyMonitor energyMonitor = new EnergyMonitor();

    private final CarService restTemplateService;
    private final CarFeignClientService feignClientService;
    private final CarWebClientService webClientService;
    private final OperatingSystemMXBean osBean;

    public PerformanceTestController(
            CarService restTemplateService,
            CarFeignClientService feignClientService,
            CarWebClientService webClientService) {
        this.restTemplateService = restTemplateService;
        this.feignClientService = feignClientService;
        this.webClientService = webClientService;
        this.osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
    }

    @GetMapping("/test/{numberOfRequests}")
    public Map<String, Map<String, Object>> runPerformanceTest(@PathVariable int numberOfRequests) {
        Map<String, Map<String, Object>> results = new HashMap<>();
        try {
            // Test RestTemplate with warm-up
            warmUp(() -> restTemplateService.findAll());
            results.put("RestTemplate", testPerformance(() -> restTemplateService.findAll(), numberOfRequests));

            // Test Feign Client with warm-up
            warmUp(() -> feignClientService.findAll());
            results.put("FeignClient", testPerformance(() -> feignClientService.findAll(), numberOfRequests));

            // Test WebClient with warm-up
            warmUp(() -> webClientService.findAll());
            results.put("WebClient", testPerformance(() -> webClientService.findAll(), numberOfRequests));
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            results.put("error", error);
        }

        return results;
    }

    private void warmUp(Supplier<List<CarResponse>> operation) {
        try {
            for (int i = 0; i < 5; i++) {
                operation.get();
            }
            TimeUnit.SECONDS.sleep(1);
        } catch (Exception e) {
            System.err.println("Warm-up failed: " + e.getMessage());
        }
    }

    private Map<String, Object> testPerformance(Supplier<List<CarResponse>> operation, int numberOfRequests) {
        Map<String, Object> metrics = new HashMap<>();

        long startTime = System.currentTimeMillis();
        long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        long startCPUTime = threadMXBean.getCurrentThreadCpuTime();
        double startSystemCpuLoad = osBean.getSystemLoadAverage();

        energyMonitor.start();


        int successCount = 0;
        int failureCount = 0;
        long totalResponseSize = 0;
        long maxResponseTime = Long.MIN_VALUE;
        long minResponseTime = Long.MAX_VALUE;

        for (int i = 0; i < numberOfRequests; i++) {
            long requestStart = System.currentTimeMillis();
            try {
                List<CarResponse> response = operation.get();
                successCount++;
                totalResponseSize += response != null ? response.size() : 0;

                long requestDuration = System.currentTimeMillis() - requestStart;
                maxResponseTime = Math.max(maxResponseTime, requestDuration);
                minResponseTime = Math.min(minResponseTime, requestDuration);
            } catch (Exception e) {
                failureCount++;
                System.err.println("Request failed: " + e.getMessage());
            }
        }
        EnergyMonitor.EnergyMetrics energyMetrics = energyMonitor.stop();

        long endTime = System.currentTimeMillis();
        long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long endCPUTime = threadMXBean.getCurrentThreadCpuTime();
        double endSystemCpuLoad = osBean.getSystemLoadAverage();

        // Basic Metrics
        double totalDuration = (endTime - startTime);
        metrics.put("averageResponseTime", totalDuration / numberOfRequests);
        metrics.put("minResponseTime", minResponseTime);
        metrics.put("maxResponseTime", maxResponseTime);
        metrics.put("throughput", numberOfRequests / (totalDuration / 1000.0));

        // Resource Usage Metrics
        metrics.put("memoryUsed", (endMemory - startMemory) / (1024.0 * 1024.0));
        metrics.put("cpuTime", (endCPUTime - startCPUTime) / 1_000_000.0);
        metrics.put("cpuLoadDelta", endSystemCpuLoad - startSystemCpuLoad);
        metrics.put("systemCpuLoad", osBean.getSystemLoadAverage());

        // Success/Failure Metrics
        metrics.put("successRate", (successCount * 100.0) / numberOfRequests);
        metrics.put("failureRate", (failureCount * 100.0) / numberOfRequests);
        metrics.put("successCount", successCount);
        metrics.put("failureCount", failureCount);

        // Data Metrics
        metrics.put("totalResponseSize", totalResponseSize);
        if (successCount > 0) {
            metrics.put("averageResponseSize", totalResponseSize / (double) successCount);
        } else {
            metrics.put("averageResponseSize", 0.0);
        }

        // Resource Efficiency
        double memoryUsed = (double) metrics.get("memoryUsed");
        if (memoryUsed != 0) {
            metrics.put("requestsPerMB", numberOfRequests / memoryUsed);
        } else {
            metrics.put("requestsPerMB", 0.0);
        }
        // Add energy metrics
        metrics.put("powerConsumptionWatts", energyMetrics.getPowerConsumptionWatts());
        metrics.put("energyConsumptionKWh", energyMetrics.getEnergyConsumptionKWh());

        // Power Consumption Estimates
        double estimatedPowerWatts = osBean.getSystemLoadAverage() * 15;
        metrics.put("estimatedPowerConsumption", estimatedPowerWatts);
        metrics.put("estimatedEnergyConsumption", (estimatedPowerWatts * totalDuration) / (1000.0 * 3600.0));

        return metrics;
    }

    @GetMapping("/system-info")
    public Map<String, Object> getSystemInfo() {
        Map<String, Object> systemInfo = new HashMap<>();
        Runtime runtime = Runtime.getRuntime();

        systemInfo.put("availableProcessors", runtime.availableProcessors());
        systemInfo.put("totalMemory", runtime.totalMemory() / (1024.0 * 1024.0));
        systemInfo.put("maxMemory", runtime.maxMemory() / (1024.0 * 1024.0));
        systemInfo.put("freeMemory", runtime.freeMemory() / (1024.0 * 1024.0));
        systemInfo.put("systemLoadAverage", osBean.getSystemLoadAverage());
        systemInfo.put("systemCpuLoad", osBean.getSystemLoadAverage());

        return systemInfo;
    }
    @GetMapping("/energy-test/{numberOfRequests}")
    public Map<String, Object> runEnergyTest(@PathVariable int numberOfRequests) {
        Map<String, Object> results = new HashMap<>();

        // Test each client type
        results.put("RestTemplate", testEnergyConsumption(() -> restTemplateService.findAll(), numberOfRequests));
        results.put("FeignClient", testEnergyConsumption(() -> feignClientService.findAll(), numberOfRequests));
        results.put("WebClient", testEnergyConsumption(() -> webClientService.findAll(), numberOfRequests));

        return results;
    }

    private Map<String, Object> testEnergyConsumption(Supplier<List<CarResponse>> operation, int numberOfRequests) {
        EnergyMonitor monitor = new EnergyMonitor();
        Map<String, Object> metrics = new HashMap<>();

        monitor.start();

        // Run operation multiple times
        for (int i = 0; i < numberOfRequests; i++) {
            operation.get();
        }

        EnergyMonitor.EnergyMetrics energyMetrics = monitor.stop();

        metrics.put("powerConsumptionWatts", energyMetrics.getPowerConsumptionWatts());
        metrics.put("energyConsumptionKWh", energyMetrics.getEnergyConsumptionKWh());

        return metrics;
    }


}