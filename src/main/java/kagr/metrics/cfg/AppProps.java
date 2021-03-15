/****************************************************************************
 * FILE: CtrailProps.java
 * DSCRPT: 
 ****************************************************************************/





package kagr.metrics.cfg;




import java.io.FileNotFoundException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;



import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.lang3.StringUtils;



import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;





@Slf4j
public class AppProps
{
        private static final long serialVersionUID = 1306480900200475926L;

	private static final String CFG_XML = "appname.xml";
	private static final String APP_SYSPROP_NAME = "APP_CFG";
	private static final boolean FAIL_ON_MISSING_VAL = false;

	@Getter @Setter private int _someIntVal = 2;
	@Getter @Setter private boolean _someBoolVal = false;


	public static class AppPropsHelper
	{
		public static final AppProps _instance = new AppProps();
	}





	public static AppProps getInstance()
	{
		return AppPropsHelper._instance;
	}





	public AppProps()
	{
		String propsFileName = getConfigFile();
		Parameters params = new Parameters();
		_logger.debug("config file: {}", propsFileName);
		FileBasedConfigurationBuilder<XMLConfiguration> builder = new FileBasedConfigurationBuilder<XMLConfiguration>(XMLConfiguration.class).configure(params.xml()
				.setThrowExceptionOnMissing(FAIL_ON_MISSING_VAL)
				.setEncoding("UTF-8")
				.setListDelimiterHandler(new DefaultListDelimiterHandler(';'))
				.setValidating(false)
				.setFileName(propsFileName));
		try
		{

			XMLConfiguration config = builder.getConfiguration();
			setSomeIntVal(config.getInt("sublevel.intval", _someIntVal));
			setSomeBoolVal(config.getBoolean("topLevelBoolean", _someBoolVal));
		}
		catch (Exception ex_)
		{
			_logger.error(ex_.toString());
		}
	}





	/**
	 * this is used to extract the count of embedded keys. For example:
	 * <SomeKey>
	 * 	<KeyName></KeyName>
	 * 	<KeyName></KeyName>
	 * </SomeKey>
	 * 
	 * extractCount(cnt_, "SomeKey.KeyName") == 2
	 */
	private int extractCount(XMLConfiguration config_, String key_)
	{
		if (config_.getProperty(key_) != null)
		{
			try
			{
				return ((Collection<?>) config_.getProperty(key_)).size();
			}
			catch (ClassCastException ex_)
			{
				return 1;
			}
		}
		return 0;

	}




  public static final String decrypt(final XMLConfiguration cfg_, final String key_)
    {
        String encryptedString = cfg_.getString(key_);
        if (StringUtils.isEmpty(encryptedString))
        {
            _logger.error("not found, key:{}", key_);
            return null;
        }

        BasicTextEncryptor encryptor = new BasicTextEncryptor();
        encryptor.setPassword(Long.toString(AppProps.serialVersionUID));
        return encryptor.decrypt(encryptedString);
    }






	private static String getConfigFile()
	{
		Path cfgFilePath;
		String cfgOverride = System.getProperty(APP_SYSPROP_NAME);
		if (!StringUtils.isEmpty(cfgOverride))
		{
			cfgFilePath = Paths.get(cfgOverride);
			if (Files.exists(cfgFilePath))
			{
				return cfgFilePath.toString();
			}
			else
			{
			  throw new RuntimeException(APP_SYSPROP_NAME + " specified, but file not found:" + cfgFilePath);
			}
		}

		cfgFilePath = Paths.get(".", CFG_XML);
		if (Files.exists(cfgFilePath))
		{
			return cfgFilePath.toString();
		}


		cfgFilePath = Paths.get("/etc", CFG_XML);
		if (Files.exists(cfgFilePath))
		{
			return cfgFilePath.toString();
		}


		URL url = AppProps.class.getClassLoader().getResource(CFG_XML);
		if ( url!=null )
		{
			return url.toExternalForm();
		}

		return "";
	}
}
