package net.hollowbit.archipeloeditor.world.worldrenderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import net.hollowbit.archipeloshared.CollisionRect;

public class GameCamera {
	
	public static final float MAX_ZOOM = 30;
	public static final float MIN_ZOOM = 0.1f;
	
	private OrthographicCamera cam;
	private ScreenViewport viewport;
	
	public GameCamera () {
		cam = new OrthographicCamera();
		viewport = new ScreenViewport(cam);
		//viewport.setUnitsPerPixel(WorldRenderer.UNITS_PER_PIXEL);
		viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		viewport.apply();
		cam.position.set(cam.viewportWidth / 2, cam.viewportHeight / 2, 0);
		cam.update();
	}
	
	public void resize (int width, int height) {
		viewport.update(width, height);
		viewport.apply();
		cam.position.set(cam.viewportWidth / 2, cam.viewportHeight / 2, 0);
		cam.update();
	}
	
	public Matrix4 combined () {
		return cam.combined;
	}
	
	public void update (float deltatime) {
		if (Gdx.input.isKeyPressed(Keys.SPACE) && Gdx.input.isTouched()) {
			Vector2 oldPos = unproject(new Vector2(Gdx.input.getX() - Gdx.input.getDeltaX(), Gdx.input.getY() - Gdx.input.getDeltaY()));
			Vector2 newPos = unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
			cam.translate(oldPos.x - newPos.x, oldPos.y - newPos.y);
			cam.update();
		}
	}
	
	public void move (float x, float y, float z) {
		cam.position.set(x, y, z);
		cam.update();
	}
	
	/**
	 * Zoom camera by a certain amount.
	 * Note: Does not set zoom, it adds the given amount to it.
	 * @param zoom
	 * @param x
	 * @param y
	 */
	public void zoom (float zoom, float x, float y) {
		Vector3 posToZoomTo = cam.unproject(new Vector3(x, y, 0));//Save world position of mouse cursor
		
		//Zoom camera
		cam.zoom += zoom;
		if (cam.zoom > MAX_ZOOM)
			cam.zoom = MAX_ZOOM;
		if (cam.zoom < MIN_ZOOM)
			cam.zoom = MIN_ZOOM;
		cam.update();
		
		//Move save world coordinate to mouse cursor
		Vector3 newUnprojected = cam.unproject(new Vector3(x, y, 0));
		cam.translate(posToZoomTo.x - newUnprojected.x, posToZoomTo.y - newUnprojected.y);
		cam.update();
	}
	
	/** Convert screen coords to world coords */
	public Vector2 unproject (Vector2 screenCoords) {
		Vector3 unprojected = cam.unproject(new Vector3(screenCoords.x, screenCoords.y, 0));
		return new Vector2(unprojected.x, unprojected.y);
	}
	
	/** Convert world coords to world screen */
	public Vector2 project (Vector2 worldCoords) {
		Vector3 projected = cam.project(new Vector3(worldCoords.x, worldCoords.y, 0));
		return new Vector2(projected.x, projected.y);
	}
	
	public float getWidth () {
		return cam.viewportWidth * cam.zoom;
	}
	
	public float getHeight () {
		return cam.viewportHeight * cam.zoom;
	}
	
	public float getX () {
		return cam.position.x;
	}
	
	public float getY () {
		return cam.position.y;
	}
	
	public CollisionRect getViewRect () {
		return new CollisionRect(cam.position.x - getWidth() / 2, cam.position.y - getHeight() / 2, 0, 0, getWidth(), getHeight());
	}
	
}
