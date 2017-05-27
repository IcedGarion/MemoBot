package httpServer;

import java.math.BigInteger;
import java.util.Map;

public class Waiter extends Thread
{
	private int millisecs;
	private String msg;
	private BigInteger chatId;
	
	public Waiter(int millisecs, String message, BigInteger chatId)
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
			sleep(millisecs);
		}
		catch(Exception e)
		{
			msg = "ERRORE";
		}
		finally
		{
			String responseJSON = "{ \"text\" : " + msg + ", \"chat_id\" : " + chatId + " }";
			//chiama evento fineTimeout
			String response = null;
			try
			{
				response = HttpClientUtil.post
				(
						"https://api.telegram.org/bot381629683:AAG35c3Q1TMgxJ74TofHUkpHyyiqI9Swm58/sendMessage",
						responseJSON
				);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
					
			System.out.println("sendTimer:\n" + response.toString());
		}
		
	}
}
