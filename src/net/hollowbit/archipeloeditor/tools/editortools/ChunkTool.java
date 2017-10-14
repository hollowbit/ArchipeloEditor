package net.hollowbit.archipeloeditor.tools.editortools;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.hollowbit.archipeloeditor.MainEditor;
import net.hollowbit.archipeloeditor.changes.ChunkAddChange;
import net.hollowbit.archipeloeditor.changes.ChunkRemoveChange;
import net.hollowbit.archipeloeditor.changes.MapChange;
import net.hollowbit.archipeloeditor.tools.generators.Generator;
import net.hollowbit.archipeloeditor.tools.generators.IslandGenerator;
import net.hollowbit.archipeloeditor.tools.generators.ResetGenerator;
import net.hollowbit.archipeloeditor.world.Chunk;
import net.hollowbit.archipeloeditor.world.ChunkRow;
import net.hollowbit.archipeloeditor.worldeditor.WorldEditor;
import net.hollowbit.archipeloshared.ChunkData;

public class ChunkTool extends Tool {
	
	int selectedChunkX, selectedChunkY;
	boolean isChunkSelected = false;
	
	JButton addRemoveBtn;
	JLabel generateLbl;
	JComboBox<Generator> generatorComboBox;
	JButton generateBtn;
	
	public ChunkTool(MainEditor editor, WorldEditor worldRenderer) {
		super(editor, worldRenderer);
	}

