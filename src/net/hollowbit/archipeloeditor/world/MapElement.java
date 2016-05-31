package net.hollowbit.archipeloeditor.world;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import javax.swing.Icon;

import net.hollowbit.archipeloeditor.MainEditor;

public class MapElement implements Icon{
	
	public String id = "ID";
	public String name = "Element";
	public int width = 1, height = 1;
	public int offsetX = 0, offsetY = 0;
	public boolean[][] collisionTable = new boolean[height][width];
	public boolean animated = false;
	public float animationSpeed = 0f;
	public int numberOfFrames = 1;
	public int rotation = 0;
	public boolean flipX = false, flipY = false;
	
	public BufferedImage texture = null;
	
	public MapElement(ElementData data, BufferedImage texture){
		this.id = data.id;
		this.name = data.name;
		this.width = data.width;
		this.height = data.height;
		this.offsetX = data.offsetX;
		this.offsetY = data.offsetY;
		this.animated = data.animated;
		this.numberOfFrames = data.animationFrames;
		this.animationSpeed = data.animationTime;
		this.collisionTable = data.collisionTable;
		
		//TODO HANDLE ROTATION AND FLIP                     
		AffineTransform affineTransform = AffineTransform.getScaleInstance((flipX ? -1:1), (flipY ? -1:1));
		affineTransform.translate((flipX ? -18 * width:0), (flipY ? -18 * height:0));
		affineTransform.rotate(Math.toRadians(90 * rotation), width * 18 / 2, height * 18 /2);
		AffineTransformOp affineTransformOp = new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		this.texture = affineTransformOp.filter(texture, null);
	}
	
	public void draw(Graphics g, int x, int y){
		g.drawImage(texture, x + offsetX * MainEditor.TILE_SIZE / TileData.COLLISION_MAP_SCALE, y + offsetY * MainEditor.TILE_SIZE / TileData.COLLISION_MAP_SCALE - (height - 1) * MainEditor.TILE_SIZE, null);
	}
	
	@Override
	public int getIconHeight() {
		return height * MainEditor.TILE_SIZE;
	}

	@Override
	public int getIconWidth() {
		return width * MainEditor.TILE_SIZE;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		g.drawImage(texture, x, y, null);
	}
	
}
