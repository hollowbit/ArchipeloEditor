package net.hollowbit.archipeloeditor.world;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import javax.swing.Icon;

import net.hollowbit.archipeloeditor.MainEditor;

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
	
	public BufferedImage texture = null;
	
	public MapTile(TileData data, BufferedImage texture){
		this.id = data.id;
		this.name = data.name;
		this.numberOfFrames = data.animationFrames;
		this.animationSpeed = data.animationTime;
		this.collisionTable = data.collisionTable;
		this.swimmable = data.swimmable;
		this.speedMultiplier = data.speedMultiplier;
		//To be implemented:
		//this.damageSpeed = data.damageSpeed;
		//this.damage = data.damage;
		
		//TODO HANDLE ROTATION AND FLIP                     
		AffineTransform affineTransform = AffineTransform.getScaleInstance((data.flipX ? -1:1), (data.flipY ? -1:1));
		affineTransform.translate((data.flipX ? -18:0), (data.flipY ? -18:0));
		affineTransform.rotate(Math.toRadians(90 * data.rotation), 9, 9);
		AffineTransformOp affineTransformOp = new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		this.texture = affineTransformOp.filter(texture, null);
	}
	
	public void draw(Graphics2D g, int x, int y){
		g.drawImage(texture, x, y, null);
	}

	@Override
	public int getIconHeight() {
		return MainEditor.TILE_SIZE;
	}

	@Override
	public int getIconWidth() {
		return MainEditor.TILE_SIZE;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		g.drawImage(texture, x, y, null);
	}
	
}
