package gui;
import java.awt.Color;
//Nekosparkin: Character Editor class by ViveTheJoestar
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import cmd.CsvHandler;
import cmd.ParamHandler;
/* TODO:
 * 1. Add window listener (to unlock the selector frame) when the editor is closed - DONE. 
 * 2. Finish (and test) the UI (in terms of appearance and functionality) - DONE.
 * 3. Finish the action listener for the Apply button (make sure it carries over changes to static enemyParams array). */
public class CharaEditor {
	public static int[] getComboBoxAndSpinVals(JComboBox<String> charaCb, JComboBox<String>[] itemCbs, JSpinner[] spins) {
		int[] ints = new int[11];
		int charaId = charaCb.getSelectedIndex(), numChars = charaCb.getItemCount();
		if (charaId == numChars - 1) charaId = 999;
		else if (charaId == numChars - 2) charaId = 998;
		ints[0] = charaId;
		ints[1] = (int) spins[0].getValue() - 1;
		ints[2] = (int) spins[1].getValue();
		int numItems = itemCbs[0].getItemCount();
		for (int itemCnt = 0; itemCnt < itemCbs.length; itemCnt++) {
			int itemId = itemCbs[itemCnt].getSelectedIndex();
			if (itemId == numItems - 1) ints[itemCnt + 3] = 999;
			else ints[itemCnt + 3] = itemId;
		}
		return ints;
	}
	private static void setComboBoxAndSpinVals(JComboBox<String> charaCb, JComboBox<String>[] itemCbs, JSpinner[] spins, int[] ints) {
		int numChars = charaCb.getItemCount();
		if (ints[0] == 999) charaCb.setSelectedIndex(numChars - 1);
		else if (ints[0] == 998) charaCb.setSelectedIndex(numChars - 2);
		else charaCb.setSelectedIndex(ints[0]);
		spins[0].setValue(ints[1] + 1);
		spins[1].setValue(ints[2]);
		for (int itemCnt = 0; itemCnt < itemCbs.length; itemCnt++) {
			int itemId = ints[itemCnt + 3];
			if (itemId != 999) itemCbs[itemCnt].setSelectedIndex(ints[itemCnt + 3]);
			else itemCbs[itemCnt].setSelectedIndex(itemCbs[itemCnt].getItemCount() - 1);
		}
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void start(JFrame selectorFrame, JFrame startFrame, File[] csvArray, byte[] prm, boolean be) throws IOException {
		selectorFrame.setEnabled(false);
		byte[] paramBytes = new byte[4];
		int searchResult = CsvHandler.getCsvSearchResult(csvArray, "chara");
		int[] enemyParamInts = new int[prm.length / 4];
		for (int i = 0; i < enemyParamInts.length; i++) {
			System.arraycopy(prm, i * 4, paramBytes, 0, 4);
			enemyParamInts[i] = ParamHandler.getVal(paramBytes, false);
		}
		String[] charaNames = CsvHandler.getParamNames(csvArray[searchResult]);
		String[] generalNames = { "Costume: ", "COM Level: " };
		String[] headingNames = { "General: ", "Z-Items: " };
		searchResult = CsvHandler.getCsvSearchResult(csvArray, "item");
		String[] itemNames = CsvHandler.getParamNames(csvArray[searchResult]);
		//Set components
		Box charaBox = Box.createHorizontalBox();
		Box[] spinnerBoxes = new Box[2];
		Box[] itemBoxes = new Box[8];
		Dimension minFrameSize = new Dimension(350, 500);
		JButton apply = new JButton("Apply");
		JComboBox<String> charaDropDown = new JComboBox<String>(charaNames);
		JComboBox[] itemDropDowns = new JComboBox[8];
		for (int i = 0; i < itemDropDowns.length; i++) {
			itemDropDowns[i] = new JComboBox<String>(itemNames);
			itemDropDowns[i].setFont(Launcher.SUBHEADING);
			((JLabel) itemDropDowns[i].getRenderer()).setHorizontalAlignment(JLabel.CENTER);
		}
		JFrame frame = new JFrame();
		JLabel imgLabel = new JLabel(" ");
		JLabel[] generalLbls = new JLabel[2], headingLbls = new JLabel[2];
		JPanel panel = new JPanel();
		//The actual maximum is unknown (whether it's 255 or 32767)
		SpinnerNumberModel comLvlMdl = new SpinnerNumberModel(1, 1, 255, 1);
		SpinnerNumberModel costumeMdl = new SpinnerNumberModel(1, 1, 4, 1);
		JSpinner[] spinners = new JSpinner[2];
		spinners[0] = new JSpinner(comLvlMdl);
		spinners[1] = new JSpinner(costumeMdl);
		setComboBoxAndSpinVals(charaDropDown, itemDropDowns, spinners, enemyParamInts);
		//Set component properties
		for (int boxCnt = 0; boxCnt < spinnerBoxes.length; boxCnt++) {
			generalLbls[boxCnt] = new JLabel(generalNames[boxCnt]);
			generalLbls[boxCnt].setAlignmentX(JLabel.CENTER_ALIGNMENT);
			generalLbls[boxCnt].setFont(Launcher.HEADING);
			generalLbls[boxCnt].setForeground(Color.WHITE);
			generalLbls[boxCnt].setHorizontalAlignment(JLabel.CENTER);
			spinners[boxCnt].setAlignmentX(JSpinner.CENTER_ALIGNMENT);
			spinnerBoxes[boxCnt] = Box.createHorizontalBox();
			spinnerBoxes[boxCnt].add(Box.createHorizontalGlue());
			spinnerBoxes[boxCnt].add(generalLbls[boxCnt]);
			spinnerBoxes[boxCnt].add(new JLabel(" "));
			spinnerBoxes[boxCnt].add(spinners[boxCnt]);
			spinnerBoxes[boxCnt].add(Box.createHorizontalGlue());
		}
		for (int boxCnt = 0; boxCnt < itemBoxes.length; boxCnt++) {
			itemBoxes[boxCnt] = Box.createHorizontalBox();
			itemBoxes[boxCnt].add(Box.createHorizontalGlue());
			itemBoxes[boxCnt].add(itemDropDowns[boxCnt]);
			itemBoxes[boxCnt].add(Box.createHorizontalGlue());
		}
		for (int lblCnt = 0; lblCnt < headingNames.length; lblCnt++) {
			headingLbls[lblCnt] = new JLabel(headingNames[lblCnt]);
			headingLbls[lblCnt].setAlignmentX(JLabel.CENTER_ALIGNMENT);
			headingLbls[lblCnt].setFont(Launcher.HEADING);
			headingLbls[lblCnt].setForeground(Launcher.BG_COLOR);
			headingLbls[lblCnt].setHorizontalAlignment(JLabel.CENTER);
		}
		apply.setAlignmentX(JButton.CENTER_ALIGNMENT);
		apply.setBackground(Launcher.BG_COLOR);
		apply.setContentAreaFilled(false);
		apply.setFont(Launcher.HEADING);
		apply.setForeground(Launcher.TX_COLOR);
		apply.setOpaque(true);
		panel.setBackground(Launcher.FG_COLOR);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		//Add components
		charaBox.add(Box.createHorizontalGlue());
		charaBox.add(imgLabel);
		charaBox.add(charaDropDown);
		charaBox.add(Box.createHorizontalGlue());
		panel.add(Box.createVerticalGlue());
		panel.add(headingLbls[0]);
		panel.add(charaBox);
		panel.add(spinnerBoxes[0]);
		panel.add(spinnerBoxes[1]);
		panel.add(new JLabel(" "));
		panel.add(headingLbls[1]);
		for (int i = 0; i < itemBoxes.length; i++) panel.add(itemBoxes[i]);
		panel.add(new JLabel(" "));
		panel.add(apply);
		panel.add(Box.createVerticalGlue());
		frame.add(panel);
		//Add action listener
		apply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				
			}
		});
		//Add window listener
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				selectorFrame.setEnabled(true);
				frame.dispose();
			}
		});
		//Set frame properties
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setSize(minFrameSize);
		int x = startFrame.getX() - frame.getWidth() - 10;
		int y = selectorFrame.getY();
		frame.setLocation(x, y);
		frame.setTitle("Edit Character");
		frame.setVisible(true);
	}
}