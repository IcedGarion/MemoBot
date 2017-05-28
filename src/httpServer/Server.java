package httpServer;

import java.util.ArrayList;
import org.json.*;

public class Server
{
	private static String responseJSON = "", response = "";
	private final static int UPDATE_FREQUENCY = 1000;
	private static long updateId = 0;
	
	
	
	public static void main(String args[]) throws InterruptedException
	{
		//cicla sempre sulla prima get per aspettare update
		//quando arriva fa partire il timer e manda l'ok
		//il timer manda fine timer
		//manda POST getUpdates "offset" : updateId++ per pulire
		//torna sul primo ciclo
		
		while(true)
		{
			firstUpdate();
		}
	}

	private static String firstUpdate() throws InterruptedException
	{
		//waits for the first update (message length != 0)
		int msgQty = 0;
		String updateText = "";
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
		
		try
		{
			JSONObject message1, message2, chat;
			long chatId = 0;
			ArrayList<Thread> commandExecuter = new ArrayList<Thread>();

			//iterates through the messages and gets the last
			for(int i=0; i<msgQty; i++)
			{
				message1 = result.getJSONObject(i);
				updateId = message1.getLong("update_id");
				message2 = message1.getJSONObject("message");
				chat = message2.getJSONObject("chat");
				updateText = message2.getString("text");
				chatId = chat.getLong("id");
				
				//start a thread for every message
				commandExecuter.add(new MessageExecuter(updateText, chatId));
				commandExecuter.get(i).start();
				
				System.out.println("messages:\n" + result.toString());
			}
			
			//waits for all the messages in the chat to be executed
			for(Thread m : commandExecuter)
				m.join();
			
			//send POST getUpdates with updateId++ to sync
			syncUpdate();
		}
		catch(Exception e)
		{
			e.printStackTrace();	
		}
			
		return updateText;
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