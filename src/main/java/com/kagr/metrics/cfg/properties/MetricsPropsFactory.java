package com.kagr.metrics.cfg.properties;





import java.util.Map;





public class MetricsPropsFactory
{
    static Map<String, MetricsProps> _metricsPropsMap = Map.of(
        "Influx", new InfluxMetricsProps(),
        "Datadog", new StatsdMetricsProps()
    );





    public static MetricsProps getMetricsProps(String registry_)
    {
        return _metricsPropsMap.getOrDefault(registry_, new StatsdMetricsProps());
    }
}
