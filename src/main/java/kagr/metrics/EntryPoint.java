/****************************************************************************
 * FILE: TxFileParserEntryPoint.java
 * DSCRPT: 
 ****************************************************************************/





package kagr.metrics;





import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;



import kagr.metrics.cfg.AppProps;



import lombok.extern.slf4j.Slf4j;





@Slf4j
public class EntryPoint
{
	private AppProps _props;





	public EntryPoint()
	{
	}





	public void initCli(String[] args_)
	{
		try
		{
			CommandLineParser parser = new DefaultParser();
			Options options = new Options();
			options.addOption(Option.builder("h").longOpt("help").desc("prints help and exits").build());
			options.addOption(Option.builder("d").longOpt("dumpcfg").desc("dump config to log file and exit").build());


			CommandLine line = parser.parse(options, args_);
			if (line.hasOption("h"))
			{
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp(getClass().getSimpleName(), options);
				System.exit(0);
			}
			
			
		}
		catch (ParseException ex_)
		{
			_logger.error(ex_.toString());
		}

	}





	public static void main(String[] args_)
	{
		EntryPoint ep = new EntryPoint();
		ep.initCli(args_);
	}

}
