package parser;

import java.util.logging.Logger;

import functions.Util;
import httpServer.Server;

public class MessageExecuter
{
	private static final String COMMANDS_MESSAGE = "Uso:\n"
			+ "'/timer <x_secondi> <messaggio>' : aspetta per x_secondi e scrive il messaggio\n"
			+ "'/timer <HH:MM> <messaggio>' : aspetta per ore e minuti e scrive il messaggio\n"
			+ "'/help' : scrive questo messaggio\n" + "'/doomsday' : Doomsday clock dell'anno corrente\n"
			+ "'/doomsday <anno> : Doomsday clock dell'anno inserito\n" + "'/random' : Numero random fra 0 e 1\n"
			+ "'/random <min> <max> : Numero random fra i due estremi\n"
			+ "'/random <numero> <numero> <numero>... : Numero random fra i dati\n";

	private static final String HELLO_MESSAGE = "Ciao! Questo e' un Bot semplice per ricordare appuntamenti.\n"	+ COMMANDS_MESSAGE;
	private static final String ERROR_MESSAGE = "Comando non riconosicuto.\n" + COMMANDS_MESSAGE;
	private static final int MAX_RANDOM_SEQUENCE = 20;
	private static long chatId;
	private static final Logger LOGGER = Logger.getLogger( MessageExecuter.class.getName() );


	public static void executeMessage(String updateText, long chatId)
	{
		MessageExecuter.chatId = chatId;

		String[] readMessage;
		int sec = 1;
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
					
					//timer sec msg
					if(length >= 3)
					{
						if(readMessage[1].matches("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$"))
							sec = Util.toMillisec(readMessage[1]);
						else
							sec = Integer.parseInt(readMessage[1]);

						if(sec <= 0)
						{
							Server.sendResponse(ERROR_MESSAGE);
							break;
						}

						message = "";
						for (int i = 2; i < readMessage.length; i++)
							message += readMessage[i] + " ";
						Util.startTimer(sec, message, chatId);
						Server.sendResponse("Timer di " + sec + " secondi avviato");
					}
					else
						Server.sendResponse(ERROR_MESSAGE);
					break;
				case "/doomsday":
				case "/doomsday@stanzinomemobot":
					
					//no parameters : today's doomsday 
					if(length == 1)
						Server.sendResponse(Util.getDoomsday(null));
					
					//year parameter : doomsday of that year
					else if(length == 2)
					{
						if(readMessage[1].matches("^(19|20)\\d{2}$"))
							Server.sendResponse(Util.getDoomsday(readMessage[1]));
						else
							Server.sendResponse("Prova con un altro anno...");
					}
					else
						Server.sendResponse(ERROR_MESSAGE);

					break;
				case "/random":
				case "/random@stanzinomemobot":

					// length 1 : random 0/1 (a call with null runs this type of random)
					if(length == 1)
						Server.sendResponse("Random: " + Util.randomize(null));

					// length 2 : invalid; length >= x : may cause problems
					else if(length >= MAX_RANDOM_SEQUENCE || length == 2)
						Server.sendResponse(ERROR_MESSAGE);
					else
						Server.sendResponse("Random: " + Util.randomize(readMessage));

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
			LOGGER.warning(e.getMessage());
		}

		return;
	}
}
