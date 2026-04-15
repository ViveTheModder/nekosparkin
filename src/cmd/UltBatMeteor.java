package cmd;
//Nekosparkin: Ultimate Battle (BT3) Class by ViveTheJoestar

public class UltBatMeteor {
	public static final int SURVIVAL = 2;
	public static final int CHALLENGE = 4;
	public static final int[] BATTLE_PARAMS_SIZE = { 28, 52, 224, 28, 28, 56 };
	public static final int[] DAT_SIZES = { 256, 320, 5248, 22016, 704, 6656, 2816, 4416, 1088, 1664, 320, 1792 };
	public static final int[] LIST_SIZES = { 64, 128, 448 };
	public static final int[] NUM_ENEMIES = { 1, 5, 50, 1, 1, 8 };
	public static final int[] NUM_MISSIONS = { 7, 100, 3, 99, 37, 5 };
	public static final String[] BATTLE_PARAM_NAMES = {
		"Referee", "Map Destruction", "Time", "Map", "BGM", "COM Transformations/Switches", "Condition", "DP Points"
	};
	//The rest of the parameters are assumed to be opponent IDs (integers), so no need to specify
	public static final String[] BATTLE_PARAM_TYPES = {
		"ref", "bool", "time", "map", "bgm", "bool", "cond", "dp"
	};
	public static final String[] MODE_NAMES = {
		"Sim Dragon", "Mission 100", "Survival", "Ranking Battle", "Ranking Battle (Challengers)", "Circuit Battle",
		"Ranking Battle (BGM List)", "Ranking Battle (Map List)", "Sim Dragon / Survival (Random Character List)"
	};
	public static final String[] MODE_TYPES = {
		"", "m100", "srv", "", "", "crs", "", "", ""
	};
}