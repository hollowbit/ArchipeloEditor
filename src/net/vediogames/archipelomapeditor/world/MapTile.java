package net.vediogames.archipelomapeditor.world;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import javax.swing.Icon;

import net.vediogames.archipelomapeditor.MainEditor;

public class MapTile implements Icon{

	public String id;
	public String name;
	
	public boolean animated = false;
	public int numberOfFrames = 0;
	public float animationSpeed = 0f;
	
	public boolean collidable = false;
	public boolean swimmable = false;
	
	public boolean doesDamage = false;
	public float damageSpeed = 1f;
	public int damage = 0;//DAMAGE CAN BE SET TO A NEGATIVE NUMBER TO HEAL ENTITIES//
	public boolean multipliesSpeed = false;
	public float speedMultiplier = 0f;
	
	public AssetPack assetPack;
	
	public BufferedImage grassyTexture = null;
	public BufferedImage snowyTexture = null;
	public BufferedImage sandyTexture = null;
	
	public MapTile(String id, String name, AssetPack assetPack, int numberOfFrames, float animationSpeed, boolean collidable, boolean swimmable, float damageSpeed, int damage, float speedMultiplier, int rotation, boolean flipX, boolean flipY, BufferedImage grassyTexture, BufferedImage sandyTexture, BufferedImage snowyTexture){
		this.id = id;
		this.name = name;
		this.numberOfFrames = numberOfFrames;
		this.animationSpeed = animationSpeed;
		this.collidable = collidable;
		this.swimmable = swimmable;
		this.damageSpeed = damageSpeed;
		this.damage = damage;
		this.speedMultiplier = speedMultiplier;
		this.assetPack = assetPack;
		
		//TODO HANDLE ROTATION AND FLIP                     
		AffineTransform affineTransform = AffineTransform.getScaleInstance((flipX ? -1:1), (flipY ? -1:1));
		affineTransform.translate((flipX ? -18:0), (flipY ? -18:0));
		affineTransform.rotate(Math.toRadians(90 * rotation), 9, 9);
		AffineTransformOp affineTransformOp = new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		this.grassyTexture = affineTransformOp.filter(grassyTexture, null);
		this.sandyTexture = affineTransformOp.filter(sandyTexture, null);
		this.snowyTexture = affineTransformOp.filter(snowyTexture, null);
	}
	
	public void draw(Graphics2D g, int x, int y, int climate){
		switch(climate){
		case 0:
			g.drawImage(grassyTexture, x, y, null);
			break;
		case 1:
			g.drawImage(sandyTexture, x, y, null);
			break;
		case 2:
			g.drawImage(snowyTexture, x, y, null);
			break;
		default:
			g.drawImage(grassyTexture, x, y, null);
			break;
		}
	}

	@Override
	public int getIconHeight() {
		return 18;
	}

	@Override
	public int getIconWidth() {
		return 18;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		switch(MainEditor.map.getClimate()){
		case 0:
			g.drawImage(grassyTexture, x, y, null);
			break;
		case 1:
			g.drawImage(sandyTexture, x, y, null);
			break;
		case 2:
			g.drawImage(snowyTexture, x, y, null);
			break;
		default:
			g.drawImage(grassyTexture, x, y, null);
			break;
		}
	}
	
}
