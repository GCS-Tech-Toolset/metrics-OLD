package com.kagr.metrics.cfg;

import com.kagr.metrics.cfg.properties.InfluxMetricsProps;
import io.micrometer.core.instrument.Tags;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.junit.Assert;
import org.junit.Test;

public class InfluxMetricsPropsTest
{
    @Test
    public void testPropertiesLoad() throws ConfigurationException
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

        Assert.assertEquals(true, props.isEnabled());
        Assert.assertEquals(5, props.getReportingFrequencyInSeconds());
        Assert.assertNotNull(props.getHostname());
        Assert.assertFalse(props.getHostname().isEmpty());
        Assert.assertEquals("UnitTests", props.getAppName());
        Assert.assertEquals("Influx", props.getRegistry());
        Assert.assertEquals(Tags.of("hostname", props.getHostname(), "env", props.getEnv(), "service", props.getAppName()), props.getCommonTags());
        Assert.assertEquals("AEMltdqnqEmPAlD48TXVug==", props.getDbName());
        Assert.assertEquals("ee6SHf2cfaEgkYpJsQNZHUSPvpJ81ScU", props.getDbUsername());
        Assert.assertEquals("qaBernNglWl3GmR7m7KUz1bQZstYXPpz", props.getDbPassword());
        Assert.assertEquals("http://localhost:8086", props.getUri());
        Assert.assertEquals(true, props.isAutoCreateDb());
        Assert.assertEquals(1000, props.getBatchSize());
        Assert.assertEquals("2", props.getNumberOfInfluxThreads());
        Assert.assertEquals("10s", props.getInfluxReadTimeout());
        Assert.assertEquals("1s", props.getInfluxConnectTimeout());
        Assert.assertEquals("Retention", props.getInfluxRetentionPolicy());
        Assert.assertEquals("true", props.getInfluxCompressed());
        Assert.assertEquals("one", props.getInfluxConsistency());
    }
}