	@Override
	public void addComponents(JPanel panel) {
		addRemoveBtn = new JButton("Add/Remove");
		addRemoveBtn.setSize(120, 40);
		addRemoveBtn.setPreferredSize(new Dimension(120, 40));
		GridBagConstraints gdc_addRemoveBtn = new GridBagConstraints();
		gdc_addRemoveBtn.insets = new Insets(0, 0, 5, 5);
		gdc_addRemoveBtn.gridx = 0;
		gdc_addRemoveBtn.gridy = 0;
		gdc_addRemoveBtn.gridwidth = 5;
		
		addRemoveBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addRemove();
			}
			
		});
		addRemoveBtn.setVisible(false);
		panel.add(addRemoveBtn, gdc_addRemoveBtn);
		
		//Generator label
		generateLbl = new JLabel("Generator:");
		generateLbl.setPreferredSize(new Dimension(194, 15));

		GridBagConstraints gbc_generateLbl = new GridBagConstraints();
		gbc_generateLbl.insets = new Insets(0, 0, 5, 5);
		gbc_generateLbl.anchor = GridBagConstraints.WEST;
		gbc_generateLbl.gridx = 0;
		gbc_generateLbl.gridy = 1;
		gbc_generateLbl.gridwidth = 3;
		generateLbl.setVisible(false);
		panel.add(generateLbl, gbc_generateLbl);
		
		//Combo box
		generatorComboBox =  new JComboBox<Generator>();
		generatorComboBox.setPreferredSize(new Dimension(194, 35));
		
		//Add generators
		generatorComboBox.addItem(new IslandGenerator(editor.getMap()));
		generatorComboBox.addItem(new ResetGenerator());
		
		GridBagConstraints gbc_generatorComboBox = new GridBagConstraints();
		gbc_generatorComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_generatorComboBox.anchor = GridBagConstraints.WEST;
		gbc_generatorComboBox.gridx = 0;
		gbc_generatorComboBox.gridy = 2;
		gbc_generatorComboBox.gridwidth = 3;
		generatorComboBox.setVisible(false);
		panel.add(generatorComboBox, gbc_generatorComboBox);
		
		//Generate button
		generateBtn = new JButton("Generate");
		generateBtn.setSize(120, 40);
		generateBtn.setPreferredSize(new Dimension(120, 40));
		GridBagConstraints gdc_generateBtn = new GridBagConstraints();
		gdc_generateBtn.insets = new Insets(0, 0, 5, 5);
		gdc_generateBtn.gridx = 0;
		gdc_generateBtn.gridy = 3;
		gdc_generateBtn.gridwidth = 3;
		
		generateBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				generate();
			}
			
		});
		generateBtn.setVisible(false);
		panel.add(generateBtn, gdc_generateBtn);
	}
	
	private void generate() {
		if (editor.getMap() != null && isChunkSelected) {
			Chunk mapChunk = editor.getMap().getChunk(selectedChunkX, selectedChunkY);
			if (mapChunk != null) {
				MapChange change = new MapChange(editor.getMap());
				change.addChunk(mapChunk);
				
				editor.getChangeList().addChanges(change);
				editor.setJustSaved(false);
				
				Chunk chunk = ((Generator) generatorComboBox.getSelectedItem()).generate(selectedChunkX, selectedChunkY);
				mapChunk.set(chunk);
			}
		}
	}
	
	private void addRemove() {
		if (editor.getMap() != null && isChunkSelected) {
			Chunk chunk = editor.getMap().getChunk(selectedChunkX, selectedChunkY);
			if (chunk != null) {
				editor.getChangeList().addChanges(new ChunkRemoveChange(editor.getMap(), editor.getMap().getChunk(selectedChunkX, selectedChunkY)));
				editor.setJustSaved(false);
				editor.getMap().removeChunk(selectedChunkX, selectedChunkY);
			} else {
				editor.getChangeList().addChanges(new ChunkAddChange(editor.getMap(), selectedChunkX, selectedChunkY));
				editor.setJustSaved(false);
				editor.getMap().addChunk(selectedChunkX, selectedChunkY);
			}
		}
	}

	@Override
	public void render(SpriteBatch batch) {
		if (editor.getMap() != null) {
			if (Gdx.input.isKeyJustPressed(Keys.G) && worldRenderer.shiftPressed())
				generate();
			
			if (Gdx.input.isKeyJustPressed(Keys.SPACE) && worldRenderer.shiftPressed())
				addRemove();
			
			if (Gdx.input.isKeyJustPressed(Keys.UP) && isChunkSelected)
				selectedChunkY++;
			if (Gdx.input.isKeyJustPressed(Keys.LEFT) && isChunkSelected)
				selectedChunkX--;
			if (Gdx.input.isKeyJustPressed(Keys.DOWN) && isChunkSelected)
				selectedChunkY--;
			if (Gdx.input.isKeyJustPressed(Keys.RIGHT) && isChunkSelected)
				selectedChunkX++;
			
			for (ChunkRow row : editor.getMap().getChunkRows().values()) {
				for (Chunk chunk : row.getChunks().values()) {
					batch.setColor(0, 0, 0, 1);
					batch.draw(editor.getAssetManager().getChunkTexture(), chunk.getPixelX(), chunk.getPixelY(), ChunkData.SIZE * MainEditor.TILE_SIZE, ChunkData.SIZE * MainEditor.TILE_SIZE);
					batch.setColor(1, 1, 1, 1);
				}
			}
			
			if (isChunkSelected) {
				batch.setColor(1, 1, 0, 1);
				batch.draw(editor.getAssetManager().getChunkTexture(), selectedChunkX * ChunkData.SIZE * MainEditor.TILE_SIZE, selectedChunkY * ChunkData.SIZE * MainEditor.TILE_SIZE, ChunkData.SIZE * MainEditor.TILE_SIZE, ChunkData.SIZE * MainEditor.TILE_SIZE);
				batch.setColor(1, 1, 1, 1);
			}
			
		}
	}

	@Override
	public void touchDown(float x, float y, int tileX, int tileY, int button) {
		if (editor.getMap() != null) {
			selectedChunkX = (int) Math.floor((float) tileX / ChunkData.SIZE);
			selectedChunkY = (int) Math.floor((float) tileY / ChunkData.SIZE);
			isChunkSelected = true;
			
			addRemoveBtn.setVisible(true);
			generateLbl.setVisible(true);
			generatorComboBox.setVisible(true);
			generateBtn.setVisible(true);
		}
	}

	@Override
	public void touchUp(float x, float y, int tileX, int tileY, int button) {
	}

	@Override
	public void touchDragged(float x, float y, int tileX, int tileY) {
	}

	@Override
	public void mouseScrolled(int amount) {
	}

}
