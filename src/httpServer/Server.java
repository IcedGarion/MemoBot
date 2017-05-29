package httpServer;

import org.json.*;

import parser.MessageExecuter;

public class Server
{
	private static String responseJSON = "", response = "";
	private final static int UPDATE_FREQUENCY = 1000;
	private static long updateId = 0;
	private static long chatId = 0;
	
	
	public static void main(String args[]) throws InterruptedException
	{
		JSONArray result;
		String msgText;
		//cicla sempre sulla prima get per aspettare update
		//quando arriva fa partire il timer e manda l'ok
		//il timer manda fine timer
		//manda POST getUpdates "offset" : updateId++ per pulire
		//torna sul primo ciclo
		
		while(true)
		{
			result = firstUpdate();
			msgText = parseMessage(result);
			
			//parse the last message
			MessageExecuter e = new MessageExecuter(msgText, chatId, updateId);
			e.executeMessage(msgText);
		}
	}
	
	public static JSONArray firstUpdate() throws InterruptedException
	{
		//waits for the first update (message length != 0)
		int msgQty = 0;
		JSONObject obj;
		JSONArray result = null;
		
		do
		{
			Thread.sleep(UPDATE_FREQUENCY);
			response = HttpClientUtil.get
			(
				"https://api.telegram.org/bot381629683:AAG35c3Q1TMgxJ74TofHUkpHyyiqI9Swm58/getUpdates"			
		    );
			
			//parse response JSON to get number of messages pendings
			try
			{			
				obj = new JSONObject(response);
				System.out.println("getUpdates:\n" + response.toString());
				result = obj.getJSONArray("result");
				msgQty = result.length();		
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}	
		}
		while(msgQty <= 0);
		
		return result;
	}

	public static String parseMessage(JSONArray result)
	{
		JSONObject message1, message2, chat;
		String updateText = "/help";
		
		try
		{
			//iterates through the messages and gets the last
			for(int i=0; i<result.length(); i++)
			{
				message1 = result.getJSONObject(i);
				updateId = message1.getLong("update_id");
				message2 = message1.getJSONObject("message");
				chat = message2.getJSONObject("chat");
				updateText = message2.getString("text");
				chatId = chat.getLong("id");
				
				System.out.println("messages:\n" + result.toString());
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();	
		}
		
		return updateText;
	}
	
	public static void sendResponse(String message)
	{
		String responseJSON, response = "";
		try
		{
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
		
		syncUpdate();
		
		return;
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