package gui;
//Nekosparkin: Mission Selector Class by ViveTheJoestar
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import cmd.CsvHandler;
import cmd.ParamHandler;
import cmd.UltBatMeteor;
import cmd.UltBatNeo;
/* TODO:
 * 1. Add character icons (from BT2 and BT3; retrieve them first - DONE) based on the mission's characters - DONE.
 * 2. Retrieve mission names for Survival and Course Battle - DONE.
 * 3. Make up mission names (or just disable the dropdown menu - DONE, for the launcher) for Sim Dragon and Ranking Battle - DONE.
 * 4. This is more for UltBatMeteor, but implement writing/editing mission parameters - DONE.
 * 5. Add listener to every character chip that opens up a new dialog to edit their parameters - DONE.
 * 6. Retrieve map icons (from BT2 and BT3) - DONE.
 * 7. Include battle parameters in UI (not just the character icons) - DONE.
 * 8. Move Save & Save As buttons (and menu items) from Launcher to Selector - DONE.
 * 9. Fix mission 75's name (in Mission 100) - DONE.
 * 10. Replace "Mission No." text with "Opponent No." for Sim Dragon and Ranking Battle - DONE.
 * 11. If the selector window is closed, PLEASE set the containers back to null - DONE.
 * 12. Add listeners for the Save and Save As buttons - DONE.
 * 13. Edit saveAs() method to use the currDir variable from Launcher as the last opened directory - DONE.
 * 14. If needed, search for any instance of UltBatMeteor.X and refactor code to include final variables from UltBatNeo. - DONE
 */
public class Selector {
	static byte[] battleParams, enemyParams;
	
