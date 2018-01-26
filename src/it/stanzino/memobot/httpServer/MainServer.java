package it.stanzino.memobot.httpServer;

import java.io.File;
import java.io.IOException;

import org.json.*;

import it.stanzino.memobot.configurations.PropertiesManager;
import it.stanzino.memobot.functions.Timer;
import it.stanzino.memobot.functions.Util;
import it.stanzino.memobot.in_out.NamesLogger;
import it.stanzino.memobot.in_out.OutLogger;
import it.stanzino.memobot.parser.MessageExecuter;

public class MainServer
{
	private static String BOT_URL;
	private static String responseJSON = "", response = "";
	//private static String BOT_URL;
	private static long updateId = 0;
	public static long chatId = 0;
	private static OutLogger logger;
	private static NamesLogger namesLogger;
	private static Timer timer;
	public static boolean debugMode;
	
	public static void main(String args[]) throws Exception
	{
		init();
		
		JSONArray result;
		String[] msgText = new String[2];
		//BOT_URL = PropertiesManager.TELEGRAM_TEST_BOT_URL;
		BOT_URL = PropertiesManager.TELEGRAM_BOT_URL;
		namesLogger = new NamesLogger(PropertiesManager.RESOURCES_NAMES_PATH);
		logger = new OutLogger(PropertiesManager.RESOURCES_OUTPUT_PATH);
		timer = new Timer();
		timer.start();
		debugMode = true;
		//cicla sempre sulla prima get per aspettare update
		//quando arriva un comando chiama MessageExecuter che esegue chiamando la funzione giusta
		//manda POST getUpdates "offset" : updateId++ per pulire
		//torna sul primo ciclo
		
		
		logger.info("Bot Running... ");
		while(true)
		{
			try
			{
				//waits for update
				result = firstUpdate();
				
				//when update available, parses command
				msgText = parseMessage(result);
			
				//parse the last message
				MessageExecuter.executeMessage(msgText[0], msgText[1], chatId);
			}
			catch(Exception e)
			{
				continue;
			}
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
			if(conta >= PropertiesManager.SERVER_MAX_NOCONNECTION)
			{
				conta = 0;
				Thread.sleep(PropertiesManager.SERVER_TIMEOUT);
			}
			try
			{
				Thread.sleep(PropertiesManager.SERVER_UPDATE_FREQUENCY);
				response = HttpClientUtil.get(BOT_URL + "/getUpdates");
				
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
				e.printStackTrace();
				logger.warning("NO CONNECTION!\n");
				conta++;
			}	
		}
		while(msgQty <= 0);
		
		return result;
	}

	public static String[] parseMessage(JSONArray result) throws SecurityException, IOException
	{
		JSONObject message1, message2, chat, from;
		String firstName = "";
		String[] updateText = new String[2];
		updateText[0] = "/help";
		
		try
		{
			//iterates through the messages and gets the last
			for(int i=0; i<result.length(); i++)
			{
				message1 = result.getJSONObject(i);
				updateId = message1.getLong("update_id");
				message2 = message1.getJSONObject("message");
				chat = message2.getJSONObject("chat");
				updateText[0] = message2.getString("text");
				chatId = chat.getLong("id");
				from = message2.getJSONObject("from");
				updateText[1] = firstName = from.getString("first_name");
				
				//logs all the commands read		
				namesLogger.write(firstName + " " + updateText[0] + " " + Util.getDate());
				
				logger.info("New Message   : " + updateText[0] + " From : " + firstName + "\n");
			}
		}
		catch(Exception e)
		{
			//e.printStackTrace();
			logger.info("EXCEPTION in parseMessage : " + e.getMessage() + "\n");
		}
		
		return updateText;
	}

	private static void init()
	{
		//carica configurazioni
		PropertiesManager.loadProperties();
		
		//crea le cartelle
    	new File("out/importants").mkdirs();
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
		String converted = "";
		
		try
		{
			//convert message into utf-8
			converted = Util.convertToUtf(message);
			
			responseJSON = "{ \"text\" : \"" + converted + "\", \"chat_id\" : " + aChatId+ " }";
			response = HttpClientUtil.post(BOT_URL + "/sendMessage", responseJSON);
			logger.info("Response sent : " + converted + "\n");
			
		}
		catch(Exception e)
		{
			//e.printStackTrace();
			logger.warning("EXCEPTION in sendResponse : " + e.getMessage() + "\n");
			notifyDev(e + "\n" + e.getMessage() + "\n\nMessage:\n" + message);
		}	
		
	}

	//resets the offset to wait for NEW msg
	private static void syncUpdate() throws SecurityException, IOException
	{
		try
		{
			updateId++;
			responseJSON = "{ \"offset\" : " + updateId + " }";
			response = HttpClientUtil.post(BOT_URL + "/getUpdates", responseJSON);
			logger.info("Sync          : OK" + "\n");
			
		}
		catch(Exception e)
		{
			//e.printStackTrace();
			logger.info("EXCEPTION in syncUpdate : " + e.getMessage() + "\n");
			
		}
	}
	
	public static void logException(String msg)
	{
		try
		{
			logger.severe(msg);
			notifyDev(msg);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private static void notifyDev(String e) throws SecurityException, IOException
	{
		if(debugMode)
		{
			try
			{
				sendAsyncResponse("We have a problem!\n" + e, PropertiesManager.TELEGRAM_DEV_CHAT_ID);
			}
			catch(Exception ex)
			{
				logger.severe(ex.getMessage());
				ex.printStackTrace();
			}
		}		
	}
}