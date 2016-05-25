package net.vediogames.archipelomapeditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JViewport;
import javax.swing.ListModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.vediogames.archipelomapeditor.changes.*;
import net.vediogames.archipelomapeditor.world.Assets;
import net.vediogames.archipelomapeditor.world.Map;
import net.vediogames.archipelomapeditor.world.MapElement;
import net.vediogames.archipelomapeditor.world.MapTile;

public class MainEditor implements Runnable{
	
	public static final String PATH = new File(".").getAbsolutePath();
	
	public static BufferedImage ICON;
	public static BufferedImage invalidTile;
	public static BufferedImage gridTile;
	public static Cursor CURSOR;
	
	public static boolean showTiles = true;
	public static boolean showElements = true;
	public static int selectedLayer = 0;//0 = tiles, 1 = elements
	public static int selectedTool = 0;//0 = pencil, 1 = bucket	
	public static String saveLocation = null;	
	public static boolean showGrid = false;
	
	public static JList<Object> list;
	JLabel lblMapPath;
	public JPanel panelMapPanel;
	JScrollPane scrollPane;
	private JMenuItem mntmSave;
	private JMenuItem mntmSaveAs;
	private JMenuItem mntmClose;
	private JMenuItem mntmEdit;
	private JMenuItem mntmGenerate;
	private JMenuItem mntmReset;
	private JLabel lblListTitle;
	private JToggleButton btnBucketTool;
	private JLabel lblTileName;
	
	private JFrame frame;
	private JTextField textFieldSearch;
	
	private Thread thread;
	private boolean running = true;
	
	private Point origin;
	private int x, y;
	private int mouseX, mouseY;
	private boolean mouse1Pressed = false;
	private boolean mouse2Pressed = false;
	private boolean controlPressed = false;
	private boolean shiftPressed = false;
	private boolean spacePressed = false;
	
	public static Map map;
	
	public static int tileX, tileY;
	
	Icon iconHoveredOver = null;
	
	public static boolean justSaved = true;
	
	JCheckBoxMenuItem mntmToggleGrid;
	JCheckBoxMenuItem mntmToggleTiles;
	JCheckBoxMenuItem mntmToggleElements;
	JCheckBox checkBoxTilesVisible;
	JCheckBox checkBoxElementsVisible;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					map = new Map();
					ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
					ICON = ImageIO.read(classLoader.getResourceAsStream("images/icon.png"));
					invalidTile = ImageIO.read(classLoader.getResourceAsStream("images/invalid.png"));
					gridTile = ImageIO.read(classLoader.getResourceAsStream("images/grid.png"));
					CURSOR = Toolkit.getDefaultToolkit().createCustomCursor(ImageIO.read(classLoader.getResourceAsStream("images/cursor.png")), new Point(16, 16), "blank");
					MainEditor window = new MainEditor();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public MainEditor() {
		initialize();
		thread = new Thread(this);
		thread.start();
	}

