package functions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import httpServer.HttpClientUtil;

public class Util
{
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
	
	public static int toMillisec(String string)
	{
		String tmp[] = string.split(":");
		int hour = Integer.parseInt(tmp[0]);
		int min = Integer.parseInt(tmp[1]);
		
		return (min * 60) + (hour * 3600);
	}
	
	public static void startTimer(int millisec, String message, long chatId)
	{
		//starts waiter thread
		Thread waiter = new Timer(millisec * 1000, message, chatId);
		waiter.start();
		
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
}
