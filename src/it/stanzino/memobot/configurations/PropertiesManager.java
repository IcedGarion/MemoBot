package it.stanzino.memobot.configurations;

import java.io.FileInputStream;
import java.util.Properties;

public class PropertiesManager
{
	private static final String PROPERTIES_FILE = "resources/MemoBot.config";
	public static String TELEGRAM_BOT_URL;
	public static String TELEGRAM_TEST_BOT_URL;
	public static long TELEGRAM_DEV_CHAT_ID;
	public static int SERVER_TIMEOUT; 				//30 sec di timeout se rimane senza connessione per troppo
	public static int SERVER_MAX_NOCONNECTION;			//max 15 richieste senza connessione e aspetta		
	public static int SERVER_UPDATE_FREQUENCY;
	public static String RESOURCES_OUTPUT_PATH;
	public static String RESOURCES_NAMES_PATH;
	public static String RESOURCES_TIMES_PATH;
	public static String RESOURCES_IMPORTANTS_PATH;

	public static void loadProperties()
	{
		Properties prop = new Properties();
		
		//legge il file
		try(FileInputStream input = new FileInputStream(PROPERTIES_FILE))
		{
			prop.load(input);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		//salva le properties lette
		TELEGRAM_BOT_URL = prop.getProperty("TELEGRAM_BOT_URL");
		TELEGRAM_TEST_BOT_URL = prop.getProperty("TELEGRAM_TEST_BOT_URL");
		TELEGRAM_DEV_CHAT_ID = Long.parseLong(prop.getProperty("TELEGRAM_DEV_CHAT_ID"));
		SERVER_TIMEOUT = Integer.parseInt(prop.getProperty("SERVER_TIMEOUT"));
		SERVER_MAX_NOCONNECTION = Integer.parseInt(prop.getProperty("SERVER_MAX_NOCONNECTION"));
		SERVER_TIMEOUT = Integer.parseInt(prop.getProperty("SERVER_TIMEOUT"));
		SERVER_UPDATE_FREQUENCY = Integer.parseInt(prop.getProperty("SERVER_UPDATE_FREQUENCY"));
		RESOURCES_OUTPUT_PATH = prop.getProperty("RESOURCES_OUTPUT_PATH");
		RESOURCES_NAMES_PATH = prop.getProperty("RESOURCES_NAMES_PATH");
		RESOURCES_TIMES_PATH = prop.getProperty("RESOURCES_TIMES_PATH");
		RESOURCES_IMPORTANTS_PATH = prop.getProperty("RESOURCES_IMPORTANTS_PATH");
	}
}
