package lwjglEngine.gui;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import lwjglEngine.shaders.ShaderProgram;

public class GuiShader extends ShaderProgram {
	
	private static final String VERTEX_FILE = "src/lwjglEngine/gui/guiVertexShader.txt";
	private static final String FRAGMENT_FILE = "src/lwjglEngine/gui/guiFragmentShader.txt";
	
	private int location_transformationMatrix, location_colour;

	public GuiShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
	
	public void loadTransformation(Matrix4f matrix){
		super.loadMatrix(location_transformationMatrix, matrix);
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_colour = super.getUniformLocation("colour");
	}
	
	protected void loadColor(Vector4f vector4f){
		super.loadVector4f(location_colour, vector4f);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}
	
	
	

}
