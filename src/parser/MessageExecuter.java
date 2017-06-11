package parser;

import org.json.JSONArray;

import httpServer.Server;
import timer.Waiter;

public class MessageExecuter
{
	private static final String COMMANDS_MESSAGE = "Uso:\n'/timer <x_secondi> <messaggio>' : aspetta per x_secondi e scrive il messaggio\n" 
			+ "'/help' : scrive questo messaggio";
	private static final String HELLO_MESSAGE = "Ciao! Questo è un Bot semplice per ricordare appuntamenti.\n" + COMMANDS_MESSAGE;
	private static final String ERROR_MESSAGE = "Comando non riconosicuto.\n" + COMMANDS_MESSAGE;
	private long chatId;
	
	public MessageExecuter(String updateText, long chatId, long updateId)
	{
		this.chatId = chatId;
	}
	
	//PARSING RESPONSE TEXT : USE JFLEX MAYBE?
	//TEMPORARY
	public void executeMessage(String updateText)
	{
		String[] readMessage;
		int millisec = 1;
		String message = ERROR_MESSAGE;
		
		try
		{
			readMessage = updateText.split(" ");
			int length = readMessage.length;
			switch(readMessage[0].toLowerCase())
			{
				case "/start":
				case "/start@stanzinomemobot":
					Server.sendResponse(HELLO_MESSAGE);
					break;
				case "/timer":
				case "/timer@stanzinomemobot":
					if(length >= 3)
					{
						millisec = Integer.parseInt(readMessage[1]);
						if(millisec <= 0)
						{
							Server.sendResponse(ERROR_MESSAGE);
							break;
						}
							
						message = "";
						for(int i=2; i<readMessage.length; i++)
							message += readMessage[i];
						startTimer(millisec, message);
						Server.sendResponse("Timer di " + millisec + " secondi avviato");
					}
					/*
					else if(length == 1)
					{
						Server.sendResponse("Inserisci secondi e messaggio, separati da spazio");
							
						//waits for the response text
						JSONArray tmpAr = Server.firstUpdate();
						String msgText = Server.parseMessage(tmpAr);
						String[] readMessage2 = msgText.split(" ");
							
						millisec = Integer.parseInt(readMessage2[0]);
						
						if(millisec < 0)
							Server.sendResponse(ERROR_MESSAGE);
						else
						{
							message = "";
							for(int i=1; i<readMessage2.length; i++)
								message += readMessage2[i];
							startTimer(millisec, message);
							Server.sendResponse("Timer di " + millisec + " secondi avviato");
						}
					}
					*/
					else
						Server.sendResponse(ERROR_MESSAGE);
					break;
				case "/help":
				case "help":
				case "/help@stanzinomemobot":
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
		
		
		
		private void startTimer(int millisec, String message)
		{
			//starts waiter thread
			Thread waiter = new Waiter(millisec * 1000, message, chatId);
			waiter.start();
			
			return;
		}

		
}
