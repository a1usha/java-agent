package ru.nsu.fit.ojp.ushaev.ojp_3;

import java.util.*;

public class TimeRegistry {

    public static TimeRegistry instance;

    private final Map<String, TreeSet<Long>> metricMap = Collections.synchronizedMap(new HashMap<>());

    private TimeRegistry() {}

    static {
        instance = new TimeRegistry();
    }

    public void addMetric(String methodName, long metric) {
        synchronized (instance.metricMap) {
            if (instance.metricMap.containsKey(methodName)) {
                instance.metricMap.get(methodName).add(metric);
            } else {
                instance.metricMap.put(methodName, new TreeSet<>(Collections.singletonList(metric)));
            }
        }
    }

    public Long getMinTime(String methodName) {
        synchronized (instance.metricMap) {
            if (instance.metricMap.containsKey(methodName)) {
                return instance.metricMap.get(methodName).first();
            } else {
                return null;
            }
        }
    }

    public Long getMaxTime(String methodName) {
        synchronized (instance.metricMap) {
            if (instance.metricMap.containsKey(methodName)) {
                return instance.metricMap.get(methodName).last();
            } else {
                return null;
            }
        }
    }

    public Long getAvgTime(String methodName) {
        synchronized (instance.metricMap) {
            if (instance.metricMap.containsKey(methodName)) {
                Long total = 0L;
                for (Long t: instance.metricMap.get(methodName)) {
                    total += t;
                }
                return total / instance.metricMap.get(methodName).size();
            } else {
                return null;
            }
        }
    }
}

