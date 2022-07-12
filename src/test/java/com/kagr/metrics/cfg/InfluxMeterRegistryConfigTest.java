package com.kagr.metrics.cfg;

import com.kagr.metrics.cfg.properties.InfluxMetricsProps;
import com.kagr.metrics.cfg.registries.InfluxMeterRegistryConfig;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.junit.Assert;
import org.junit.Test;

import java.time.Duration;

public class InfluxMeterRegistryConfigTest
{
    @Test
    public void createInfluxRegistry() throws ConfigurationException
    {
        final Parameters params = new Parameters();
        final FileBasedConfigurationBuilder<XMLConfiguration> builder = new FileBasedConfigurationBuilder<XMLConfiguration>(XMLConfiguration.class)
                .configure(params.xml()
                        .setThrowExceptionOnMissing(false)
                        .setEncoding("UTF-8")
                        .setListDelimiterHandler(new DefaultListDelimiterHandler(';'))
                        .setValidating(false)
                        .setFileName("./src/test/resources/junit-metrics.xml"));
        final XMLConfiguration config = builder.getConfiguration();
        InfluxMetricsProps props = new InfluxMetricsProps();
        props.loadFromConfig(config);
        InfluxMeterRegistryConfig influxMeterRegistryConfig = new InfluxMeterRegistryConfig(props);

        Assert.assertEquals(props.getDbName(), influxMeterRegistryConfig.db());
        Assert.assertEquals(props.getUri(), influxMeterRegistryConfig.uri());
        Assert.assertEquals(Duration.ofSeconds(5), influxMeterRegistryConfig.step());
    }
}
