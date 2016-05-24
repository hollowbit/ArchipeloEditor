package net.vediogames.archipelomapeditor;

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
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.vediogames.archipelomapeditor.world.Map;

public class NewMapMenu extends JFrame {

	private static final long serialVersionUID = 1L;

	private JTextField textFieldName;
	private JTextField textFieldMusic;
	
	private boolean nameEmpty = true;
	
	MainEditor editor;

	public NewMapMenu(final MainEditor editor) {
		setAlwaysOnTop(true);
		this.editor = editor;
		setResizable(false);
		setTitle("New Map");
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
		textFieldName.getDocument().addDocumentListener(new DocumentListener() {
			  public void changedUpdate(DocumentEvent e) {
			    changed();
			  }
			  public void removeUpdate(DocumentEvent e) {
			    changed();
			  }
			  public void insertUpdate(DocumentEvent e) {
			    changed();
			  }

			  public void changed() {
			     if (textFieldName.getText().equals("")){
			       nameEmpty = true;
			     }
			     else {
			       nameEmpty = false;
			    }

			  }
			});
		textFieldName.setBounds(10, 36, 198, 20);
		getContentPane().add(textFieldName);
		textFieldName.setColumns(10);
		
		final JComboBox<String> comboBoxClimate = new JComboBox<String>();
		comboBoxClimate.setMaximumRowCount(3);
		comboBoxClimate.setBounds(162, 107, 86, 20);
		comboBoxClimate.addItem("Grassy - 0");
		comboBoxClimate.addItem("Sandy - 1");
		comboBoxClimate.addItem("Snowy - 2");
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
		
		SpinnerNumberModel sizeSpinnerModelWidth = new SpinnerNumberModel(50, 0, Integer.MAX_VALUE, 1);
		final JSpinner spinnerWidth = new JSpinner(sizeSpinnerModelWidth);
		spinnerWidth.setBounds(345, 78, 89, 20);
		getContentPane().add(spinnerWidth);
		
		SpinnerNumberModel sizeSpinnerModelHeight = new SpinnerNumberModel(50, 0, Integer.MAX_VALUE, 1);
		final JSpinner spinnerHeight = new JSpinner(sizeSpinnerModelHeight);
		spinnerHeight.setBounds(345, 110, 89, 20);
		getContentPane().add(spinnerHeight);
		
		JButton btnSave = new JButton("Create");
		final JFrame frame = this;
		btnSave.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(nameEmpty || (int) spinnerWidth.getValue() <= 0 || (int) spinnerHeight.getValue() <= 0){
					JOptionPane.showMessageDialog(frame, "A field must be empty or less than or equal to 0. Required fields are: Name, width & height.", "Error", JOptionPane.WARNING_MESSAGE);
					return;
				}
				MainEditor.map.setName(textFieldName.getText());
				MainEditor.map.setType((byte) comboBoxType.getSelectedIndex());
				MainEditor.map.setClimate((byte) comboBoxClimate.getSelectedIndex());
				MainEditor.map.setTiles(new String[(int) spinnerHeight.getValue()][(int) spinnerWidth.getValue()]);
				MainEditor.map.setElements(new String[(int) spinnerHeight.getValue()][(int) spinnerWidth.getValue()]);
				MainEditor.map.resize((int) spinnerWidth.getValue(), (int) spinnerHeight.getValue());
				for(int i = 0; i < MainEditor.map.getHeight(); i++){
					for(int u = 0; u < MainEditor.map.getWidth(); u++){
						MainEditor.map.getTiles()[i][u] = "-1";
						MainEditor.map.getElements()[i][u] = "0";
					}
				}
				Map.isMapOpen = true;
                editor.panelMapPanel.setPreferredSize(new Dimension(MainEditor.map.getWidth() * 18, MainEditor.map.getHeight() * 18));
                editor.panelMapPanel.revalidate();
                MainEditor.list.repaint();
				MainEditor.saveLocation = null;
				editor.lblMapPath.setText("");
				setVisible(false);
				dispose();
			}
		});
		
		btnSave.setBounds(246, 238, 89, 23);
		getContentPane().add(btnSave);
		getRootPane().setDefaultButton(btnSave);
	}

}
