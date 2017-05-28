package httpServer;

import org.json.*;

public class Server
{
	private static long chatId = 0;
	private static long updateId = 0;
	private static String responseJSON = "", response = "";
	private final static int UPDATE_FREQUENCY = 1000;
	
	private static final String COMMANDS_MESSAGE = "Uso:\n'!timer <x_secondi> <messaggio>' : aspetta per x_secondi e scrive il messaggio\n" 
			+ "'!help' : scrive questo messaggio";
	//private static final String HELLO_MESSAGE = "Ciao! Questo è un Bot semplice per ricordare appuntamenti.\n" + COMMANDS_MESSAGE;
	private static final String ERROR_MESSAGE = "Comando non riconosicuto.\n" + COMMANDS_MESSAGE;
	
	
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
		String updateText;
		String response;
		
		//waits for an update
		updateText = firstUpdate();
		
		//read the (last) message received and executes command (timer, for now)
		response = parseMessage(updateText);
		
		//writes response elaborated
		sendResponse(response);	
		
		//send POST getUpdates with updateId++ to sync
		syncUpdate();
		
	}

	private static String firstUpdate() throws InterruptedException
	{
		//waits for the first update (message length != 0)
		int msgQty = 0;
		String updateText = "";
		
		do
		{
			Thread.sleep(UPDATE_FREQUENCY);
			response = HttpClientUtil.get
			(
				"https://api.telegram.org/bot381629683:AAG35c3Q1TMgxJ74TofHUkpHyyiqI9Swm58/getUpdates"			
		    );
			
			//parse response JSON
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
					updateText = message2.getString("text");
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
		
		return updateText;
	}
	
	//PARSING RESPONSE TEXT : USE JFLEX MAYBE?
	//TEMPORARY
	private static String parseMessage(String updateText)
	{
		String response = ERROR_MESSAGE;
		String[] tmp;
		int millisec = 1;
		String message = ERROR_MESSAGE;
		
		if(updateText == null || updateText == "")
			response = (ERROR_MESSAGE);
		else
		{
			try
			{
				tmp = updateText.split(" ");
				
				switch(tmp[0].toLowerCase())
				{
					case "!timer":
						millisec = Integer.parseInt(tmp[1]);
						message = tmp[2];
						startTimer(millisec, message);
						response = "Timer di " + millisec + " secondi avviato";
						break;
					case "!help":
					case "help":
						response = COMMANDS_MESSAGE;
						break;
					default:
						response = (ERROR_MESSAGE);
						break;
				}
			}
			catch(Exception e)
			{
				response = (ERROR_MESSAGE);
			}
		}
		return response;
	}
	
	private static void startTimer(int millisec, String message)
	{
		//starts waiter thread
		Thread waiter = new Waiter(millisec * 1000, message, chatId);
		waiter.start();
		
		return;
	}
	
	private static void sendResponse(String message)
	{
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