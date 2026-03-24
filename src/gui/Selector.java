package gui;
//Nekosparkin: Mission Selector Class by ViveTheJoestar
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import cmd.CsvHandler;
import cmd.UltBatMeteor;
/* TODO:
 * 1. Add character icons (from BT2 and BT3; retrieve them first) based on the mission's characters.
 * 2. Retrieve mission names for Survival and Course Battle.
 * 3. Make up mission names (or just disable the dropdown menu) for Sim Dragon and Ranking Battle.
 * 4. This is more for UltBatMeteor, but implement writing/editing mission parameters. */
public class Selector {
	static void start(JFrame startFrame, Dimension minFrameSize, int gameModeIdx) throws IOException {
		startFrame.setEnabled(false);
		int x = startFrame.getX() + startFrame.getWidth() + 10;
		int y = startFrame.getY();
		//Get mission names (only works for Mission 100 at the moment; code should be stored elsewhere)
		File[] csvArray = CsvHandler.getAvailableCsvFiles();
		File namesCsv = csvArray[CsvHandler.getCsvSearchResult(csvArray, "m100")];
		String[] names = CsvHandler.getParamNames(namesCsv);
		//Set components
		Box spinnerBox = Box.createHorizontalBox();
		SpinnerNumberModel missionModel = new SpinnerNumberModel(1, 1, UltBatMeteor.NUM_MISSIONS[gameModeIdx], 1);
		JComboBox<String> missionDropDown = new JComboBox<String>(names);
		JSpinner missionSelect = new JSpinner(missionModel);
		JFrame editFrame = new JFrame(Launcher.TITLE + " - " + UltBatMeteor.MODE_NAMES[gameModeIdx] + " Editor");
		JLabel label = new JLabel("Mission No. ");
		JPanel panel = new JPanel();
		//Set component properties
		label.setFont(Launcher.HEADING);
		label.setForeground(Launcher.TX_COLOR);
		missionDropDown.setEnabled(false);
		panel.setBackground(Launcher.BG_COLOR);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		//Add components
		spinnerBox.add(Box.createHorizontalGlue());
		spinnerBox.add(label);
		spinnerBox.add(new JLabel(" "));
		spinnerBox.add(missionSelect);
		spinnerBox.add(new JLabel(" "));
		spinnerBox.add(missionDropDown);
		spinnerBox.add(Box.createHorizontalGlue());
		panel.add(Box.createVerticalGlue());
		panel.add(spinnerBox);
		panel.add(Box.createVerticalGlue());
		editFrame.add(panel);
		//Add change listener
		missionSelect.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent ce) {
				try {
					int missionIdx = (int) missionSelect.getValue() - 1;
					missionDropDown.setSelectedIndex(missionIdx);
					byte[] params = UltBatMeteor.getMissionParams(Launcher.containers, gameModeIdx, missionIdx, true);
					String msg = "";
					String[] info = UltBatMeteor.getMissionInfo(params, true);
					for (String i: info) msg += i + "\n";
					JOptionPane.showMessageDialog(null, msg, Launcher.TITLE, JOptionPane.INFORMATION_MESSAGE);
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
}
