/****************************************************************************
 * FILE: CtrailProps.java
 * DSCRPT: 
 ****************************************************************************/





package com.kagr.metrics.cfg;





import java.beans.IntrospectionException;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;



import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.lang3.StringUtils;
import org.jasypt.util.text.BasicTextEncryptor;



import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.binder.jvm.JvmCompilationMetrics;
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





@Slf4j
@Data
public class AppMetrics
{

	private static final long serialVersionUID = 1741838256855050257L;


	@Getter
	private CompositeMeterRegistry _registry;

	private String _appName;

	private String	_dbName;
	private String	_dbUsername;
	private String	_dbPassword;
	private String	_uri;

	private boolean	_autoCreateDb;
	private boolean	_enabled;

	private int	_batchSize;
	private int	_nPublisherThreads;
	private int	_reportingFrequencyInSeconds;





	private AppMetrics()
	{
		try
		{
			_registry = new CompositeMeterRegistry(Clock.SYSTEM);
		}
		catch (Exception ex_)
		{
			_logger.error(ex_.toString(), ex_);
			throw new RegistryNotReadyException("AppMetrics::AppMetrics() - registy not ready!");
		}
	}





	public static final AppMetrics initFromConfig(String cfg_)
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
			XMLConfiguration config = builder.getConfiguration();
			return initFromConfig(config);
		}
		catch (ConfigurationException ex_)
		{
			_logger.error(ex_.toString(), ex_);
		}

		return null;
	}





	public static final AppMetrics initFromConfig(XMLConfiguration cfg_)
	{

		AppMetrics appM = getInstance();

		appM.setEnabled(cfg_.getBoolean("AppMetrics.Enabled", true));
		appM._registry.add(new SimpleMeterRegistry(SimpleConfig.DEFAULT, Clock.SYSTEM));
		new TimedAspect(appM._registry);
		if (!appM.isEnabled())
		{
			if (_logger.isDebugEnabled())
			{
				_logger.debug("application metrics are not enabled, no further configuration will be read/applied");
			}

			return appM;
		}

		_logger.info("loading config for: {}", cfg_);
		appM.loadConfig(cfg_);
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
				.tags(tags_)
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
		_registry.gauge(name_, val);
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

		return _registry.counter(name_, tags_);
	}





	public Counter createCounter(@NonNull String name_)
	{
		return createCounter(name_, null);
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
		_uri = cfg_.getString(buildKey("DB.URI"));
		_appName = cfg_.getString(buildKey("AppName"), "tradesys-app");

		_autoCreateDb = cfg_.getBoolean(buildKey("AutoCreateDb"), true);

		_batchSize = cfg_.getInt(buildKey("BatchSizing"), 1000);
		_reportingFrequencyInSeconds = cfg_.getInt(buildKey("ReportingFrequencyInSeconds"), 5);


		if (_logger.isInfoEnabled())
		{
			_logger.info("app-name:{}", _appName);
			_logger.info("URI:{}", _uri);
			_logger.info("database:{}", _dbName);
		}

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





			@Override
			public String uri()
			{
				return _uri;
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
		new JvmCompilationMetrics().bindTo(_registry);

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
