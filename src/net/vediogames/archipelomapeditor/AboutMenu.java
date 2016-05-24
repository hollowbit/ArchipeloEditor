package net.vediogames.archipelomapeditor;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class AboutMenu extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	public AboutMenu() {
		setAlwaysOnTop(true);
		setResizable(false);
		setTitle("About");
		setIconImage(MainEditor.ICON);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 250);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setLocationRelativeTo(null);
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblAbout = new JLabel("About:");
		lblAbout.setBounds(10, 11, 46, 14);
		contentPane.add(lblAbout);
		
		JLabel lblMadeByVedioboy = new JLabel("<html>Made by vedioboy for his game called Archipelo. This map editor is made as a utility tool "
				+ "for players of Archipelo or developers of the game. For more details on the game, please visit <a href=\"http://archipelo.ve"
				+ "diogames.net\">http://archipelo.vediogames.net</a><br><br>This is open-source software. It may be used for any purpose you "
				+ "may want and is not restricted to use for the game Archipelo.<br><br>Additional Tips:<ul><li>Pressing F5 will reload all tilepacks</li><li>Holding Space and dragging Mouse will move map</li></ul></html>");
		lblMadeByVedioboy.setVerticalAlignment(SwingConstants.TOP);
		lblMadeByVedioboy.setBounds(10, 36, 424, 211);
		contentPane.add(lblMadeByVedioboy);
	}

}
