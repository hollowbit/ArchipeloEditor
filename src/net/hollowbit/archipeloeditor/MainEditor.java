package net.hollowbit.archipeloeditor;

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

import net.hollowbit.archipeloeditor.changes.ChangeList;
import net.hollowbit.archipeloeditor.changes.ElementMapChange;
import net.hollowbit.archipeloeditor.changes.TileMapChange;
import net.hollowbit.archipeloeditor.world.AssetManager;
import net.hollowbit.archipeloeditor.world.Map;
import net.hollowbit.archipeloeditor.world.MapElement;
import net.hollowbit.archipeloeditor.world.MapTile;

public class MainEditor implements Runnable{

	public static final int TILE_SIZE = 16;
	public static final String PATH = new File(".").getAbsolutePath();
	
	private static final int TILE_LAYER = 0;
	private static final int ELEMENT_LAYER = 1;
	
	private static final int PENCIL_TOOL = 0;
	private static final int BUCKET_TOOL = 1;
	
	public static BufferedImage ICON;
	public static BufferedImage invalidTile;
	public static BufferedImage gridTile;
	public static Cursor CURSOR;
	
	private boolean showTiles = true;
	private boolean showElements = true;
	private int selectedLayer = 0;//0 = tiles, 1 = elements
	private int selectedTool = 0;//0 = pencil, 1 = bucket	
	private String saveLocation = null;	
	private boolean showGrid = false;
	
	private JList<Object> list;
	private JLabel lblMapPath;
	private JPanel panelMapPanel;
	private JScrollPane scrollPane;
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
	
	private Map map;
	
	private int tileX, tileY;
	
	long startTime = 0;
	
	Icon iconHoveredOver = null;
	
	private AssetManager assetManager;
	private ChangeList changeList;
	
	public boolean justSaved = true;
	
	JCheckBoxMenuItem mntmToggleGrid;
	JCheckBoxMenuItem mntmToggleTiles;
	JCheckBoxMenuItem mntmToggleElements;
	JCheckBox checkBoxTilesVisible;
	JCheckBox checkBoxElementsVisible;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//Load basic images for editor
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
		//Initialize
		changeList = new ChangeList(this);
		assetManager = new AssetManager();
		assetManager.load();
		initialize();
		
