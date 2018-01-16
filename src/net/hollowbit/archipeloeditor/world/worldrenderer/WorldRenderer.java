package net.hollowbit.archipeloeditor.world.worldrenderer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import net.hollowbit.archipeloeditor.MainEditor;
import net.hollowbit.archipeloeditor.world.AssetManager;
import net.hollowbit.archipeloeditor.world.Map;
import net.hollowbit.archipeloshared.CollisionRect;

public class WorldRenderer extends ApplicationAdapter implements InputProcessor {
	
	public static final float REDO_UNDO_TIMER = 0.15f;
	public static final float UNITS_PER_PIXEL = 1 / 3f;//World pixels per screen pixel.
	protected static final float ZOOM_SCALE = 0.4f;
	
	protected MainEditor editor;
	protected AssetManager assetManager;
	protected SpriteBatch batch;
	protected GameCamera cam;
	protected UiCamera uiCam;
	protected InputMultiplexer inputMultiplexer;
	protected boolean paused = false;
	
	protected BitmapFont font;
	
	public WorldRenderer(MainEditor editor, AssetManager assetManager) {
		this.editor = editor;
		this.assetManager = assetManager;
		inputMultiplexer = new InputMultiplexer(this);
	}
	
	@Override
	public void create() {
		this.batch = new SpriteBatch();
		
		Gdx.input.setInputProcessor(inputMultiplexer);
		
		this.cam = new GameCamera();
		this.uiCam = new UiCamera();
		
		this.font = new BitmapFont();
		
		super.create();
	}
	
	public void render(Map map) {
		if (paused)
			return;
		
		cam.update(Gdx.graphics.getDeltaTime());
		
		batch.setProjectionMatrix(cam.combined());
		batch.begin();
		if (map != null) {
			Vector2 mouseLocation = cam.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
			CollisionRect rect = cam.getViewRect();
			
			Object selectedItem = null;
			int selectedLayer = -1;
			if (editor.getSelectedTool() != null) {
				selectedItem = editor.getSelectedTool().getSelectedItem();
				selectedLayer = editor.getSelectedTool().getSelectedLayer();
			}
			
			map.draw(editor.getAssetManager(), editor.showTiles(), editor.showMapElements(), editor.showGrid(), editor.showCollisionMap(), (int) (mouseLocation.x / MainEditor.TILE_SIZE) - (mouseLocation.x < 0 ? 1 : 0), (int) (mouseLocation.y / MainEditor.TILE_SIZE) - (mouseLocation.y < 0 ? 1 : 0), selectedLayer, selectedItem, batch, (int) (rect.xWithOffset() / MainEditor.TILE_SIZE), (int) (rect.yWithOffset() / MainEditor.TILE_SIZE), (int) (rect.width / MainEditor.TILE_SIZE), (int) (rect.height / MainEditor.TILE_SIZE), rect);
		}
		
		//TODO render tile coordinate of mouse
		
		batch.end();
		
		batch.setProjectionMatrix(uiCam.combined());
		batch.begin();
		Vector2 pos = cam.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
		font.draw(batch, "x: " + ((int) pos.x), 10, 40);
		font.draw(batch, "y: " + ((int) pos.y), 10, 20);
		batch.end();
		
		//Set batch back for any child classes
		batch.setProjectionMatrix(cam.combined());
		
		super.render();
	}
	
	public void pauseRendering() {
		paused = true;
	}
	
	public void resumeRendering() {
		paused = false;
	}
	
	public void addInputListener(InputProcessor inputProcessor) {
		this.inputMultiplexer.addProcessor(inputProcessor);
	}
	
	@Override
	public void resize(int width, int height) {
		cam.resize(width, height);
		uiCam.resize(width, height);
		super.resize(width, height);
	}
	
	@Override
	public boolean keyDown(int keycode) {return false;}

	@Override
	public boolean keyUp(int keycode) {return false;}

	@Override
	public boolean keyTyped(char character) {return false;}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {return false;}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {return false;}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {return false;}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {return false;}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
	
	public boolean controlPressed() {
		return Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Keys.CONTROL_RIGHT);
	}
	
	public boolean shiftPressed() {
		return Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT);
	}
	
	public boolean altPressed() {
		return Gdx.input.isKeyPressed(Keys.ALT_LEFT) || Gdx.input.isKeyPressed(Keys.ALT_RIGHT);
	}
	
	public GameCamera getCam() {
		return cam;
	}

}
