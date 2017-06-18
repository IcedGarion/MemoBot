package httpServer;

import java.io.IOException;

import java.util.logging.Logger;


import org.json.*;

import functions.Timer;
import functions.Util;
import logger.Writer;
import parser.MessageExecuter;

public class Server
{
	private static final int TIMEOUT = 30000; 				//30 sec di timeout se rimane senza connessione per troppo
	private static final int MAX_NOCONNECTION = 15;			//max 15 richieste senza connessione e aspetta		
	public static String OUTPUT_PATH = "./out/log";
	private static String NAMES_PATH = "./out/commands";
	public static String TIMES_PATH = "./out/times";
	private static String responseJSON = "", response = "";
	private final static int UPDATE_FREQUENCY = 500;
	private static long updateId = 0;
	private static long chatId = 0;
	private static Writer logger;
	private static Writer namesLogger;
	private static Timer timer;
	
	public static void MemoBot() throws Exception
	{
		JSONArray result;
		String msgText;
		logger = new Writer(Logger.getLogger(Server.class.getName()), OUTPUT_PATH);
		namesLogger = new Writer(NAMES_PATH);
		timer = new Timer();
		timer.start();
		//cicla sempre sulla prima get per aspettare update
		//quando arriva un comando chiama MessageExecuter che esegue chiamando la funzione giusta
		//manda POST getUpdates "offset" : updateId++ per pulire
		//torna sul primo ciclo
		
		logger.info("Bot Running... ");
		while(true)
		{
			result = firstUpdate();
			msgText = parseMessage(result);
			
			//parse the last message
			MessageExecuter.executeMessage(msgText, chatId);
		}
	}
	
	public static JSONArray firstUpdate() throws SecurityException, IOException, InterruptedException
	{
		//waits for the first update (message length != 0)
		int msgQty = 0, conta = 0;
		boolean first = true;
		JSONObject obj;
		JSONArray result = null;
		
		do
		{
			if(conta >= MAX_NOCONNECTION)
			{
				conta = 0;
				Thread.sleep(TIMEOUT);
			}
			try
			{
				Thread.sleep(UPDATE_FREQUENCY);
				response = HttpClientUtil.get
				(
					"https://api.telegram.org/bot381629683:AAG35c3Q1TMgxJ74TofHUkpHyyiqI9Swm58/getUpdates"			
				);
			
				//parse response JSON to get number of messages pendings
				obj = new JSONObject(response);
				result = obj.getJSONArray("result");
				msgQty = result.length();	
				
				if(first)
				{
					first = false;
					logger.info("Bot Can Get Updates...");
				}
			}
			catch(Exception e)
			{
				//e.printStackTrace();
				logger.warning("NO CONNECTION!\n");
				conta++;
			}	
		}
		while(msgQty <= 0);
		
		return result;
	}

	public static String parseMessage(JSONArray result) throws SecurityException, IOException
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
				namesLogger.write(firstName + " " + updateText + " " + Util.getDate());
				logger.info("New Message   : " + updateText + "From : " + firstName + "\n");
				
			}
		}
		catch(Exception e)
		{
			//e.printStackTrace();
			logger.info("EXCEPTION in parseMessage : " + e.getMessage() + "\n");
			
		}
		
		return updateText;
	}

	//for immediate response
	public static void sendResponse(String message) throws SecurityException, IOException
	{
		send(message, chatId);
		
		syncUpdate();
		
		return;
	}
	
	//for timer
	public static void sendAsyncResponse(String message, long anotherChatId) throws SecurityException, IOException
	{
		send(message, anotherChatId);
		
		return;
	}
	
	private static void send(String message, long aChatId) throws SecurityException, IOException
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
			logger.warning("EXCEPTION in sendResponse : " + e.getMessage() + "\n");
			
		}	
		
		logger.info("Response sent : " + message + "\n");
		
	}
	
	//resets the offset to wait for NEW msg
	private static void syncUpdate() throws SecurityException, IOException
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
			
			logger.info("Sync          : OK" + "\n");
			
		}
		catch(Exception e)
		{
			//e.printStackTrace();
			logger.info("EXCEPTION in syncUpdate : " + e.getMessage() + "\n");
			
		}
	}
}