		startTime = System.currentTimeMillis();
		thread = new Thread(this);
		thread.start();
	}
	
	//Method to initialize all components, keeps the constructor clean
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
				//Check for hotkey presses
				if (e.getID() == KeyEvent.KEY_PRESSED) {
					if (e.getKeyCode() == KeyEvent.VK_CONTROL)
						controlPressed = true;
					if (e.getKeyCode() == KeyEvent.VK_SHIFT)
						shiftPressed = true;
					if (e.getKeyCode() == KeyEvent.VK_SPACE)
						spacePressed = true;
					
					if (e.getKeyCode() == KeyEvent.VK_G && controlPressed ){//Toggle show grid
						if(!showGrid){
							mntmToggleGrid.setSelected(true);
							showGrid = true;
						}else{
							mntmToggleGrid.setSelected(false);
							showGrid = false;
						}
					}
					
					if (e.getKeyCode() == KeyEvent.VK_T && controlPressed) {//Toggle show tiles
						if (!showTiles){
							mntmToggleTiles.setSelected(true);
							checkBoxTilesVisible.setSelected(true);
							showTiles = true;
						} else {
							mntmToggleTiles.setSelected(false);
							checkBoxTilesVisible.setSelected(false);
							showTiles = false;
						}
					}
					
					if (e.getKeyCode() == KeyEvent.VK_E && controlPressed) {//Toggle show elements
						if (!showElements) {
							mntmToggleElements.setSelected(true);
							checkBoxElementsVisible.setSelected(true);
							showElements = true;
						} else {
							mntmToggleElements.setSelected(false);
							checkBoxElementsVisible.setSelected(false);
							showElements = false;
						}
					}
					
					if (e.getKeyCode() == KeyEvent.VK_Z && controlPressed && !shiftPressed)//Undo
						changeList.undo();
					
					if (e.getKeyCode() == KeyEvent.VK_Y && controlPressed)//Redo
						changeList.redo();
					
					if (e.getKeyCode() == KeyEvent.VK_S && controlPressed)//Save
						showMapSaveDialog(false);
					
					if (e.getKeyCode() == KeyEvent.VK_Z && controlPressed && shiftPressed)//Another kind of redo
						changeList.redo();
					
					if (e.getKeyCode() == KeyEvent.VK_F5)//Reload assets
						reloadAssets();
					
				} else if (e.getID() == KeyEvent.KEY_RELEASED) {
					if (e.getKeyCode() == KeyEvent.VK_CONTROL)
						controlPressed = false;
					if (e.getKeyCode() == KeyEvent.VK_SHIFT)
						shiftPressed = false;
					if (e.getKeyCode() == KeyEvent.VK_SPACE)
						spacePressed = false;
				}
				return false;
			}
			
		});
		
		frame.addWindowListener(new WindowListener() {
			
			//Event for window close, make sure user saved first before exiting
			@Override
			public void windowClosing(WindowEvent e) {
				if (justSaved) {
					return;
				}
				
				boolean saved = false;
				while(!saved){
					int option = JOptionPane.showConfirmDialog(frame, "Would you like to save first?", "Map Close", JOptionPane.YES_NO_CANCEL_OPTION);
					if (option == JOptionPane.YES_OPTION) {
						showMapSaveDialog(false);
					} else
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
			public void mouseReleased(MouseEvent e) {//Show open map dialog, make sure user saved current map (if there is one) first
				if (map != null && !justSaved) {
					boolean saved = false;
					while(!saved){
						int option = JOptionPane.showConfirmDialog(frame, "Would you like to save first?", "Map Close", JOptionPane.YES_NO_CANCEL_OPTION);
						if (option == JOptionPane.YES_OPTION) {
							saved = showMapSaveDialog(false);
							
							if (saved) {
								showMapOpenDialog();
							}
						} else if (option == JOptionPane.NO_OPTION){
			                saved = true;
			                showMapOpenDialog();
			            } else
							return;
					}
				} else {
					showMapOpenDialog();
				}
			}
		});
		
		JMenuItem mntmNew = new JMenuItem("New...");
		final MainEditor mainEditor = this;
		mntmNew.addMouseListener(new MouseAdapter() {//Open new map dialog, make sure they saved current map
			@Override
			public void mouseReleased(MouseEvent e) {
				if(map != null && !justSaved){
					boolean saved = false;
					while(!saved){
						int option = JOptionPane.showConfirmDialog(frame, "Would you like to save first?", "Map Close", JOptionPane.YES_NO_CANCEL_OPTION);
						if (option == JOptionPane.YES_OPTION) {
							saved = showMapSaveDialog(false);
							
							if (saved) {
								showNewMapDialog();
							}
						} else if(option == JOptionPane.NO_OPTION) {
							showNewMapDialog();
						} else
							return;
					}
				} else {
					showNewMapDialog();
				}
			}
		});
		mnFile.add(mntmNew);
		mnFile.add(mntmOpen);
		
		mntmSave = new JMenuItem("Save (Ctrl + S)");
		mntmSave.addMouseListener(new MouseAdapter(){
			
			@Override
			public void mouseReleased(MouseEvent e) {//Open save dialog
				if(mntmSave.isEnabled()){
					showMapSaveDialog(false);
				}
			}
			
		});
		mnFile.add(mntmSave);
		
		mntmSaveAs = new JMenuItem("Save As...");
		mntmSaveAs.addMouseListener(new MouseAdapter() {//Same as save but forces saving in a new location
			@Override
			public void mouseReleased(MouseEvent e) {
				if(mntmSaveAs.isEnabled()){
					showMapSaveDialog(true);
				}
			}
		});
		mnFile.add(mntmSaveAs);
		
		mntmClose = new JMenuItem("Close");
		mntmClose.addMouseListener(new MouseAdapter(){//Closes map but makes sure it is saved
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if (!justSaved) {
					boolean saved = false;
					while (!saved) {
						int option = JOptionPane.showConfirmDialog(frame, "Would you like to save first?", "Map Close", JOptionPane.YES_NO_CANCEL_OPTION);
						if (option == JOptionPane.YES_OPTION) {
							saved = showMapSaveDialog(false);
						} else if (option == JOptionPane.NO_OPTION) {
							saved = true;
						} else if (option == JOptionPane.CANCEL_OPTION) {
							return;
						}
					}
				}
				map.close();
				map = null;
				changeList.reset();
				lblMapPath.setText("");
			}
			
		});
		mnFile.add(mntmClose);
		
		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);
		
		JMenuItem mntmUndo = new JMenuItem("Undo (Ctrl + Z)");
		mntmUndo.addMouseListener(new MouseAdapter(){//undo, but within menu
			
			@Override
			public void mouseReleased(MouseEvent e) {
				changeList.undo();
			}
			
		});
		mnEdit.add(mntmUndo);
		
		JMenuItem mntmRedo = new JMenuItem("Redo (Ctrl + Y)");
		mntmRedo.addMouseListener(new MouseAdapter(){//redo, but within menu
			
			@Override
			public void mouseReleased(MouseEvent e) {
				changeList.redo();
			}
			
		});
		mnEdit.add(mntmRedo);
		
		JMenu mnMap = new JMenu("Map");
		menuBar.add(mnMap);
		
		mntmEdit = new JMenuItem("Edit...");
		mntmEdit.addMouseListener(new MouseAdapter() {//Open map settings editor
			@Override
			public void mouseReleased(MouseEvent e) {
				if(mntmEdit.isEnabled()){
					MapSettingsEditor mapDetailEditor = new MapSettingsEditor(mainEditor, new MapSettingsEditor.MapSettingsEditorListener() {
						
						@Override
						public void mapSettingsChanged() {
			                panelMapPanel.setPreferredSize(new Dimension(map.getWidth() * MainEditor.TILE_SIZE, map.getHeight() * MainEditor.TILE_SIZE));
			                panelMapPanel.revalidate();
						}
					});
					mapDetailEditor.setVisible(true);
				}
			}
		});
		mnMap.add(mntmEdit);
		
		mntmGenerate = new JMenuItem("Generate...");
		mntmGenerate.addMouseListener(new MouseAdapter() {//Open map generator menu
			@Override
			public void mouseReleased(MouseEvent e) {
				if(mntmEdit.isEnabled())
					JOptionPane.showMessageDialog(frame, "Map generating is not yet implemented, sorry :(");
			}
		});
		mnMap.add(mntmGenerate);
		
		mntmReset = new JMenuItem("Reset");
		mntmReset.addMouseListener(new MouseAdapter(){//Resets the map, makes sure user really wants to do that
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if(mntmEdit.isEnabled()){
					int option = JOptionPane.showConfirmDialog(frame, "Are you sure you want to reset the map?", "Map Reset", JOptionPane.YES_NO_OPTION);
					if(option == JOptionPane.YES_OPTION){
						for(int i = 0; i < map.getHeight(); i++){
							for(int u = 0; u < map.getWidth(); u++){
								map.getTiles()[i][u] = "null";
								map.getElements()[i][u] = "null";
							}
						}
					}
				}
			}
			
		});
		mnMap.add(mntmReset);
		
		JMenu mnView = new JMenu("View");
		menuBar.add(mnView);
		
		mntmToggleGrid = new JCheckBoxMenuItem("Show Grid (Ctrl + G)");//Control grid showing
		mntmToggleGrid.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent e) {
				showGrid = mntmToggleGrid.isSelected();
			}
			
		});
		mnView.add(mntmToggleGrid);
		
		mntmToggleTiles = new JCheckBoxMenuItem("Show Tiles (Ctrl + T)");//Control tile showing
		mntmToggleTiles.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent e) {
				showTiles = mntmToggleTiles.isSelected();
				checkBoxTilesVisible.setSelected(mntmToggleTiles.isSelected());
			}
			
		});
		mnView.add(mntmToggleTiles);
		
		mntmToggleElements = new JCheckBoxMenuItem("Show Elements (Ctrl + E)");//Control element showing
		mntmToggleElements.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent e) {
				showElements = mntmToggleElements.isSelected();
				checkBoxElementsVisible.setSelected(mntmToggleElements.isSelected());
			}
			
		});
		mnView.add(mntmToggleElements);
		
		JMenu mnAbout = new JMenu("About");
		mnAbout.addMouseListener(new MouseAdapter() {//Opens about menu, with some info on this program
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
		
		//Divider to split tools from map
		JSplitPane splitPane = new JSplitPane();
		splitPane.setEnabled(false);
		splitPane.setDividerLocation(282);
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);
		
		panelMapPanel = new JPanel(){

			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g1d) {//Renders map, if one is loaded
				super.paintComponent(g1d);
				Graphics2D g = (Graphics2D) g1d;
				
				if (map != null) {
					g.clearRect(0, 0, map.getWidth() * TILE_SIZE, map.getWidth() * TILE_SIZE);
					
					//Calculates the visible area, this is good for big maps so that they don't take too much computing power.
					x = (map.getWidth() * TILE_SIZE <= scrollPane.getViewportBorderBounds().getWidth() ? (int) scrollPane.getViewportBorderBounds().getWidth() / 2 - (map.getWidth() * TILE_SIZE) / 2:0);
					y = (map.getHeight() * TILE_SIZE <= scrollPane.getViewportBorderBounds().getHeight() ? (int) scrollPane.getViewportBorderBounds().getHeight() / 2 - (map.getHeight() * TILE_SIZE) / 2:0);
					
					//Draws the map
					map.draw(assetManager, showTiles, showElements, showGrid, tileY, tileX, selectedLayer, list.getSelectedValue(), g, x, y, scrollPane.getHorizontalScrollBar().getValue() / TILE_SIZE, scrollPane.getVerticalScrollBar().getValue() / TILE_SIZE, scrollPane.getViewport().getWidth() / TILE_SIZE, scrollPane.getViewport().getHeight() / TILE_SIZE);
					
					//Draws coordinates of cursor on map
					g.setColor(Color.BLACK);
					g.drawString("X: " + tileY + " Y: " + (map.getHeight() - tileX - 1), scrollPane.getHorizontalScrollBar().getValue() + 5, scrollPane.getVerticalScrollBar().getValue() + 15);
				}
			}
			
			@Override
			public Dimension getPreferredSize() {
				if (map != null)
					return new Dimension(map.getWidth() * TILE_SIZE, map.getHeight() * TILE_SIZE);
				else
					return new Dimension(0, 0);
			}
			
		};
		
		panelMapPanel.setAutoscrolls(true);
		panelMapPanel.addMouseListener(new MouseAdapter(){//Determines if a tool was sued
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					if( spacePressed)
						origin = new Point(e.getPoint());
					else if (selectedLayer == 0) {
						changeList.addChanges(new TileMapChange(map));
						justSaved = false;
					} else if (selectedLayer == 1) {
						changeList.addChanges(new ElementMapChange(map));
						justSaved = false;
					}
						
					mouse1Pressed = true;
				}
				if(e.getButton() == MouseEvent.BUTTON3)
					mouse2Pressed = true;
			}
			
			//Determines if a tools was stopped being used
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					mouse1Pressed = false;
					origin = null;
				}
				if (e.getButton() == MouseEvent.BUTTON3)
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

			//Move map if space is pressed
			@Override
			public void mouseDragged(MouseEvent e) {
				mouseX = e.getX() - x;
				mouseY = e.getY() - y;
				if(origin == null) return;
				JViewport viewPort = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, panelMapPanel);
				if(viewPort == null) return;
				int deltaX = origin.x - e.getX();
				int deltaY = origin.y - e.getY();
				
				if (spacePressed) {
					scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getValue() + deltaY);
					scrollPane.getHorizontalScrollBar().setValue(scrollPane.getHorizontalScrollBar().getValue() + deltaX);
				}
			}

			//Keeps track of the mouse's location
			@Override
			public void mouseMoved(MouseEvent e) {
				mouseX = e.getX() - x;
				mouseY = e.getY() - y;
			}
			
			
		});
		
		panelMapPanel.setBackground(Color.WHITE);
		panelMapPanel.addMouseWheelListener(new MouseWheelListener() {
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {//Handles scrolling to move map
				if (controlPressed) {
					if (shiftPressed) {
						scrollPane.getHorizontalScrollBar().setValue(scrollPane.getHorizontalScrollBar().getValue() + e.getWheelRotation() * 100);
					} else {
						scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getValue() + e.getWheelRotation() * 100);
					}
				} else {
					if (list.getSelectedValue() != null)
						list.setSelectedIndex(list.getSelectedIndex() + e.getWheelRotation());
					else {
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
		
		//Load tool images
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		BufferedImage pencilIcon = null;
		BufferedImage bucketIcon = null;
		BufferedImage entityIcon = null;
		try {
			pencilIcon = ImageIO.read(classLoader.getResourceAsStream("images/icons/pencil.png"));
			bucketIcon = ImageIO.read(classLoader.getResourceAsStream("images/icons/bucket.png"));
			entityIcon = ImageIO.read(classLoader.getResourceAsStream("images/icons/entity.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		btnBucketTool = new JToggleButton(new ImageIcon(bucketIcon));
		final JToggleButton btnEntityTool = new JToggleButton(new ImageIcon(entityIcon));
		
		//Pencil
		final JToggleButton btnPencilTool = new JToggleButton(new ImageIcon(pencilIcon));
		btnPencilTool.setSelected(true);
		btnPencilTool.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				btnBucketTool.setSelected(false);
				btnEntityTool.setSelected(false);
				selectedTool = 0;
			}
		});
		GridBagConstraints gbc_btnPencilTool = new GridBagConstraints();
		gbc_btnPencilTool.insets = new Insets(0, 0, 5, 5);
		gbc_btnPencilTool.gridx = 0;
		gbc_btnPencilTool.gridy = 1;
		panel.add(btnPencilTool, gbc_btnPencilTool);
		
		//Bucket
		btnBucketTool.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				btnPencilTool.setSelected(false);
				btnEntityTool.setSelected(false);
				selectedTool = 1;
			}
		});
		GridBagConstraints gbc_btnBucketTool = new GridBagConstraints();
		gbc_btnBucketTool.insets = new Insets(0, 0, 5, 5);
		gbc_btnBucketTool.gridx = 1;
		gbc_btnBucketTool.gridy = 1;
		panel.add(btnBucketTool, gbc_btnBucketTool);
		
		//Entity
		btnEntityTool.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				btnPencilTool.setSelected(false);
				btnBucketTool.setSelected(false);
				selectedTool = 2;
			}
		});
		GridBagConstraints gbc_btnEntityTool = new GridBagConstraints();
		gbc_btnEntityTool.insets = new Insets(0, 0, 5, 5);
		gbc_btnEntityTool.gridx = 2;
		gbc_btnEntityTool.gridy = 1;
		panel.add(btnEntityTool, gbc_btnEntityTool);
		
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
				if (selectedLayer != 0) {
					selectedLayer = 0;
					rdbtnElements.setSelected(false);
					rdbtnTiles.setSelected(true);
					reloadLists();
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
				if (selectedLayer != 1) {
					selectedLayer = 1;
					rdbtnTiles.setSelected(false);
					rdbtnElements.setSelected(true);
					reloadLists();
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
		textFieldSearch.getDocument().addDocumentListener(new DocumentListener() {//If search field is changed, update list accordingly
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				reloadLists();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				reloadLists();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				reloadLists();
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
		
		//Load items to list
		list = new JList<Object>(assetManager.getMapTiles().toArray());
		list.setVisibleRowCount(-1);
		list.addListSelectionListener(new ListSelectionListener(){

			@Override
			public void valueChanged(ListSelectionEvent event) {
				if(list.getSelectedValue() != null){
					if(selectedLayer == 0)
						lblTileName.setText(((MapTile) list.getSelectedValue()).name);
					else if(selectedLayer == 1)
						lblTileName.setText(((MapElement) list.getSelectedValue()).name);
				}
			}
			
		});
		
		//Display tile/element info when right-clicked
		list.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON3){
					ListModel<Object> model = list.getModel();
					int index = list.locationToIndex(e.getPoint());
					if (index > -1) {
						list.setToolTipText(null);
						if (selectedLayer == 0) {
							MapTile tile = (MapTile) model.getElementAt(index);
							iconHoveredOver = tile;
							String text = "<html>"
						    		+ "ID: " + tile.id + "<br>"
						    		+ "Name: " + tile.name + "<br>"
						    		+ "Swimmable: " + tile.swimmable + "<br>"
						    		+ "Damage: " + tile.damage + "<br>"
						    		+ "Time(s) Between damage: " + tile.damageSpeed + "<br>"
						    		+ "Speed Multiplier: " + tile.speedMultiplier + "<br>"
						    		+ "Collision Table:<br>";
							if (tile.animated) {
								text += "# of Animation Frames: " + tile.numberOfFrames + "<br>"
									    + "Time(s) Between Frames: " + tile.animationSpeed + "<br>";
							}
							
							for(int i = 0; i < tile.collisionTable.length; i++){
								for(int u = 0; u < tile.collisionTable[0].length; u++){
									text += tile.collisionTable[i][u] ? 1 : 0;
								}
								text += "<br>";
							}
						    text += "</html>";
							list.setToolTipText(text);
						} else if(selectedLayer == 1) {
							MapElement element = (MapElement) model.getElementAt(index);
							iconHoveredOver = element;
							String text = "<html>"
						    		+ "ID: " + element.id + "<br>"
						    		+ "Name: " + element.name + "<br>"
						    		+ "Width: " + element.width + "<br>"
						    		+ "Height: " + element.height + "<br>"
						    		+ "OffsetX: " + element.offsetX + "<br>"
						    		+ "OffsetY: " + element.offsetY + "<br>"
				    				+ "Collision Table:<br>";
							if (element.animated) {
								text += "# of Animation Frames: " + element.numberOfFrames + "<br>"
									    + "Time(s) Between Frames: " + element.animationSpeed + "<br>";
							}
							
							for(int i = 0; i < element.height; i++){
								for(int u = 0; u < element.width; u++){
									text +=  element.collisionTable[i][u] ? 1 : 0;
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
			public void mouseEntered(MouseEvent arg0) {}

			@Override
			public void mouseExited(MouseEvent arg0) {}

			@Override
			public void mousePressed(MouseEvent e) {}

			@Override
			public void mouseReleased(MouseEvent arg0) {}
			
		});
		
		list.addMouseMotionListener(new MouseMotionListener(){

			@Override
			public void mouseDragged(MouseEvent e) {}

			@Override
			public void mouseMoved(MouseEvent e) {//Remove info if no longer hover on tile/element
				if (list.getModel().getElementAt(list.locationToIndex(e.getPoint())) != iconHoveredOver) {
					list.setToolTipText(null);
					iconHoveredOver = null;
				}
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
	
	//Method to reload assets
	public void reloadAssets(){
		assetManager.clear();
		assetManager.load();
		reloadLists();
		
        panelMapPanel.setPreferredSize(new Dimension(map.getWidth() * TILE_SIZE, map.getHeight() * TILE_SIZE));
        panelMapPanel.revalidate();
	}
	
	//Thread to edit map and keep track of things
	@Override
	public void run() {
		
		while (running) {
			//Limits app to running at 60fps, great for optimization
			long delta = System.currentTimeMillis() - startTime;
			long timeToSleep = (1000 / 60) - delta;
			try {
				Thread.sleep(timeToSleep > 0 ? timeToSleep : 0);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			startTime = System.currentTimeMillis();
			
			//Repaint map
			panelMapPanel.repaint();

			//Makes sure everything is properly set
			changeList.update();
			mntmSave.setEnabled(map != null);
			mntmSaveAs.setEnabled(map != null);
			mntmClose.setEnabled(map != null);
			mntmEdit.setEnabled(map != null);
			mntmGenerate.setEnabled(map != null);
			mntmReset.setEnabled(map != null);
			
			if(selectedLayer == 0)
				lblListTitle.setText("Tiles:");
			else
				lblListTitle.setText("Elements:");
			
			//Updates tilex/y
			tileX = mouseY / TILE_SIZE;
			tileY = mouseX / TILE_SIZE;
			
			//If a map is loaded and buttons are being pressed, edit the map
			if (map != null) {
				if (tileX < map.getHeight() && tileY < map.getWidth()  && tileX >= 0 && tileY >= 0 && map.getTiles() != null) {
					if (mouse1Pressed && !spacePressed) {
						switch (selectedLayer) {
						case TILE_LAYER:
							switch (selectedTool) {
							case PENCIL_TOOL:
								if(list.getSelectedValue() != null) {
									map.getTiles()[tileX][tileY] = ((MapTile) list.getSelectedValue()).id;
								}
								break;
							case BUCKET_TOOL:
								boolean[][] filledTiles = new boolean[map.getHeight()][map.getWidth()];
								String replaceTile = map.getTiles()[tileX][tileY];
								bucketFillTiles(replaceTile, filledTiles, tileX, tileY);
								break;
							}
							break;
						case ELEMENT_LAYER:
							switch (selectedTool) {
							case PENCIL_TOOL:
								if (list.getSelectedValue() != null) {
									map.getElements()[tileX][tileY] = ((MapElement) list.getSelectedValue()).id;
								}
								break;
							case BUCKET_TOOL:
								boolean[][] filledTiles = new boolean[map.getHeight()][map.getWidth()];
								String replaceTile = map.getElements()[tileX][tileY];
								bucketFillElements(replaceTile, filledTiles, tileX, tileY);
								break;
							}
							break;
						}
					}
					
					if (mouse2Pressed) {
						switch (selectedLayer) {
						case TILE_LAYER:
							list.setSelectedValue(assetManager.getTileByID(map.getTiles()[tileX][tileY]), true);
							break;
						case ELEMENT_LAYER:
							list.setSelectedValue(assetManager.getElementByID(map.getElements()[tileX][tileY]), true);
							break;
						}
					}
				}
			}
		}
		
	}

	//Recursion bucket fill algorithm for tiles
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

	//Recursion bucket fill algorithm for elements
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
	
	//Shows new map dialog
	public void showNewMapDialog () {
		NewMapMenu newMapMenu = new NewMapMenu(new NewMapMenu.NewMapMenuListener() {
			
			@Override
			public void newMapCreated (Map map_) {
				map = map_;
				
                panelMapPanel.setPreferredSize(new Dimension(map.getWidth() * TILE_SIZE, map.getHeight() * TILE_SIZE));
                panelMapPanel.revalidate();
				saveLocation = null;
				lblMapPath.setText("");
			}
		});
		newMapMenu.setVisible(true);
	}
	
	//Shows map open dialog
	public boolean showMapOpenDialog () {
		JFileChooser openFile = new JFileChooser(System.getProperty("user.home") + "/Desktop");
		openFile.setFileFilter(new FileNameExtensionFilter("json", "json"));
    	openFile.showOpenDialog(null);
    	File selectedFile = openFile.getSelectedFile();
    	
    	if (selectedFile == null) 
    		return false;
    	else {
    		saveLocation = selectedFile.getPath();
    		lblMapPath.setText("         " + saveLocation);
    		map = new Map();
    		map.load(selectedFile);
    		panelMapPanel.setPreferredSize(new Dimension(map.getWidth() * TILE_SIZE, map.getHeight() * TILE_SIZE));
    		panelMapPanel.revalidate();
    		return true;
    	}
	}
	
	//shows map save dialog
	public boolean showMapSaveDialog (boolean forceNewLocation) {
		if (saveLocation == null || forceNewLocation) {
			JFileChooser saveFile = new JFileChooser(System.getProperty("user.home") + "/Desktop");
			saveFile.setFileFilter(new FileNameExtensionFilter("json", "json"));
            saveFile.showSaveDialog(null);
            File selectedFile = saveFile.getSelectedFile();
            
            if (selectedFile == null)
            	return false;
            else {
            	saveLocation = selectedFile.getPath();
            	saveLocation = saveLocation.replaceFirst("[.][^.]+$", "");
            	saveLocation = saveLocation + ".json";
            	lblMapPath.setText("         " + saveLocation);
                map.save(new File(saveLocation));
                justSaved = true;
                JOptionPane.showMessageDialog(frame, "You map was successfully saved!", "Saved!", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
		} else {
			map.save(new File(saveLocation));
			justSaved = true;
            JOptionPane.showMessageDialog(frame, "You map was successfully saved!", "Saved!", JOptionPane.INFORMATION_MESSAGE);
			return true;
		}
	}
	
	//Reloads the lists depending on the seleted layer
	public void reloadLists () {
		ArrayList<Icon> iconList = new ArrayList<Icon>();
		switch (selectedLayer) {
		case 0:

			for (MapTile tile : assetManager.getMapTiles()){
				if(tile.name.toLowerCase().contains(textFieldSearch.getText().toLowerCase()))
					iconList.add(tile);
			}
			break;
		case 1:
			for (MapElement element : assetManager.getMapElements()){
				if (element.name.toLowerCase().contains(textFieldSearch.getText().toLowerCase()))
					iconList.add(element);
			}
			break;
		}

		lblTileName.setText("");
		list.clearSelection();
		list.setListData(iconList.toArray());
	}
	
	public Map getMap () {
		return map;
	}
	
	public ChangeList getChangeList () {
		return changeList;
	}
	
}