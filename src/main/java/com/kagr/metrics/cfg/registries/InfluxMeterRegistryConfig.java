package com.kagr.metrics.cfg.registries;





import com.kagr.metrics.cfg.properties.InfluxMetricsProps;
import com.kagr.metrics.cfg.properties.MetricsProps;
import io.micrometer.influx.InfluxConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.Duration;





@Builder
@AllArgsConstructor
public class InfluxMeterRegistryConfig implements InfluxConfig
{
    InfluxMetricsProps _influxMetricsProps;





    public InfluxMeterRegistryConfig(MetricsProps props_)
    {
        this._influxMetricsProps = (InfluxMetricsProps) props_;
    }





    @Override
    public Duration step()
    {
        return Duration.ofSeconds(_influxMetricsProps.getReportingFrequencyInSeconds());
    }





    @Override
    public String db()
    {
        return _influxMetricsProps.getDbName();
    }





    @Override
    public String get(String key_)
    {
        return null;
    }





    @Override
    public String uri()
    {
        return _influxMetricsProps.getUri();
    }
}
