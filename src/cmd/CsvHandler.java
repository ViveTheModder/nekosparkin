package cmd;
//Nekosparkin: CSV Handler Class by ViveTheJoestar
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Scanner;

public class CsvHandler {
	/* Hilarious flaw for this method: if the last line is empty, BAM! Exception.
	private static int getNumRows(File csv) throws IOException {
		byte[] charBytes = new byte[2];
		int fileSize = (int) csv.length();
		RandomAccessFile raf = new RandomAccessFile(csv, "r");
		for (int pos = fileSize - 2; pos > 0; pos--) {
			raf.seek(pos);
			raf.read(charBytes);
			//Check for presence of new lines
			if (charBytes[0] == 0x0D && charBytes[1] == 0x0A) {
				//Calculate number of bytes for last row contents
				byte[] rowNumBytes = new byte[fileSize - pos - 2];
				raf.read(rowNumBytes);
				String rowNumStr = new String(rowNumBytes);
				//Only get the last row's ID, then add 1 and return it
				int rowNum = Integer.parseInt(rowNumStr.split(",")[0]);
				raf.close();
				return rowNum + 1;
			}
		}
		raf.close();
		return 0;
	} */
	private static int getNumRows(File csv) throws IOException {
		byte[] charBytes = new byte[2];
		int rowCnt = 0, fileSize = (int) csv.length();
		RandomAccessFile raf = new RandomAccessFile(csv, "r");
		raf.seek(0);
		for (int pos = 0; pos < fileSize; pos++) {
			raf.seek(pos);
			raf.read(charBytes);
			if (charBytes[0] == 0x0D && charBytes[1] == 0x0A) rowCnt++;
		}
		raf.close();
		return rowCnt + 1;
	}
	public static File[] getAvailableCsvFiles() {
		File csvFolder = new File("./csv/");
		File[] csvFiles = csvFolder.listFiles((dir, name) -> (name.toLowerCase().endsWith(".csv")));
		//This step is only required for Linux users, as Windows already sorts the files
		Arrays.sort(csvFiles);
		return csvFiles;
	}
	public static int getCsvSearchResult(File[] csvFiles, String type) {
		File csvKey = new File("./csv/" + type + ".csv");
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
