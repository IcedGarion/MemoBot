package httpServer;

import org.json.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Map;

public class Server
{
	public static void main(String args[])
	{
		String message = "ERROR", responseJSON;
		BigInteger chatId = BigInteger.valueOf(31149648);
		
		
			String response = HttpClientUtil.get
			(
					"https://api.telegram.org/bot381629683:AAG35c3Q1TMgxJ74TofHUkpHyyiqI9Swm58/getUpdates"			
		    );
		
			try
			{
				//retrieve Text
				
				
				JSONObject obj = new JSONObject(response);
				JSONArray result = obj.getJSONArray("result");
				JSONObject message1 = result.getJSONObject(1);
				JSONObject message2 = message1.getJSONObject("message");
				JSONObject chat = message2.getJSONObject("chat");
				chatId = chat.getBigInteger("id");
				
				
				
				
				/*
				ArrayList<Map<String, ?>> result = (ArrayList<Map<String, ?>>) response.get("result");
				Map<String, ?> msg = result.get(1);
				Map<String, ?> msg2 = (Map<String, ?>) msg.get("message");
				Map<String, ?> chat = (Map<String, ?>) msg2.get("chat");
				chatId = (byte) chat.get("id");
				*/
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
			message = "Timer Started";
		
			System.out.println("getUpdates:\n" + response.toString());
			responseJSON = "{ \"text\" : " + message + ", \"chat_id\" : " + chatId+ " }";
			//responseJSON = "{ \"text\" : \"lol\", \"chat_id\" : 31149648 }";
			
			//send "timer started"
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