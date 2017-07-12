package net.hollowbit.archipeloeditor.world;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import javax.swing.Icon;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.hollowbit.archipeloeditor.MainEditor;
import net.hollowbit.archipeloshared.TileData;

public class MapTile implements Icon{

	public String id;
	public String name;
	
	public boolean animated = false;
	public int numberOfFrames = 0;
	public float animationSpeed = 0f;
	
	public boolean[][] collisionTable;
	public boolean swimmable = false;
	
	public boolean doesDamage = false;
	public float damageSpeed = 1f;
	public int damage = 0;//DAMAGE CAN BE SET TO A NEGATIVE NUMBER TO HEAL ENTITIES//
	public boolean multipliesSpeed = false;
	public float speedMultiplier = 0f;
	
	public boolean flipX, flipY;
	public float rotation;
	
	public TextureRegion texture = null;
	public BufferedImage icon = null;
	
	public MapTile(TileData data, TextureRegion texture, BufferedImage icon){
		//Apply data from file
		this.id = data.id;
		this.name = data.name;
		this.animated = data.animated;
		this.numberOfFrames = data.animationFrames;
		this.animationSpeed = data.animationTime;
		this.collisionTable = data.collisionTable;
		this.swimmable = data.swimmable;
		this.speedMultiplier = data.speedMultiplier;
		this.flipX = data.flipX;
		this.flipY = data.flipY;
		this.rotation = data.rotation;
		//To be implemented:
		//this.damageSpeed = data.damageSpeed;
		//this.damage = data.damage;
		
		//TODO HANDLE ROTATION AND FLIP                     
		this.texture = texture;
		
		AffineTransform affineTransform = AffineTransform.getScaleInstance((data.flipX ? -1:1), (data.flipY ? -1:1));
		affineTransform.translate((data.flipX ? -18:0), (data.flipY ? -18:0));
		affineTransform.rotate(Math.toRadians(90 * data.rotation), 9, 9);
		AffineTransformOp affineTransformOp = new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		this.icon = affineTransformOp.filter(icon, null);
	}
	
	public void draw(SpriteBatch batch, int x, int y){
		batch.draw(texture, getDrawX(x), getDrawY(y), getOriginX(), getOriginY(), getDrawWidth(), getDrawHeight(), 1, 1, rotation * 90);
	}

	@Override
	public int getIconHeight() {
		return MainEditor.TILE_SIZE;
	}

	@Override
	public int getIconWidth() {
		return MainEditor.TILE_SIZE;
	}
	
	protected float getDrawX (float x) {
		return x + (flipX ? MainEditor.TILE_SIZE : 0);
	}
	
	protected float getDrawY (float y) {
		return y + (flipY ? MainEditor.TILE_SIZE : 0);
	}
	
	protected float getOriginX () {
		return MainEditor.TILE_SIZE / 2;
	}
	
	protected float getOriginY () {
		return MainEditor.TILE_SIZE / 2;
	}
	
	protected float getDrawWidth () {
		return (flipX ? -1:1) * MainEditor.TILE_SIZE;
	}
	
	protected float getDrawHeight () {
		return (flipY ? -1:1) * MainEditor.TILE_SIZE;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		g.drawImage(icon, x, y, null);
	}
	
}
