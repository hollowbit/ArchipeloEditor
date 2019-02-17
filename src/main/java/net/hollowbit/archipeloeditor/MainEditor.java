package net.hollowbit.archipeloeditor;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglCanvas;

import net.hollowbit.archipeloeditor.changes.ChangeList;
import net.hollowbit.archipeloeditor.tools.editortools.Bucket;
import net.hollowbit.archipeloeditor.tools.editortools.ChunkTool;
import net.hollowbit.archipeloeditor.tools.editortools.EntityAdderTool;
import net.hollowbit.archipeloeditor.tools.editortools.Pencil;
import net.hollowbit.archipeloeditor.tools.editortools.Tool;
import net.hollowbit.archipeloeditor.tools.editortools.TransitionPlacer;
import net.hollowbit.archipeloeditor.world.AssetManager;
import net.hollowbit.archipeloeditor.world.Map;
import net.hollowbit.archipeloeditor.worldeditor.WorldEditor;
import net.hollowbit.archipeloshared.InvalidMapFolderException;

public class MainEditor implements Runnable {

	public static final int TILE_SIZE = 16;
	public static final String PATH = new File(".").getAbsolutePath();
	
	public static final int TILE_LAYER = 0;
	public static final int ELEMENT_LAYER = 1;
	
	public static final int PENCIL_TOOL = 0;
	public static final int BUCKET_TOOL = 1;
	public static final int ENTITY_TOOL = 2;
	public static final int CHUNK_TOOL = 3;
	public static final int TRANSITION_TOOL = 4;
	
	public static BufferedImage ICON;
	public static Cursor CURSOR;
	
	private boolean showTiles = true;
	private boolean showElements = true;
	private boolean showGrid = false;
	private boolean showCollisionMap = false;
	
	private Tool selectedTool;
	private String saveLocation = null;
	
	private JLabel lblMapPath;
	private WorldEditor worldEditor;
	private LwjglCanvas lwjglCanvas;
	private JMenuItem mntmSave;
	private JMenuItem mntmSaveAs;
	private JMenuItem mntmReload;
	private JMenuItem mntmReloadCollisions;
	private JMenuItem mntmClose;
	private JMenuItem mntmEdit;
	private JToggleButton btnBucketTool;
	private JPanel toolSettingsPanel;
	
	private JFrame frame;
	private JFrame worldEditorWindow;
	
	private Thread thread;
	private boolean running = true;
	
	private Map map;
	
	long startTime = 0;
	
	private AssetManager assetManager;
	private ChangeList mapChangeList;
	private ChangeList entityChangeList;
	
	public boolean justSaved = true;
	
	JCheckBoxMenuItem mntmToggleGrid;
	JCheckBoxMenuItem mntmToggleTiles;
	JCheckBoxMenuItem mntmToggleElements;
	JCheckBoxMenuItem mntmToggleCollisionMap;
	
	private HashMap<String, Boolean> openWindows;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//Load basic images for editor
					ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
					ICON = ImageIO.read(classLoader.getResourceAsStream("images/icon.png"));
					CURSOR = Toolkit.getDefaultToolkit().createCustomCursor(ImageIO.read(classLoader.getResourceAsStream("images/cursor.png")), new Point(8, 8), "blank");
					MainEditor window = new MainEditor();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public MainEditor() {
		assetManager = new AssetManager();
		//Map renderer
		worldEditorWindow = new JFrame("Edit map...");
		worldEditor = new WorldEditor(this, assetManager);
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		lwjglCanvas = new LwjglCanvas(worldEditor, config);
		lwjglCanvas.setCursor(CURSOR);
		
		//Initialize
		mapChangeList = new ChangeList(this);
		entityChangeList = new ChangeList(this);
		openWindows = new HashMap<String, Boolean>();
		initialize();
		
		startTime = System.currentTimeMillis();
		thread = new Thread(this);
		thread.start();
	}
	
