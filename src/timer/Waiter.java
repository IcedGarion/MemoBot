package timer;

import httpServer.HttpClientUtil;

public class Waiter extends Thread
{
	private int millisecs;
	private String msg;
	private long chatId;
	
	public Waiter(int millisecs, String message, long chatId)
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
		}
		finally
		{
			//writes message
			String responseJSON = "{ \"text\" : \"" + msg + "\", \"chat_id\" : " + chatId+ " }";
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
			
					
			System.out.println("sendScaduto:\n" + response.toString());
		}
		
	}
}
