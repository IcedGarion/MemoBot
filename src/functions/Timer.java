package functions;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.List;

import httpServer.Server;

/**
 * Reads forever from the file, waiting the time to notify
 */
public class Timer extends Thread
{	
	private final static int CHECK_TIME = 1000;  //1 sec sleep
	private Calendar calendar;
	private File file;
	
	public Timer() throws FileNotFoundException
	{
		file = new File(Server.TIMES_PATH);
	}
	
	@Override
	public void run()
	{
		String message = "";
		String[] tmp;
		long millisec, chatId, current;
		int index = 0;
		List<String> lines; 
		int removeIndex = -1;
			
		while(true)
		{
			try
			{
				//waits
				sleep(CHECK_TIME);
			
				//reads the file's lines
				lines = Files.readAllLines(file.toPath());
				
				index = 0;
				//check time for every line
				for(String line : lines)
				{
					tmp = line.split(",");
					millisec = Long.parseLong(tmp[0]);
					message = tmp[1];
					chatId = Long.parseLong(tmp[2]);
					calendar = Calendar.getInstance();
					current = calendar.getTimeInMillis();
					
					//checks the times: if current time >= timer set, "remove" line and notify
					if(current >= millisec)
					{
						removeIndex = index;
						Server.sendAsyncResponse(message, chatId);
						break;
					}
					
					index++;
				}
				
				if(removeIndex != -1)
				{
					lines.remove(removeIndex);
					Files.write(file.toPath(), lines);
					removeIndex = -1;
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				continue;
			}
		}
	}
}
