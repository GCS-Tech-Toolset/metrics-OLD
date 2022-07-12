package com.kagr.metrics.cfg;

import com.kagr.metrics.cfg.properties.StatsdMetricsProps;
import io.micrometer.core.instrument.Tags;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.junit.Assert;
import org.junit.Test;

public class DatadogMetricsPropsTest
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
                        .setFileName("./src/test/resources/junit-metrics-datadog.xml"));
        final XMLConfiguration config = builder.getConfiguration();
        StatsdMetricsProps props = new StatsdMetricsProps();
        props.loadFromConfig(config);

        Assert.assertEquals(true, props.isEnabled());
        Assert.assertEquals(5, props.getReportingFrequencyInSeconds());
        Assert.assertNotNull(props.getHostname());
        Assert.assertFalse(props.getHostname().isEmpty());
        Assert.assertEquals("UnitTests", props.getAppName());
        Assert.assertEquals("Datadog", props.getRegistry());
        Assert.assertEquals(Tags.of("hostname", props.getHostname(), "env", props.getEnv(), "service", props.getAppName()), props.getCommonTags());
        Assert.assertEquals("127.0.0.1", props.getHostIP());
        Assert.assertEquals(8125, props.getPort());
    }

}
