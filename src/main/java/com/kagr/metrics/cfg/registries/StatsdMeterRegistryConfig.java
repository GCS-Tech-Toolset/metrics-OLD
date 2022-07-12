package com.kagr.metrics.cfg.registries;





import com.kagr.metrics.cfg.properties.MetricsProps;
import com.kagr.metrics.cfg.properties.StatsdMetricsProps;
import io.micrometer.core.instrument.config.validate.Validated;
import io.micrometer.core.instrument.config.validate.ValidationException;
import io.micrometer.statsd.StatsdConfig;
import io.micrometer.statsd.StatsdFlavor;
import io.micrometer.statsd.StatsdProtocol;
import java.time.Duration;





public class StatsdMeterRegistryConfig implements StatsdConfig
{
    private StatsdMetricsProps _statsdMetricsProps;





    public StatsdMeterRegistryConfig(MetricsProps props_)
    {
        this._statsdMetricsProps = (StatsdMetricsProps) props_;
    }



    @Override
    public String get(String s)
    {
        return null;
    }





    @Override
    public String prefix()
    {
        return StatsdConfig.super.prefix();
    }





    @Override
    public StatsdFlavor flavor()
    {
        return StatsdConfig.super.flavor();
    }





    @Override
    public boolean enabled()
    {
        return _statsdMetricsProps.isEnabled();
    }





    @Override
    public String host()
    {
        return _statsdMetricsProps.getHostIP();
    }





    @Override
    public int port()
    {
        return _statsdMetricsProps.getPort();
    }





    @Override
    public StatsdProtocol protocol()
    {
        return StatsdConfig.super.protocol();
    }





    @Override
    public int maxPacketLength()
    {
        return StatsdConfig.super.maxPacketLength();
    }





    @Override
    public Duration pollingFrequency()
    {
        return Duration.ofSeconds(_statsdMetricsProps.getReportingFrequencyInSeconds());
    }





    @Override
    public int queueSize()
    {
        return StatsdConfig.super.queueSize();
    }





    @Override
    public Duration step()
    {
        return StatsdConfig.super.step();
    }





    @Override
    public boolean publishUnchangedMeters()
    {
        return StatsdConfig.super.publishUnchangedMeters();
    }





    @Override
    public boolean buffered()
    {
        return StatsdConfig.super.buffered();
    }





    @Override
    public Validated<?> validate()
    {
        return StatsdConfig.super.validate();
    }





    @Override
    public void requireValid() throws ValidationException
    {
        StatsdConfig.super.requireValid();
    }
}
