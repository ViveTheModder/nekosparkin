package gui;
//Nekosparkin: Audio Handler class by ViveTheJoestar
import java.awt.Toolkit;
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class AudioHandler {
	private static final String[] WAV_NAMES = { "open", "save", "error", "up", "down", "chara", "stop", "exit", "confirm" };
	public static void playAudio(int index) {
		try {
			if (index >= WAV_NAMES.length) return;
			File wav = new File("./wav/" + WAV_NAMES[index] + ".wav");
			if (!(wav.isFile() && wav.exists())) return;
			AudioInputStream ais = AudioSystem.getAudioInputStream(wav);
			Clip clip = AudioSystem.getClip();
			clip.open(ais);
			clip.start();
		}
		catch (Exception e) {
			Launcher.error(e, Toolkit.getDefaultToolkit(), true);
		}
	}
}
