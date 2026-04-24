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
	public static void start(JFrame selF, JFrame staF, JLabel chip, Toolkit tk, File[] csvs, byte[] prm, int pos, int idx, int gm, boolean be, boolean dbg)
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
		frame.add(panel);//Add action listener
		apply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					int[] valArray = new int[enemyParamInts.length];
					//Just to be 100% certain, paste the existing character IDs before overwriting
					System.arraycopy(enemyParamInts, 0, valArray, 0, 8);
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
					selF.setEnabled(true);
					frame.dispose();
				}
				catch (Exception e) {
					Launcher.error(e, tk, be);
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