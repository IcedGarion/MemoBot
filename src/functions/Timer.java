package functions;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.logging.Logger;

import httpServer.Server;
import logger.Writer;

/**
 * Reads forever from the file, waiting the time to notify
 */
public class Timer extends Thread
{	
	private final static int CHECK_TIME = 1000;  //1 sec sleep
	private final static int DELTA_PRECISION = 5000;  //5 sec oscillazione
	private Writer writer;
	private BufferedReader reader;
	private Calendar calendar;
	
	public Timer() throws FileNotFoundException
	{
		writer = new Writer(Server.TIMES_PATH);
		reader = new BufferedReader(new FileReader(Server.TIMES_PATH));
		calendar = Calendar.getInstance();
	}
	
	@Override
	public void run()
	{
		String line, message = "", chatId = "", file = "";
		String[] tmp;
		long millisec;
		
		while(true)
		{
			try
			{
				//waits
				sleep(CHECK_TIME);
			
				//reads the file
				while((line = reader.readLine()) != null)
				{
					tmp = line.split(",");
					millisec = Long.parseLong(tmp[0]);
					message = tmp[1];
					chatId = tmp[2];
					
					//checks the times: if current time >= timer set, "remove" line and notify
					if(calendar.getTimeInMillis() >= millisec)
					{
						//marks the line "done"
						...vediProva
						
					}
				}
				
			}
			catch(Exception e)
			{
				continue;
			}
		}
	}
}
