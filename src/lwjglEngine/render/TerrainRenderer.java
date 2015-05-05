package lwjglEngine.render;

import java.util.ArrayList;

import lwjglEngine.models.RawModel;
import lwjglEngine.models.TexturedModel;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import lwjglEngine.entities.Entity;
import lwjglEngine.shaders.TerrainShader;
import lwjglEngine.terrain.Terrain;
import lwjglEngine.textures.ModelTexture;
import lwjglEngine.textures.TerrainTexturePack;
import lwjglEngine.toolbox.Maths;

public class TerrainRenderer {

	private TerrainShader shader;
	
	public TerrainRenderer(TerrainShader shader, Matrix4f projectionMatrix)
	{
		this.shader = shader;
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.connectTextures();
		shader.stop();
	}
	
	public void render(ArrayList<Terrain> terrains)
	{
		System.out.println("dont do it yo");
		for (Terrain terrain: terrains)
		{
			System.out.println("dont do it hoe");
			prepareTerrain(terrain);
			System.out.println("dont do it hoe2");
			loadModelMatrix(terrain);
			System.out.println("dont do it hoe3");
			GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.model.vertexCount, GL11.GL_UNSIGNED_INT, 0);
			System.out.println("dont do it hoe4");
			unbindTexturedModel();
		}
	}
	
	//VVV Repurposed methods to deal with terrain, not entities
	
	private void prepareTerrain(Terrain terrain)
	{
		RawModel model = terrain.model;
		
		//Whenever a VAO is edited, it must be bound
		GL30.glBindVertexArray(model.vaoID);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		
		bindTextures(terrain);
		shader.loadShineVariables(1,0);
		//ModelTexture texture = terrain.texture;
		//shader.loadShineVariables(texture.shineDamper, texture.reflectiveness);
	}
	
	private void bindTextures(Terrain terrain)
	{
		TerrainTexturePack textures = terrain.texturePack;
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textures.backgroundTexture.textureID);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textures.rTexture.textureID);
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textures.gTexture.textureID);
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textures.bTexture.textureID);
		GL13.glActiveTexture(GL13.GL_TEXTURE4);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.blendMap.textureID);
	}
	
	private void unbindTexturedModel()
	{
		//Disable after finished rendering
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		
		GL30.glBindVertexArray(0); //Unbind the current bound VAO
	}
	
	private void loadModelMatrix(Terrain terrain)
	{
		//Access transformMatrix
		Matrix4f transformMatrix = Maths.createTransformMatrix(
				new Vector3f(terrain.x, 0, terrain.z), 
				0,
				0,
				0,
				1
				);
		shader.loadTransformMatrix(transformMatrix);
	}
	
}
