package httpServer;

public class MessageExecuter extends Thread
{
	private static final String COMMANDS_MESSAGE = "Uso:\n'!timer <x_secondi> <messaggio>' : aspetta per x_secondi e scrive il messaggio\n" 
			+ "'!help' : scrive questo messaggio";
	private static final String HELLO_MESSAGE = "Ciao! Questo è un Bot semplice per ricordare appuntamenti.\n" + COMMANDS_MESSAGE;
	private static final String ERROR_MESSAGE = "Comando non riconosicuto.\n" + COMMANDS_MESSAGE;
	private String updateText;
	private static long chatId;
	
	public MessageExecuter(String updateText, long chatId)
	{
		this.updateText = updateText;
		this.chatId = chatId;
	}
	
	@Override
	public void run()
	{
		//read the (last) message received and executes command (timer, for now)
		String response = parseMessage(updateText);
		
		//writes response elaborated
		sendResponse(response);
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
						case "/start":
							response = HELLO_MESSAGE;
							break;
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
		
		private static void sendResponse(String message)
		{
			String responseJSON, response = "";
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
		
		private static void startTimer(int millisec, String message)
		{
			//starts waiter thread
			Thread waiter = new Waiter(millisec * 1000, message, chatId);
			waiter.start();
			
			return;
		}

		
}
