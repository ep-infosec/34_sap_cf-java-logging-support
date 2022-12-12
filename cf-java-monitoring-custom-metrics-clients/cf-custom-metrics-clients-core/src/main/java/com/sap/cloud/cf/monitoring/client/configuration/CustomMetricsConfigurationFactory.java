package com.sap.cloud.cf.monitoring.client.configuration;

import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CustomMetricsConfigurationFactory {

    private static final String CUSTOM_METRICS_KEY = "CUSTOM_METRICS";
    private static final Gson gson =
        new GsonBuilder().registerTypeAdapter(long.class, new LongIntervalGsonTypeAdapter()).create();

    public static CustomMetricsConfiguration create() {
        return create(System.getenv());
    }

    public static CustomMetricsConfiguration create(Map<String, String> env) {
        String customMetricsString = env.get(CUSTOM_METRICS_KEY);
        if (customMetricsString == null || customMetricsString.isEmpty()) {
            return new CustomMetricsConfiguration();
        }
        return gson.fromJson(customMetricsString, CustomMetricsConfiguration.class);
    }
}
