import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;
import java.io.IOException;

public class Log {
	private File file;
	
	public static void main(String[] args) {
		Log myLog = new Log("log.txt");
		myLog.newEntry("Record 1", "Swiper", "2020-01-14", "Ruined everything");
		myLog.printLog();
	}
	
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

	public void newEntry(String record, String date, String editor, String edit) {
		try {
			FileWriter writer = new FileWriter(file.getName());
			writer.append("blabla");
			writer.write(record + " " + editor + " " + date + " " + edit);
			writer.flush();
			writer.close();
			System.out.println("we done");
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