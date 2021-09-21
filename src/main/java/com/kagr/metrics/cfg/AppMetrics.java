/****************************************************************************
 * FILE: CtrailProps.java
 * DSCRPT: 
 ****************************************************************************/





package com.kagr.metrics.cfg;





import java.time.Duration;



import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.jasypt.util.text.BasicTextEncryptor;



import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.simple.SimpleConfig;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.influx.InfluxConfig;
import io.micrometer.influx.InfluxMeterRegistry;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;





@Slf4j @Data
public class AppMetrics
{

    private static final long serialVersionUID = 1741838256855050257L;


    @Getter private CompositeMeterRegistry _registry;

    private String _appName;

    private String _dbName;
    private String _dbUsername;
    private String _dbPassword;
    private String _uri;

    private boolean _autoCreateDb;
    private boolean _enabled;

    private int _batchSize;
    private int _nPublisherThreads;
    private int _reportingFrequencyInSeconds;





    private AppMetrics()
    {
        _registry = new CompositeMeterRegistry(Clock.SYSTEM);
    }





    public static final AppMetrics initFromConfig(XMLConfiguration cfg_)
    {
        AppMetrics appM = getInstance();

        appM.setEnabled(cfg_.getBoolean("AppMetrics.Enabled", true));
        appM._registry.add(new SimpleMeterRegistry(SimpleConfig.DEFAULT, Clock.SYSTEM));
        if (!appM.isEnabled())
        {
            if (_logger.isDebugEnabled())
            {
                _logger.debug("application metrics are not enabled, no further configuration will be read/applied");
            }
            
            return appM;
        }

        appM.loadConfig(cfg_);
        appM.configureRegistry();


        return appM;
    }





    private String buildKey(@NonNull String key_)
    {
        String key = String.format("AppMetrics.%s", key_);
        if (_logger.isTraceEnabled())
        {
            _logger.trace("key:{}", key);
        }
        return key;
    }





    private void loadConfig(XMLConfiguration cfg_)
    {
        _enabled = cfg_.getBoolean(buildKey("Enabled"), false);
        if (!_enabled)
        {
            _logger.error("Metrics not enabled, config load abandoned");
            return;
        }
        else
        {
            if (_logger.isTraceEnabled())
            {
                _logger.trace("Metrics enabled, config loading");
            }
        }


        _dbUsername = decrypt(cfg_, buildKey("DB.UserName"), "grafana");
        _dbPassword = decrypt(cfg_, buildKey("DB.Pasword"), "grafana");
        _dbName = cfg_.getString(buildKey("DB.Name"), "grafana");
        _uri = cfg_.getString(buildKey("URI"), "http://localhost:8086");
        _appName = cfg_.getString(buildKey("AppName"), "tradesys-app");

        _autoCreateDb = cfg_.getBoolean(buildKey("AutoCreateDb"), true);

        _batchSize = cfg_.getInt(buildKey("BatchSizing"), 1000);
        _reportingFrequencyInSeconds = cfg_.getInt(buildKey("ReportingFrequencyInSeconds"), 5);

    }





    private void configureRegistry()
    {
        if (!_enabled)
        {
            _logger.warn("influx metrics not enabled!");
            return;
        }

        InfluxConfig cfg = new InfluxConfig()
        {
            @Override
            public Duration step()
            {
                return Duration.ofSeconds(_reportingFrequencyInSeconds);
            }





            @Override
            public String db()
            {
                return _dbName;
            }





            @Override
            public String get(String key_)
            {
                return null;
            }
        };

        // # Whether to create the Influx database if it does not exist before attempting to publish metrics to it.
        System.setProperty("management.metrics.export.influx.auto-create-db", Boolean.toString(_autoCreateDb));

        // Number of measurements per request to use for this backend. If more measurements are found, then multiple requests will be made.
        System.setProperty("management.metrics.export.influx.batch-size", Integer.toString(_batchSize));

        // Whether exporting of metrics to this backend is enabled.
        System.setProperty("management.metrics.export.influx.enabled", Boolean.toString(_enabled));

        // Step size (i.e. reporting frequency) to use.
        System.setProperty("management.metrics.export.influx.step", Integer.toString(_reportingFrequencyInSeconds) + "s");

        // URI of the Influx server.
        System.setProperty("management.metrics.export.influx.uri", _uri);
        System.setProperty("management.metrics.export.influx.user-name", _dbUsername);
        System.setProperty("management.metrics.export.influx.password", _dbPassword);
        System.setProperty("management.metrics.export.influx.db", _dbName);

        // Number of threads to use with the metrics publishing scheduler.
        System.setProperty("management.metrics.export.influx.num-threads", "2");

        // Read timeout for requests to this backend.
        System.setProperty("management.metrics.export.influx.read-timeout", "10s");

        // policy to use (Influx writes to the DEFAULT retention policy if one is not specified).
        System.setProperty("management.metrics.export.influx.retention-policy", "Retention");

        // Whether to enable GZIP compression of metrics batches published to Influx.
        System.setProperty("management.metrics.export.influx.compressed", "true");

        // Connection timeout for requests to this backend.
        System.setProperty("management.metrics.export.influx.connect-timeout", "1s");

        // Write consistency for each point.
        System.setProperty("management.metrics.export.influx.consistency", "one");

        _registry.add(new InfluxMeterRegistry(cfg, Clock.SYSTEM));

        new JvmMemoryMetrics().bindTo(_registry);
        new JvmGcMetrics().bindTo(_registry);
        new ProcessorMetrics().bindTo(_registry);
        new JvmThreadMetrics().bindTo(_registry);
    }





    public static final String decrypt(final XMLConfiguration cfg_, final String key_, String default_)
    {
        String encryptedString = cfg_.getString(key_);
        if (StringUtils.isEmpty(encryptedString))
        {
            _logger.error("not found, key:{}, returning default:{}", key_, default_);
            return default_;
        }

        BasicTextEncryptor encryptor = new BasicTextEncryptor();
        encryptor.setPassword(Long.toString(serialVersionUID));
        return encryptor.decrypt(encryptedString);
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



}
