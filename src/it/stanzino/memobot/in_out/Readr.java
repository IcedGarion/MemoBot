package it.stanzino.memobot.in_out;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class Readr
{
	private File file;
	
	public Readr(String outPath) throws FileNotFoundException
	{
		file = new File(outPath);
	}
	
	public List<String> readFile() throws IOException
	{
		
		return Files.readAllLines(file.toPath());
	}
}
