package httpServer;

import org.json.*;

public class Server
{
	private static long chatId = 0;
	private static long updateId = 0;
	private static String responseJSON = "", response = "";
	
	public static void main(String args[]) throws InterruptedException
	{
		//cicla sempre sulla prima get per aspettare update
		//quando arriva fa partire il timer e manda l'ok
		//il timer manda fine timer
		//manda POST getUpdates "offset" : updateId++ per pulire
		//torna sul primo ciclo
		
		while(true)
		{
			mainLoop();
		}
	}
	
	private static void mainLoop() throws InterruptedException
	{
		//waits for an update
		firstUpdate();
		
		//starts waiter thread
		Thread waiter = new Waiter(5000, "Timer Scaduto", chatId);
		waiter.start();
		
		//writes message ok
		sendStarted();	
		
		//send POST getUpdates with updateId++ to sync
		syncUpdate();
		
	}

	private static void firstUpdate() throws InterruptedException
	{
		//waits for the first update (message length != 0)
		int msgQty = 0;
		
		do
		{
			Thread.sleep(1000);
			response = HttpClientUtil.get
			(
				"https://api.telegram.org/bot381629683:AAG35c3Q1TMgxJ74TofHUkpHyyiqI9Swm58/getUpdates"			
		    );
			
			//	parse response
			try
			{			
				JSONObject obj = new JSONObject(response);
				System.out.println("getUpdates:\n" + response.toString());
				JSONArray result = obj.getJSONArray("result");
				msgQty = result.length();
				//	iterates through the messages and gets the last
				for(int i=0; i<msgQty; i++)
				{
					JSONObject message1 = result.getJSONObject(i);
					updateId = message1.getLong("update_id");
					JSONObject message2 = message1.getJSONObject("message");
					JSONObject chat = message2.getJSONObject("chat");
					chatId = chat.getLong("id");
					
					System.out.println("messages:\n" + result.toString());
				}		
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}	
		}
		while(msgQty <= 0);
	}
	
	private static void sendStarted()
	{
		String message = "";
		try
		{
			message = "Timer Partito";
			responseJSON = "{ \"text\" : \"" + message + "\", \"chat_id\" : " + chatId+ " }";
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
		System.out.println("sendPartito:\n" + response.toString());
	}
	
	private static void syncUpdate()
	{
		try
		{
			updateId++;
			responseJSON = "{ \"offset\" : " + updateId + " }";
			response = HttpClientUtil.post
			(
					"https://api.telegram.org/bot381629683:AAG35c3Q1TMgxJ74TofHUkpHyyiqI9Swm58/getUpdates",
					responseJSON
		    );
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
			
		System.out.println("sendLastUpdate:\n" + response.toString());	
	}
}