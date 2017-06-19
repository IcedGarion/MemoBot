package parser;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import functions.Util;
import httpServer.Server;
import in_out.Readr;
import in_out.Writer;

public class MessageExecuter
{
	private static final String COMMANDS_MESSAGE = "Uso:\n"
			+ "'/timer <x_secondi> <messaggio>' : aspetta per x_secondi e scrive il messaggio\n"
			+ "'/timer <HH:MM> <messaggio>' : aspetta per ore e minuti e scrive il messaggio\n"
			+ "'/help' : scrive questo messaggio\n" + "'/doomsday' : Doomsday clock dell'anno corrente\n"
			+ "'/doomsday <anno> : Doomsday clock dell'anno inserito\n" + "'/random' : Numero random fra 0 e 1\n"
			+ "'/random <min> <max> : Numero random fra i due estremi\n"
			+ "'/random <numero> <numero> <numero>... : Numero random fra i dati\n"
			+ "'/importante' : lista messaggi importanti\n"
			+ "'/importante <messaggio importante>' : aggiunge il messaggio alla lista dei messaggi importanti\n"
			+ "'/importante /rimuovi <numero messaggio>' : rimuove il messaggio dalla lista";

	private static final String HELLO_MESSAGE = "Ciao! Questo e' un Bot semplice per ricordare appuntamenti.\n"	+ COMMANDS_MESSAGE;
	private static final String ERROR_MESSAGE = "Comando non riconosicuto.\n" + COMMANDS_MESSAGE;
	private static final int MAX_RANDOM_SEQUENCE = 20;
	@SuppressWarnings("unused")
	private static long chatId;


	public static void executeMessage(String updateText, String senderName, long chatId) throws SecurityException, IOException
	{
		Writer logger;
		Writer overwriter;
		Writer writer;
		Readr reader;
		MessageExecuter.chatId = chatId;
		logger = new Writer(Server.OUTPUT_PATH, "log", Logger.getLogger(MessageExecuter.class.getName()), -1 );
		String[] readMessage;
		long sec = 1;
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
							sec = Util.toSec(readMessage[1]);
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
				case "/importante":
					if(length == 1)
					{
						reader = new Readr(Server.IMPORTANTS_PATH);
						List<String> lines = reader.readFile();
						String msg = "";
						int i = 0;
						
						msg += "LISTA IMPORTANTI :\n";
						for(String line : lines)
						{
							msg += (++i) + " ------- \n" + line + "\n";
						}
						if(i == 0)
							msg += "VUOTA!\n";
						
						Server.sendResponse(msg);
					}
					else if(length >= 2)
					{
						if(length == 3 && readMessage[1].toLowerCase().equals("/rimuovi"))
						{
							overwriter = new Writer(Server.IMPORTANTS_PATH, "overwrite", null, Integer.parseInt(readMessage[2]));
							
							Server.sendResponse("Messaggio numero " + readMessage[2] + " rimosso\n");
						}
						else
						{
							SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy / kk:mm:ss");
							String msgTot = dateFormatter.format(new Date()) + " --- " + senderName + " --- ";
							
							for(int i=1; i<length; i++)
								msgTot += readMessage[i] + " ";
							
							writer = new Writer(Server.IMPORTANTS_PATH, "write", null, -1);
							writer.write(msgTot);
							
							Server.sendResponse("Messaggio aggiunto");
						}
						
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
			logger.warning(e.getMessage() + "\n");
		}

		return;
	}
}
