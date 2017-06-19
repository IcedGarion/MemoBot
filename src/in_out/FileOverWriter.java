package in_out;

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
		writer = new PrintWriter(new BufferedWriter(new FileWriter(outPath)));
	}
	
	public void overwrite(int removeIndex) throws IOException
	{
		List<String> lines = Files.readAllLines(file.toPath());
		lines.remove(removeIndex);
		Files.write(file.toPath(), lines);
	}
	
	public void write(String line)
	{
		writer.write(line);
		writer.flush();
	}
}
