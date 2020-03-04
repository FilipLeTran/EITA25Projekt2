import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;
import java.io.IOException;

public class Log {
	private File file;
	
	public Log(String fileName) {
		try {
			file = new File(fileName);
			if (file.createNewFile()) {
				System.out.println("File created: " + file.getName());
			} else {
				System.out.println("File exists.");
			}
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		
	}

	public void newEntry(String entry) {
		try {
			FileWriter writer = new FileWriter(file.getName(), true);
			writer.append(entry+"\n");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			System.out.println("An error occurred concerning filewriter.");
		}
	}

	public void deleteEntry() {
		try {
			FileWriter writer = new FileWriter(file.getName());
			writer.close();
		} catch (IOException e) {
			System.out.println("An error occurred concerning filewriter.");
		}
	}
	
	public void printLog() {
		Scanner scan = new Scanner(file.getName());
		while (scan.hasNextLine()) {
			System.out.println(scan.nextLine());
		}
		scan.close();
	}

}