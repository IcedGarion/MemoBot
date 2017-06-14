package httpServer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.logging.Logger;

import org.json.*;
import functions.Util;
import parser.MessageExecuter;

public class Server
{
	private static String responseJSON = "", response = "";
	private final static int UPDATE_FREQUENCY = 500;
	private static long updateId = 0;
	private static long chatId = 0;
	private static PrintWriter writer;
	private static final Logger LOGGER = Logger.getLogger( Server.class.getName() );
	
	public static void main(String args[]) throws Exception
	{
		JSONArray result;
		String msgText;
		writer = new PrintWriter(new BufferedWriter(new FileWriter("./log", true)));
		//cicla sempre sulla prima get per aspettare update
		//quando arriva un comando chiama MessageExecuter che esegue chiamando la funzione giusta
		//manda POST getUpdates "offset" : updateId++ per pulire
		//torna sul primo ciclo
		
		while(true)
		{
			result = firstUpdate();
			msgText = parseMessage(result);
			
			//parse the last message
			MessageExecuter.executeMessage(msgText, chatId);
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
				result = obj.getJSONArray("result");
				msgQty = result.length();	
				
				if(msgQty == 0)
					LOGGER.info("There are no new messages");
			}
			catch(Exception e)
			{
				e.printStackTrace();
				LOGGER.info("EXCEPTION in getUpdates : " + e.getMessage());
			}	
		}
		while(msgQty <= 0);
		
		return result;
	}

	public static String parseMessage(JSONArray result)
	{
		JSONObject message1, message2, chat, from;
		String updateText = "/help", firstName = "";
		
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
				from = message2.getJSONObject("from");
				firstName = from.getString("first_name");
				
				//anyway, logs all the commands read
				writer.println(firstName + " " + updateText + " " + Util.getDate());
				writer.flush();
				
				LOGGER.info("New Message   : " + updateText + "From : " + firstName);
			}
		}
		catch(Exception e)
		{
			//e.printStackTrace();
			LOGGER.info("EXCEPTION in parseMessage : " + e.getMessage());
		}
		
		return updateText;
	}

	public static void sendResponse(String message)
	{
		send(message, chatId);
		
		syncUpdate();
		
		return;
	}
	
	public static void sendAsyncResponse(String message, long anotherChatId)
	{
		send(message, chatId);
		
		return;
	}
	
	private static void send(String message, long aChatId)
	{
		String responseJSON = "";
		try
		{
			responseJSON = "{ \"text\" : \"" + message + "\", \"chat_id\" : " + aChatId+ " }";
			response = HttpClientUtil.post
			(
					"https://api.telegram.org/bot381629683:AAG35c3Q1TMgxJ74TofHUkpHyyiqI9Swm58/sendMessage",
					responseJSON
		    );
		}
		catch(Exception e)
		{
			//e.printStackTrace();
			LOGGER.warning("EXCEPTION in sendResponse : " + e.getMessage());
		}	
		
		LOGGER.info("Response sent : " + responseJSON.toString());
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
			
			LOGGER.info("Sync          : OK");
		}
		catch(Exception e)
		{
			//e.printStackTrace();
			LOGGER.info("EXCEPTION in syncUpdate : " + e.getMessage());
		}
	}
}