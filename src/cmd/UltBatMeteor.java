package cmd;
//Nekosparkin: Ultimate Battle (BT3) Class by ViveTheJoestar
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class UltBatMeteor {
	//These variables are unused at the moment, because they are here just for readability
	public static final int SIM_DRAGON = 0;
	public static final int MISSION_100 = 1;
	public static final int SURVIVAL = 2;
	public static final int RANKING = 3;
	public static final int CHALLENGE = 4;
	public static final int CIRCUIT = 5;
	public static final String[] MODE_NAMES = {
		"Sim Dragon", "Mission 100", "Survival", "Ranking Battle", "Ranking Battle (Challengers)", "Circuit Battle"	
	};
	private static String[][] paramNames;
	private static final int[] DAT_SIZES = { 256, 320, 5248, 22016, 704, 6656, 2816, 4416, 1088, 1664, 320, 1792 };
	private static final int[] NUM_MISSIONS = { 7, 100, 3, 100, 37, 5 };
	private static final int[] BATTLE_PARAMS_SIZE = { 28, 52, 448, 28, 28, 56 };
	private static final int[] NUM_ENEMIES = { 1, 5, 50, 99, 1, 8 };
	//The rest of the parameters are assumed to be opponent IDs (integers), so no need to specify
	private static final String[] BATTLE_PARAM_TYPES = {
		"ref", "bool", "time", "map", "bgm", "bool", "cond", "dp"
	};
	private static final String[] ENEMY_PARAM_TYPES = { 
		"chara", "int", "int", "item", "item", "item", "item", "item", "item", "item", "item"
	};
	
	public static RandomAccessFile[] getMissionParamContainers(File ultBatDir, int gameModeIdx) throws IOException {
		File[] datFiles = ultBatDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".dat"));
		RandomAccessFile[] containers = new RandomAccessFile[2];
		for (int datCnt = 0; datCnt < datFiles.length; datCnt++) {
			long datFileSize = datFiles[datCnt].length();
			if (datFileSize == DAT_SIZES[2 * gameModeIdx])
				containers[0] = new RandomAccessFile(datFiles[datCnt], "rw");
			else if (datFileSize == DAT_SIZES[2 * gameModeIdx + 1])
				containers[1] = new RandomAccessFile(datFiles[datCnt], "rw");
		}
		return containers;
	}
	public static String[] getMissionInfo(byte[] params, boolean enemy) throws IOException {
		byte[] paramBytes = new byte[4];
		int size = params.length / 4;
		File[] csvFiles = CsvHandler.getAvailableCsvFiles();
		String[] info = new String[size];
		String[] paramTypes = enemy ? ENEMY_PARAM_TYPES : BATTLE_PARAM_TYPES;
		paramNames = new String[csvFiles.length][]; //If this function is called several times, move this line elsewhere
		for (int typeCnt = 0; typeCnt < size; typeCnt++) {
			System.arraycopy(params, typeCnt * 4, paramBytes, 0, 4);
			int paramVal = ParamHandler.getVal(paramBytes);
			int csvKey = -1;
			int cappedTypeCnt = typeCnt % paramTypes.length;
			if (cappedTypeCnt < paramTypes.length) {
				csvKey = CsvHandler.getCsvSearchResult(csvFiles, paramTypes[cappedTypeCnt]);
				if (csvKey >= 0) {
					if (paramNames[csvKey] == null)
						paramNames[csvKey] = CsvHandler.getParamNames(csvFiles[csvKey]);
					if (paramVal < paramNames[csvKey].length) info[typeCnt] = paramNames[csvKey][paramVal];
					else if (paramVal == 999) info[typeCnt] = "Blank";
					else info[typeCnt] = "Unknown (" + paramVal + ")";
				}
				else {
					if (paramTypes[cappedTypeCnt].equals("bool"))
						info[typeCnt] = (paramVal == 1) ? "Enabled" : "Disabled";
					else info[typeCnt] = paramVal + "";
				}
			}
			else info[typeCnt] = paramVal + "";
		}
		return info;
	}
	public static byte[] getMissionParams(RandomAccessFile[] dats, int gameModeIdx, int missionIdx, boolean enemy) throws IOException {
		missionIdx %= NUM_MISSIONS[gameModeIdx];
		int size = enemy ? 44 * NUM_ENEMIES[gameModeIdx] : BATTLE_PARAMS_SIZE[gameModeIdx];
		int selectedIdx = enemy ? 1 : 0;
		byte[] params = new byte[size];
		RandomAccessFile dat = dats[selectedIdx];
		dat.seek(missionIdx * size);
		dat.read(params);
		return params;
	}
}