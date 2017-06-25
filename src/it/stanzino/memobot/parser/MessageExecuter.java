package it.stanzino.memobot.parser;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import it.stanzino.memobot.functions.Util;
import it.stanzino.memobot.httpServer.MainServer;
import it.stanzino.memobot.in_out.FileOverWriter;
import it.stanzino.memobot.in_out.Readr;

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
			+ "'/rimuovi <numero messaggio>' : rimuove il messaggio dalla lista importanti";

	private static final String HELLO_MESSAGE = "Ciao! Questo e' un Bot semplice per ricordare appuntamenti.\n"	+ COMMANDS_MESSAGE;
	private static final String ERROR_MESSAGE = "Comando non riconosicuto.\n" + COMMANDS_MESSAGE;
	private static final int MAX_RANDOM_SEQUENCE = 20;
	@SuppressWarnings("unused")
	private static long chatId;


	public static void executeMessage(String updateText, String senderName, long chatId) throws SecurityException, IOException
	{
		FileOverWriter writer = new FileOverWriter(MainServer.IMPORTANTS_PATH);
		FileOverWriter timesOverwriter = new FileOverWriter(MainServer.TIMES_PATH);
		Readr reader;
		MessageExecuter.chatId = chatId;
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
					MainServer.sendResponse(HELLO_MESSAGE);
					break;
				case "/timer":
				case "/timer@stanzinomemobot":
					
					//timer sec msg
					if(length >= 3)
					{
						if(readMessage[1].matches("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$"))
							sec = Util.toSec(readMessage[1]);
						else
							sec = Long.parseLong(readMessage[1]);

						if(sec <= 0)
						{
							MainServer.sendResponse(ERROR_MESSAGE);
							break;
						}

						message = "";
						for (int i = 2; i < readMessage.length; i++)
							message += readMessage[i] + " ";
						Util.startTimer(sec, message, chatId);
						MainServer.sendResponse("Timer di " + sec + " secondi avviato");
					}
					else
						MainServer.sendResponse(ERROR_MESSAGE);
					break;
				case "/doomsday":
				case "/doomsday@stanzinomemobot":
					
					//no parameters : today's doomsday 
					if(length == 1)
						MainServer.sendResponse(Util.getDoomsday(null));
					
					//year parameter : doomsday of that year
					else if(length == 2)
					{
						if(readMessage[1].matches("^(19|20)\\d{2}$"))
							MainServer.sendResponse(Util.getDoomsday(readMessage[1]));
						else
							MainServer.sendResponse("Prova con un altro anno...");
					}
					else
						MainServer.sendResponse(ERROR_MESSAGE);

					break;
				case "/random":
				case "/random@stanzinomemobot":

					// length 1 : random 0/1 (a call with null runs this type of random)
					if(length == 1)
						MainServer.sendResponse("Random: " + Util.randomize(null));

					// length 2 : invalid; length >= x : may cause problems
					else if(length >= MAX_RANDOM_SEQUENCE || length == 2)
						MainServer.sendResponse(ERROR_MESSAGE);
					else
						MainServer.sendResponse("Random: " + Util.randomize(readMessage));

					break;
				case "/importante":
					if(length == 1)
					{
						reader = new Readr(MainServer.IMPORTANTS_PATH);
						List<String> lines = reader.readFile();
						String msg = "";
						int i = 0;
						
						msg += "LISTA IMPORTANTI :\n";
						for(String line : lines)
						{
							msg += "\n" + (++i) + "\n" + line + "\n";
						}
						if(i == 0)
							msg += "VUOTA!\n";
						
						MainServer.sendResponse(msg);
					}
					else if(length >= 2)
					{
						SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy / kk:mm:ss");
						String msgTot = dateFormatter.format(new Date()) + " --- " + senderName + " --- ";
						
						for(int i=1; i<length; i++)
							msgTot += readMessage[i] + " ";
					
						writer.write(msgTot + "\n");
							
						MainServer.sendResponse("Messaggio aggiunto");
					}
					else
						MainServer.sendResponse(ERROR_MESSAGE);
					break;
				case "/rimuovi":
					if(length == 2)
					{
						try
						{
							String all = readMessage[1].toLowerCase(); 
							
							if(all.equals("tutti") || all.equals("tutto"))
							{
								writer.overwrite(-1);
								MainServer.sendResponse("TUTTI I MESSAGGI ELIMINATI");
							}
							
							else
							{
								writer.overwrite(Integer.parseInt(readMessage[1]));
								MainServer.sendResponse("Messaggio numero " + readMessage[1] + " rimosso\n");
							}
						}
						catch(Exception e)
						{
							MainServer.sendResponse("Non c'e' nella lista");
						}
					}
					else 
						MainServer.sendResponse(ERROR_MESSAGE);
					break;
				case "/help":
				case "help":
				case "/help@stanzinomemobot":
					MainServer.sendResponse(COMMANDS_MESSAGE);
					break;
				case "_rimuoviTimer":
					timesOverwriter.overwrite(-1);
					MainServer.sendResponse("TUTTI I TIMER RIMOSSI");
					break;
				case "/echo":
					if(length == 2)
						MainServer.sendResponse(readMessage[1]);
					else
						MainServer.sendResponse("Manca il parametro! ");
					break;
				default:
					MainServer.sendResponse(ERROR_MESSAGE);
					break;
			}
		}
		catch(Exception e)
		{
			MainServer.sendResponse(ERROR_MESSAGE);
			MainServer.logException(e + "\n" + e.getMessage() + "\n");
		}

		return;
	}
}
