package gui;
//Nekosparkin: Mission Selector Class by ViveTheJoestar
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import cmd.CsvHandler;
import cmd.ParamHandler;
import cmd.UltBatMeteor;
/* TODO:
 * 1. Add character icons (from BT2 and BT3; retrieve them first - DONE) based on the mission's characters.
 * 2. Retrieve mission names for Survival and Course Battle - DONE.
 * 3. Make up mission names (or just disable the dropdown menu - DONE, for the launcher) for Sim Dragon and Ranking Battle - DONE.
 * 4. This is more for UltBatMeteor, but implement writing/editing mission parameters.
 * 5. Add listener to every character chip that opens up a new dialog to edit their parameters.
 * 6. Retrieve map icons (from BT2 and BT3).
 * 7. Include battle parameters in UI (not just the character icons).
 * 8. Move Save & Save As buttons (and menu items) from Launcher to Selector.
 * 9. Fix mission 75's name (in Mission 100).
 * 10. Replace "Mission No." text with "Opponent No." for Sim Dragon and Ranking Battle. */
public class Selector {
	static void start(JFrame startFrame, Dimension minFrameSize, int gameModeIdx) throws IOException {
		int[] charaIds = new int[UltBatMeteor.NUM_ENEMIES[gameModeIdx]];
		startFrame.setEnabled(false);
		int x = startFrame.getX() + startFrame.getWidth() + 10;
		int y = startFrame.getY();
		int rows = UltBatMeteor.NUM_ENEMIES[gameModeIdx] / 5;
		//Get mission names (code should be stored elsewhere)
		File[] csvArray = CsvHandler.getAvailableCsvFiles();
		int csvIndex = CsvHandler.getCsvSearchResult(csvArray, UltBatMeteor.MODE_TYPES[gameModeIdx]);
		String[] names = new String[1];
		if (csvIndex >= 0) {
			File namesCsv = csvArray[csvIndex];
			names = CsvHandler.getParamNames(namesCsv);
		}
		//Set components
		Box charaBox = Box.createHorizontalBox();
		Box spinnerBox = Box.createHorizontalBox();
		ImageIcon[] charaChips = new ImageIcon[UltBatMeteor.NUM_ENEMIES[gameModeIdx]];
		SpinnerNumberModel missionModel = new SpinnerNumberModel(1, 1, UltBatMeteor.NUM_MISSIONS[gameModeIdx], 1);
		JComboBox<String> missionDropDown = new JComboBox<String>(names);
		JSpinner missionSelect = new JSpinner(missionModel);
		JFrame editFrame = new JFrame(Launcher.TITLE + " - " + UltBatMeteor.MODE_NAMES[gameModeIdx] + " Editor");
		JLabel label = new JLabel("Mission No. ");
		JLabel[] charaLabels = new JLabel[UltBatMeteor.NUM_ENEMIES[gameModeIdx]];
		JPanel charaPanel = new JPanel(new GridLayout(rows, UltBatMeteor.NUM_ENEMIES[gameModeIdx], 1, 1));
		JPanel panel = new JPanel();
		//Set component properties
		charaPanel.setBackground(Launcher.FG_COLOR);
		label.setFont(Launcher.HEADING);
		label.setForeground(Launcher.TX_COLOR);		
		missionDropDown.setEnabled(false);
		((JLabel) missionDropDown.getRenderer()).setHorizontalAlignment(JLabel.CENTER);
		panel.setBackground(Launcher.BG_COLOR);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		//Set first-time character chips
		updateCharaImgs(charaLabels, charaChips, charaPanel, charaIds, gameModeIdx, 0);
		//Add components
		charaBox.add(Box.createHorizontalGlue());
		charaBox.add(charaPanel);
		charaBox.add(Box.createHorizontalGlue());
		spinnerBox.add(Box.createHorizontalGlue());
		spinnerBox.add(label);
		spinnerBox.add(new JLabel(" "));
		spinnerBox.add(missionSelect);
		spinnerBox.add(new JLabel(" "));
		if (csvIndex >= 0) spinnerBox.add(missionDropDown);
		spinnerBox.add(Box.createHorizontalGlue());
		panel.add(Box.createVerticalGlue());
		panel.add(spinnerBox);
		panel.add(new JLabel(" "));
		panel.add(charaBox);
		panel.add(Box.createVerticalGlue());
		editFrame.add(panel);
		//Add change listener
		missionSelect.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent ce) {
				try {
					int missionIdx = (int) missionSelect.getValue() - 1;
					if (csvIndex >= 0) missionDropDown.setSelectedIndex(missionIdx);
					updateCharaImgs(charaLabels, charaChips, charaPanel, charaIds, gameModeIdx, missionIdx);
					/* TODO: Code for debugging/testing; remove later
					String msg = "";
					String[] info = UltBatMeteor.getMissionInfo(params, true);
					for (String i: info) msg += i + "\n";
					JOptionPane.showMessageDialog(null, msg, Launcher.TITLE, JOptionPane.INFORMATION_MESSAGE); */
				} catch (IOException e) {
					e.printStackTrace();
				}
			}	
		});
		//Add window listener
		editFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				startFrame.setEnabled(true);
				editFrame.dispose();
			}
		});
		//Set frame properties
		editFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		editFrame.setIconImage(Launcher.LOGO);
		editFrame.setLocation(x, y);
		editFrame.setMinimumSize(minFrameSize);
		editFrame.setVisible(true);
	}
	static void updateCharaImgs(JLabel[] chLbs, ImageIcon[] chImgs, JPanel chPnl, int[] chIds, int gmIdx, int mIdx) throws IOException {
		byte[] params = UltBatMeteor.getMissionParams(Launcher.containers, gmIdx, mIdx, true);
		byte[] paramBytes = new byte[4];
		chPnl.removeAll();
		for (int i = 0; i < params.length; i += 4) {
			int oppId = i / 44;
			System.arraycopy(params, i, paramBytes, 0, 4);
			if (i % 44 == 0) {
				Box vertBox = Box.createVerticalBox();
				chIds[oppId] = ParamHandler.getVal(paramBytes);
				File imgFile = new File("chips/chara/bt3/" + chIds[oppId] + ".png");
				Image img = ImageIO.read(imgFile);
				chImgs[oppId] = new ImageIcon(img.getScaledInstance(64, 64, Image.SCALE_FAST));
				chLbs[oppId] = new JLabel(chImgs[oppId]);
				chLbs[oppId].setAlignmentX(JLabel.CENTER_ALIGNMENT);
				chLbs[oppId].setHorizontalAlignment(JLabel.CENTER);
				JLabel oppIdLbl = new JLabel(oppId + 1 + "");
				oppIdLbl.setForeground(Color.WHITE);
				oppIdLbl.setFont(Launcher.HEADING);
				//This was the only way to properly center the text inside the vertical box
				Box oppIdLblBox = Box.createHorizontalBox();
				oppIdLblBox.add(Box.createHorizontalGlue());
				oppIdLblBox.add(oppIdLbl);
				oppIdLblBox.add(Box.createHorizontalGlue());
				//This was the only way to properly take up the excess space from the character panel
				vertBox.add(Box.createVerticalGlue());
				vertBox.add(oppIdLblBox);
				vertBox.add(chLbs[oppId]);
				vertBox.add(Box.createVerticalGlue());
				chPnl.add(vertBox);
			}
		}
		chPnl.revalidate();
	}
}
