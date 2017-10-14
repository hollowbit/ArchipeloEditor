package net.hollowbit.archipeloeditor.worldeditor.modes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import net.hollowbit.archipeloeditor.world.AssetManager;
import net.hollowbit.archipeloeditor.world.Map;
import net.hollowbit.archipeloeditor.worldeditor.WorldEditor;
import net.hollowbit.archipeloeditor.worldeditor.WorldEditorMode;
import net.hollowbit.archipeloshared.Point;

public class PointWorldEditorMode extends WorldEditorMode<Point> {
	
	public PointWorldEditorMode(Point defaultPoint, Map map, WorldEditorMode.WorldEditorModeListener<Point> listener) {
		super("Select a Point on the map. Press enter to continue or ESC to cancel.", defaultPoint, map, listener);
	}
	
	@Override
	public void setWorldEditor(WorldEditor worldEditor) {
		super.setWorldEditor(worldEditor);
		if (object != null)
			worldEditor.getCam().move(object.x, object.y, 0);
	}
	
	@Override
	public void render(SpriteBatch batch, AssetManager assetManager) {
		if (object != null) {
			batch.draw(assetManager.getLocationIcon(), object.x - assetManager.getLocationIcon().getWidth() / 2, object.y);
		}
		super.render(batch, assetManager);
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (Gdx.input.isKeyPressed(Keys.SPACE))
			return super.touchUp(screenX, screenY, pointer, button);
		
		if (worldEditor != null) {
			Vector2 mouseLocation = worldEditor.getCam().unproject(new Vector2(screenX, screenY));
			object = new Point((int) mouseLocation.x, (int) mouseLocation.y);
		}
		return super.touchUp(screenX, screenY, pointer, button);
	}
	
	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Keys.ENTER)
			finishMode();
		else if (keycode == Keys.ESCAPE)
			cancelMode();
		return super.keyDown(keycode);
	}

}
