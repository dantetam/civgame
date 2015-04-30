package lwjglEngine.textures;

public class TerrainTexturePack {

	public TerrainTexture backgroundTexture;
	public TerrainTexture rTexture;
	public TerrainTexture gTexture;
	public TerrainTexture bTexture;

	public TerrainTexturePack(TerrainTexture backgroundTexture,
			TerrainTexture rTexture, TerrainTexture gTexture,
			TerrainTexture bTexture) {
		this.backgroundTexture = backgroundTexture;
		this.rTexture = rTexture;
		this.gTexture = gTexture;
		this.bTexture = bTexture;
	}
	
}
