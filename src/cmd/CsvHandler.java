package cmd;
//Nekosparkin: CSV Handler Class by ViveTheJoestar
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Scanner;

public class CsvHandler {
	private static int getNumRows(File csv) throws IOException {
		byte charByte; //The method was rewritten to search for 1 byte instead of 2
		int rowCnt = 0, fileSize = (int) csv.length();
		RandomAccessFile raf = new RandomAccessFile(csv, "r");
		raf.seek(0);
		for (int pos = 0; pos < fileSize; pos++) {
			raf.seek(pos);
			charByte = raf.readByte();
			//Check for Line Feed (originally, Carriage Return was also included, but only Windows uses it)
			if (charByte == 0x0A) rowCnt++;
		}
		raf.close();
		return rowCnt + 1;
	}
	public static File[] getAvailableCsvFiles(boolean toggleNeo) {
		String folderName = toggleNeo ? "bt2" : "bt3";
		File csvFolder = new File("./csv/" + folderName);
		File[] csvFiles = csvFolder.listFiles((dir, name) -> (name.toLowerCase().endsWith(".csv")));
		//This step is only required for Linux users, as Windows already sorts the files
		Arrays.sort(csvFiles);
		return csvFiles;
	}
	public static int getCsvSearchResult(File[] csvFiles, String type, boolean toggleNeo) {
		String folderName = toggleNeo ? "bt2" : "bt3";
		File csvKey = new File("./csv/" + folderName + "/" + type + ".csv");
		int csvIndex = Arrays.binarySearch(csvFiles, csvKey);
		return csvIndex;
	}
	public static String[] getParamNames(File csv) throws IOException {
		int lineCnt = 0;
		int numParamNames = getNumRows(csv);
		if (numParamNames == 0) return null;
		String[] paramNames = new String[numParamNames]; 
		Scanner sc = new Scanner(csv);
		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			/* Due to param IDs like 255 and 999 (for null/random params) not matching the expected param sizes,
			 * I deliberately went by the line count rather than the ID specified in the CSV's row,
			 * essentially preventing any ArrayIndexOutOfBoundsExceptions from occuring. */
			if (lineCnt < numParamNames)
				paramNames[lineCnt] = line.split(",")[1];
			lineCnt++;
		}
		sc.close();
		return paramNames;
	}
	
}
