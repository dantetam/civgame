package lwjglEngine.models;

import lwjglEngine.textures.ModelTexture;

public class TexturedModel {

	private RawModel rawModel;
	private ModelTexture texture;
	
	public TexturedModel(RawModel m, ModelTexture t)
	{
		rawModel = m;
		texture = t;
	}

	public RawModel getRawModel() {
		return rawModel;
	}

	public ModelTexture getTexture() {
		return texture;
	}

}
