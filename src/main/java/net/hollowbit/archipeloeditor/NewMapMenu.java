package net.hollowbit.archipeloeditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import net.hollowbit.archipeloeditor.world.Map;

public class NewMapMenu extends JFrame {

	private static final long serialVersionUID = 1L;

	private JTextField textFieldName;
	private JTextField textFieldMusic;
	
	//Dialog to create a new map
	public NewMapMenu (MainEditor editor, NewMapMenuListener listener) {
		setAlwaysOnTop(true);
		setResizable(false);
		setTitle("New Map");
		setBounds(100, 100, 450, 375);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setIconImage(MainEditor.ICON);
		setLocationRelativeTo(null);
		getContentPane().setLayout(null);
		
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addMouseListener(new MouseAdapter(){

			@Override
			public void mouseReleased(MouseEvent arg0) {
				setVisible(false);
				dispose();
			}
			
		});
		btnCancel.setBounds(345, 288, 89, 23);
		getContentPane().add(btnCancel);
		
		JLabel lblName = new JLabel("Name:");
		lblName.setBounds(10, 11, 198, 14);
		getContentPane().add(lblName);
		
		textFieldName = new JTextField();
		textFieldName.setBounds(10, 36, 198, 20);
		textFieldName.setColumns(10);
		getContentPane().add(textFieldName);
		
		JLabel lblNaturalLighting = new JLabel("Natural Lighting:");
		lblNaturalLighting.setBounds(10, 213, 110, 14);
		getContentPane().add(lblNaturalLighting);
		
		JCheckBox chckbxYesno = new JCheckBox("Yes/No");
		chckbxYesno.setBounds(6, 224, 97, 23);
		getContentPane().add(chckbxYesno);
		
		JLabel lblMusic = new JLabel("Music:");
		lblMusic.setBounds(162, 203, 46, 14);
		getContentPane().add(lblMusic);
		
		textFieldMusic = new JTextField();
		textFieldMusic.setBounds(162, 225, 162, 20);
		getContentPane().add(textFieldMusic);
		textFieldMusic.setColumns(10);
		
		JButton btnSave = new JButton("Create");
		final JFrame frame = this;
		btnSave.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//Makes sure that required fields aren't empty
				if (textFieldName.getText().equals("")) {
					JOptionPane.showMessageDialog(frame, "Please give the map a name. Ex: archipelo-world", "Error", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				//If it all checks out, create a new map
				Map map = new Map(textFieldName.getText(), textFieldMusic.getText(), chckbxYesno.isSelected());
				listener.newMapCreated(map);
				
				setVisible(false);
				dispose();
			}
		});
		
		btnSave.setBounds(246, 288, 89, 23);
		getContentPane().add(btnSave);
		getRootPane().setDefaultButton(btnSave);
	}

	public interface NewMapMenuListener {
		
		public void newMapCreated (Map map);
		
	}
	
}