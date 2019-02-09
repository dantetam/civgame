package lwjglEngine.render;

import java.util.ArrayList;
import java.util.HashMap;

import lwjglEngine.models.RawModel;
import lwjglEngine.models.TexturedModel;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import lwjglEngine.shaders.ShaderProgram;
import lwjglEngine.shaders.StaticShader;
import lwjglEngine.textures.ModelTexture;
import lwjglEngine.toolbox.MatrixMathUtil;
import lwjglEngine.entities.Entity;

public class EntityRenderer {
	
	private StaticShader shader;
	
	public EntityRenderer(StaticShader shader, Matrix4f projectionMatrix)
	{
		this.shader = shader;
		//Create a new matrix, for the first time
		//Access the shader to load the projectionMatrix
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}
	
	public void render(HashMap<TexturedModel, ArrayList<Entity>> entities)
	{
		for (TexturedModel model: entities.keySet())
		{
			prepareTexturedModel(model);
			ArrayList<Entity> all = entities.get(model);
			for (Entity entity: all)
			{
				prepareInstance(entity);
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().vertexCount, GL11.GL_UNSIGNED_INT, 0);
			}
			unbindTexturedModel();
		}
	}
	
	private void prepareTexturedModel(TexturedModel texturedModel)
	{
		RawModel model = texturedModel.getRawModel();
		
		//Whenever a VAO is edited, it must be bound
		GL30.glBindVertexArray(model.vaoID);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		
		ModelTexture texture = texturedModel.getTexture();
		if (texture.transparent)
		{
			MasterRenderer.disableCulling();
		}
		shader.loadFastLighting(texture.fastLighting);
		
		shader.loadShineVariables(texture.shineDamper, texture.reflectiveness);
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturedModel.getTexture().textureID);
	}
	
	private void unbindTexturedModel()
	{
		//Enable culling by default and stop culling manually if desired
		MasterRenderer.enableCulling();
		//Disable after finished rendering
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		
		GL30.glBindVertexArray(0); //Unbind the current bound VAO
	}
	
	private void prepareInstance(Entity entity)
	{
		//Access transformMatrix
		Matrix4f transformMatrix = MatrixMathUtil.createTransformMatrix(
				entity.position, 
				entity.rotX, 
				entity.rotY, 
				entity.rotZ, 
				entity.scale
				);
		shader.loadTransformMatrix(transformMatrix);
	}
	
	//The textured model encapsulates a RawModel
	//Access a VAO by using the data contained within a RawModel object
	/*public void render(Entity entity, StaticShader shader)
	{
		TexturedModel texturedModel = entity.getModel();
		RawModel model = texturedModel.getRawModel();
		
		//Whenever a VAO is edited, it must be bound
		GL30.glBindVertexArray(model.vaoID);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		
		//Access transformMatrix
		Matrix4f transformMatrix = Maths.createTransformMatrix(
				entity.position, 
				entity.rotX, 
				entity.rotY, 
				entity.rotZ, 
				entity.scale
				);
		shader.loadTransformMatrix(transformMatrix);
		ModelTexture texture = texturedModel.getTexture();
		shader.loadShineVariables(texture.shineDamper, texture.reflectiveness);
		
		//Texture bank 0
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturedModel.getTexture().textureID);
		
		//0 indicates the first vertex to be drawn in triangles
		//vertexCount property indicates upper bound
		
		//GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, model.vertexCount); 
		//replace the above method because we are using
		GL11.glDrawElements(GL11.GL_TRIANGLES, model.vertexCount, GL11.GL_UNSIGNED_INT, 0);
		
		//Disable after finished rendering
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		
		GL30.glBindVertexArray(0); //Unbind the current bound VAO
	}*/

}
