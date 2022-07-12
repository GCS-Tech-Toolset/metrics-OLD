package com.kagr.metrics.cfg;





import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.jasypt.util.text.BasicTextEncryptor;





@Slf4j
public class MetricsUtils
{
    private static final long serialVersionUID = 1741838256855050257L;





    public static String buildMetricsKey(@NonNull String key_)
    {
        String key = String.format("AppMetrics.%s", key_);
        if (_logger.isTraceEnabled())
        {
            _logger.trace("key:{}", key);
        }
        return key;
    }





    public static String decryptValue(final XMLConfiguration cfg_, final String key_, String default_)
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

}
