package it.stanzino.memobot.in_out;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.List;

public class FileOverWriter
{
	private File file;
	PrintWriter writer;
	
	public FileOverWriter(String outPath) throws IOException
	{
		file = new File(outPath);
		writer = new PrintWriter(new BufferedWriter(new FileWriter(outPath, true)));
	}
	
	public String overwrite(int removeIndex) throws IOException
	{
		List<String> lines = Files.readAllLines(file.toPath());
		String removed = "";
		
		if(removeIndex == -1)
		{
			for(int i=0; i<lines.size(); i++)
				lines.remove(i);
		}
		else
			removed = lines.remove(removeIndex - 1);
		
		Files.write(file.toPath(), lines);
		
		return removed;
	}
	
	public void write(String line)
	{
		writer.write(line);
		writer.flush();
	}
}
