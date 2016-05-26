package net.hollowbit.archipeloeditor.world;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import javax.swing.Icon;

import net.hollowbit.archipeloeditor.MainEditor;

public class Tile implements Icon{

	public String id;
	public String name;
	
	public BufferedImage grassyTexture = null;
	public BufferedImage snowyTexture = null;
	public BufferedImage sandyTexture = null;
	
	public Tile(String id, String name, int rotation, boolean flipX, boolean flipY, BufferedImage grassyTexture, BufferedImage sandyTexture, BufferedImage snowyTexture){
		this.id = id;
		this.name = name;
		
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
