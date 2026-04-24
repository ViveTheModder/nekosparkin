package gui;
//Nekosparkin: GUI Launcher Class by ViveTheJoestar
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import cmd.ParamContainer;
import cmd.UltBatMeteor;
import cmd.UltBatNeo;

public class Launcher { 
	static File currDir = null;
	static ParamContainer container = null;
	static final String TITLE = "Nekosparkin";
	static final Color BG_COLOR = new Color(138, 208, 242);
	static final Color FG_COLOR = new Color(7, 129, 163);
	static final Color TX_COLOR = new Color(193, 34, 100);
	static final Font HEADING = new Font("Tahoma", Font.BOLD, 16);
	static final Font SUBHEADING = new Font("Tahoma", Font.PLAIN, 13);
	
	private static ParamContainer getParamContainerFromChooser(JComboBox<String> dd, Toolkit tk, int gmIdx, boolean neo, boolean lf)
	throws IOException {
		ParamContainer pc = null;
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Open Folder with " + dd.getSelectedItem() + " parameters...");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (currDir != null) chooser.setCurrentDirectory(currDir);
		int result = chooser.showOpenDialog(null);
		if (result == JFileChooser.APPROVE_OPTION) {
			File tmp = chooser.getSelectedFile();
			if (tmp.isDirectory()) currDir = tmp;
			else {
				error("Chosen directory does not point to a folder!", tk);
				return pc;
			}
			pc = new ParamContainer(currDir, gmIdx, lf, neo);
			RandomAccessFile[] containers = pc.getContainers();
			if (containers[0] == null) {
				String msg = "Chosen folder contains no valid " + dd.getSelectedItem() + " parameters!";
				error(msg, tk);
			}
		}
		return pc;
	}
	private static void errorBeep(Toolkit tk) {
		Runnable runWinErrorSnd = (Runnable) tk.getDesktopProperty("win.sound.exclamation");
		if (runWinErrorSnd!=null) runWinErrorSnd.run();
	}
	private static void open(JComboBox<String> modeDropDown, JPanel panel, Toolkit tk, boolean neo, boolean isListFile, boolean debug) {
		try {
			int gameModeIdx = modeDropDown.getSelectedIndex();
			if (isListFile) gameModeIdx -= UltBatMeteor.NUM_MISSIONS.length;
			((JLabel) modeDropDown.getRenderer()).setHorizontalAlignment(JLabel.CENTER);
			panel.repaint();
			container = getParamContainerFromChooser(modeDropDown, tk, gameModeIdx, neo, isListFile);
			((JLabel) modeDropDown.getRenderer()).setHorizontalAlignment(JLabel.CENTER);
			panel.repaint();
		} catch (Exception e) {
			((JLabel) modeDropDown.getRenderer()).setHorizontalAlignment(JLabel.CENTER);
			error(e, tk, debug);
		}
	}
	private static void start(Toolkit tk, boolean debug) {
		//Tired of using static variables, sorry...
		final boolean[] toggles = new boolean[2], isListFile = new boolean[1];
		Image logo = tk.getImage(ClassLoader.getSystemResource("img/logo.png"));
		Image open = tk.getImage(ClassLoader.getSystemResource("img/open.png"));
		ImageIcon logoIco = new ImageIcon(logo.getScaledInstance(64, 64, Image.SCALE_SMOOTH));
		String[] imgNames = {"bt3.png", "bt2.png", "ps2.png", "wii.png"};
		//Set components
		Box modeBox = Box.createHorizontalBox();
		Dimension minFrameSize = new Dimension(700, 400);
		ImageIcon openIco = new ImageIcon(open.getScaledInstance(32, 32, Image.SCALE_SMOOTH));
		JToggleButton[] toggleBtns = new JToggleButton[2];
		Image[] toggleImgs = new Image[toggleBtns.length * 2];
		ImageIcon[] toggleIcos = new ImageIcon[toggleBtns.length * 2];
		for (int icoCnt = 0; icoCnt < toggleIcos.length; icoCnt++) {
			toggleImgs[icoCnt] = tk.getImage(ClassLoader.getSystemResource("img/" + imgNames[icoCnt]));
			toggleIcos[icoCnt] = new ImageIcon(toggleImgs[icoCnt].getScaledInstance(48, 48, Image.SCALE_SMOOTH));
			if (icoCnt % 2 == 0) toggleBtns[icoCnt / 2] = new JToggleButton(toggleIcos[icoCnt]);
		}
		JButton editBtn = new JButton("Edit");
		JButton openBtn = new JButton("Open Folder...");
		JComboBox<String> modeDropDown = new JComboBox<String>(UltBatMeteor.MODE_NAMES);
		JFrame frame = new JFrame(TITLE);
		JLabel label = new JLabel("Select Mode from Ultimate Battle:");
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenu helpMenu = new JMenu("Help");
		JMenuItem aboutItem = new JMenuItem("About");
		JMenuItem openItem = new JMenuItem("Open Folder...");
		JPanel mainPanel = new JPanel(new BorderLayout());
		JPanel modePanel = new JPanel();
		JToolBar toolBar = new JToolBar();
		KeyStroke openShortcut = KeyStroke.getKeyStroke('O', KeyEvent.CTRL_DOWN_MASK);
		//Set components
		editBtn.setAlignmentX(JButton.CENTER_ALIGNMENT);
		editBtn.setBackground(FG_COLOR);
		editBtn.setForeground(Color.WHITE);
		editBtn.setContentAreaFilled(false);
		editBtn.setOpaque(true);
		editBtn.setFont(HEADING);
		label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		label.setFont(HEADING);
		label.setForeground(TX_COLOR);
		label.setHorizontalAlignment(JLabel.CENTER);
		modeDropDown.setFont(SUBHEADING);
		((JLabel) modeDropDown.getRenderer()).setHorizontalAlignment(JLabel.CENTER);
		modePanel.setBackground(BG_COLOR);
		modePanel.setLayout(new BoxLayout(modePanel, BoxLayout.Y_AXIS));
		openBtn.setIcon(openIco);
		openItem.setAccelerator(openShortcut);
		openItem.setIcon(openIco);
		toolBar.addSeparator();
		toolBar.setFloatable(false);
		//Add components
		mainPanel.add(toolBar, BorderLayout.PAGE_START);
		mainPanel.add(modePanel, BorderLayout.CENTER);
		modeBox.add(Box.createHorizontalGlue());
		modeBox.add(modeDropDown);
		modeBox.add(Box.createHorizontalGlue());
		modePanel.add(Box.createVerticalGlue());
		modePanel.add(label);
		modePanel.add(new JLabel(" "));
		modePanel.add(modeBox);
		modePanel.add(new JLabel(" "));
		modePanel.add(editBtn);
		modePanel.add(Box.createVerticalGlue());
		toolBar.add(openBtn);
		toolBar.add(Box.createHorizontalGlue());
		toolBar.add(toggleBtns[0]);
		toolBar.add(toggleBtns[1]);
		frame.add(mainPanel);
		fileMenu.add(openItem);
		helpMenu.add(aboutItem);
		menuBar.add(fileMenu);
		menuBar.add(helpMenu);
		//Add listeners
		aboutItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				String msg = "Program made by ViveTheJoestar\nIcon made by SpideyGirl";
				JOptionPane.showMessageDialog(frame, msg, TITLE, JOptionPane.INFORMATION_MESSAGE, logoIco);
			}
		});
		editBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					String modeName = (String) modeDropDown.getSelectedItem();
					if (container == null)
						error("Please choose a folder containing " + modeName + " files!", tk);
					else {
						RandomAccessFile[] datFiles = container.getContainers();
						if (datFiles != null) {
							if (datFiles[0] != null) {
								int idx = modeDropDown.getSelectedIndex();
								if (!isListFile[0])
									Selector.start(frame, minFrameSize, logo, tk, idx, toggles[1], toggles[0], debug);
								else
									ParamListEditor.start(frame, modeName, minFrameSize, logo, tk, idx, toggles[1], toggles[0], debug);
							}
						}
					}
				}
				catch (Exception e) {
					error(e, tk, debug);
				}
			}
		});
		for (int toggleCnt = 0; toggleCnt < toggles.length; toggleCnt++) {
			final int index = toggleCnt;
			toggleBtns[toggleCnt].addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent ie) {
					int state = ie.getStateChange();
					if (state == ItemEvent.SELECTED) {
						toggles[index] = !toggles[index]; //What actually causes the toggle to work properly
						modeDropDown.removeAllItems();
						if (toggles[index]) {
							toggleBtns[index].setIcon(toggleIcos[toggles.length * index + 1]);
							for (String i: UltBatNeo.MODE_NAMES) modeDropDown.addItem(i);
						}
						else {
							toggleBtns[index].setIcon(toggleIcos[toggles.length * index]);
							for (String i: UltBatMeteor.MODE_NAMES) modeDropDown.addItem(i);
						}
					}
				}
			});
		}
		modeDropDown.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent ae) {	
					try {
						int gameModeIdx = modeDropDown.getSelectedIndex();
						/* When switching from BT3 to BT2, the number of gamemodes changes,
						 * which could cause index out of range exceptions. To fix this,
						 * select the first game mode by default. */
						if (gameModeIdx == -1) gameModeIdx = 0;
						String gameModeName = ((String) modeDropDown.getSelectedItem());
						if (gameModeName != null) isListFile[0] = gameModeName.contains("List");
						if (isListFile[0]) gameModeIdx -= UltBatMeteor.NUM_MISSIONS.length;
						if (currDir != null)
							container = new ParamContainer(currDir, gameModeIdx, isListFile[0], toggles[0]);
					} catch (Exception e) {
						error(e, tk, debug);
					}
			}
		});
		openBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				isListFile[0] = ((String) modeDropDown.getSelectedItem()).contains("List");
				open(modeDropDown, modePanel, tk, toggles[0], isListFile[0], debug);
			}
		});
		openItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				isListFile[0] = ((String) modeDropDown.getSelectedItem()).contains("List");
				open(modeDropDown, modePanel, tk, toggles[0], isListFile[0], debug);
			}
		});
		//Set frame properties
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setIconImage(logo);
		frame.setJMenuBar(menuBar);
		frame.setMinimumSize(minFrameSize);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public static void error(String msg, Toolkit tk) {
		errorBeep(tk);
		JOptionPane.showMessageDialog(null, msg, TITLE + " - Error", JOptionPane.ERROR_MESSAGE);
	}
	public static void error(Exception e, Toolkit tk, boolean debug) {
		errorBeep(tk);
		String err = e.getClass().getSimpleName() + ": " + e.getMessage() + "\n";
		if (debug) {
			StackTraceElement[] ste = e.getStackTrace();
			for (StackTraceElement el: ste) {
				//Add new lines and remove brackets
				String line = el.toString().replace("[", "").replace("]", "");
				if (line.startsWith("gui") || line.startsWith("cmd")) err += line + "\n";
			}
		}
		JOptionPane.showMessageDialog(null, err, TITLE + " - Exception", JOptionPane.ERROR_MESSAGE);
	}
	public static void main(String[] args) {
		boolean debug = false;
		if (args.length > 0) {
			if (args[0].equals("-d")) debug = true;
		}
		Toolkit tk = Toolkit.getDefaultToolkit();
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			start(tk, debug);
		}
		catch (Exception e) {
			error(e, tk, debug);
		}
	}
}