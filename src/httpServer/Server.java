package httpServer;

import org.json.*;

public class Server
{
	//cicla sempre sulla prima get per aspettare update
	//quando arriva fa partire il timer e manda l'ok
	//il timer manda fine timer
	//manda POST getUpdates "offset" : updateId++ per pulire
	//torna sul primo ciclo
	
	public static void main(String args[]) throws InterruptedException
	{
		String message = "ERROR", responseJSON, firstUpdate, response2 = "";
		long chatId = 0, updateId = 0;
		int msgQty = 0;
		
		//waits for the first update (message length != 0)
		do
		{
			Thread.sleep(1000);
			firstUpdate = HttpClientUtil.get
			(
				"https://api.telegram.org/bot381629683:AAG35c3Q1TMgxJ74TofHUkpHyyiqI9Swm58/getUpdates"			
			);
			
			//parse response
			try
			{			
				JSONObject obj = new JSONObject(firstUpdate);
				System.out.println("getUpdates:\n" + firstUpdate.toString());
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
		
		//starts waiter thread
		Thread waiter = new Waiter(5000, "Timer Scaduto", chatId);
		waiter.start();
		
		//writes message ok
		try
		{
			message = "Timer Partito";
			responseJSON = "{ \"text\" : \"" + message + "\", \"chat_id\" : " + chatId+ " }";
			response2 = HttpClientUtil.post
			(
					"https://api.telegram.org/bot381629683:AAG35c3Q1TMgxJ74TofHUkpHyyiqI9Swm58/sendMessage",
					responseJSON
		    );
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}	
		System.out.println("sendPartito:\n" + response2.toString());	
		
		//send POST getUpdates with updateId++ to sync
		try
		{
			updateId++;
			responseJSON = "{ \"offset\" : " + updateId + " }";
			response2 = HttpClientUtil.post
			(
					"https://api.telegram.org/bot381629683:AAG35c3Q1TMgxJ74TofHUkpHyyiqI9Swm58/getUpdates",
					responseJSON
		    );
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
			
		System.out.println("sendLastUpdate:\n" + response2.toString());	
		}	
	}