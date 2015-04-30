package lwjglEngine.textures;

public class ModelTexture {

	public int textureID;
	
	public float shineDamper = 1, reflectiveness = 0;
	
	public boolean transparent = false, fastLighting = false;
	
	public ModelTexture(int id)
	{
		textureID = id;
	}

}
