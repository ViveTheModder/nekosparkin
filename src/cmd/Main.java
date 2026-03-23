package cmd;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Main {
	public static void debug() throws IOException {
		long start = System.currentTimeMillis();
		File ultBarDir = new File("E:\\Programs\\My Tools\\Nekosparkin'\\BT3\\Ultimate_Battle_US\\ub_missions");
		int gameModeIdx = UltBatMeteor.SURVIVAL;
		RandomAccessFile[] containers = UltBatMeteor.getMissionParamContainers(ultBarDir, gameModeIdx);
		byte[] params = UltBatMeteor.getMissionParams(containers, gameModeIdx, 0, true);
		String[] info = UltBatMeteor.getMissionInfo(params, true);
		for (String i: info) System.out.println(i);
		long end = System.currentTimeMillis();
		System.out.println("Total Time Elapsed: " + (end - start) / 1000.0 + " seconds");
	}
	public static void main(String[] args) throws Exception {
		
	}
}
