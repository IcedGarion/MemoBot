package guard;

import httpServer.Server;

public class Main
{
	public static void main(String args[])
	{
		//this is only a guard
		while(true)
		{
			try
			{
				Server.MemoBot();
			}
			catch(Exception e)
			{
				continue;
			}
		}
	}

}
