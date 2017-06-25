package it.stanzino.memobot.in_out;


import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class OutLogger extends Thread
{
	private Logger OUT_LOGGER;
	private FileHandler outHandler;
	private SimpleFormatter formatter;
	int removeIndex;
	
	/**
	 * Writes files in multiple modes: writer, logger and overwrite a line
	 * Create in different modes; call info, severe... or write, and then run() 
	 * 
	 * @param outPath	
	 * @param mode		"write", "log", "overwrite"
	 * @param logger	Logger l if mode = "log"; null otherwise
	 * @param lineNumber the file's line to remove, if mode = "overwrite"; -1 otherwise
	 */
	public OutLogger(String outPath)
	{
		try
		{
			this.OUT_LOGGER = Logger.getLogger(OutLogger.class.getName());
		   	this.outHandler = new FileHandler(outPath);  
		   	OUT_LOGGER.addHandler(outHandler);
		   	formatter = new SimpleFormatter();  
		    outHandler.setFormatter(formatter);  
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
    
	public void info(String msg) throws SecurityException, IOException
	{
		OUT_LOGGER.info(msg); 
	}
	
	public void warning(String msg) throws SecurityException, IOException
	{
		OUT_LOGGER.warning(msg); 
	}
	
	public void severe(String msg) throws SecurityException, IOException
	{
		OUT_LOGGER.severe(msg); 
	}
}
