package net.hollowbit.archipeloeditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import net.hollowbit.archipeloeditor.world.Map;

public class NewMapMenu extends JFrame {

	private static final long serialVersionUID = 1L;

	private JTextField textFieldName;
	private JTextField textFieldDisplayName;
	private JTextField textFieldMusic;
	
	//Dialog to create a new map
	public NewMapMenu (NewMapMenuListener listener) {
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
		
		JLabel lblDisplayName = new JLabel("Display Name:");
		lblDisplayName.setBounds(10, 70, 198, 14);
		getContentPane().add(lblDisplayName);
		
		textFieldDisplayName = new JTextField();
		textFieldDisplayName.setBounds(10, 90, 198, 20);
		textFieldDisplayName.setColumns(10);
		getContentPane().add(textFieldDisplayName);
		
		final JComboBox<String> comboBoxClimat = new JComboBox<String>();
		comboBoxClimat.setMaximumRowCount(3);
		comboBoxClimat.setBounds(162, 157, 86, 20);
		comboBoxClimat.addItem("Grassy - 0");
		comboBoxClimat.addItem("Sandy - 1");
		comboBoxClimat.addItem("Snowy - 2");
		getContentPane().add(comboBoxClimat);
		
		JLabel lblClimat = new JLabel("Climat:");
		lblClimat.setBounds(162, 141, 46, 14);
		getContentPane().add(lblClimat);
		
		JLabel lblType = new JLabel("Type:");
		lblType.setBounds(10, 141, 46, 14);
		getContentPane().add(lblType);
		
		final JComboBox<String> comboBoxType = new JComboBox<String>();
		comboBoxType.setMaximumRowCount(4);
		comboBoxType.setBounds(10, 157, 86, 20);
		comboBoxType.addItem("Island - 0");
		comboBoxType.addItem("Dungeon - 1");
		comboBoxType.addItem("House - 2");
		comboBoxType.addItem("Shop - 3");
		comboBoxType.addItem("Cave - 4");
		getContentPane().add(comboBoxType);
		
		JLabel lblNaturalLighting = new JLabel("Natural Lighting:");
		lblNaturalLighting.setBounds(10, 213, 110, 14);
		getContentPane().add(lblNaturalLighting);
		
		JCheckBox chckbxYesno = new JCheckBox("Yes/No");
		chckbxYesno.setBounds(6, 224, 97, 23);
		getContentPane().add(chckbxYesno);
		
		JLabel lblWidth = new JLabel("Width:");
		lblWidth.setBounds(278, 131, 46, 14);
		getContentPane().add(lblWidth);
		
		JLabel lblHeight = new JLabel("Height:");
		lblHeight.setBounds(278, 160, 46, 14);
		getContentPane().add(lblHeight);
		
		JLabel lblMusic = new JLabel("Music:");
		lblMusic.setBounds(162, 203, 46, 14);
		getContentPane().add(lblMusic);
		
		textFieldMusic = new JTextField();
		textFieldMusic.setBounds(162, 225, 162, 20);
		getContentPane().add(textFieldMusic);
		textFieldMusic.setColumns(10);
		
		SpinnerNumberModel sizeSpinnerModelWidth = new SpinnerNumberModel(50, 0, Integer.MAX_VALUE, 1);
		final JSpinner spinnerWidth = new JSpinner(sizeSpinnerModelWidth);
		spinnerWidth.setBounds(345, 128, 89, 20);
		getContentPane().add(spinnerWidth);
		
		SpinnerNumberModel sizeSpinnerModelHeight = new SpinnerNumberModel(50, 0, Integer.MAX_VALUE, 1);
		final JSpinner spinnerHeight = new JSpinner(sizeSpinnerModelHeight);
		spinnerHeight.setBounds(345, 160, 89, 20);
		getContentPane().add(spinnerHeight);
		
		JButton btnSave = new JButton("Create");
		final JFrame frame = this;
		btnSave.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//Makes sure that required fields aren't empty
				if (textFieldName.getText().equals("") || textFieldDisplayName.getText().equals("") || (int) spinnerWidth.getValue() <= 0 || (int) spinnerHeight.getValue() <= 0) {
					JOptionPane.showMessageDialog(frame, "A field must be empty or less than or equal to 0. Required fields are: Name, display name, width & height.", "Error", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				//If it all checks out, create a new map
				Map map = new Map(textFieldName.getText(), textFieldDisplayName.getText(), comboBoxType.getSelectedIndex(), comboBoxClimat.getSelectedIndex(), (int) spinnerWidth.getValue(), (int) spinnerHeight.getValue());
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