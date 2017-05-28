package httpServer;

import org.json.JSONArray;

public class MessageExecuter
{
	private static final String COMMANDS_MESSAGE = "Uso:\n'/timer <x_secondi> <messaggio>' : aspetta per x_secondi e scrive il messaggio\n" 
			+ "'/help' : scrive questo messaggio";
	private static final String HELLO_MESSAGE = "Ciao! Questo è un Bot semplice per ricordare appuntamenti.\n" + COMMANDS_MESSAGE;
	private static final String ERROR_MESSAGE = "Comando non riconosicuto.\n" + COMMANDS_MESSAGE;
	private String updateText;
	private static long chatId;
	private static long updateId;
	
	public MessageExecuter(String updateText, long chatId, long updateId)
	{
		this.updateText = updateText;
		this.chatId = chatId;
		this.updateId = updateId;
	}
	
	//PARSING RESPONSE TEXT : USE JFLEX MAYBE?
		//TEMPORARY
		public void executeMessage(String updateText)
		{
			String[] tmp;
			int millisec = 1;
			String message = ERROR_MESSAGE;
			
				try
				{
					tmp = updateText.split(" ");
					int length = tmp.length;
					switch(tmp[0].toLowerCase())
					{
						case "/start":
							Server.sendResponse(HELLO_MESSAGE);
							break;
						case "/timer":
							if(length == 3)
							{
								millisec = Integer.parseInt(tmp[1]);
								message = tmp[2];
								startTimer(millisec, message);
								Server.sendResponse("Timer di " + millisec + " secondi avviato");
							}
							else if(length == 1)
							{
								Server.sendResponse("Inserisci secondi e messaggio, separati da spazio");
								
								//waits for the response text
								JSONArray tmpAr = Server.firstUpdate();
								String msgText = Server.parseMessage(tmpAr);
								String[] tmp2 = msgText.split(" ");
								
								millisec = Integer.parseInt(tmp2[0]);
								message = tmp2[1];
								startTimer(millisec, message);
								Server.sendResponse("Timer di " + millisec + " secondi avviato");								
							}
							else
								Server.sendResponse(ERROR_MESSAGE);
							break;
						case "/help":
						case "help":
							Server.sendResponse(COMMANDS_MESSAGE);
							break;
						default:
							Server.sendResponse(ERROR_MESSAGE);
							break;
					}
				}
				catch(Exception e)
				{
					Server.sendResponse(ERROR_MESSAGE);
				}
			
			
			return;
		}
		
		
		
		private static void startTimer(int millisec, String message)
		{
			//starts waiter thread
			Thread waiter = new Waiter(millisec * 1000, message, chatId);
			waiter.start();
			
			return;
		}

		
}
