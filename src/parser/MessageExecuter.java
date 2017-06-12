package parser;

import java.util.Calendar;

import org.json.JSONArray;

import httpServer.HttpClientUtil;
import httpServer.Server;
import timer.Waiter;

public class MessageExecuter
{
	private static final String COMMANDS_MESSAGE = "Uso:\n'/timer <x_secondi> <messaggio>' : aspetta per x_secondi e scrive il messaggio\n" 
			+ "'/help' : scrive questo messaggio\n"
			+ "'/doomsday' : Doomsday clock dell'anno corrente\n"
			+ "'/doomsday <anno> : Doomsday clock dell'anno inserito\n"
			+ "'/random' : Numero random fra 0 e 1\n"
			+ "'/random <min> <max> : Numero random fra i due estremi";
	private static final String HELLO_MESSAGE = "Ciao! Questo è un Bot semplice per ricordare appuntamenti.\n" + COMMANDS_MESSAGE;
	private static final String ERROR_MESSAGE = "Comando non riconosicuto.\n" + COMMANDS_MESSAGE;	
	private static long chatId;
	
	//PARSING RESPONSE TEXT : USE JFLEX MAYBE?
	//TEMPORARY
	public static void executeMessage(String updateText, long chatId)
	{
		MessageExecuter.chatId = chatId;
		
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
					 * USARE FORCEREPLY
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
				case "/doomsday":
				case "/doomsday@stanzinomemobot":
					if(length == 1)
						Server.sendResponse(getDoomsday(null));
					else if(length == 2)
					{
						if(readMessage[1].matches("^(19|20)\\d{2}$"))
							Server.sendResponse(getDoomsday(readMessage[1]));
					}
					else
						Server.sendResponse(ERROR_MESSAGE);
					
					break;
				case "/random":
				case "/random@stanzinomemobot":
					if(length == 1)
					{
						if(Math.random() < 0.5)
							Server.sendResponse("Random: 0");
						else
							Server.sendResponse("Random: 1");
					}
					else if(length == 3)
					{
						int min = Integer.parseInt(readMessage[1]);
						int max = Integer.parseInt(readMessage[2]);
						
						if(max < min)
						{
							int tmp = max;
							max = min;
							min = tmp;
						}
						
						Server.sendResponse("Random: " + (int) (Math.random() * ((max - min) + 1) + min));
					}
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
		
		private static void startTimer(int millisec, String message)
		{
			//starts waiter thread
			Thread waiter = new Waiter(millisec * 1000, message, chatId);
			waiter.start();
			
			return;
		}

		private static String getDoomsday(String year)
		{
			if(year == null)
			{
				year = "" + Calendar.getInstance().get(Calendar.YEAR);
			}
			
			String response = HttpClientUtil.get
			(
					"http://thebulletin.org/clock/2017"	
			);
			
			//parse response and extract HMTL TITLE
			
			return null;
		}
}
