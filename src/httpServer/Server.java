package httpServer;

import org.json.*;

public class Server
{
	//una prima get per trovare updateId;
	//un ciclo di post (getUpdates) con il updateId++, e solo se il JSON è pieno, fai cose
	
	
	
	public static void main(String args[])
	{
		String message = "ERROR", responseJSON, updateJSON;
		long chatId = 0, updateId = 0;
		
		//QUANDO SI FERMA??
		while(true)
		{
			updateJSON = "";
			//get updates
			String response = HttpClientUtil.get
			(
					"https://api.telegram.org/bot381629683:AAG35c3Q1TMgxJ74TofHUkpHyyiqI9Swm58/getUpdates"			
		    );
			//QUANDO SI FERMA??
			
			//parse response
			try
			{			
				JSONObject obj = new JSONObject(response);
				JSONArray result = obj.getJSONArray("result");
				JSONObject message1 = result.getJSONObject(0);
				updateId = message1.getLong("update_id");
				JSONObject message2 = message1.getJSONObject("message");
				JSONObject chat = message2.getJSONObject("chat");
				chatId = chat.getLong("id");
				
				System.out.println("getUpdates:\n" + response.toString());
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}	
		

			//starts waiter thread
			Thread waiter = new Waiter(10000, "Timer Scaduto", chatId);
			waiter.start();

			//writes message ok
			message = "Timer Partito";
			responseJSON = "{ \"text\" : \"" + message + "\", \"chat_id\" : " + chatId+ " }";
			String response2 = "";
			try
			{
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
				
			System.out.println("sendOK:\n" + response2.toString());	
		}
	}
}