	//Method to initialize all components, keeps the constructor clean
	private void initialize() {
		MainEditor editor = this;
		
		//Map editor window
		worldEditorWindow.setBounds(100, 100, 1300, 900);
		worldEditorWindow.setLocationRelativeTo(null);
		worldEditorWindow.setIconImage(ICON);
		worldEditorWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		worldEditorWindow.add(lwjglCanvas.getCanvas());
		worldEditorWindow.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				lwjglCanvas.getCanvas().setBounds(0, 0, e.getComponent().getSize().width, e.getComponent().getSize().height);
				lwjglCanvas.getCanvas().revalidate();
				super.componentResized(e);
			}
		});
		
		worldEditorWindow.setVisible(true);
		
		frame = new JFrame("Archipelo Map Editor v1.0");
		frame.setBounds(100, 100, 300, 900);
		frame.setLocationRelativeTo(worldEditorWindow);
		frame.setLocation(worldEditorWindow.getLocation().x - frame.getWidth() - 5, worldEditorWindow.getLocation().y);
		frame.setIconImage(ICON);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.setGlobalCurrentFocusCycleRoot(frame);
		
		WindowListener mainWindowListener = new WindowListener() {
			
			//Event for window close, make sure user saved first before exiting
			@Override
			public void windowClosing(WindowEvent e) {
				if (justSaved) {
					try {
						running = false;
						thread.join();
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					lwjglCanvas.exit();
					frame.dispose();
					worldEditorWindow.dispose();
					return;
				}
				
				boolean saved = false;
				while(!saved){
					int option = JOptionPane.showConfirmDialog(frame, "Would you like to save first?", "Map Close", JOptionPane.YES_NO_CANCEL_OPTION);
					if (option == JOptionPane.YES_OPTION) {
						showMapSaveDialog(false);
					} else if (option == JOptionPane.NO_OPTION) {
						saved = true;
						try {
							running = false;
							thread.join();
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						lwjglCanvas.exit();
						frame.dispose();
						worldEditorWindow.dispose();
					} else
						break;
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
			
		};
		
		frame.addWindowListener(mainWindowListener);
		worldEditorWindow.addWindowListener(mainWindowListener);
		
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
		
		mntmReload = new JMenuItem("Reload Assets (F5)");
		mntmReload.addMouseListener(new MouseAdapter() {//Same as save but forces saving in a new location
			@Override
			public void mouseReleased(MouseEvent e) {
				worldEditor.reloadAssets();
			}
		});
		mnFile.add(mntmReload);
		
		mntmReloadCollisions = new JMenuItem("Reload Collision Map (F6)");
		mntmReloadCollisions.addMouseListener(new MouseAdapter() {//Same as save but forces saving in a new location
			@Override
			public void mouseReleased(MouseEvent e) {
				if (map != null)
					map.regenerateCollisionMaps(assetManager);
			}
		});
		mnFile.add(mntmReloadCollisions);
		
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
				mapChangeList.reset();
				entityChangeList.reset();
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
				selectedTool.getChangeList().undo();
			}
			
		});
		mnEdit.add(mntmUndo);
		
		JMenuItem mntmRedo = new JMenuItem("Redo (Ctrl + Y)");
		mntmRedo.addMouseListener(new MouseAdapter(){//redo, but within menu
			
			@Override
			public void mouseReleased(MouseEvent e) {
				selectedTool.getChangeList().redo();
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
			                /*panelMapPanel.setPreferredSize(new Dimension(map.getWidth() * MainEditor.TILE_SIZE, map.getHeight() * MainEditor.TILE_SIZE));
			                panelMapPanel.revalidate();*/
						}
					});
					mapDetailEditor.setVisible(true);
				}
			}
		});
		mnMap.add(mntmEdit);
		
		JMenu mnView = new JMenu("View");
		menuBar.add(mnView);
		
		mntmToggleGrid = new JCheckBoxMenuItem("Show Grid (G)");//Control grid showing
		mntmToggleGrid.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent e) {
				showGrid = mntmToggleGrid.isSelected();
				if (selectedTool != null)
					selectedTool.updateVisibilities(showTiles, showElements, showGrid, showCollisionMap);
			}
			
		});
		mnView.add(mntmToggleGrid);
		
		mntmToggleTiles = new JCheckBoxMenuItem("Show Tiles (T)");//Control tile showing
		mntmToggleTiles.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent e) {
				showTiles = mntmToggleTiles.isSelected();
				if (selectedTool != null)
					selectedTool.updateVisibilities(showTiles, showElements, showGrid, showCollisionMap);
			}
			
		});
		mnView.add(mntmToggleTiles);
		
		mntmToggleElements = new JCheckBoxMenuItem("Show Elements (E)");//Control element showing
		mntmToggleElements.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent e) {
				showElements = mntmToggleElements.isSelected();
				if (selectedTool != null)
					selectedTool.updateVisibilities(showTiles, showElements, showGrid, showCollisionMap);
			}
			
		});
		mnView.add(mntmToggleElements);
		
		mntmToggleCollisionMap = new JCheckBoxMenuItem("Show Collision Map (C)");//Control element showing
		mntmToggleCollisionMap.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent e) {
				showCollisionMap = mntmToggleCollisionMap.isSelected();
				if (selectedTool != null)
					selectedTool.updateVisibilities(showTiles, showElements, showGrid, showCollisionMap);
			}
			
		});
		mnView.add(mntmToggleCollisionMap);
		
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
		
		//Tools pane
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		
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
		BufferedImage chunkIcon = null;
		try {
			pencilIcon = ImageIO.read(classLoader.getResourceAsStream("images/icons/pencil.png"));
			bucketIcon = ImageIO.read(classLoader.getResourceAsStream("images/icons/bucket.png"));
			entityIcon = ImageIO.read(classLoader.getResourceAsStream("images/icons/entity.png"));
			chunkIcon = ImageIO.read(classLoader.getResourceAsStream("images/icons/chunk.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		btnBucketTool = new JToggleButton(new ImageIcon(bucketIcon));
		final JToggleButton btnEntityTool = new JToggleButton(new ImageIcon(entityIcon));
		final JToggleButton btnChunkTool = new JToggleButton(new ImageIcon(chunkIcon));
		final JToggleButton btnTransitionTool = new JToggleButton(new ImageIcon(chunkIcon));
		
		//Pencil
		final JToggleButton btnPencilTool = new JToggleButton(new ImageIcon(pencilIcon));
		btnPencilTool.setSelected(true);
		selectedTool = new Pencil(editor, worldEditor);
		btnPencilTool.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				btnBucketTool.setSelected(false);
				btnEntityTool.setSelected(false);
				btnChunkTool.setSelected(false);
				btnTransitionTool.setSelected(false);
				setTool(new Pencil(editor, worldEditor));
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
				btnChunkTool.setSelected(false);
				btnTransitionTool.setSelected(false);
				setTool(new Bucket(editor, worldEditor));
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
				btnChunkTool.setSelected(false);
				btnTransitionTool.setSelected(false);
				setTool(new EntityAdderTool(editor, worldEditor));
			}
		});
		GridBagConstraints gbc_btnEntityTool = new GridBagConstraints();
		gbc_btnEntityTool.insets = new Insets(0, 0, 5, 5);
		gbc_btnEntityTool.gridx = 2;
		gbc_btnEntityTool.gridy = 1;
		panel.add(btnEntityTool, gbc_btnEntityTool);
		
		//Chunk
		btnChunkTool.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				btnPencilTool.setSelected(false);
				btnBucketTool.setSelected(false);
				btnEntityTool.setSelected(false);
				btnTransitionTool.setSelected(false);
				setTool(new ChunkTool(editor, worldEditor));
			}
		});
		GridBagConstraints gbc_btnChunkTool = new GridBagConstraints();
		gbc_btnChunkTool.insets = new Insets(0, 0, 5, 5);
		gbc_btnChunkTool.gridx = 3;
		gbc_btnChunkTool.gridy = 1;
		panel.add(btnChunkTool, gbc_btnChunkTool);
		
		//Transitions
		btnTransitionTool.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				btnPencilTool.setSelected(false);
				btnBucketTool.setSelected(false);
				btnEntityTool.setSelected(false);
				btnChunkTool.setSelected(false);
				setTool(new TransitionPlacer(editor, worldEditor));
			}
		});
		GridBagConstraints gbc_btnTransitionTool = new GridBagConstraints();
		gbc_btnTransitionTool.insets = new Insets(0, 0, 5, 5);
		gbc_btnTransitionTool.gridx = 4;
		gbc_btnTransitionTool.gridy = 1;
		panel.add(btnTransitionTool, gbc_btnTransitionTool);
		
		//Tool Settings
		toolSettingsPanel = new JPanel();
		
		GridBagLayout gbl_toolSettingsPanel = new GridBagLayout();
		gbl_toolSettingsPanel.columnWidths = new int[] {29, 33, 33, 33, 33};
		gbl_toolSettingsPanel.rowHeights = new int[]{14, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_toolSettingsPanel.columnWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_toolSettingsPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		toolSettingsPanel.setLayout(gbl_toolSettingsPanel);
		
		GridBagConstraints gbc_toolSettingsPanel = new GridBagConstraints();
		gbc_toolSettingsPanel.insets = new Insets(0, 0, 0, 0);
		gbc_toolSettingsPanel.gridx = 0;
		gbc_toolSettingsPanel.gridwidth = 5;
		gbc_toolSettingsPanel.gridy = 2;
		selectedTool.addComponents(toolSettingsPanel);
		panel.add(toolSettingsPanel, gbc_toolSettingsPanel);
	}
	
	public void setTool(Tool newTool) {
		selectedTool = newTool;
		toolSettingsPanel.removeAll();
		selectedTool.addComponents(toolSettingsPanel);
		toolSettingsPanel.revalidate();
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

			//Makes sure everything is properly set
			mapChangeList.update();
			entityChangeList.update();
			mntmSave.setEnabled(map != null);
			mntmSaveAs.setEnabled(map != null);
			mntmClose.setEnabled(map != null);
			mntmEdit.setEnabled(map != null);
		}
		
	}
	
	//Shows new map dialog
	public void showNewMapDialog () {
		NewMapMenu newMapMenu = new NewMapMenu(this, new NewMapMenu.NewMapMenuListener() {
			
			@Override
			public void newMapCreated (Map map_) {
				map = map_;
				saveLocation = null;
				lblMapPath.setText("");
			}
		});
		newMapMenu.setVisible(true);
	}
	
	//Shows map open dialog
	public boolean showMapOpenDialog () {
		JFileChooser openFile = new JFileChooser(System.getProperty("user.home") + "/Desktop");
		openFile.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		openFile.setAcceptAllFileFilterUsed(false);
    	openFile.showOpenDialog(null);
    	File selectedFile = openFile.getSelectedFile();
    	
    	if (selectedFile == null) 
    		return false;
    	else {
    		saveLocation = selectedFile.getParentFile().getPath();
    		lblMapPath.setText("         " + saveLocation);
    		map = new Map();
    		try {
				map.load(selectedFile);
			} catch (InvalidMapFolderException e) {
				map = null;
				JOptionPane.showMessageDialog(frame, "Could not load the given map folder: " + e.getMessage(), "Error Loading Map", JOptionPane.ERROR_MESSAGE);
			}
    		return true;
    	}
	}
	
	//shows map save dialog
	public boolean showMapSaveDialog (boolean forceNewLocation) {
		if (saveLocation == null || forceNewLocation) {
			JFileChooser saveFile = new JFileChooser(System.getProperty("user.home") + "/Desktop");
			saveFile.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			saveFile.setAcceptAllFileFilterUsed(false);
            saveFile.showSaveDialog(null);
            File selectedFile = saveFile.getSelectedFile();
            
            if (selectedFile == null)
            	return false;
            else {
            	saveLocation = selectedFile.getPath();
            	lblMapPath.setText("         " + saveLocation + "/" + map.getName());
            	boolean error = false;
                try {
					map.save(new File(saveLocation));
				} catch (IOException e) {
					JOptionPane.showMessageDialog(frame, "Could not save the map: " + e.getMessage(), "Error Saving Map", JOptionPane.ERROR_MESSAGE);
					error = true;
				}
                
                if (!error) {
	                justSaved = true;
	                JOptionPane.showMessageDialog(frame, "You map was successfully saved!", "Saved!", JOptionPane.INFORMATION_MESSAGE);
                }
                return true;
            }
		} else {
			boolean error = false;
			try {
				map.save(new File(saveLocation));
			} catch (IOException e) {
				JOptionPane.showMessageDialog(frame, "Could not save the map: " + e.getMessage(), "Error Saving Map", JOptionPane.ERROR_MESSAGE);
				error = true;
			}
			
			if (!error) {
				justSaved = true;
            	JOptionPane.showMessageDialog(frame, "You map was successfully saved!", "Saved!", JOptionPane.INFORMATION_MESSAGE);
			}
			return true;
		}
	}
	
	public Map getMap () {
		return map;
	}
	
	public ChangeList getMapChangeList () {
		return mapChangeList;
	}
	
	public ChangeList getEntityChangeList () {
		return entityChangeList;
	}
	
	public void addOpenWindow(String type) {
		openWindows.put(type, true);
	}
	
	public void removeOpenWindow(String type) {
		openWindows.put(type, false);
	}
	
	public boolean isWindowOpen(String type) {
		if (openWindows.containsKey(type))
			return openWindows.get(type).booleanValue();
		else
			return false;
	}

	public AssetManager getAssetManager() {
		return assetManager;
	}

	public boolean showTiles() {
		return showTiles;
	}

	public boolean showMapElements() {
		return showElements;
	}

	public boolean showGrid() {
		return showGrid;
	}
	
	public boolean showCollisionMap() {
		return showCollisionMap;
	}

	public void setShowTiles(boolean showTiles) {
		this.showTiles = showTiles;
		mntmToggleTiles.setSelected(showTiles);
		if (selectedTool != null)
			selectedTool.updateVisibilities(showTiles, showElements, showGrid, showCollisionMap);
	}

	public void setShowElements(boolean showElements) {
		this.showElements = showElements;
		mntmToggleElements.setSelected(showElements);
		if (selectedTool != null)
			selectedTool.updateVisibilities(showTiles, showElements, showGrid, showCollisionMap);
	}

	public void setShowGrid(boolean showGrid) {
		this.showGrid = showGrid;
		mntmToggleGrid.setSelected(showGrid);
		if (selectedTool != null)
			selectedTool.updateVisibilities(showTiles, showElements, showGrid, showCollisionMap);
	}
	
	public void setShowCollisionMap(boolean showCollisionMap) {
		this.showCollisionMap = showCollisionMap;
		mntmToggleCollisionMap.setSelected(showCollisionMap);
		if (selectedTool != null)
			selectedTool.updateVisibilities(showTiles, showElements, showGrid, showCollisionMap);
	}
	
	public Tool getSelectedTool() {
		return selectedTool;
	}
	
	public void setJustSaved(boolean justSaved) {
		this.justSaved = justSaved;
	}
	
	public void undo() {
		selectedTool.getChangeList().undo();
	}
	
	public void redo() {
		selectedTool.getChangeList().redo();
	}
	
	public void save() {
		showMapSaveDialog(false);
	}
	
	public JFrame getMainWindow() {
		return frame;
	}
	
	public JFrame getWorldEditorWindow() {
		return worldEditorWindow;
	}
	
	public WorldEditor getWorldEditor() {
		return worldEditor;
	}
	
}