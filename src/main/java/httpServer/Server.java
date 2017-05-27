package httpServer;

import org.json.*;
import java.math.BigInteger;

public class Server
{
	public static void main(String args[])
	{
		String message = "ERROR", responseJSON;
		BigInteger chatId = BigInteger.valueOf(31149648);
		
		//QUANDO SI FERMA??
		while(true)
		{
			String response = HttpClientUtil.get
			(
					"https://api.telegram.org/bot381629683:AAG35c3Q1TMgxJ74TofHUkpHyyiqI9Swm58/getUpdates"			
		    );
			//QUANDO SI FERMA??
			try
			{
				//response				
				JSONObject obj = new JSONObject(response);
				JSONArray result = obj.getJSONArray("result");
				JSONObject message1 = result.getJSONObject(1);
				JSONObject message2 = message1.getJSONObject("message");
				JSONObject chat = message2.getJSONObject("chat");
				chatId = chat.getBigInteger("id");
				
				System.out.println(chatId);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				//continue;
			}	
		

			//thread start
			Thread waiter = new Waiter(10, "Timer Scaduto", chatId);
			waiter.start();
			message = "Timer Partito";
		
			System.out.println("getUpdates:\n" + response.toString());
			responseJSON = "";
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