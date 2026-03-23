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
import javax.swing.JButton;
import javax.swing.JComboBox;
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
import javax.swing.JToolBar;
import javax.swing.UIManager;
import cmd.UltBatMeteor;
/* TODO:
 * 1. Find images to replace the text in the toolbar buttons with. 
 * 2. Add action listeners for remaining buttons (Open, Edit). */
public class Launcher {
	private static final String TITLE = "Nekosparkin";
	private static final Toolkit DEF_TK = Toolkit.getDefaultToolkit();
	private static final Color BG_COLOR = new Color(138, 208, 242);
	private static final Color FG_COLOR = new Color(7, 129, 163);
	private static final Color TX_COLOR = new Color(193, 34, 100);
	private static final Font HEADING = new Font("Tahoma", Font.BOLD, 16);
	private static final Font SUBHEADING = new Font("Tahoma", Font.PLAIN, 13);
	private static final Image LOGO = DEF_TK.getImage(ClassLoader.getSystemResource("img/logo.png"));
	private static final ImageIcon LOGO_ICO = new ImageIcon(LOGO.getScaledInstance(64, 64, Image.SCALE_SMOOTH));
	private static void start() {
		Box modeBox = Box.createHorizontalBox();
		Dimension minFrameSize = new Dimension(700, 400);
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
		saveBtn.setEnabled(false);
		saveItem.setEnabled(false);
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
		frame.add(mainPanel);
		fileMenu.add(openItem);
		fileMenu.add(saveItem);
		fileMenu.add(saveAsItem);
		helpMenu.add(aboutItem);
		menuBar.add(fileMenu);
		menuBar.add(helpMenu);
		//Add action listeners
		aboutItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				String msg = "Program made by ViveTheJoestar\nIcon made by SpideyGirl";
				JOptionPane.showMessageDialog(frame, msg, TITLE, JOptionPane.INFORMATION_MESSAGE, LOGO_ICO);
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
			
		}
	}
}
