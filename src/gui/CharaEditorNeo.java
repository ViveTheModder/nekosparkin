package gui;
//Nekosparkin: Character Editor (BT2) class by ViveTheJoestar
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import cmd.CsvHandler;
import cmd.ParamHandler;
import cmd.UltBatNeo;

public class CharaEditorNeo {
	private static void setUnkBinary35() throws IOException {
		File ultBatRoot = Launcher.container.getUltBatDir().toPath().getParent().toFile();
		File unkFolder4 = null, unkBinary35 = null;
		File[] ultBatFolders = ultBatRoot.listFiles((dir, name) -> dir.isDirectory());
		Arrays.sort(ultBatFolders);
		for (File folder: ultBatFolders) {
			if (folder.getName().startsWith("04_")) {
				unkFolder4 = folder;
				break;
			}
		}
		if (unkFolder4 == null) return;
		unkBinary35 = unkFolder4.toPath().resolve("35_.dat").toFile();
		if (unkBinary35 == null) return;
		Launcher.unkBinary35 = new RandomAccessFile(unkBinary35, "rw");
	}
	static void writeToUnkBinary35(int charaId, boolean be) throws IOException {
		Launcher.unkBinary35.seek(Launcher.charaIdPosInBinary);
		Launcher.unkBinary35.write(ParamHandler.getValBytes(charaId, be));
	}
	private static int getCharaIdPosInBin(int gameModeIdx, int missionCnt, int charaIdx) throws IOException {
		if (charaIdx == 255) return -1;		int[] missionPos = { 4, 1084, 1684, 2644, 3844, 4564, 5284, 6724, 7564, 8404, 9244, 9844, 10564, 11764, 12724 };
		int[] numChars = { 8, 5, 7, 10, 6, 5, 10, 6, 7, 6, 5, 6, 10, 7, 1 };
		int pos = 0;
		if (gameModeIdx == UltBatNeo.COURSE) {
			if (charaIdx > 9) {
				//Adjust character index for the last 5 characters of a tag mission (5 fights with 2 opponents each)
				charaIdx -= 10; //14 becomes 4, 13 becomes 3, and so on...
				charaIdx *= 2; 
			}
			else {
				//Only adjust character index for the first 5 characters of a tag mission
				if (numChars[missionCnt] == 10) charaIdx += charaIdx + 1;
			}
			//In the 35_.dat file, the characters are ordered in reverse (e.g. 5,15,4,14,3,13,2,12,1,11).
			pos = missionPos[missionCnt] + (numChars[missionCnt] - 1 - charaIdx) * 120;
		}
		else if (gameModeIdx == UltBatNeo.CHALLENGE)
			pos = missionPos[missionCnt] + (numChars[missionCnt] + charaIdx) * 120;
		return pos;
	}
	public static void start(JFrame selF, JFrame staF, JLabel chip, Toolkit tk, File[] csvs, byte[] prm, int pos, int mCnt, int idx, int gm, boolean be, boolean dbg)
	throws IOException {
		selF.setEnabled(false);
		byte[] paramBytes = new byte[4];
		int searchResult = CsvHandler.getCsvSearchResult(csvs, "chara", true);
		int[] enemyParamInts = new int[prm.length / 4];
		for (int i = 0; i < enemyParamInts.length; i++) {
			System.arraycopy(prm, i * 4, paramBytes, 0, 4);
			enemyParamInts[i] = ParamHandler.getVal(paramBytes, be);
		}
		String[] charaNames = CsvHandler.getParamNames(csvs[searchResult]);
		//Set components
		Box charaBox = Box.createHorizontalBox();
		Dimension minFrameSize = null;
		if (gm == UltBatNeo.CHALLENGE) minFrameSize = new Dimension(350, 700); 
		else minFrameSize = new Dimension(350, 150);
		JButton apply = new JButton("Apply");
		JComboBox<String> charaDropDown = new JComboBox<String>(charaNames);
		JFrame frame = new JFrame();
		JPanel panel = new JPanel();
		JSpinner[] prmSpins = new JSpinner[enemyParamInts.length - UltBatNeo.NUM_ENEMIES[gm]];
		Box[] prmSpinBoxes = new Box[prmSpins.length];
		int numChars = charaDropDown.getItemCount();
		if (gm == UltBatNeo.CHALLENGE) {
			for (int i = 0; i < prmSpins.length; i++) {
				JLabel prmSpinLbl = new JLabel("Parameter " + i + ": ");
				prmSpinLbl.setAlignmentX(JLabel.CENTER_ALIGNMENT);
				prmSpinLbl.setFont(Launcher.HEADING);
				prmSpinLbl.setForeground(Launcher.BG_COLOR);
				prmSpinLbl.setHorizontalAlignment(JLabel.CENTER);			
				SpinnerNumberModel prmModel = new SpinnerNumberModel(0, 0, 65535, 1);
				prmSpins[i] = new JSpinner(prmModel);
				prmSpins[i].setValue(enemyParamInts[i + UltBatNeo.NUM_ENEMIES[gm]]);
				prmSpinBoxes[i] = Box.createHorizontalBox();
				prmSpinBoxes[i].add(Box.createHorizontalGlue());
				prmSpinBoxes[i].add(prmSpinLbl);
				prmSpinBoxes[i].add(prmSpins[i]);
				prmSpinBoxes[i].add(Box.createHorizontalGlue());
			}
		}
		//Set component properties
		apply.setAlignmentX(JButton.CENTER_ALIGNMENT);
		apply.setBackground(Launcher.BG_COLOR);
		apply.setContentAreaFilled(false);
		apply.setFont(Launcher.HEADING);
		apply.setForeground(Launcher.TX_COLOR);
		apply.setOpaque(true);
		((JLabel) charaDropDown.getRenderer()).setHorizontalAlignment(JLabel.CENTER);
		if (enemyParamInts[idx] > 0) charaDropDown.setSelectedIndex(enemyParamInts[idx]);
		else charaDropDown.setSelectedIndex(numChars - 1);
		panel.setBackground(Launcher.FG_COLOR);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		//Add components
		charaBox.add(Box.createHorizontalGlue());
		charaBox.add(charaDropDown);
		charaBox.add(Box.createHorizontalGlue());
		panel.add(Box.createVerticalGlue());
		panel.add(new JLabel(" "));
		panel.add(charaBox);
		if (gm == UltBatNeo.CHALLENGE) {
			for (int i = 0; i < prmSpins.length; i++) {
				panel.add(new JLabel(" "));
				panel.add(prmSpinBoxes[i]);
			}
		}
		panel.add(new JLabel(" "));
		panel.add(apply);
		panel.add(Box.createVerticalGlue());
		frame.add(panel);
		//Add action listener
		apply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					int[] valArray = new int[enemyParamInts.length];
					//Just to be 100% certain, paste the existing character IDs before overwriting
					System.arraycopy(enemyParamInts, 0, valArray, 0, UltBatNeo.NUM_ENEMIES[gm]);
					valArray[idx] = charaDropDown.getSelectedIndex();
					if (valArray[idx] == numChars - 1) valArray[idx] = 255;
					File imgFile = new File("chips/chara/bt2/" + valArray[idx] + ".png");
					Image img = ImageIO.read(imgFile);
					ImageIcon ico = new ImageIcon(img.getScaledInstance(64, 64, Image.SCALE_FAST));
					chip.setIcon(ico);
					if (gm == UltBatNeo.CHALLENGE) {
						for (int spinCnt = 0; spinCnt < prmSpins.length; spinCnt++)
							valArray[spinCnt + UltBatNeo.NUM_ENEMIES[gm]] = (int) prmSpins[spinCnt].getValue();
					}
					for (int valCnt = 0; valCnt < valArray.length; valCnt++) {
						if (valArray[valCnt] == 255) valArray[valCnt] = -1;
						byte[] valBytes = ParamHandler.getValBytes(valArray[valCnt], be);
						System.arraycopy(valBytes, 0, Selector.enemyParams, pos + (valCnt * 4), 4);
					}
					setUnkBinary35();
					Launcher.charaIdPosInBinary = getCharaIdPosInBin(gm, mCnt, idx);
					Launcher.charaIdInBinary = valArray[idx];
					selF.setEnabled(true);
					frame.dispose();
				}
				catch (Exception e) {
					Launcher.error(e, tk, dbg);
				}
			}
		});
		//Add window listener
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				selF.setEnabled(true);
				frame.dispose();
			}
		});
		//Set frame properties
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setSize(minFrameSize);
		frame.setTitle("Edit Character");
		frame.setVisible(true);
		//Adjust position after rendering frame
		int height = frame.getHeight(), screenHeight = tk.getScreenSize().height;
		int x = staF.getX() - frame.getWidth() -10;
		int y = staF.getY();
		if (y + height >= screenHeight) y = screenHeight - ((int) (height * 1.2));
		if (x < 10) x = 10;
		frame.setLocation(x, y);
	}
}