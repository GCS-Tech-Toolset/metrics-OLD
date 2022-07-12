/****************************************************************************
 * FILE: MetricsPublisherTest.java
 * DSCRPT: 
 ****************************************************************************/





package com.kagr.metrics.cfg;





import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.junit.Ignore;
import org.junit.Test;



import lombok.extern.slf4j.Slf4j;





@Slf4j
public class MetricsPublisherTest
{

    @Test
    @Ignore
    public void test()
    {
        try
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
            AppMetrics metrics = AppMetrics.initFromConfig(config);


            try
            {
                Thread.sleep(10_000);
            }
            catch (InterruptedException ex_)
            {
                _logger.error(ex_.toString(), ex_);
            }
        }
        catch (ConfigurationException ex_)
        {
            _logger.error(ex_.toString(), ex_);
        }


    }

}
