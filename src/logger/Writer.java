package logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Writer extends Thread
{
	private Logger OUT_LOGGER;
	private FileHandler outHandler;
	private SimpleFormatter formatter;
	private String msg;
	private String lvl;
	private boolean log;
	private PrintWriter writer;
	
	public Writer(String outPath) throws IOException
	{
		this.log = false;
		writer = new PrintWriter(new BufferedWriter(new FileWriter(outPath, true)));
	}
	
    public Writer(Logger logger, String outPath) throws SecurityException, IOException
    {
    	this.log = true;
    	this.OUT_LOGGER = logger;
    	this.outHandler = new FileHandler(outPath);  
    	OUT_LOGGER.addHandler(outHandler);
    	formatter = new SimpleFormatter();  
        outHandler.setFormatter(formatter);  
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
	public synchronized void start()
	{
		if(log)
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
		}
		else
		{
			writer.println(msg);
			writer.flush();
		}
	}
}
