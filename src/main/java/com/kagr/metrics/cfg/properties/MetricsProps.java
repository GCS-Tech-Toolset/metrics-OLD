package com.kagr.metrics.cfg.properties;





import io.micrometer.core.instrument.Tags;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.XMLConfiguration;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.kagr.metrics.cfg.MetricsUtils.buildMetricsKey;





@Slf4j
@Data
public abstract class MetricsProps
{
    protected boolean	_enabled;
    protected int	_reportingFrequencyInSeconds;
    protected String _hostname;
    protected String _env;
    protected String _appName;
    protected String _registry;
    protected Tags _commonTags;





    public abstract void loadFromConfig(XMLConfiguration xmlConfiguration_);





    public void loadBasePropsFromConfig(XMLConfiguration xmlConfiguration_)
    {
        _reportingFrequencyInSeconds = xmlConfiguration_.getInt(buildMetricsKey("ReportingFrequencyInSeconds"), 5);
        _enabled = xmlConfiguration_.getBoolean(buildMetricsKey("Enabled"), true);
        _appName = xmlConfiguration_.getString("AppName", "Unknown");
        _env = xmlConfiguration_.getString("Environment", "localhost");

        try
        {
            _hostname = InetAddress.getLocalHost().getHostName();
            _commonTags = Tags.of("hostname", _hostname, "env", _env, "service", _appName);
        }
        catch (UnknownHostException ex_)
        {
            _logger.error(ex_.toString(), ex_);
        }

    }

}
