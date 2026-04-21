package gui;
import java.awt.BorderLayout;
//Nekosparkin: Parameter List Editor class by ViveTheJoestar
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import cmd.CsvHandler;
import cmd.ParamHandler;

public class ParamListEditor {
	private static int[] getParamsWithoutPadding(boolean bigEndian) {
		byte[] paramBytes = new byte[4], params = Launcher.container.getBattleParams();
		int paddingCnt = 0;
		//First, read the param from bottom to top to count the padding
		for (int pos = params.length - 4; pos > 0; pos -= 4) {
			System.arraycopy(params, pos, paramBytes, 0, paramBytes.length);
			if (ParamHandler.getVal(paramBytes, bigEndian) == 0) paddingCnt++;
			else break;
		}
		//Then, knowing the number of parameters, iterate through the byte array like normal
		int[] paramInts = new int[params.length / 4 - paddingCnt];
		for (int i = 0; i < paramInts.length; i++) {
			System.arraycopy(params, i * 4, paramBytes, 0, paramBytes.length);
			paramInts[i] = ParamHandler.getVal(paramBytes, bigEndian);
		}
		return paramInts;
	}
	private static void save(int[] paramArr, JComboBox<String>[] cboxArrs, String modeName, boolean saveAs, boolean be) throws IOException {
		byte[] newBattleParams = new byte[Launcher.container.getBattleParams().length];
		for (int paramCnt = 0; paramCnt < paramArr.length; paramCnt++ ) {
			paramArr[paramCnt] = cboxArrs[paramCnt].getSelectedIndex();
			if (((String) cboxArrs[paramCnt].getSelectedItem()).equals("Random")) paramArr[paramCnt] = 998;
			System.arraycopy(ParamHandler.getValBytes(paramArr[paramCnt], be), 0, newBattleParams, paramCnt * 4, 4);
		}
		Launcher.container.setBattleParams(newBattleParams);
		if (!saveAs) Launcher.container.writeParams();
		else {
			JFileChooser chooser = new JFileChooser();
			String windowTitle = "Save " + modeName + " parameters...";
			chooser.setCurrentDirectory(Launcher.currDir);
			chooser.setDialogTitle(windowTitle);
			chooser.setDialogType(JFileChooser.SAVE_DIALOG);
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int result = chooser.showSaveDialog(null);
			if (result == JFileChooser.APPROVE_OPTION) Launcher.container.writeParams(chooser.getSelectedFile(), modeName);
		}
	}
	private static void updateParamImage(int imgNum, JLabel imgLbl, String folderName) throws IOException {
		File imgFile = new File("chips/maps/" + folderName + "/" + imgNum + ".png");
		Image img = ImageIO.read(imgFile);
		ImageIcon imgIco = new ImageIcon(img.getScaledInstance(64, 64, Image.SCALE_FAST));
		imgLbl.setIcon(imgIco);
	}
	
