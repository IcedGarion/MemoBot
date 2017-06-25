package it.stanzino.memobot.in_out;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class NamesLogger extends Thread
{
	private PrintWriter writer;

	public NamesLogger(String outPath) throws IOException
	{
		writer = new PrintWriter(new BufferedWriter(new FileWriter(outPath, true)));
	}

	public void write(String msg)
	{
		writer.println(msg);
		writer.flush();
	}
}
