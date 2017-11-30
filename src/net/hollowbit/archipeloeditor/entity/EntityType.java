package net.hollowbit.archipeloeditor.entity;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.hollowbit.archipeloeditor.tools.FileReader;
import net.hollowbit.archipeloeditor.tools.StaticTools;
import net.hollowbit.archipeloshared.EntityAnimationData;
import net.hollowbit.archipeloshared.EntityTypeData;
import net.hollowbit.archipeloshared.PropertyDefinition;

public enum EntityType {
	
	PLAYER ("player"),
	TELEPORTER ("teleporter"),
	DOOR ("door"),
	DOOR_LOCKED ("door-locked"),
	SIGN ("sign"),
	BLOBBY_GRAVE ("blobby-grave"),
	COMPUTER ("computer"),
	WIZARD ("wizard"),
	SPAWNER ("spawner"),
	SLIME("slime");
	
	private String id;
	private EntityTypeData data;
	private TextureRegion[] renderTextures;
	private BufferedImage[] renderIcons;
	
	private EntityType (String id) {
		this.id = id;
		
		String dataString = FileReader.readFileIntoString("/shared/entities/" + id + ".json");
		this.data = StaticTools.getJson().fromJson(EntityTypeData.class, dataString);
	}
	
	public String getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return id;
	}

	public EntityTypeData getData() {
		return data;
	}
	
	public PropertyDefinition[] getDefaultProperties () {
		return data.defaultProperties;
	}
	
	public ArrayList<PropertyDefinition> getProperties () {
		return data.properties;
	}
	
	public float getDrawOrderY (float y) {
		return y + data.drawOrderOffsetY;
	}
	
	/**
	 * Will return null if the style is invalid
	 * @param style
	 * @return
	 */
	public TextureRegion getEditorTexture(int style) {
		if (style < 0 || style >= data.numberOfStyles)
			return null;
		
		return renderTextures[style];
	}
	
	/**
	 * Will return null if the style is invalid
	 * @param style
	 * @return
	 */
	public BufferedImage getEditorIcon(int style) {
		if (style < 0 || style >= data.numberOfStyles)
			return null;
		
		return renderIcons[style];
	}
	
	public void loadImages() {
		renderTextures = new TextureRegion[data.numberOfStyles];
		renderIcons = new BufferedImage[data.numberOfStyles];
		
		String defaultAnim = null;
		for (EntityAnimationData anim : data.animations) {
			if (anim.id.contains("default")) {
				defaultAnim = anim.fileName;
				break;
			}
		}
		
		if (defaultAnim == null)
			defaultAnim = data.animations.get(0).fileName;
		
		if (defaultAnim.equals(""))
			defaultAnim = data.animations.get(0).id;
		
		for (int style = 0; style < data.numberOfStyles; style++) {
			Texture texture = new Texture("entities/" + id + "/" + defaultAnim + "_" + style + ".png");
			BufferedImage image = null;
			try {
				image = ImageIO.read(getClass().getResourceAsStream("/entities/" + id + "/" + defaultAnim + "_" + style + ".png"));
			} catch (IOException e) {
				System.out.println("Could not load image for Entity Type: " + id);
				return;
			}
			
			renderTextures[style] = new TextureRegion(texture, 0, 0, data.imgWidth, data.imgHeight);
			renderIcons[style] = image.getSubimage(0, 0, data.imgWidth, data.imgHeight);
		}
	}
	
	public static void loadAllImages() {
		for (EntityType entityType : EntityType.values())
			entityType.loadImages();
	}
	
	private static HashMap<String, EntityType> typeMap;
	
	static {
		typeMap = new HashMap<String, EntityType>();
		for (EntityType type : EntityType.values())
			typeMap.put(type.id, type);
	}
	
	public static EntityType getById(String id) {
		return typeMap.get(id);
	}
	
}
