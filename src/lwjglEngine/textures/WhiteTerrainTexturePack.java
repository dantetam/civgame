package lwjglEngine.textures;

public class WhiteTerrainTexturePack implements TerrainTexturePack {

	public TerrainTexture backgroundTexture;
	public TerrainTexture texture1, texture2, texture3, texture4, texture5, texture6, texture7;

	public WhiteTerrainTexturePack(TerrainTexture backgroundTexture,
			TerrainTexture t1, TerrainTexture t2, TerrainTexture t3, 
			TerrainTexture t4, TerrainTexture t5, TerrainTexture t6,
			TerrainTexture t7) {
		this.backgroundTexture = backgroundTexture;
		this.texture1 = t1;
		this.texture2 = t2;
		this.texture3 = t3;
		this.texture4 = t4;
		this.texture5 = t5;
		this.texture6 = t6;
		this.texture7 = t7;
	}

}
