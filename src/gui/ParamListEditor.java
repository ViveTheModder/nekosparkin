package gui;
//Nekosparkin: Parameter List Editor class by ViveTheJoestar
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;

public class ParamListEditor {
	public static void start(JFrame launch, String modeName, Dimension min, Image logo, Toolkit tk, int gmIdx, boolean be, boolean dbg) {
		//Set components
		Dimension screen = tk.getScreenSize();
		Image save = tk.getImage(ClassLoader.getSystemResource("img/save.png"));
		Image saveAs = tk.getImage(ClassLoader.getSystemResource("img/save-as.png"));
		ImageIcon saveAsIco = new ImageIcon(saveAs.getScaledInstance(32, 32, Image.SCALE_SMOOTH));
		ImageIcon saveIco = new ImageIcon(save.getScaledInstance(32, 32, Image.SCALE_SMOOTH));
		JButton apply = new JButton("Apply"), saveBtn = new JButton("Save"), saveAsBtn = new JButton("Save As");
		JFrame editFrame = new JFrame(Launcher.TITLE + " - " + modeName + " Editor");
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenuItem saveItem = new JMenuItem("Save");
		JMenuItem saveAsItem = new JMenuItem("Save As...");
		JToolBar toolBar = new JToolBar();
		//Set component properties
		saveAsBtn.setIcon(saveAsIco);
		saveAsItem.setIcon(saveAsIco);
		saveItem.setIcon(saveIco);
		saveBtn.setIcon(saveIco);
		//Add components
		fileMenu.add(saveItem);
		fileMenu.add(saveAsItem);
		menuBar.add(fileMenu);
		toolBar.add(saveBtn);
		toolBar.add(saveAsBtn);
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
