/****************************************************************************
 * FILE: CtrailProps.java
 * DSCRPT: 
 ****************************************************************************/


package com.kagr.metrics.cfg;





import com.kagr.metrics.cfg.properties.MetricsProps;
import com.kagr.metrics.cfg.properties.MetricsPropsFactory;
import com.kagr.metrics.cfg.registries.InfluxMeterRegistryConfig;
import com.kagr.metrics.cfg.registries.RegistryNotReadyException;
import com.kagr.metrics.cfg.registries.StatsdMeterRegistryConfig;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.binder.jvm.JvmCompilationMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.simple.SimpleConfig;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.influx.InfluxMeterRegistry;
import io.micrometer.statsd.StatsdMeterRegistry;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import java.util.concurrent.atomic.AtomicLong;

import static com.kagr.metrics.cfg.MetricsUtils.buildMetricsKey;





@Slf4j
public class AppMetrics
{
    @Getter @Setter private CompositeMeterRegistry _registry;
    @Getter @Setter private MetricsProps _metricsConfig;
    @Getter @Setter private boolean _enabled;





    private AppMetrics()
    {
        try
        {
            _registry = new CompositeMeterRegistry(Clock.SYSTEM);
        } catch (Exception ex_)
        {
            _logger.error(ex_.toString(), ex_);
            throw new RegistryNotReadyException("AppMetrics::AppMetrics() - registy not ready!");
        }
    }





    public static AppMetrics initFromConfig(String cfg_)
    {
        try
        {
            _logger.info("loading config from:{}", cfg_);
            final Parameters params = new Parameters();
            final FileBasedConfigurationBuilder<XMLConfiguration> builder = new FileBasedConfigurationBuilder<XMLConfiguration>(XMLConfiguration.class)
                    .configure(params.xml()
                            .setThrowExceptionOnMissing(false)
                            .setEncoding("UTF-8")
                            .setListDelimiterHandler(new DefaultListDelimiterHandler(';'))
                            .setValidating(false)
                            .setFileName(cfg_));
            final XMLConfiguration config = builder.getConfiguration();
            return initFromConfig(config);
        } catch (ConfigurationException ex_)
        {
            _logger.error(ex_.toString(), ex_);
        }

        return null;
    }





    public static AppMetrics initFromConfig(XMLConfiguration cfg_)
    {

        AppMetrics appM = getInstance();

        appM.setEnabled(cfg_.getBoolean("AppMetrics.Enabled", true));
        appM._registry.add(new SimpleMeterRegistry(SimpleConfig.DEFAULT, Clock.SYSTEM));
        new TimedAspect(appM._registry);
        if (!appM.isEnabled())
        {
            _logger.info("application metrics are not enabled, no further configuration will be read/applied");
            return appM;
        }

        appM.setMetricsConfig(appM.loadConfig(cfg_));
        appM.configureRegistry();
        return appM;
    }





    public Timer createTimer(@NonNull String name_)
    {
        return createTimer(name_, null);
    }





    public Timer createTimer(@NonNull String name_, String[] tags_)
    {
        if (_registry == null)
        {
            throw new RegistryNotReadyException("createTimer - registy not ready!");
        }

        if (_logger.isTraceEnabled())
        {
            _logger.trace("building timer:{}", name_);
        }


        return Timer
                .builder(name_)
                .tags(getCommonTags())
                .description("a description of what this timer does") // optional
                .publishPercentileHistogram()
                .register(_registry);
    }





    public AtomicLong createGauge(@NonNull String name_)
    {
        if (_registry == null)
        {
            throw new RegistryNotReadyException("createTimer - registy not ready!");
        }

        if (_logger.isTraceEnabled())
        {
            _logger.trace("building timer:{}", name_);
        }

        AtomicLong val = new AtomicLong(0);
        _registry.gauge(name_, getCommonTags(), val);
        return val;
    }





    public Counter createCounter(@NonNull String name_, String[] tags_)
    {
        if (_registry == null)
        {
            throw new RegistryNotReadyException("createTimer - registy not ready!");
        }

        if (_logger.isTraceEnabled())
        {
            _logger.trace("building timer:{}", name_);
        }

        return _registry.counter(name_, getCommonTags());
    }





    public Counter createCounter(@NonNull String name_)
    {
        return createCounter(name_, null);
    }





    private MetricsProps loadConfig(XMLConfiguration cfg_)
    {
        _enabled = cfg_.getBoolean(buildMetricsKey("Enabled"), false);
        if (!_enabled)
        {
            _logger.error("Metrics not enabled, config load abandoned");
            return null;
        } else
        {
            if (_logger.isTraceEnabled())
            {
                _logger.trace("Metrics enabled, config loading");
            }
        }

        MetricsProps props = MetricsPropsFactory.getMetricsProps(cfg_.getString(buildMetricsKey("Registry")));
        props.loadFromConfig(cfg_);

        if (_logger.isInfoEnabled())
        {
            _logger.info("app-name:{}", props.getAppName());
            _logger.info("host:{}", props.getHostname());
            _logger.info("env:{}", props.getEnv());
        }
        return props;
    }





    private void configureRegistry()
    {
        if (!_enabled)
        {
            _logger.warn("influx metrics not enabled!");
            return;
        }

        MeterRegistry meterRegistry;

        if (_metricsConfig.getRegistry().equalsIgnoreCase("Influx"))
        {
            InfluxMeterRegistryConfig meterRegistryConfig = new InfluxMeterRegistryConfig(_metricsConfig);
            meterRegistry = new InfluxMeterRegistry(meterRegistryConfig, Clock.SYSTEM);
        } else
        {
            StatsdMeterRegistryConfig meterRegistryConfig = new StatsdMeterRegistryConfig(_metricsConfig);
            meterRegistry = new StatsdMeterRegistry(meterRegistryConfig, Clock.SYSTEM);
        }
        _registry.add(meterRegistry);
        new JvmMemoryMetrics().bindTo(_registry);
        new JvmGcMetrics().bindTo(_registry);
        new ProcessorMetrics().bindTo(_registry);
        new JvmThreadMetrics().bindTo(_registry);
        new JvmCompilationMetrics().bindTo(_registry);
    }





    ///////////////////////////////
    //
    ////////////////////////////////
    public static final AppMetrics getInstance()
    {
        return SingletonAppMetrics._instance;
    }





    private static final class SingletonAppMetrics
    {
        private static final AppMetrics _instance = new AppMetrics();
    }


    private Tags getCommonTags()
    {
        if (_metricsConfig == null)
        {
            return null;
        }
        else
        {
            return _metricsConfig.getCommonTags();
        }
    }
}
