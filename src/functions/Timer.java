package functions;

import java.util.logging.Logger;

import httpServer.Server;

public class Timer extends Thread
{
	private int millisecs;
	private String msg;
	private long chatId;
	private static final Logger LOGGER = Logger.getLogger( Timer.class.getName() );
	
	
	public Timer(int millisecs, String message, long chatId)
	{
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
			LOGGER.severe(e.getMessage());
		}
		finally
		{
			Server.sendAsyncResponse(msg, chatId);
		}	
	}
}
