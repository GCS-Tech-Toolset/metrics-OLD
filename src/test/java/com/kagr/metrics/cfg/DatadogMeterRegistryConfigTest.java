package com.kagr.metrics.cfg;

import com.kagr.metrics.cfg.properties.InfluxMetricsProps;
import com.kagr.metrics.cfg.properties.StatsdMetricsProps;
import com.kagr.metrics.cfg.registries.InfluxMeterRegistryConfig;
import com.kagr.metrics.cfg.registries.StatsdMeterRegistryConfig;
import io.micrometer.statsd.StatsdFlavor;
import io.micrometer.statsd.StatsdProtocol;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.junit.Assert;
import org.junit.Test;

import java.time.Duration;

public class DatadogMeterRegistryConfigTest
{
    @Test
    public void createDatadogRegistry() throws ConfigurationException
    {
        final Parameters params = new Parameters();
        final FileBasedConfigurationBuilder<XMLConfiguration> builder = new FileBasedConfigurationBuilder<XMLConfiguration>(XMLConfiguration.class)
                .configure(params.xml()
                        .setThrowExceptionOnMissing(false)
                        .setEncoding("UTF-8")
                        .setListDelimiterHandler(new DefaultListDelimiterHandler(';'))
                        .setValidating(false)
                        .setFileName("./src/test/resources/junit-metrics-datadog.xml"));
        final XMLConfiguration config = builder.getConfiguration();
        StatsdMetricsProps props = new StatsdMetricsProps();
        props.loadFromConfig(config);
        StatsdMeterRegistryConfig datadogMeterRegistryConfig = new StatsdMeterRegistryConfig(props);

        Assert.assertEquals(true, datadogMeterRegistryConfig.enabled());
        Assert.assertEquals(true, datadogMeterRegistryConfig.buffered());
        Assert.assertEquals(Duration.ofSeconds(5), datadogMeterRegistryConfig.pollingFrequency());
        Assert.assertEquals(Duration.ofMinutes(1), datadogMeterRegistryConfig.step());
        Assert.assertEquals(StatsdFlavor.DATADOG, datadogMeterRegistryConfig.flavor());
        Assert.assertEquals("127.0.0.1", datadogMeterRegistryConfig.host());
        Assert.assertEquals(8125, datadogMeterRegistryConfig.port());
        Assert.assertEquals(StatsdProtocol.UDP, datadogMeterRegistryConfig.protocol());
        Assert.assertEquals(true, datadogMeterRegistryConfig.publishUnchangedMeters());






    }
}
