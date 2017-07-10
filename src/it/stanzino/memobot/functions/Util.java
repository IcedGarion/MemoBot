package it.stanzino.memobot.functions;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import it.stanzino.memobot.httpServer.HttpClientUtil;
import it.stanzino.memobot.httpServer.MainServer;
import it.stanzino.memobot.in_out.FileOverWriter;

public class Util
{
	private static final String[] INVALID_UTF8 = {"à", "è", "é", "ì", "ò", "ù", "À", "È", "É", "Ì", "Ò", "Ù"};
	private static final String[] VALIDATED_UTF8 = {"a'", "e'", "e'", "i'", "o'", "u'", "A'", "E'", "E'", "I'", "O'", "U'"};
	private static FileOverWriter writer;
	private static Calendar c;
	
	public static String getDoomsday(String year)
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
	
	public static long toSec(String string)
	{
		String tmp[] = string.split(":");
		long hour = Long.parseLong(tmp[0]);
		long min = Long.parseLong(tmp[1]);
		
		return (min * 60) + (hour * 3600);
	}
	
	public static void startTimer(long millisec, String message, long chatId) throws SecurityException, IOException
	{
		//writes the time and msg in a file: current time (millisec) + timer
		c = Calendar.getInstance();
		millisec = (millisec * 1000) + c.getTimeInMillis();
		writer = new FileOverWriter(MainServer.TIMES_PATH);
		writer.write(millisec + "," + message + "," + chatId + "\n");
		
		return;
	}

	public static String randomize(String array[])
	{
		int min, max;
		String ret = "ERRORE";
		
		//no parameters : random 0 / 1
		if(array == null)
		{
			if(Math.random() < 0.5)
				ret = "0";
			else
				ret = "1";
		}
		else
		{
			//random in interval
			if(array.length == 3)
			{
				min = Integer.parseInt(array[1]);
				max = Integer.parseInt(array[2]);
				
				if(max < min)
				{
					int tmp = max;
					max = min;
					min = tmp;
				}
				
				ret = "" + (int) (Math.random() * ((max - min) + 1) + min);
			}
			//random in sequence
			else
			{
				checkInt(array);
				
				ret = array[(int) (Math.random() * (array.length - 1)) + 1];
			}
		}
		
		return ret;
	}
	
	public static void checkInt(String[] array)
	{
		for(int i=1; i<array.length; i++)
			Integer.parseInt(array[i]);
	}
	
	public static String getDate()
	{
		return new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
	}
	
	public static String convertToUtf(String s)
	{		
		for(int i=0; i<INVALID_UTF8.length; i++)
			s = s.replace(INVALID_UTF8[i], VALIDATED_UTF8[i]);
		
		byte[] bytes = s.getBytes( Charset.forName("UTF-16" ));
		String ret = new String( bytes, Charset.forName("UTF-16") );
		
		return ret;
	}
}
