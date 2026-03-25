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
import javax.swing.UIManager;
import cmd.UltBatMeteor;
/* TODO:
 * 1. Find images to replace the text in the toolbar buttons with. - DONE
 * 2. Add action listeners for remaining buttons (Open, Edit) - DONE. */
public class Launcher { 
	private static final Toolkit DEF_TK = Toolkit.getDefaultToolkit();
	static final Image LOGO = DEF_TK.getImage(ClassLoader.getSystemResource("img/logo.png"));
	static final String TITLE = "Nekosparkin";
	static final Color BG_COLOR = new Color(138, 208, 242);
	static final Color FG_COLOR = new Color(7, 129, 163);
	static final Color TX_COLOR = new Color(193, 34, 100);
	static final Font HEADING = new Font("Tahoma", Font.BOLD, 16);
	static final Font SUBHEADING = new Font("Tahoma", Font.PLAIN, 13);
	private static final Image BT2_LOGO = DEF_TK.getImage(ClassLoader.getSystemResource("img/bt2.png"));
	private static final Image BT3_LOGO = DEF_TK.getImage(ClassLoader.getSystemResource("img/bt3.png"));
	private static final Image OPEN = DEF_TK.getImage(ClassLoader.getSystemResource("img/open.png"));
	private static final Image SAVE = DEF_TK.getImage(ClassLoader.getSystemResource("img/save.png"));
	private static final Image SAVE_AS = DEF_TK.getImage(ClassLoader.getSystemResource("img/save-as.png"));
	static RandomAccessFile[] containers = null;
	private static final ImageIcon BT2_ICO = new ImageIcon(BT2_LOGO.getScaledInstance(48, 48, Image.SCALE_SMOOTH));
	private static final ImageIcon BT3_ICO = new ImageIcon(BT3_LOGO.getScaledInstance(48, 48, Image.SCALE_SMOOTH));
	private static final ImageIcon LOGO_ICO = new ImageIcon(LOGO.getScaledInstance(64, 64, Image.SCALE_SMOOTH));
	
	private static RandomAccessFile[] getContainersFromChooser(int gameModeIdx) throws IOException {
		RandomAccessFile[] containers = null;
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Open Folder with " + UltBatMeteor.MODE_NAMES[gameModeIdx] + " parameters...");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int result = chooser.showOpenDialog(null);
		if (result == JFileChooser.APPROVE_OPTION) {
			File ultBatDir = chooser.getSelectedFile();
			containers = UltBatMeteor.getMissionParamContainers(ultBatDir, gameModeIdx);
			if (containers[0] == null) {
				errorBeep();
				String err = "Chosen folder contains no valid " + UltBatMeteor.MODE_NAMES[gameModeIdx] + " parameters!";
				JOptionPane.showMessageDialog(null, err, TITLE, JOptionPane.ERROR_MESSAGE);
			}
		}
		return containers;
	}
	private static void errorBeep() {
		Runnable runWinErrorSnd = (Runnable) DEF_TK.getDesktopProperty("win.sound.exclamation");
		if (runWinErrorSnd!=null) runWinErrorSnd.run();
	}
	private static void open(JComboBox<String> modeDropDown) {
		try {
			containers = getContainersFromChooser(modeDropDown.getSelectedIndex());
		} catch (IOException e) {
			errorBeep();
			JOptionPane.showMessageDialog(null, e.getClass().getSimpleName() + ": " + e.getMessage(), TITLE, 0);
		}
	}
	private static void start() {
		//Tired of using static variables, sorry...
		final boolean[] gameSel = new boolean[1];
		Box modeBox = Box.createHorizontalBox();
		Dimension minFrameSize = new Dimension(700, 400);
		ImageIcon openIco = new ImageIcon(OPEN.getScaledInstance(32, 32, Image.SCALE_SMOOTH));
		ImageIcon saveAsIco = new ImageIcon(SAVE_AS.getScaledInstance(32, 32, Image.SCALE_SMOOTH));
		ImageIcon saveIco = new ImageIcon(SAVE.getScaledInstance(32, 32, Image.SCALE_SMOOTH));
		JButton editBtn = new JButton("Edit");
		JButton openBtn = new JButton("Open Folder...");
		JButton saveBtn = new JButton("Save");
		JComboBox<String> modeDropDown = new JComboBox<String>(UltBatMeteor.MODE_NAMES);
		JFrame frame = new JFrame(TITLE);
		JLabel label = new JLabel("Select Mode from Ultimate Battle:");
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenu helpMenu = new JMenu("Help");
		JMenuItem aboutItem = new JMenuItem("About");
		JMenuItem openItem = new JMenuItem("Open Folder...");
		JMenuItem saveItem = new JMenuItem("Save");
		JMenuItem saveAsItem = new JMenuItem("Save As...");
		JPanel mainPanel = new JPanel(new BorderLayout());
		JPanel modePanel = new JPanel();
		JToggleButton gameBtn = new JToggleButton(BT3_ICO);
		JToolBar toolBar = new JToolBar();
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
		saveAsItem.setEnabled(false);
		saveAsItem.setIcon(saveAsIco);
		saveBtn.setEnabled(false);
		saveItem.setEnabled(false);
		saveItem.setIcon(saveIco);
		openBtn.setIcon(openIco);
		openItem.setIcon(openIco);
		saveBtn.setIcon(saveIco);
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
		toolBar.add(saveBtn);
		toolBar.add(Box.createHorizontalGlue());
		toolBar.add(gameBtn);
		frame.add(mainPanel);
		fileMenu.add(openItem);
		fileMenu.add(saveItem);
		fileMenu.add(saveAsItem);
		helpMenu.add(aboutItem);
		menuBar.add(fileMenu);
		menuBar.add(helpMenu);
		//Add listeners
		aboutItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				String msg = "Program made by ViveTheJoestar\nIcon made by SpideyGirl";
				JOptionPane.showMessageDialog(frame, msg, TITLE, JOptionPane.INFORMATION_MESSAGE, LOGO_ICO);
			}
		});
		editBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					if (containers != null) {
						if (containers[0] != null) Selector.start(frame, minFrameSize, modeDropDown.getSelectedIndex());
					}
				}
				catch (IOException e) {
					errorBeep();
					JOptionPane.showMessageDialog(null, e.getClass().getSimpleName() + ": " + e.getMessage(), TITLE, 0);
				}
			}
		});
		gameBtn.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent ie) {
				int state = ie.getStateChange();
				if (state == ItemEvent.SELECTED) {
					gameSel[0] = !gameSel[0]; //What actually causes the toggle to work properly
					modeDropDown.setEnabled(!gameSel[0]);
					if (gameSel[0]) gameBtn.setIcon(BT2_ICO);
					else gameBtn.setIcon(BT3_ICO);
				}
			}
		});
		openBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				open(modeDropDown);
			}
		});
		openItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				open(modeDropDown);
			}
		});
		//Set frame properties
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setIconImage(LOGO);
		frame.setJMenuBar(menuBar);
		frame.setMinimumSize(minFrameSize);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			start();
		}
		catch (Exception e) {
			errorBeep();
			JOptionPane.showMessageDialog(null, e.getClass().getSimpleName() + ": " + e.getMessage(), TITLE, 0);
		}
	}
}