	private static byte[] getBattleParamsFromUI(JComboBox<String>[] dd, JCheckBox[] cb, int gmIdx, boolean bt2, boolean be) {
		int size = (bt2 ? UltBatNeo.BATTLE_PARAMS_SIZE : UltBatMeteor.BATTLE_PARAM_TYPES.length) * 4;
		byte[] battleParams = new byte[size];
		int paramVal = 0;
		for (int paramCnt = 0; paramCnt < battleParams.length / 4; paramCnt++) {
			if (paramCnt == 1 || paramCnt == 5) {
				if (cb[paramCnt / 5].isSelected()) paramVal = 1;
				else paramVal = 0;
			}
			else {
				if (paramCnt < dd.length) {
					if (dd[paramCnt] != null) {
						int prmIdx = dd[paramCnt].getSelectedIndex();
						if (paramCnt == 0 || paramCnt == 3 || paramCnt == 4) {
							int numPrmVals = dd[paramCnt].getItemCount();
							if (prmIdx == numPrmVals - 1) paramVal = 998;
						}
						paramVal = prmIdx;
					}
				}
			}
			System.arraycopy(ParamHandler.getValBytes(paramVal, be), 0, battleParams, paramCnt * 4, 4);
		}
		return battleParams;
	}
	private static void save() throws IOException {
		Launcher.container.writeParams();
	}
	private static void saveAs(int gmIdx, boolean toggleNeo) throws IOException {
		JFileChooser chooser = new JFileChooser();
		String windowTitle = "Save ";
		windowTitle += toggleNeo ? UltBatNeo.MODE_NAMES[gmIdx] : UltBatMeteor.MODE_NAMES[gmIdx];
		chooser.setCurrentDirectory(Launcher.currDir);
		chooser.setDialogTitle(windowTitle);
		chooser.setDialogType(JFileChooser.SAVE_DIALOG);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int result = chooser.showSaveDialog(null);
		if (result == JFileChooser.APPROVE_OPTION) Launcher.container.writeParams(chooser.getSelectedFile());
	}
	private static void updateBatPrmUI(JComboBox<String>[] dd, JCheckBox[] cb, JPanel mp, int gmIdx, int mIdx, boolean be) throws IOException {
		byte[] battleParams = Launcher.container.getBattleParams(mIdx);
		byte[] paramBytes = new byte[4];
		int numParams = ((UltBatMeteor.BATTLE_PARAMS_SIZE[gmIdx] - UltBatMeteor.NUM_ENEMIES[gmIdx] * 4) / 4);
		for (int byteCnt = 0; byteCnt < numParams * 4; byteCnt += 4) {
			int paramCnt = byteCnt / 4;
			System.arraycopy(battleParams, byteCnt, paramBytes, 0, 4);
			int param = ParamHandler.getVal(paramBytes, be);
			if (paramCnt == 1 || paramCnt == 5) {
				if (param == 1) cb[paramCnt / 5].setSelected(true);
				else cb[paramCnt / 5].setSelected(false);
			}
			else {
				if (dd[paramCnt] != null) {
					if (param == 998) dd[paramCnt].setSelectedIndex(dd[paramCnt].getItemCount() - 1);
					else dd[paramCnt].setSelectedIndex(param);
				}
			}
		}
		mp.revalidate();
	}
	private static void updateCharaImgs(JLabel[] lbl, ImageIcon[] ico, JPanel pnl, int[] chIds, int gm, int m, boolean bt2, boolean be)
	throws IOException {
		byte[] enemyParams = Launcher.container.getEnemyParams(m);
		byte[] paramBytes = new byte[4];
		String folderName = bt2 ? "bt2" : "bt3";
		pnl.removeAll();
		for (int i = 0; i < enemyParams.length; i += 4) {
			int oppId = i / 44;
			System.arraycopy(enemyParams, i, paramBytes, 0, 4);
			if (i % 44 == 0) {
				Box vertBox = Box.createVerticalBox();
				chIds[oppId] = ParamHandler.getVal(paramBytes, be);
				File imgFile = new File("chips/chara/" + folderName + "/" + chIds[oppId] + ".png");
				Image img = ImageIO.read(imgFile);
				ico[oppId] = new ImageIcon(img.getScaledInstance(64, 64, Image.SCALE_FAST));
				lbl[oppId].setAlignmentX(JLabel.CENTER_ALIGNMENT);
				lbl[oppId].setHorizontalAlignment(JLabel.CENTER);
				lbl[oppId].setIcon(ico[oppId]);
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
				vertBox.add(lbl[oppId]);
				vertBox.add(Box.createVerticalGlue());
				pnl.add(vertBox);
			}
		}
		pnl.revalidate();
	}
	@SuppressWarnings("unchecked")
	static void start(JFrame launch, Dimension min, Image logo, Toolkit tk, int gmIdx, boolean be, boolean bt2, boolean dbg) throws IOException {
		boolean isSrvOrRnk = gmIdx >= UltBatMeteor.SURVIVAL && gmIdx <= UltBatMeteor.CHALLENGE;
		int[] charaIds = new int[bt2 ? UltBatNeo.NUM_ENEMIES[gmIdx] : UltBatMeteor.NUM_ENEMIES[gmIdx]];
		int numMissions = bt2 ? UltBatNeo.NUM_MISSIONS[gmIdx] : UltBatMeteor.NUM_MISSIONS[gmIdx];
		int rows = charaIds.length / 5;
		battleParams = Launcher.container.getBattleParams();
		enemyParams = Launcher.container.getEnemyParams();
		launch.setEnabled(false);
		//Get mission and battle parameter names
		File[] csvArray = CsvHandler.getAvailableCsvFiles();
		File[] batPrmCsvs = new File[UltBatMeteor.BATTLE_PARAM_TYPES.length];
		for (int paramCnt = 0; paramCnt < batPrmCsvs.length; paramCnt++) {
			int searchResult = CsvHandler.getCsvSearchResult(csvArray, UltBatMeteor.BATTLE_PARAM_TYPES[paramCnt]);
			if (searchResult >= 0) batPrmCsvs[paramCnt] = csvArray[searchResult];
		}
		String modeName = bt2 ? UltBatNeo.MODE_NAMES[gmIdx] : UltBatMeteor.MODE_NAMES[gmIdx];
		String modeType = bt2 ? UltBatNeo.MODE_TYPES[gmIdx] : UltBatMeteor.MODE_TYPES[gmIdx];
		int csvIndex = CsvHandler.getCsvSearchResult(csvArray, modeType);
		String[] names = new String[1];
		String[][] batPrmNames = new String[batPrmCsvs.length][];
		for (int csvCnt = 0; csvCnt < batPrmNames.length; csvCnt++) {
			if (batPrmCsvs[csvCnt] != null) batPrmNames[csvCnt] = CsvHandler.getParamNames(batPrmCsvs[csvCnt]);
		}
		if (csvIndex >= 0) {
			File namesCsv = csvArray[csvIndex];
			names = CsvHandler.getParamNames(namesCsv);
		}
		//Set components
		Box applyBox = Box.createHorizontalBox();
		Box charaBox = Box.createHorizontalBox(), spinnerBox = Box.createHorizontalBox();
		Dimension screen = tk.getScreenSize();
		Image save = tk.getImage(ClassLoader.getSystemResource("img/save.png"));
		Image saveAs = tk.getImage(ClassLoader.getSystemResource("img/save-as.png"));
		ImageIcon saveAsIco = new ImageIcon(saveAs.getScaledInstance(32, 32, Image.SCALE_SMOOTH));
		ImageIcon saveIco = new ImageIcon(save.getScaledInstance(32, 32, Image.SCALE_SMOOTH));
		ImageIcon[] charaChips = new ImageIcon[charaIds.length];
		SpinnerNumberModel missionModel = new SpinnerNumberModel(1, 1, numMissions, 1);
		JButton apply = new JButton("Apply"), saveBtn = new JButton("Save"), saveAsBtn = new JButton("Save As");
		JComboBox<String> missionDropDown = new JComboBox<String>(names);
		JComboBox<String>[] batPrmDropDown = new JComboBox[batPrmCsvs.length];
		JCheckBox[] toggles = new JCheckBox[2];
		JSpinner missionSelect = new JSpinner(missionModel);
		JFrame editFrame = new JFrame(Launcher.TITLE + " - " + modeName + " Editor");
		JLabel label = new JLabel("Mission No. ");
		if (csvIndex < 0) label.setText("Opponent No. ");
		JLabel[] charaLabels = new JLabel[charaIds.length];
		for (int charaCnt = 0; charaCnt < charaLabels.length; charaCnt++) {
			final int index = charaCnt;
			charaLabels[charaCnt] = new JLabel();
			charaLabels[charaCnt].setCursor(new Cursor(Cursor.HAND_CURSOR));
			charaLabels[charaCnt].addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent me) {
					try {
						byte[] charaParams = new byte[44];
						int missionCnt = (int) missionSelect.getValue() - 1;
						int pos = (missionCnt * 44 * charaIds.length) + (index * 44);
						System.arraycopy(enemyParams, pos, charaParams, 0, 44);
						CharaEditor.start(editFrame, launch, charaLabels[index], tk, csvArray, charaParams, pos, be, dbg);
					} catch (IOException e) {
						Launcher.error(e, tk, dbg);
					}
				}
			});
		}
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenuItem saveItem = new JMenuItem("Save");
		JMenuItem saveAsItem = new JMenuItem("Save As...");
		JPanel charaPanel = new JPanel(new GridLayout(rows, charaIds.length, 1, 1));
		JPanel charaBoxPanel = new JPanel(), mainPanel = new JPanel(new BorderLayout());
		JPanel selectorPanel = new JPanel(new BorderLayout());
		JPanel battlePanel = new JPanel(), missionPanel = new JPanel();
		JScrollPane battleScroll = new JScrollPane(battlePanel);
		JScrollPane charaScroll = new JScrollPane(charaBoxPanel);
		JToolBar toolBar = new JToolBar();
		//Set component properties
		apply.setAlignmentX(JButton.CENTER_ALIGNMENT);
		apply.setBackground(Launcher.TX_COLOR);
		apply.setContentAreaFilled(false);
		apply.setFont(Launcher.HEADING);
		apply.setForeground(Color.WHITE);
		apply.setOpaque(true);
		battlePanel.setBackground(Launcher.BG_COLOR);
		battleScroll.setBorder(BorderFactory.createLineBorder(Launcher.BG_COLOR));
		charaPanel.setBackground(Launcher.FG_COLOR);
		charaScroll.setBorder(BorderFactory.createLineBorder(Launcher.BG_COLOR));
		label.setFont(Launcher.HEADING);
		label.setForeground(Color.WHITE);		
		missionDropDown.setEnabled(false);
		missionPanel.setBackground(Launcher.FG_COLOR);
		((JLabel) missionDropDown.getRenderer()).setHorizontalAlignment(JLabel.CENTER);
		charaBoxPanel.setBackground(Launcher.BG_COLOR);
		charaBoxPanel.setLayout(new BoxLayout(charaBoxPanel, BoxLayout.Y_AXIS));
		battlePanel.setLayout(new BoxLayout(battlePanel, BoxLayout.Y_AXIS));
		saveAsBtn.setIcon(saveAsIco);
		saveAsItem.setIcon(saveAsIco);
		saveItem.setIcon(saveIco);
		saveBtn.setIcon(saveIco);
		//Add components
		applyBox.add(Box.createHorizontalGlue());
		applyBox.add(apply);
		applyBox.add(Box.createHorizontalGlue());
		battlePanel.add(Box.createVerticalGlue());
		for (int compCnt = 0; compCnt < batPrmNames.length; compCnt++) {
			JLabel compName = new JLabel(UltBatMeteor.BATTLE_PARAM_NAMES[compCnt] + ": ");
			compName.setAlignmentX(JLabel.CENTER_ALIGNMENT);
			compName.setFont(Launcher.HEADING);
			compName.setForeground(Launcher.TX_COLOR);
			compName.setHorizontalAlignment(JLabel.CENTER);
			if (compCnt == 1 || compCnt == 5) {
				toggles[compCnt / 5] = new JCheckBox();
				toggles[compCnt / 5].setBackground(Launcher.BG_COLOR);
				Box toggleBox = Box.createHorizontalBox();
				toggleBox.add(Box.createHorizontalGlue());
				toggleBox.add(compName);
				toggleBox.add(toggles[compCnt / 5]);
				toggleBox.add(Box.createHorizontalGlue());
				battlePanel.add(toggleBox);
			}
			else {
				//Only add Condition and DP Points combo boxes if the gamemode needs them
				if (isSrvOrRnk && (compCnt == 6 || compCnt == 7)) continue;
				batPrmDropDown[compCnt] = new JComboBox<String>(batPrmNames[compCnt]);
				batPrmDropDown[compCnt].setAlignmentX(JComboBox.CENTER_ALIGNMENT);
				((JLabel) batPrmDropDown[compCnt].getRenderer()).setHorizontalAlignment(JLabel.CENTER);
				Box dropDownBox = Box.createHorizontalBox();
				dropDownBox.add(Box.createHorizontalGlue());
				dropDownBox.add(compName);
				dropDownBox.add(batPrmDropDown[compCnt]);
				dropDownBox.add(Box.createHorizontalGlue());
				battlePanel.add(dropDownBox);
			}
			battlePanel.add(new JLabel(" "));
		}
		battlePanel.add(applyBox);
		battlePanel.add(new JLabel(" "));
		battlePanel.add(Box.createVerticalGlue());
		charaBox.add(Box.createHorizontalGlue());
		charaBox.add(charaPanel);
		charaBox.add(Box.createHorizontalGlue());
		fileMenu.add(saveItem);
		fileMenu.add(saveAsItem);
		menuBar.add(fileMenu);
		spinnerBox.add(Box.createHorizontalGlue());
		spinnerBox.add(label);
		spinnerBox.add(new JLabel(" "));
		spinnerBox.add(missionSelect);
		spinnerBox.add(new JLabel(" "));
		//Only add combo box with mission names if mission names are present
		if (csvIndex >= 0) spinnerBox.add(missionDropDown);
		spinnerBox.add(Box.createHorizontalGlue());
		charaBoxPanel.add(Box.createVerticalGlue());
		charaBoxPanel.add(spinnerBox);
		charaBoxPanel.add(new JLabel(" "));
		charaBoxPanel.add(charaBox);
		charaBoxPanel.add(Box.createVerticalGlue());
		mainPanel.add(toolBar, BorderLayout.PAGE_START);
		mainPanel.add(selectorPanel, BorderLayout.CENTER);
		missionPanel.add(spinnerBox);
		selectorPanel.add(missionPanel, BorderLayout.NORTH);
		selectorPanel.add(battleScroll, BorderLayout.EAST);
		selectorPanel.add(charaScroll, BorderLayout.CENTER);
		toolBar.add(saveBtn);
		toolBar.add(saveAsBtn);
		editFrame.add(mainPanel);
		//Add change listener
		missionSelect.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent ce) {
				try {
					int missionIdx = (int) missionSelect.getValue() - 1;
					if (csvIndex >= 0) missionDropDown.setSelectedIndex(missionIdx);
					updateBatPrmUI(batPrmDropDown, toggles, missionPanel, gmIdx, missionIdx, be);
					updateCharaImgs(charaLabels, charaChips, charaPanel, charaIds, gmIdx, missionIdx, bt2, be);
				}
				catch (IOException e) {
					Launcher.error(e, tk, dbg);
				}
			}	
		});
		//Add action listeners
		apply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				int missionIdx = (int) missionSelect.getValue() - 1;
				byte[] currBatPrms = getBattleParamsFromUI(batPrmDropDown, toggles, gmIdx, bt2, be);
				Launcher.container.setBattleParams(currBatPrms, missionIdx);
			}
		});
		saveAsBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					byte[] currBatPrms = getBattleParamsFromUI(batPrmDropDown, toggles, gmIdx, bt2, be);
					Launcher.container.setBattleParams(currBatPrms, (int) missionSelect.getValue() - 1);
					saveAs(gmIdx, bt2);
				} catch (IOException e) {
					Launcher.error(e, tk, dbg);
				}
			}
		});
		saveBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					byte[] currBatPrms = getBattleParamsFromUI(batPrmDropDown, toggles, gmIdx, bt2, be);
					Launcher.container.setBattleParams(currBatPrms, (int) missionSelect.getValue() - 1);
					save();
				} catch (IOException e) {
					Launcher.error(e, tk, dbg);
				}
			}
		});
		//Add window listener
		editFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				launch.setEnabled(true);
				editFrame.dispose();
			}
		});
		//Set first-time character chips
		updateBatPrmUI(batPrmDropDown, toggles, missionPanel, gmIdx, 0, be);
		updateCharaImgs(charaLabels, charaChips, charaPanel, charaIds, gmIdx, 0, bt2, be);
		//Set frame properties
		editFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		editFrame.setIconImage(logo);
		editFrame.setJMenuBar(menuBar);
		editFrame.setMinimumSize(min);
		editFrame.setVisible(true);
		//Adjust position after rendering frame
		int height = editFrame.getHeight();
		int width = editFrame.getWidth();
		int x = launch.getX() + launch.getWidth() + 10;
		int y = launch.getY();
		if (y + height >= screen.height) y = screen.height - ((int) (height * 1.2));
		if (x + width + 10 >= screen.width) x = screen.width - width - 10;
		editFrame.setLocation(x, y);
	}
}