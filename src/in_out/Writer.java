package in_out;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import httpServer.Server;

public class Writer extends Thread
{
	private Logger OUT_LOGGER;
	private FileHandler outHandler;
	private SimpleFormatter formatter;
	private String msg;
	private String lvl;
	private String mode;
	private PrintWriter writer;
	private File file;
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
	public Writer(String outPath, String mode, Logger logger, int lineNumber)
	{
		this.mode = mode;
		try
		{
			if(mode.equals("write"))
				writer = new PrintWriter(new BufferedWriter(new FileWriter(outPath, true)));
			else if(mode.equals("log"))
			{
				this.OUT_LOGGER = logger;
		    	this.outHandler = new FileHandler(outPath);  
		    	OUT_LOGGER.addHandler(outHandler);
		    	formatter = new SimpleFormatter();  
		        outHandler.setFormatter(formatter);  
			}
			else if(mode.equals("overwrite"))
			{
				file = new File(outPath);
				this.removeIndex = lineNumber;
			}	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
    
	public void info(String msg) throws SecurityException, IOException
	{
		this.msg = msg;
		this.lvl = "info";
		this.start();
	}
	
	public void warning(String msg) throws SecurityException, IOException
	{
		this.msg = msg;
		this.lvl = "warning";
		this.start();
	}
	
	public void severe(String msg) throws SecurityException, IOException
	{
		this.msg = msg;
		this.lvl = "severe";
		this.start();
	}
	
	public void write(String msg)
	{
		this.msg = msg;
		this.start();
	}
	
	@Override
	public synchronized void run()
	{
		switch(mode)
		{
		case "log":
		{
			switch(lvl)
			{
				case "info":
					OUT_LOGGER.info(msg);  
					break;
				case "warning":
					OUT_LOGGER.warning(msg);  
					break;
				case "severe":
					OUT_LOGGER.severe(msg);  
					break;
				default:
					OUT_LOGGER.info(msg);  
					break;
			}
			break;
		}
		case "write":
		{
			writer.println(msg);
			writer.flush();
		}
		break;
		case "overwrite":
		{
			try 
			{
				List<String> lines = Files.readAllLines(file.toPath());
				lines.remove(removeIndex);
				Files.write(file.toPath(), lines);
			} 
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		break;
		}
	}
}
