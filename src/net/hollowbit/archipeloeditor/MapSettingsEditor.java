package net.hollowbit.archipeloeditor;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;

import net.hollowbit.archipeloeditor.changes.ChangeList;
import net.hollowbit.archipeloeditor.changes.ResizeChange;
import net.hollowbit.archipeloeditor.changes.SettingsChange;


public class MapSettingsEditor extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField textFieldName;
	private JTextField textFieldMusic;
	
	private MainEditor editor;
	
	/**
	 * Create the application.
	 */
	public MapSettingsEditor(MainEditor editor) {
		this.editor = editor;
		setAlwaysOnTop(true);
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		setResizable(false);
		setTitle("Map Settings");
		setBounds(100, 100, 450, 300);
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
		btnCancel.setBounds(345, 238, 89, 23);
		getContentPane().add(btnCancel);
		
		JLabel lblName = new JLabel("Name:");
		lblName.setBounds(10, 11, 198, 14);
		getContentPane().add(lblName);
		
		textFieldName = new JTextField();
		textFieldName.setText(new String(MainEditor.map.getName()));
		textFieldName.setBounds(10, 36, 198, 20);
		getContentPane().add(textFieldName);
		textFieldName.setColumns(10);
		
		final JComboBox<String> comboBoxClimate = new JComboBox<String>();
		comboBoxClimate.setMaximumRowCount(3);
		comboBoxClimate.setBounds(162, 107, 86, 20);
		comboBoxClimate.addItem("Grassy - 0");
		comboBoxClimate.addItem("Sandy - 1");
		comboBoxClimate.addItem("Snowy - 2");
		comboBoxClimate.setSelectedIndex(new Integer(MainEditor.map.getClimat()));
		getContentPane().add(comboBoxClimate);
		
		JLabel lblClimate = new JLabel("Climate:");
		lblClimate.setBounds(162, 81, 46, 14);
		getContentPane().add(lblClimate);
		
		JLabel lblType = new JLabel("Type:");
		lblType.setBounds(10, 81, 46, 14);
		getContentPane().add(lblType);
		
		final JComboBox<String> comboBoxType = new JComboBox<String>();
		comboBoxType.setMaximumRowCount(4);
		comboBoxType.setBounds(10, 107, 86, 20);
		comboBoxType.addItem("Island - 0");
		comboBoxType.addItem("Dungeon - 1");
		comboBoxType.addItem("House - 2");
		comboBoxType.addItem("Shop - 3");
		comboBoxType.addItem("Cave - 4");
		comboBoxType.setSelectedIndex(new Integer(MainEditor.map.getType()));
		getContentPane().add(comboBoxType);
		
		JLabel lblNaturalLighting = new JLabel("Natural Lighting:");
		lblNaturalLighting.setBounds(10, 153, 86, 14);
		getContentPane().add(lblNaturalLighting);
		
		JCheckBox chckbxYesno = new JCheckBox("Yes/No");
		chckbxYesno.setBounds(6, 174, 97, 23);
		getContentPane().add(chckbxYesno);
		
		JLabel lblWidth = new JLabel("Width:");
		lblWidth.setBounds(278, 81, 46, 14);
		getContentPane().add(lblWidth);
		
		JLabel lblHeight = new JLabel("Height:");
		lblHeight.setBounds(278, 110, 46, 14);
		getContentPane().add(lblHeight);
		
		JLabel lblMusic = new JLabel("Music:");
		lblMusic.setBounds(162, 153, 46, 14);
		getContentPane().add(lblMusic);
		
		textFieldMusic = new JTextField();
		textFieldMusic.setBounds(162, 175, 162, 20);
		getContentPane().add(textFieldMusic);
		textFieldMusic.setColumns(10);
		
		final JSpinner spinnerWidth = new JSpinner();
		spinnerWidth.setValue(new Integer(MainEditor.map.getWidth()));
		spinnerWidth.setBounds(345, 78, 89, 20);
		getContentPane().add(spinnerWidth);
		
		final JSpinner spinnerHeight = new JSpinner();
		spinnerHeight.setValue(new Integer(MainEditor.map.getHeight()));
		spinnerHeight.setBounds(345, 110, 89, 20);
		getContentPane().add(spinnerHeight);
		
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ChangeList.addChanges(new SettingsChange(), new ResizeChange());
				MainEditor.map.setName(textFieldName.getText());
				MainEditor.map.setType((byte) comboBoxType.getSelectedIndex());
				MainEditor.map.setClimat((byte) comboBoxClimate.getSelectedIndex());
				MainEditor.map.resize((int) spinnerWidth.getValue(), (int) spinnerHeight.getValue());
                editor.panelMapPanel.setPreferredSize(new Dimension(MainEditor.map.getWidth() * 18, MainEditor.map.getHeight() * 18));
                editor.panelMapPanel.revalidate();
                MainEditor.list.repaint();
				setVisible(false);
				dispose();
			}
		});
		btnSave.setBounds(246, 238, 89, 23);
		getContentPane().add(btnSave);
		getRootPane().setDefaultButton(btnSave);
		
	}
}
