package net.hollowbit.archipeloeditor.tools.editortools;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.hollowbit.archipeloeditor.MainEditor;
import net.hollowbit.archipeloeditor.entity.EntityType;
import net.hollowbit.archipeloeditor.tools.propertydefiners.entitydefiner.EntityDefinerWindow;
import net.hollowbit.archipeloeditor.tools.propertydefiners.entitydefiner.EntityDefinerWindow.EntityDefinerListener;
import net.hollowbit.archipeloeditor.worldeditor.WorldEditor;
import net.hollowbit.archipeloshared.EntitySnapshot;
import net.hollowbit.archipeloshared.Point;

public class EntityAdderTool extends Tool {
	
	JComboBox<EntityType> entityType;
	JPanel entityDefinerPanel;
	JPanel panel;
	
	public EntityAdderTool(MainEditor editor, WorldEditor worldRenderer) {
		super(editor, worldRenderer);
	}

	@Override
	public void addComponents(JPanel panel) {
		this.panel = panel;
	}

	@Override
	public void render(SpriteBatch batch) {
		
	}

	@Override
	public void touchDown(float x, float y, int tileX, int tileY, int button) {}

	@Override
	public void touchUp(float x, float y, int tileX, int tileY, int button) {
		if (editor.getMap() == null || tileX >= editor.getMap().getMaxTileX() || tileY >= editor.getMap().getMaxTileY() || tileX < editor.getMap().getMinTileX() || tileY < editor.getMap().getMinTileY())
			return;
		
		new EntityDefinerWindow("", new Point(Math.round(x), Math.round(y)), editor.getMainWindow(), editor, new EntityDefinerListener() {
			
			@Override
			public void complete(EntitySnapshot snapshot) {
				//TODO set value of textarea to the snapshot
			}
		});
	}

	@Override
	public void touchDragged(float x, float y, int tileX, int tileY) {}

	@Override
	public void mouseScrolled(int amount) {}

}
