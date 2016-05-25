package net.vediogames.archipelomapeditor.world;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import javax.swing.Icon;

import net.vediogames.archipelomapeditor.MainEditor;

public class MapElement implements Icon{
	
	public String id = "ID";
	public String name = "Element";
	public int width = 1, height = 1;
	public int offsetX = 0, offsetY = 0;
	public int[][] collidable = new int[height][width];
	public float animationSpeed = 0f;
	public int numberOfFrames = 1;
	public int rotation = 0;
	public boolean flipX = false, flipY = false;
	
	public AssetPack assetPack = null;
	
	public BufferedImage grassyTexture = null;
	public BufferedImage sandyTexture = null;
	public BufferedImage snowyTexture = null;
	
	public MapElement(String id, String name, AssetPack assetPack, int width, int height, int offsetX, int offsetY, int[][] collidable, float animationSpeed, int numberOfFrames, int rotation, boolean flipX, boolean flipY, BufferedImage grassyTexture, BufferedImage sandyTexture, BufferedImage snowyTexture){
		this.id = id;
		this.name = name;
		this.width = width;
		this.height = height;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.numberOfFrames = numberOfFrames;
		this.animationSpeed = animationSpeed;
		this.collidable = collidable;
		this.assetPack = assetPack;
		
		//TODO HANDLE ROTATION AND FLIP                     
		AffineTransform affineTransform = AffineTransform.getScaleInstance((flipX ? -1:1), (flipY ? -1:1));
		affineTransform.translate((flipX ? -18 * width:0), (flipY ? -18 * height:0));
		affineTransform.rotate(Math.toRadians(90 * rotation), width * 18 / 2, height * 18 /2);
		AffineTransformOp affineTransformOp = new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		this.grassyTexture = affineTransformOp.filter(grassyTexture, null);
		this.sandyTexture = affineTransformOp.filter(sandyTexture, null);
		this.snowyTexture = affineTransformOp.filter(snowyTexture, null);
	}
	
	public void draw(Graphics g, int x, int y, int climate){
		switch(climate){
		case 0:
			g.drawImage(grassyTexture, x + offsetX, y + offsetY * 18 - (height - 1) * 18, null);
			break;
		case 1:
			g.drawImage(sandyTexture, x + offsetX, y + offsetY * 18 - (height - 1) * 18, null);
			break;
		case 2:
			g.drawImage(snowyTexture, x + offsetX, y + offsetY * 18 - (height - 1) * 18, null);
			break;
		default:
			g.drawImage(grassyTexture, x + offsetX, y + offsetY * 18 - (height - 1) * 18, null);
			break;
		}
	}
	
	@Override
	public int getIconHeight() {
		return height * 18;
	}

	@Override
	public int getIconWidth() {
		return width * 18;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		switch(MainEditor.map.getClimat()){
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
