package net.hollowbit.archipeloeditor.objectdefiners;

import java.util.HashMap;

import net.hollowbit.archipeloeditor.MainEditor;
import net.hollowbit.archipeloeditor.world.AssetManager;

public class JsonObjectDefiner extends ObjectDefiner<HashMap<String, Object>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2299201894861879256L;

	public JsonObjectDefiner(MainEditor editor, AssetManager assetManager,
			net.hollowbit.archipeloeditor.objectdefiners.ObjectDefiner.DefinitionCompleteListener<HashMap<String, Object>> listener) {
		super(editor, assetManager, listener);
	}

	@Override
	protected HashMap<String, Object> createObject() {
		return new HashMap<String, Object>();
	}	
}
