package parser;

import java.util.Calendar;

import org.json.JSONArray;

import httpServer.HttpClientUtil;
import httpServer.Server;
import timer.Waiter;

public class MessageExecuter
{
	private static final String COMMANDS_MESSAGE = "Uso:\n"
			+ "'/timer <x_secondi> <messaggio>' : aspetta per x_secondi e scrive il messaggio\n" 
			+ "'/timer <HH:MM> <messaggio>' : aspetta per ore e minuti e scrive il messaggio\n"
			+ "'/help' : scrive questo messaggio\n"
			+ "'/doomsday' : Doomsday clock dell'anno corrente\n"
			+ "'/doomsday <anno> : Doomsday clock dell'anno inserito\n"
			+ "'/random' : Numero random fra 0 e 1\n"
			+ "'/random <min> <max> : Numero random fra i due estremi\n"
			+ "'/random <numero> <numero> <numero>... : Numero random fra i dati\n";
	
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
						if(readMessage[1].matches("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$"))
							millisec = toMillisec(readMessage[1]);
						else
							millisec = Integer.parseInt(readMessage[1]);
						
						if(millisec <= 0)
						{
							Server.sendResponse(ERROR_MESSAGE);
							break;
						}
							
						message = "";
						for(int i=2; i<readMessage.length; i++)
							message += readMessage[i] + " ";
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
						else
							Server.sendResponse("Prova con un altro anno...");
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
					else if(length >= 4)
						Server.sendResponse("Random: " + randomize(readMessage, length));
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
		
		private static int toMillisec(String string)
		{
			String tmp[] = string.split(":");
			int hour = Integer.parseInt(tmp[0]);
			int min = Integer.parseInt(tmp[1]);
			
			return (min * 60) + (hour * 3600);
		}

		private static void startTimer(int millisec, String message)
		{
			//starts waiter thread
			Thread waiter = new Waiter(millisec * 1000, message, chatId);
			waiter.start();
			
			return;
		}
		
		private static String randomize(String[] array, int length)
		{
			//controlla che siano tutti int
			for(int i=1; i<length; i++)
				Integer.parseInt(array[i]);
						
			return array[(int) (Math.random() * (length - 1)) + 1];
		}

		private static String getDoomsday(String year)
		{
			if(year == null)
			{
				year = "" + Calendar.getInstance().get(Calendar.YEAR);
			}
			
			String response = HttpClientUtil.get
			(
					"http://thebulletin.org/clock/" + year	
			);
			
			//parse response and extract HMTL TITLE
			int start = response.indexOf("<title>") + 7;
			String text = "";
			
			while(response.charAt(start) != '|')
				text += response.charAt(start++);
			
			if(text.contains("Search"))
				text = "Prova con un altro anno... ";
			return text;
		}
}