	private void initialize() {
		frame = new JFrame("Archipelo Map Editor v1.0");
		frame.setBounds(100, 100, 1280, 720);
		frame.setLocationRelativeTo(null);
		frame.setIconImage(ICON);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(new KeyEventDispatcher(){

			@Override
			public boolean dispatchKeyEvent(KeyEvent e) {
				if(e.getID() == KeyEvent.KEY_PRESSED){
					if(e.getKeyCode() == KeyEvent.VK_CONTROL)
						controlPressed = true;
					if(e.getKeyCode() == KeyEvent.VK_SHIFT)
						shiftPressed = true;
					if(e.getKeyCode() == KeyEvent.VK_SPACE)
						spacePressed = true;
					
					if(e.getKeyCode() == KeyEvent.VK_G && controlPressed){
						if(!showGrid){
							mntmToggleGrid.setSelected(true);
							showGrid = true;
						}else{
							mntmToggleGrid.setSelected(false);
							showGrid = false;
						}
					}
					
					if(e.getKeyCode() == KeyEvent.VK_T && controlPressed){
						if(!showTiles){
							mntmToggleTiles.setSelected(true);
							checkBoxTilesVisible.setSelected(true);
							showTiles = true;
						}else{
							mntmToggleTiles.setSelected(false);
							checkBoxTilesVisible.setSelected(false);
							showTiles = false;
						}
					}
					
					if(e.getKeyCode() == KeyEvent.VK_E && controlPressed){
						if(!showElements){
							mntmToggleElements.setSelected(true);
							checkBoxElementsVisible.setSelected(true);
							showElements = true;
						}else{
							mntmToggleElements.setSelected(false);
							checkBoxElementsVisible.setSelected(false);
							showElements = false;
						}
					}
					
					if(e.getKeyCode() == KeyEvent.VK_Z && controlPressed && !shiftPressed)
						ChangeList.undo();
					
					if(e.getKeyCode() == KeyEvent.VK_Y && controlPressed)
						ChangeList.redo();
					
					if(e.getKeyCode() == KeyEvent.VK_S && controlPressed)
						save();
					
					if(e.getKeyCode() == KeyEvent.VK_Z && controlPressed && shiftPressed)
						ChangeList.redo();
					
					if(e.getKeyCode() == KeyEvent.VK_F5)
						reloadTiles();
					
				}else if(e.getID() == KeyEvent.KEY_RELEASED){
					if(e.getKeyCode() == KeyEvent.VK_CONTROL)
						controlPressed = false;
					if(e.getKeyCode() == KeyEvent.VK_SHIFT)
						shiftPressed = false;
					if(e.getKeyCode() == KeyEvent.VK_SPACE)
						spacePressed = false;
				}
				return false;
			}
			
		});
		
		frame.addWindowListener(new WindowListener() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				if(justSaved){
					return;
				}
				boolean saved = false;
				while(!saved){
					int option = JOptionPane.showConfirmDialog(frame, "Would you like to save first?", "Map Close", JOptionPane.YES_NO_CANCEL_OPTION);
					if(option == JOptionPane.YES_OPTION){
						if(saveLocation == null){
							JFileChooser saveFile = new JFileChooser();
							saveFile.setFileFilter(new FileNameExtensionFilter("map", "map"));
				               saveFile.showSaveDialog(null);
				               File selectedFile = saveFile.getSelectedFile();
				               if(selectedFile != null){
				               	saveLocation = selectedFile.getPath();
				               	saveLocation = saveLocation.replaceFirst("[.][^.]+$", "");
				               	saveLocation = saveLocation + ".map";
				               	lblMapPath.setText("         " + saveLocation);
					               map.save(new File(saveLocation));
					               saved = true;
				               }
						}else{
							map.save(new File(saveLocation));
							saved = true;
						}
					}else
						saved = true;
				}
			}

			@Override
			public void windowActivated(WindowEvent e) {}

			@Override
			public void windowClosed(WindowEvent e) {}

			@Override
			public void windowDeactivated(WindowEvent e) {}

			@Override
			public void windowDeiconified(WindowEvent e) {}

			@Override
			public void windowIconified(WindowEvent e) {}

			@Override
			public void windowOpened(WindowEvent e) {}
			
		});
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmOpen = new JMenuItem("Open...");
		mntmOpen.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(Map.isMapOpen && !justSaved){
					boolean saved = false;
					while(!saved){
						int option = JOptionPane.showConfirmDialog(frame, "Would you like to save first?", "Map Close", JOptionPane.YES_NO_CANCEL_OPTION);
						if(option == JOptionPane.YES_OPTION){
							if(saveLocation == null){
								JFileChooser saveFile = new JFileChooser(System.getProperty("user.home") + "/Desktop");
								saveFile.setFileFilter(new FileNameExtensionFilter("json", "json"));
				                saveFile.showSaveDialog(null);
				                if(saveFile.getSelectedFile() == null) return;
				                File selectedFile = saveFile.getSelectedFile();
				                if(selectedFile != null){
				                	saveLocation = selectedFile.getPath();
				                	saveLocation = saveLocation.replaceFirst("[.][^.]+$", "");
				                	saveLocation = saveLocation + ".map";
				                	lblMapPath.setText("         " + saveLocation);
					                map.save(new File(saveLocation));
					                saved = true;
				                }
							}else{
								map.save(new File(saveLocation));
								saved = true;
							}
							if(saved){
								JFileChooser openFile = new JFileChooser(System.getProperty("user.home") + "/Desktop");
								openFile.setFileFilter(new FileNameExtensionFilter("json", "json"));
			                	openFile.showOpenDialog(null);
				                if(openFile.getSelectedFile() == null) return;
			                	File selectedFile = openFile.getSelectedFile();
			                	if(selectedFile != null){
			                		saveLocation = selectedFile.getPath();
			                		lblMapPath.setText("         " + saveLocation);
			                		map.load(selectedFile);
			                		panelMapPanel.setPreferredSize(new Dimension(map.getWidth() * 18, map.getHeight() * 18));
			                		panelMapPanel.revalidate();
			                	}
							}
						}else if(option == JOptionPane.NO_OPTION){
			                saved = true;
							JFileChooser openFile = new JFileChooser(System.getProperty("user.home") + "/Desktop");
							openFile.setFileFilter(new FileNameExtensionFilter("json", "json"));
			                openFile.showOpenDialog(null);
			                if(openFile.getSelectedFile() == null) return;
			                File selectedFile = openFile.getSelectedFile();
			                saveLocation = selectedFile.getPath();
			                lblMapPath.setText("         " + saveLocation);
			                map.load(selectedFile);
			                panelMapPanel.setPreferredSize(new Dimension(map.getWidth() * 18, map.getHeight() * 18));
			                panelMapPanel.revalidate();
						}else
							saved = true;
					}
				}else{
					JFileChooser openFile = new JFileChooser(System.getProperty("user.home") + "/Desktop");
					openFile.setFileFilter(new FileNameExtensionFilter("json", "json"));
	                openFile.showOpenDialog(null);
	                if(openFile.getSelectedFile() == null) return;
	                File selectedFile = openFile.getSelectedFile();
	                saveLocation = selectedFile.getPath();
	                lblMapPath.setText("         " + saveLocation);
	                map.load(selectedFile);
	                panelMapPanel.setPreferredSize(new Dimension(map.getWidth() * 18, map.getHeight() * 18));
	                panelMapPanel.revalidate();
				}
				list.repaint();
			}
		});
		
		JMenuItem mntmNew = new JMenuItem("New...");
		final MainEditor mainEditor = this;
		mntmNew.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(Map.isMapOpen && !justSaved){
					boolean saved = false;
					while(!saved){
						int option = JOptionPane.showConfirmDialog(frame, "Would you like to save first?", "Map Close", JOptionPane.YES_NO_CANCEL_OPTION);
						if(option == JOptionPane.YES_OPTION){
							if(saveLocation == null){
								JFileChooser saveFile = new JFileChooser();
								saveFile.setFileFilter(new FileNameExtensionFilter("json", "json"));
				                saveFile.showSaveDialog(null);
				                if(saveFile.getSelectedFile() == null) return;
				                File selectedFile = saveFile.getSelectedFile();
				                if(selectedFile != null){
				                	saveLocation = selectedFile.getPath();
				                	saveLocation = saveLocation.replaceFirst("[.][^.]+$", "");
				                	saveLocation = saveLocation + ".json";
				                	lblMapPath.setText("         " + saveLocation);
					                map.save(new File(saveLocation));
					                saved = true;
				                }
							}else{
								map.save(new File(saveLocation));
								saved = true;
							}
							if(saved){
								NewMapMenu newMapMenu = new NewMapMenu(mainEditor);
								newMapMenu.setVisible(true);
							}
						}else if(option == JOptionPane.NO_OPTION){
							saved = true;
							NewMapMenu newMapMenu = new NewMapMenu(mainEditor);
							newMapMenu.setVisible(true);
						}else
							saved = true;
					}
				}else{
					NewMapMenu newMapMenu = new NewMapMenu(mainEditor);
					newMapMenu.setVisible(true);
				}
			}
		});
		mnFile.add(mntmNew);
		mnFile.add(mntmOpen);
		
		mntmSave = new JMenuItem("Save (Ctrl + S)");
		mntmSave.addMouseListener(new MouseAdapter(){
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if(mntmSave.isEnabled()){
					if(saveLocation == null){
						JFileChooser saveFile = new JFileChooser(System.getProperty("user.home") + "/Desktop");
						saveFile.setFileFilter(new FileNameExtensionFilter("json", "json"));
		                saveFile.showSaveDialog(null);
		                if(saveFile.getSelectedFile() == null) return;
		                File selectedFile = saveFile.getSelectedFile();
		                if(selectedFile != null){
		                	saveLocation = selectedFile.getPath();
		                	saveLocation = saveLocation.replaceFirst("[.][^.]+$", "");
		                	saveLocation = saveLocation + ".json";
		                	lblMapPath.setText("         " + saveLocation);
			                map.save(new File(saveLocation));
		                }
					}else
						map.save(new File(saveLocation));
					justSaved = true;
				}
			}
			
		});
		mnFile.add(mntmSave);
		
		mntmSaveAs = new JMenuItem("Save As...");
		mntmSaveAs.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(mntmSaveAs.isEnabled()){
					JFileChooser saveFile = new JFileChooser(System.getProperty("user.home") + "/Desktop");
					saveFile.setFileFilter(new FileNameExtensionFilter("json", "json"));
	                saveFile.showSaveDialog(null);
	                if(saveFile.getSelectedFile() == null) return;
	                File selectedFile = saveFile.getSelectedFile();
	                if(selectedFile != null){
	                	saveLocation = selectedFile.getPath();
	                	saveLocation = saveLocation.replaceFirst("[.][^.]+$", "");
	                	saveLocation = saveLocation + ".json";
	                	lblMapPath.setText("         " + saveLocation);
		                map.save(new File(saveLocation));
	                }
					justSaved = true;
				}
			}
		});
		mnFile.add(mntmSaveAs);
		
		mntmClose = new JMenuItem("Close");
		mntmClose.addMouseListener(new MouseAdapter(){
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if(justSaved){
					map.close();
					ChangeList.reset();
					lblMapPath.setText("");
					return;
				}
				int option = JOptionPane.showConfirmDialog(frame, "Would you like to save first?", "Map Close", JOptionPane.YES_NO_CANCEL_OPTION);
				if(option == JOptionPane.YES_OPTION){
					if(saveLocation == null){
						JFileChooser saveFile = new JFileChooser(System.getProperty("user.home") + "/Desktop");
						saveFile.setFileFilter(new FileNameExtensionFilter("json", "json"));
		                saveFile.showSaveDialog(null);
		                if(saveFile.getSelectedFile() == null) return;
		                File selectedFile = saveFile.getSelectedFile();
		                if(selectedFile != null){
		                	saveLocation = selectedFile.getPath();
		                	saveLocation = saveLocation.replaceFirst("[.][^.]+$", "");
		                	saveLocation = saveLocation + ".json";
		                	lblMapPath.setText("         " + saveLocation);
			                map.save(new File(saveLocation));
		                }
					}else
						map.save(new File(saveLocation));
					map.close();
					ChangeList.reset();
					lblMapPath.setText("");
				}else if(option == JOptionPane.NO_OPTION){
					map.close();
					ChangeList.reset();
					lblMapPath.setText("");
				}
			}
			
		});
		mnFile.add(mntmClose);
		
		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);
		
		JMenuItem mntmUndo = new JMenuItem("Undo (Ctrl + Z)");
		mntmUndo.addMouseListener(new MouseAdapter(){
			
			@Override
			public void mouseReleased(MouseEvent e) {
				ChangeList.undo();
			}
			
		});
		mnEdit.add(mntmUndo);
		
		JMenuItem mntmRedo = new JMenuItem("Redo (Ctrl + Y)");
		mntmRedo.addMouseListener(new MouseAdapter(){
			
			@Override
			public void mouseReleased(MouseEvent e) {
				ChangeList.redo();
			}
			
		});
		mnEdit.add(mntmRedo);
		
		JMenu mnMap = new JMenu("Map");
		menuBar.add(mnMap);
		
		mntmEdit = new JMenuItem("Edit...");
		mntmEdit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(mntmEdit.isEnabled()){
					MapSettingsEditor mapDetailEditor = new MapSettingsEditor(mainEditor);
					mapDetailEditor.setVisible(true);
				}
			}
		});
		mnMap.add(mntmEdit);
		
		mntmGenerate = new JMenuItem("Generate...");
		mntmGenerate.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(mntmEdit.isEnabled())
					JOptionPane.showMessageDialog(frame, "Map generating is not yet implemented, sorry :(");
			}
		});
		mnMap.add(mntmGenerate);
		
		mntmReset = new JMenuItem("Reset");
		mntmReset.addMouseListener(new MouseAdapter(){
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if(mntmEdit.isEnabled()){
					int option = JOptionPane.showConfirmDialog(frame, "Are you sure you want to reset the map?", "Map Reset", JOptionPane.YES_NO_OPTION);
					if(option == JOptionPane.YES_OPTION){
						for(int i = 0; i < map.getHeight(); i++){
							for(int u = 0; u < map.getWidth(); u++){
								map.getTiles()[i][u] = "-1";
								map.getElements()[i][u] = "0";
							}
						}
					}
				}
			}
			
		});
		mnMap.add(mntmReset);
		
		JMenu mnView = new JMenu("View");
		menuBar.add(mnView);
		
		mntmToggleGrid = new JCheckBoxMenuItem("Show Grid (Ctrl + G)");
		mntmToggleGrid.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent e) {
				showGrid = mntmToggleGrid.isSelected();
			}
			
		});
		mnView.add(mntmToggleGrid);
		
		mntmToggleTiles = new JCheckBoxMenuItem("Show Tiles (Ctrl + T)");
		mntmToggleTiles.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent e) {
				showTiles = mntmToggleTiles.isSelected();
				checkBoxTilesVisible.setSelected(mntmToggleTiles.isSelected());
			}
			
		});
		mnView.add(mntmToggleTiles);
		
		mntmToggleElements = new JCheckBoxMenuItem("Show Elements (Ctrl + E)");
		mntmToggleElements.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent e) {
				showElements = mntmToggleElements.isSelected();
				checkBoxElementsVisible.setSelected(mntmToggleElements.isSelected());
			}
			
		});
		mnView.add(mntmToggleElements);
		
		JMenu mnAbout = new JMenu("About");
		mnAbout.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				AboutMenu aboutMenu = new AboutMenu();
				aboutMenu.setVisible(true);
			}
		});
		menuBar.add(mnAbout);
		
		lblMapPath = new JLabel("");
		lblMapPath.setHorizontalAlignment(SwingConstants.RIGHT);
		menuBar.add(lblMapPath);
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setEnabled(false);
		splitPane.setDividerLocation(282);
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);
		
		panelMapPanel = new JPanel(){

			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g1) {
				super.paintComponent(g1);
				Graphics2D g = (Graphics2D) g1;
				g.clearRect(0, 0, map.getWidth() * 18, map.getWidth() * 18);
				x = (map.getWidth() * 18 <= scrollPane.getViewportBorderBounds().getWidth() ? (int) scrollPane.getViewportBorderBounds().getWidth() / 2 - (map.getWidth() * 18) / 2:0);
				y = (map.getHeight() * 18 <= scrollPane.getViewportBorderBounds().getHeight() ? (int) scrollPane.getViewportBorderBounds().getHeight() / 2 - (map.getHeight() * 18) / 2:0);
				map.draw(g, x, y, scrollPane.getHorizontalScrollBar().getValue() / 18, scrollPane.getVerticalScrollBar().getValue() / 18, scrollPane.getViewport().getWidth() / 18, scrollPane.getViewport().getHeight() / 18);
				g.setColor(Color.BLACK);
				g.drawString("X: " + tileY + " Y: " + (map.getHeight() - tileX - 1), scrollPane.getHorizontalScrollBar().getValue() + 5, scrollPane.getVerticalScrollBar().getValue() + 15);
			}
			
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(map.getWidth() * 18, map.getHeight() * 18);
			}
			
		};
		panelMapPanel.setAutoscrolls(true);
		panelMapPanel.addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON1){
					if(spacePressed)
						origin = new Point(e.getPoint());
					else if(selectedLayer == 0)
						ChangeList.addChanges(new TileMapChange());
					else if(selectedLayer == 1)
						ChangeList.addChanges(new ElementMapChange());
					mouse1Pressed = true;
				}
				if(e.getButton() == MouseEvent.BUTTON3)
					mouse2Pressed = true;
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON1){
					mouse1Pressed = false;
					origin = null;
				}
				if(e.getButton() == MouseEvent.BUTTON3)
					mouse2Pressed = false;
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				frame.setCursor(CURSOR);
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
			
		});
		
		panelMapPanel.addMouseMotionListener(new MouseMotionListener(){

			@Override
			public void mouseDragged(MouseEvent e) {
				mouseX = e.getX() - x;
				mouseY = e.getY() - y;
				if(origin == null) return;
				JViewport viewPort = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, panelMapPanel);
				if(viewPort == null) return;
				int deltaX = origin.x - e.getX();
				int deltaY = origin.y - e.getY();
				if(spacePressed){
					scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getValue() + deltaY);
					scrollPane.getHorizontalScrollBar().setValue(scrollPane.getHorizontalScrollBar().getValue() + deltaX);
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				mouseX = e.getX() - x;
				mouseY = e.getY() - y;
			}
			
			
		});
		
		panelMapPanel.setBackground(Color.WHITE);
		panelMapPanel.addMouseWheelListener(new MouseWheelListener() {
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if(controlPressed){
					if(shiftPressed){
						scrollPane.getHorizontalScrollBar().setValue(scrollPane.getHorizontalScrollBar().getValue() + e.getWheelRotation() * 100);
					}else{
						scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getValue() + e.getWheelRotation() * 100);
					}
				}else{
					if(list.getSelectedValue() != null)
						list.setSelectedIndex(list.getSelectedIndex() + e.getWheelRotation());
					else{
						list.setSelectedIndex(0);
						list.setSelectedIndex(list.getSelectedIndex() + e.getWheelRotation());
					}
				}	
			}
		});
		
		scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		splitPane.setRightComponent(scrollPane);
		scrollPane.setViewportView(panelMapPanel);
		
		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		splitPane.setLeftComponent(panel);
		
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] {29, 33, 33, 33, 33};
		gbl_panel.rowHeights = new int[]{14, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panel.columnWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JLabel lblTools = new JLabel("Tools:");
		GridBagConstraints gbc_lblTools = new GridBagConstraints();
		gbc_lblTools.gridwidth = 5;
		gbc_lblTools.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblTools.insets = new Insets(0, 0, 5, 0);
		gbc_lblTools.gridx = 0;
		gbc_lblTools.gridy = 0;
		panel.add(lblTools, gbc_lblTools);
		
		
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		BufferedImage pencilIcon = null;
		BufferedImage bucketIcon = null;
		try {
			pencilIcon = ImageIO.read(classLoader.getResourceAsStream("images/icons/pencil.png"));
			bucketIcon = ImageIO.read(classLoader.getResourceAsStream("images/icons/bucket.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		btnBucketTool = new JToggleButton(new ImageIcon(bucketIcon));
		
		final JToggleButton btnPencilTool = new JToggleButton(new ImageIcon(pencilIcon));
		btnPencilTool.setSelected(true);
		btnPencilTool.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				btnBucketTool.setSelected(!btnPencilTool.isSelected());
				selectedTool = (btnPencilTool.isSelected() ? 0:selectedTool);
			}
		});
		GridBagConstraints gbc_btnPencilTool = new GridBagConstraints();
		gbc_btnPencilTool.insets = new Insets(0, 0, 5, 5);
		gbc_btnPencilTool.gridx = 0;
		gbc_btnPencilTool.gridy = 1;
		panel.add(btnPencilTool, gbc_btnPencilTool);

		btnBucketTool.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				btnPencilTool.setSelected(!btnBucketTool.isSelected());
				selectedTool = (btnBucketTool.isSelected() ? 1:selectedTool);
			}
		});
		GridBagConstraints gbc_btnBucketTool = new GridBagConstraints();
		gbc_btnBucketTool.insets = new Insets(0, 0, 5, 5);
		gbc_btnBucketTool.gridx = 1;
		gbc_btnBucketTool.gridy = 1;
		panel.add(btnBucketTool, gbc_btnBucketTool);
		
		JLabel lblVisibility = new JLabel("Visibility:");
		GridBagConstraints gbc_lblVisibility = new GridBagConstraints();
		gbc_lblVisibility.insets = new Insets(0, 0, 5, 5);
		gbc_lblVisibility.gridx = 0;
		gbc_lblVisibility.gridy = 3;
		panel.add(lblVisibility, gbc_lblVisibility);
		
		JLabel lblEditingLayer = new JLabel("Selected Layer:");
		GridBagConstraints gbc_lblEditingLayer = new GridBagConstraints();
		gbc_lblEditingLayer.anchor = GridBagConstraints.WEST;
		gbc_lblEditingLayer.insets = new Insets(0, 0, 5, 5);
		gbc_lblEditingLayer.gridx = 1;
		gbc_lblEditingLayer.gridy = 3;
		panel.add(lblEditingLayer, gbc_lblEditingLayer);
		
		checkBoxTilesVisible = new JCheckBox("");
		checkBoxTilesVisible.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				showTiles = checkBoxTilesVisible.isSelected();
				mntmToggleTiles.setSelected(checkBoxTilesVisible.isSelected());
			}
		});
		checkBoxTilesVisible.setSelected(true);
		GridBagConstraints gbc_checkBoxTilesVisible = new GridBagConstraints();
		gbc_checkBoxTilesVisible.insets = new Insets(0, 0, 5, 5);
		gbc_checkBoxTilesVisible.gridx = 0;
		gbc_checkBoxTilesVisible.gridy = 4;
		panel.add(checkBoxTilesVisible, gbc_checkBoxTilesVisible);
		
		final JRadioButton rdbtnElements = new JRadioButton("Elements");
		
		final JRadioButton rdbtnTiles = new JRadioButton("Tiles");
		rdbtnTiles.addMouseListener(new MouseAdapter(){
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if(selectedLayer == 0){
					rdbtnElements.setSelected(false);
					rdbtnTiles.setSelected(true);
				}else{
					rdbtnElements.setSelected(false);
					rdbtnTiles.setSelected(true);
					selectedLayer = 0;
					ArrayList<MapTile> tiles = new ArrayList<MapTile>();
					for(MapTile tile : Assets.TileList){
						if(tile.name.toLowerCase().contains(textFieldSearch.getText().toLowerCase()) || tile.assetPack.name.toLowerCase().contains(textFieldSearch.getText().toLowerCase()))
							tiles.add(tile);
					}
					
					MapTile[] tilesArray = new MapTile[tiles.size()];
					for(int i = 0; i < tilesArray.length; i++)
						tilesArray[i] = tiles.get(i);
					lblTileName.setText("");
					list.clearSelection();
					list.setListData(tilesArray);
				}
			}
		});
		rdbtnTiles.setSelected(true);
		GridBagConstraints gbc_rdbtnTiles = new GridBagConstraints();
		gbc_rdbtnTiles.gridwidth = 4;
		gbc_rdbtnTiles.anchor = GridBagConstraints.WEST;
		gbc_rdbtnTiles.insets = new Insets(0, 0, 5, 0);
		gbc_rdbtnTiles.gridx = 1;
		gbc_rdbtnTiles.gridy = 4;
		panel.add(rdbtnTiles, gbc_rdbtnTiles);
		
		checkBoxElementsVisible = new JCheckBox("");
		checkBoxElementsVisible.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				showElements = checkBoxElementsVisible.isSelected();
				mntmToggleElements.setSelected(checkBoxElementsVisible.isSelected());
			}
		});
		checkBoxElementsVisible.setSelected(true);
		GridBagConstraints gbc_checkBoxElementsVisible = new GridBagConstraints();
		gbc_checkBoxElementsVisible.insets = new Insets(0, 0, 5, 5);
		gbc_checkBoxElementsVisible.gridx = 0;
		gbc_checkBoxElementsVisible.gridy = 5;
		panel.add(checkBoxElementsVisible, gbc_checkBoxElementsVisible);
		
		
		GridBagConstraints gbc_rdbtnElements = new GridBagConstraints();
		gbc_rdbtnElements.gridwidth = 4;
		gbc_rdbtnElements.anchor = GridBagConstraints.WEST;
		gbc_rdbtnElements.insets = new Insets(0, 0, 5, 0);
		gbc_rdbtnElements.gridx = 1;
		gbc_rdbtnElements.gridy = 5;
		rdbtnElements.addMouseListener(new MouseAdapter(){
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if(selectedLayer == 1){
					rdbtnTiles.setSelected(false);
					rdbtnElements.setSelected(true);
				}else{
					rdbtnTiles.setSelected(false);
					rdbtnElements.setSelected(true);
					selectedLayer = 1;
					ArrayList<MapElement> elements = new ArrayList<MapElement>();
					for(MapElement element : Assets.ElementList){
						if(element.name.toLowerCase().contains(textFieldSearch.getText().toLowerCase()) || element.assetPack.name.toLowerCase().contains(textFieldSearch.getText().toLowerCase()))
							elements.add(element);
					}
					
					MapElement[] elementsArray = new MapElement[elements.size()];
					for(int i = 0; i < elementsArray.length; i++)
						elementsArray[i] = elements.get(i);
					lblTileName.setText("");
					list.clearSelection();
					list.setListData(elementsArray);
				}
			}
			
		});
		panel.add(rdbtnElements, gbc_rdbtnElements);
		
		JLabel lblSearch = new JLabel("Search:");
		GridBagConstraints gbc_lblSearch = new GridBagConstraints();
		gbc_lblSearch.gridwidth = 5;
		gbc_lblSearch.anchor = GridBagConstraints.WEST;
		gbc_lblSearch.insets = new Insets(0, 0, 5, 0);
		gbc_lblSearch.gridx = 0;
		gbc_lblSearch.gridy = 6;
		panel.add(lblSearch, gbc_lblSearch);
		
		textFieldSearch = new JTextField();
		textFieldSearch.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				if(selectedLayer == 0){
					ArrayList<MapTile> tiles = new ArrayList<MapTile>();
					for(MapTile tile : Assets.TileList){
						if(tile.name.toLowerCase().contains(textFieldSearch.getText().toLowerCase()) || tile.assetPack.name.toLowerCase().contains(textFieldSearch.getText().toLowerCase()))
							tiles.add(tile);
					}
					
					MapTile[] tilesArray = new MapTile[tiles.size()];
					for(int i = 0; i < tilesArray.length; i++)
						tilesArray[i] = tiles.get(i);
					list.clearSelection();
					list.setListData(tilesArray);
				}else if(selectedLayer == 1){
					ArrayList<MapElement> elements = new ArrayList<MapElement>();
					for(MapElement element : Assets.ElementList){
						if(element.name.toLowerCase().contains(textFieldSearch.getText().toLowerCase()) || element.assetPack.name.toLowerCase().contains(textFieldSearch.getText().toLowerCase()))
							elements.add(element);
					}
					
					MapElement[] elementsArray = new MapElement[elements.size()];
					for(int i = 0; i < elementsArray.length; i++)
						elementsArray[i] = elements.get(i);
					list.clearSelection();
					list.setListData(elementsArray);
				}
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				if(selectedLayer == 0){
					ArrayList<MapTile> tiles = new ArrayList<MapTile>();
					for(MapTile tile : Assets.TileList){
						if(tile.name.toLowerCase().contains(textFieldSearch.getText().toLowerCase()) || tile.assetPack.name.toLowerCase().contains(textFieldSearch.getText().toLowerCase()))
							tiles.add(tile);
					}
					
					MapTile[] tilesArray = new MapTile[tiles.size()];
					for(int i = 0; i < tilesArray.length; i++)
						tilesArray[i] = tiles.get(i);
					list.clearSelection();
					list.setListData(tilesArray);
				}else if(selectedLayer == 1){
					ArrayList<MapElement> elements = new ArrayList<MapElement>();
					for(MapElement element : Assets.ElementList){
						if(element.name.toLowerCase().contains(textFieldSearch.getText().toLowerCase()) || element.assetPack.name.toLowerCase().contains(textFieldSearch.getText().toLowerCase()))
							elements.add(element);
					}
					
					MapElement[] elementsArray = new MapElement[elements.size()];
					for(int i = 0; i < elementsArray.length; i++)
						elementsArray[i] = elements.get(i);
					list.clearSelection();
					list.setListData(elementsArray);
				}
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				if(selectedLayer == 0){
					ArrayList<MapTile> tiles = new ArrayList<MapTile>();
					for(MapTile tile : Assets.TileList){
						if(tile.name.toLowerCase().contains(textFieldSearch.getText().toLowerCase()) || tile.assetPack.name.toLowerCase().contains(textFieldSearch.getText().toLowerCase()))
							tiles.add(tile);
					}
					
					MapTile[] tilesArray = new MapTile[tiles.size()];
					for(int i = 0; i < tilesArray.length; i++)
						tilesArray[i] = tiles.get(i);
					list.clearSelection();
					list.setListData(tilesArray);
				}else if(selectedLayer == 1){
					ArrayList<MapElement> elements = new ArrayList<MapElement>();
					for(MapElement element : Assets.ElementList){
						if(element.name.toLowerCase().contains(textFieldSearch.getText().toLowerCase()) || element.assetPack.name.toLowerCase().contains(textFieldSearch.getText().toLowerCase()))
							elements.add(element);
					}
					
					MapElement[] elementsArray = new MapElement[elements.size()];
					for(int i = 0; i < elementsArray.length; i++)
						elementsArray[i] = elements.get(i);
					list.clearSelection();
					list.setListData(elementsArray);
				}
			}
		});
		GridBagConstraints gbc_textFieldSearch = new GridBagConstraints();
		gbc_textFieldSearch.gridwidth = 5;
		gbc_textFieldSearch.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldSearch.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldSearch.gridx = 0;
		gbc_textFieldSearch.gridy = 7;
		panel.add(textFieldSearch, gbc_textFieldSearch);
		textFieldSearch.setColumns(10);
		
		lblListTitle = new JLabel("Tiles:  ");
		GridBagConstraints gbc_lblListTitle = new GridBagConstraints();
		gbc_lblListTitle.weightx = 40;
		gbc_lblListTitle.anchor = GridBagConstraints.WEST;
		gbc_lblListTitle.insets = new Insets(0, 0, 5, 5);
		gbc_lblListTitle.gridx = 0;
		gbc_lblListTitle.gridy = 8;
		panel.add(lblListTitle, gbc_lblListTitle);
		
		lblTileName = new JLabel("");
		lblTileName.setPreferredSize(new Dimension(194, 15));
		GridBagConstraints gbc_lblTileName = new GridBagConstraints();
		gbc_lblTileName.anchor = GridBagConstraints.WEST;
		gbc_lblTileName.fill = GridBagConstraints.NONE;
		gbc_lblTileName.gridwidth = 4;
		gbc_lblTileName.insets = new Insets(0, 0, 5, 5);
		gbc_lblTileName.gridx = 1;
		gbc_lblTileName.gridy = 8;
		panel.add(lblTileName, gbc_lblTileName);
		
		Assets.initiate();
		MapTile[] tiles = new MapTile[Assets.TileList.size()];
		for(int i = 0; i < tiles.length; i++)
			tiles[i] = Assets.TileList.get(i);
		
		list = new JList<Object>(tiles);
		list.setVisibleRowCount(-1);
		list.addListSelectionListener(new ListSelectionListener(){

			@Override
			public void valueChanged(ListSelectionEvent event) {
				if(list.getSelectedValue() != null){
					if(selectedLayer == 0)
						lblTileName.setText(((MapTile) list.getSelectedValue()).name + " (" + ((MapTile) list.getSelectedValue()).assetPack.name + ")");
					else if(selectedLayer == 1)
						lblTileName.setText(((MapElement) list.getSelectedValue()).name + " (" + ((MapElement) list.getSelectedValue()).assetPack.name + ")");
				}
			}
			
		});
		list.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON3){
					ListModel<Object> model = list.getModel();
					int index = list.locationToIndex(e.getPoint());
					if (index > -1) {
						list.setToolTipText(null);
						if(selectedLayer == 0){
							MapTile tile = (MapTile) model.getElementAt(index);
							iconHoveredOver = tile;
							list.setToolTipText("<html>"
						    		+ "ID: " + tile.id + "<br>"
						    		+ "Name: " + tile.name + "<br>"
						    		+ "AssetPack: " + tile.assetPack.name + " v." + tile.assetPack.version + "<br>"
						    		+ "Collidable: " + tile.collidable + "<br>"
						    		+ "Swimmable: " + tile.swimmable + "<br>"
						    		+ "# of Animation Frames: " + tile.numberOfFrames + "<br>"
						    		+ "Time(s) Between Frames: " + tile.animationSpeed + "<br>"
						    		+ "Damage: " + tile.damage + "<br>"
						    		+ "Time(s) Between damage: " + tile.damageSpeed + "<br>"
						    		+ "Speed Multiplier: " + tile.speedMultiplier + "<br>"
						    		+ "</html>");
						}else if(selectedLayer == 1){
							MapElement element = (MapElement) model.getElementAt(index);
							iconHoveredOver = element;
							String text = "<html>"
						    		+ "ID: " + element.id + "<br>"
						    		+ "Name: " + element.name + "<br>"
						    		+ "AssetPack: " + element.assetPack.name + " v." + element.assetPack.version + "<br>"
						    		+ "Width: " + element.width + "<br>"
						    		+ "Height: " + element.height + "<br>"
						    		+ "OffsetX: " + element.offsetX + "<br>"
						    		+ "OffsetY: " + element.offsetY + "<br>"
						    		+ "# of Animation Frames: " + element.numberOfFrames + "<br>"
						    		+ "Time(s) Between Frames: " + element.animationSpeed + "<br>"
				    				+ "Collidable:<br>";
							for(int i = 0; i < element.height; i++){
								for(int u = 0; u < element.width; u++){
									text += element.collidable[i][u];
								}
								text += "<br>";
							}
						    text += "</html>";
							list.setToolTipText(text);
						}
					    ToolTipManager.sharedInstance().mouseMoved(new MouseEvent(list, 0, 0, 0, e.getX(), e.getY(), 0, false));
					    ToolTipManager.sharedInstance().setInitialDelay(0);
					}
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				
			}
			
		});
		
		list.addMouseMotionListener(new MouseMotionListener(){

			@Override
			public void mouseDragged(MouseEvent e) {
				
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				if(list.getModel().getElementAt(list.locationToIndex(e.getPoint())) != iconHoveredOver)
					list.setToolTipText(null);
			}
			
		});
		
		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		GridBagConstraints gbc_list = new GridBagConstraints();
		gbc_list.gridheight = 7;
		gbc_list.gridwidth = 5;
		gbc_list.fill = GridBagConstraints.BOTH;
		gbc_list.gridx = 0;
		gbc_list.gridy = 9;
		panel.add(list, gbc_list);
	}
	
	public void reloadTiles(){
		Assets.TileList.removeAll(Assets.TileList);
		Assets.initiate();
		ArrayList<MapTile> tiles = new ArrayList<MapTile>();
		for(int i = 0; i < Assets.TileList.size(); i++){
			if(Assets.TileList.get(i).name.toLowerCase().contains(textFieldSearch.getText().toLowerCase()) || Assets.TileList.get(i).assetPack.name.toLowerCase().contains(textFieldSearch.getText().toLowerCase()))
				tiles.add(Assets.TileList.get(i));
		}
		
		MapTile[] tilesArray = new MapTile[tiles.size()];
		for(int i = 0; i < tilesArray.length; i++)
			tilesArray[i] = tiles.get(i);
		list.clearSelection();
		list.setListData(tilesArray);
        panelMapPanel.setPreferredSize(new Dimension(map.getWidth() * 18, map.getHeight() * 18));
        panelMapPanel.revalidate();
	}

	@Override
	public void run() {
		
		while(running){
			panelMapPanel.repaint();
			ChangeList.update();
			mntmSave.setEnabled(Map.isMapOpen);
			mntmSaveAs.setEnabled(Map.isMapOpen);
			mntmClose.setEnabled(Map.isMapOpen);
			mntmEdit.setEnabled(Map.isMapOpen);
			mntmGenerate.setEnabled(Map.isMapOpen);
			mntmReset.setEnabled(Map.isMapOpen);
			
			if(selectedLayer == 0)
				lblListTitle.setText("Tiles:");
			else
				lblListTitle.setText("Elements:");
			
			tileX = mouseY / 18;
			tileY = mouseX / 18;
			
			if(map.getHeight() > tileX && map.getWidth() > tileY  && tileX >= 0 && tileY >= 0 && map.getTiles() != null){
				if(mouse1Pressed && !spacePressed){
					if(selectedLayer == 0){
						if(selectedTool == 0){
							if(list.getSelectedValue() != null)
								map.getTiles()[tileX][tileY] = ((MapTile) list.getSelectedValue()).id;
						}else if(selectedTool == 1){
							boolean[][] filledTiles = new boolean[map.getHeight()][map.getWidth()];
							String replaceTile = map.getTiles()[tileX][tileY];
							bucketFillTiles(replaceTile, filledTiles, tileX, tileY);
						}
					}else{
						if(selectedTool == 0){
							if(list.getSelectedValue() != null)
								map.getElements()[tileX][tileY] = ((MapElement) list.getSelectedValue()).id;
						}else if(selectedTool == 1){
							boolean[][] filledTiles = new boolean[map.getHeight()][map.getWidth()];
							String replaceTile = map.getElements()[tileX][tileY];
							bucketFillElements(replaceTile, filledTiles, tileX, tileY);
						}
					}
				}
				if(mouse2Pressed){
					if(selectedLayer == 0){
						list.setSelectedValue(Assets.getTileByID(map.getTiles()[tileX][tileY]), true);
					}else{
						list.setSelectedValue(Assets.getElementByID(map.getElements()[tileX][tileY]), true);
					}
				}
			}
		}
		
	}
	
	public void bucketFillTiles(String replaceTile, boolean[][] filledTiles, int tileX, int tileY){
		if(tileX >= map.getHeight()) return;
		if(tileY >= map.getWidth()) return;
		if(tileX < 0) return;
		if(tileY < 0) return;
		
		if(filledTiles[tileX][tileY]) return;
		if(!map.getTiles()[tileX][tileY].equals(replaceTile)) return;
		
		filledTiles[tileX][tileY] = true;
		
		map.getTiles()[tileX][tileY] = ((MapTile) list.getSelectedValue()).id;
		bucketFillTiles(replaceTile, filledTiles, tileX + 1, tileY);
		bucketFillTiles(replaceTile, filledTiles, tileX - 1, tileY);
		bucketFillTiles(replaceTile, filledTiles, tileX, tileY + 1);
		bucketFillTiles(replaceTile, filledTiles, tileX, tileY - 1);
	}
	
	public void bucketFillElements(String replaceTile, boolean[][] filledTiles, int tileX, int tileY){
		if(tileX >= map.getHeight()) return;
		if(tileY >= map.getWidth()) return;
		if(tileX < 0) return;
		if(tileY < 0) return;
		
		if(filledTiles[tileX][tileY]) return;
		if(!map.getElements()[tileX][tileY].equals(replaceTile)) return;
		
		filledTiles[tileX][tileY] = true;
		
		map.getElements()[tileX][tileY] = ((MapElement) list.getSelectedValue()).id;
		bucketFillElements(replaceTile, filledTiles, tileX + 1, tileY);
		bucketFillElements(replaceTile, filledTiles, tileX - 1, tileY);
		bucketFillElements(replaceTile, filledTiles, tileX, tileY + 1);
		bucketFillElements(replaceTile, filledTiles, tileX, tileY - 1);
	}
	
	public void save(){
		if(saveLocation == null){
			JFileChooser saveFile = new JFileChooser(System.getProperty("user.home") + "/Desktop");
			saveFile.setFileFilter(new FileNameExtensionFilter("map", "map"));
            saveFile.showSaveDialog(null);
            if(saveFile.getSelectedFile() == null) return;
            File selectedFile = saveFile.getSelectedFile();
            if(selectedFile != null){
            	saveLocation = selectedFile.getPath();
            	saveLocation = saveLocation.replaceFirst("[.][^.]+$", "");
            	saveLocation = saveLocation + ".map";
            	lblMapPath.setText("         " + saveLocation);
                map.save(new File(saveLocation));
            }
		}else
			map.save(new File(saveLocation));
		justSaved = true;
	}
	
}