	@SuppressWarnings("unchecked")
	public static void start(JFrame launch, String name, Dimension min, Image logo, Toolkit tk, int gmIdx, boolean be, boolean bt2, boolean dbg)
	throws IOException {
		File[] csvArray = CsvHandler.getAvailableCsvFiles();
		String[] modeNameArr = name.split(" ");
		String csvName = modeNameArr[modeNameArr.length - 2].replace("(", "").toLowerCase();
		String folderName = bt2 ? "bt2" : "bt3";
		int searchResult = CsvHandler.getCsvSearchResult(csvArray, csvName);
		String[] paramNames = CsvHandler.getParamNames(csvArray[searchResult]);
		int[] paramArr = getParamsWithoutPadding(be);
		//Set components
		Box[] paramBoxArr = new Box[paramArr.length];
		Dimension screen = tk.getScreenSize();
		Image save = tk.getImage(ClassLoader.getSystemResource("img/save.png"));
		Image saveAs = tk.getImage(ClassLoader.getSystemResource("img/save-as.png"));
		ImageIcon saveAsIco = new ImageIcon(saveAs.getScaledInstance(32, 32, Image.SCALE_SMOOTH));
		ImageIcon saveIco = new ImageIcon(save.getScaledInstance(32, 32, Image.SCALE_SMOOTH));
		JButton saveBtn = new JButton("Save"), saveAsBtn = new JButton("Save As");
		JComboBox<String>[] paramCboxArr = new JComboBox[paramBoxArr.length];
		JFrame editFrame = new JFrame(Launcher.TITLE + " - " + name + " Editor");
		JLabel[] paramImgLbls = new JLabel[paramArr.length];
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenuItem saveItem = new JMenuItem("Save");
		JMenuItem saveAsItem = new JMenuItem("Save As...");
		JPanel mainPanel = new JPanel(new BorderLayout()), paramPanel = new JPanel();
		JScrollPane scroll = new JScrollPane(paramPanel);
		JToolBar toolBar = new JToolBar();
		//Set component properties
		launch.setEnabled(false);
		paramPanel.setBackground(Launcher.BG_COLOR);
		paramPanel.setLayout(new BoxLayout(paramPanel, BoxLayout.Y_AXIS));
		saveAsBtn.setIcon(saveAsIco);
		saveAsItem.setIcon(saveAsIco);
		saveItem.setIcon(saveIco);
		saveBtn.setIcon(saveIco);
		toolBar.addSeparator();
		toolBar.setFloatable(false);
		//Add components
		fileMenu.add(saveItem);
		fileMenu.add(saveAsItem);
		mainPanel.add(toolBar, BorderLayout.PAGE_START);
		mainPanel.add(scroll, BorderLayout.CENTER);
		menuBar.add(fileMenu);
		paramPanel.add(Box.createVerticalGlue());
		for (int paramCnt = 0; paramCnt < paramArr.length; paramCnt++) {
			final int index = paramCnt;
			paramBoxArr[paramCnt] = Box.createHorizontalBox();
			Box nestedBox = Box.createHorizontalBox();
			nestedBox.setBackground(Launcher.FG_COLOR);
			nestedBox.setBorder(BorderFactory.createLineBorder(Launcher.FG_COLOR, 8));
			nestedBox.setOpaque(true);
			paramCboxArr[paramCnt] = new JComboBox<String>(paramNames);
			paramCboxArr[paramCnt].setAlignmentX(JComboBox.CENTER_ALIGNMENT);
			((JLabel) paramCboxArr[paramCnt].getRenderer()).setHorizontalAlignment(JLabel.CENTER);
			paramCboxArr[paramCnt].setSelectedIndex(paramArr[paramCnt]);
			paramBoxArr[paramCnt].add(Box.createHorizontalGlue());
			//Only add images for maps, not background music
			JLabel paramCntLbl = new JLabel((paramCnt + 1) + ": ");
			paramCntLbl.setFont(Launcher.HEADING);
			paramCntLbl.setForeground(Color.WHITE);
			nestedBox.add(paramCntLbl);
			if (csvName.equals("map")) {
				paramImgLbls[paramCnt] = new JLabel(" ");
				paramImgLbls[paramCnt].setAlignmentX(JLabel.CENTER_ALIGNMENT);
				paramImgLbls[paramCnt].setHorizontalAlignment(JLabel.CENTER);
				updateParamImage(paramCnt, paramImgLbls[paramCnt], folderName);
				nestedBox.add(paramImgLbls[paramCnt]);
			}
			nestedBox.add(paramCboxArr[paramCnt]);
			paramBoxArr[paramCnt].add(nestedBox);
			paramBoxArr[paramCnt].add(Box.createHorizontalGlue());
			paramPanel.add(paramBoxArr[paramCnt]);
			paramPanel.add(new JLabel(" "));
			//Add item listener for combo boxes (to update the image labels)
			paramCboxArr[paramCnt].addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent ie) {
						try {
							int change = ie.getStateChange();
							if (change == ItemEvent.SELECTED) {
								int newIdx = paramCboxArr[index].getSelectedIndex();
								if (newIdx == paramCboxArr[index].getItemCount() - 1) newIdx = 998;
								if (csvName.equals("map")) updateParamImage(newIdx, paramImgLbls[index], folderName);
							}
						} catch (Exception e) {
							Launcher.error(e, tk, dbg);
						}
					}
				}
			);
		}
		paramPanel.add(Box.createVerticalGlue());
		toolBar.add(saveBtn);
		toolBar.add(saveAsBtn);
		editFrame.add(mainPanel);
		//Add action listeners
		saveAsBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					save(paramArr, paramCboxArr, name, true, be);
				} catch (Exception e) {
					Launcher.error(e, tk, dbg);
				}
			}
		});
		saveAsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					save(paramArr, paramCboxArr, name, true, be);
				} catch (Exception e) {
					Launcher.error(e, tk, dbg);
				}
			}
		});
		saveBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					save(paramArr, paramCboxArr, name, false, be);
				} catch (IOException e) {
					Launcher.error(e, tk, dbg);
				}
			}
		});
		saveItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					save(paramArr, paramCboxArr, folderName, false, be);
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
