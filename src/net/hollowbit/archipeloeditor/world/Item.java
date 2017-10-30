package net.hollowbit.archipeloeditor.world;

import com.badlogic.gdx.graphics.Color;

public class Item {
	
	public static final int DEFAULT_COLOR = Color.rgba8888(new Color(1, 1, 1, 1));
	
	public String id;
	public int color = DEFAULT_COLOR;
	public int durability = 1;
	public int style = 0;
	public int quantity = 1;
	
	public Item () {}
	
}
