package functions;

import java.io.IOException;
import java.util.logging.Logger;

import httpServer.Server;
import logger.Writer;

public class Timer extends Thread
{
	private int millisecs;
	private String msg;
	private long chatId;
	private static Writer logger;	
	
	public Timer(int millisecs, String message, long chatId) throws SecurityException, IOException
	{
		logger = new Writer(Logger.getLogger(Timer.class.getName()), Server.OUTPUT_PATH);
		this.millisecs = millisecs;
		this.msg = message;
		this.chatId = chatId;
	}
	
	@Override
	public void run()
	{
		try
		{
			//waits
			sleep(millisecs);
		}
		catch(Exception e)
		{
			msg = "ERRORE";
			try {
				logger.severe(e.getMessage() + "\n");
				logger.start();
			} catch (SecurityException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		finally
		{
			try {
				Server.sendAsyncResponse(msg, chatId);
			} catch (SecurityException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	}
}
