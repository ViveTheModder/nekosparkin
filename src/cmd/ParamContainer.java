package cmd;
//Nekosparkin: Parameter Container class by ViveTheJoestar
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class ParamContainer {
	private boolean isListFile, toggleNeo;
	private byte[] battleParams, enemyParams;
	private int gameModeIdx;
	private File ultBatDir;
	private RandomAccessFile[] containers;
	
	public ParamContainer(File dir, int gmIdx, boolean list, boolean toggle) throws IOException {
		ultBatDir = dir;
		gameModeIdx = gmIdx;
		isListFile = list;
		toggleNeo = toggle;
		setContainers();
		initMissionParams();
	}

	public byte[] getBattleParams() {
		return battleParams;
	}
	public byte[] getBattleParams(int missionIdx) {
		int size = 0;
		if (!isListFile) size = toggleNeo ? UltBatNeo.BATTLE_PARAMS_SIZE : UltBatMeteor.BATTLE_PARAMS_SIZE[gameModeIdx];
		else size = UltBatMeteor.LIST_SIZES[gameModeIdx];
		byte[] params = new byte[size];
		System.arraycopy(battleParams, missionIdx * size, params, 0, size);
		return params;
	}
	public byte[] getEnemyParams() {
		return enemyParams;
	}
	public byte[] getEnemyParams(int missionIdx) {
		int size = toggleNeo ? UltBatNeo.NUM_ENEMIES[gameModeIdx] : UltBatMeteor.NUM_ENEMIES[gameModeIdx] * 44;
		byte[] params = new byte[size];
		System.arraycopy(enemyParams, missionIdx * size, params, 0, size);
		return params;
	}
	public RandomAccessFile[] getContainers() {
		return containers;
	}
	public void setBattleParams(byte[] params, int missionIdx) {
		int size = 0;
		if (!isListFile) size = toggleNeo ? UltBatNeo.BATTLE_PARAMS_SIZE : UltBatMeteor.BATTLE_PARAMS_SIZE[gameModeIdx];
		else size = UltBatMeteor.LIST_SIZES[gameModeIdx];
		System.arraycopy(params, 0, battleParams, missionIdx * size, params.length);
	}
	public void setEnemyParams(byte[] params, int missionIdx) {
		int size = toggleNeo ? UltBatNeo.NUM_ENEMIES[gameModeIdx] : UltBatMeteor.NUM_ENEMIES[gameModeIdx] * 44;
		System.arraycopy(params, 0, enemyParams, missionIdx * size, size);
	}
	public void writeParams() throws IOException {
		for (int fileCnt = 0; fileCnt < containers.length; fileCnt++) {
			containers[fileCnt].seek(0);
			if (fileCnt % 2 == 0) containers[fileCnt].write(battleParams);
			else containers[fileCnt].write(enemyParams);
			containers[fileCnt].close();
		}
	}
	public void writeParams(File newDir) throws IOException {
		File[] newDatFiles = new File[containers.length];
		RandomAccessFile[] newContainers = new RandomAccessFile[containers.length];
		String namePrefix = toggleNeo ? UltBatNeo.MODE_NAMES[gameModeIdx] : UltBatMeteor.MODE_NAMES[gameModeIdx];
		namePrefix = namePrefix.toLowerCase().replace(" ", "_");
		String[] nameSuffices = { "_battle_params.dat", "_enemy_params.dat" };
		for (int fileCnt = 0; fileCnt < newDatFiles.length; fileCnt++) {
			newDatFiles[fileCnt] = newDir.toPath().resolve(namePrefix + nameSuffices[fileCnt]).toFile();
			newContainers[fileCnt] = new RandomAccessFile(newDatFiles[fileCnt], "rw");
			if (fileCnt % 2 == 0) newContainers[fileCnt].write(battleParams);
			else newContainers[fileCnt].write(enemyParams);
			newContainers[fileCnt].close();
		}
	}
	private void initMissionParams() throws IOException {
		int[] datSizes = new int[containers.length];
		datSizes[0] = toggleNeo ? UltBatNeo.DAT_SIZES[2 * gameModeIdx] : UltBatMeteor.DAT_SIZES[2 * gameModeIdx];
		if (datSizes.length == 2) 
			datSizes[1] = toggleNeo ? UltBatNeo.DAT_SIZES[2 * gameModeIdx + 1] : UltBatMeteor.DAT_SIZES[2 * gameModeIdx + 1];
		for (int datCnt = 0; datCnt < datSizes.length; datCnt++) {
			byte[] params = new byte[datSizes[datCnt]];
			if (containers[datCnt] != null) containers[datCnt].read(params);
			if (datCnt % 2 != 0) enemyParams = params;
			else battleParams = params;
		}
	}
	private void setContainers() throws IOException {
		int numFiles = isListFile ? 1 : 2;
		File[] datFiles = ultBatDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".dat"));
		containers = new RandomAccessFile[numFiles];
		if (!isListFile) {
			int datSize1 = toggleNeo ? UltBatNeo.DAT_SIZES[2 * gameModeIdx] : UltBatMeteor.DAT_SIZES[2 * gameModeIdx];
			int datSize2 = toggleNeo ? UltBatNeo.DAT_SIZES[2 * gameModeIdx + 1] : UltBatMeteor.DAT_SIZES[2 * gameModeIdx + 1];
			for (int datCnt = 0; datCnt < datFiles.length; datCnt++) {
				long datFileSize = datFiles[datCnt].length();
				if (datFileSize == datSize1) 
					containers[0] = new RandomAccessFile(datFiles[datCnt], "rw");
				else if (datFileSize == datSize2)
					containers[1] = new RandomAccessFile(datFiles[datCnt], "rw");
			}
		}
		else {
			for (int datCnt = 0; datCnt < datFiles.length; datCnt++) {
				long datFileSize = datFiles[datCnt].length();
				if (datFileSize == UltBatMeteor.LIST_SIZES[gameModeIdx]) {
					containers[0] = new RandomAccessFile(datFiles[datCnt], "rw");
					break;
				}
			}
		}
	}
}