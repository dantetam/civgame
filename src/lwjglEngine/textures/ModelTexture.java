package lwjglEngine.textures;

public class ModelTexture {

	public int textureID;
	
	public float shineDamper = 1, reflectiveness = 0;
	
	public boolean transparent = false, fastLighting = false;
	
	public ModelTexture(int id)
	{
		if (id < 0) id = 0; //-1 "default" id
		textureID = id;
	}

}
