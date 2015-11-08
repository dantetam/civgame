package lwjglEngine.shaders;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

//'Template' for the general shader class

public abstract class ShaderProgram {

	//Wrapper class for the IDs of the individual programs and their respective shader files
	public int programID, vertexShaderID, fragmentShaderID;
	
	public ShaderProgram(String vertex, String fragment)
	{
		//Load the shader from text and get their IDs
		vertexShaderID = loadShader(vertex,GL20.GL_VERTEX_SHADER);
		fragmentShaderID = loadShader(fragment,GL20.GL_FRAGMENT_SHADER);
		programID = GL20.glCreateProgram();
		//Link the program to shaders
		GL20.glAttachShader(programID, vertexShaderID);
		GL20.glAttachShader(programID, fragmentShaderID);
		bindAttributes();
		GL20.glLinkProgram(programID);
		GL20.glValidateProgram(programID);
		getAllUniformLocations();
	}
	
	protected abstract void getAllUniformLocations();
	
	//Get the ID of a stored uniform variable in shader
	protected int getUniformLocation(String uniformName)
	{
		return GL20.glGetUniformLocation(programID, uniformName);
	}
	
	//Same values to locations in the GLSL uniform variables of various types
	protected void loadFloat(int location, float value) {GL20.glUniform1f(location, value);}
	protected void loadInt(int location, int value) {GL20.glUniform1i(location, value);}
	protected void loadVector(int location, Vector3f vector) {GL20.glUniform3f(location, vector.x, vector.y, vector.z);}
	protected void loadVector2f(int location, Vector2f vector) {GL20.glUniform2f(location, vector.x, vector.y);}
	protected void loadVector4f(int location, Vector4f vector) {GL20.glUniform4f(location, vector.x, vector.y, vector.z, vector.w);}
	
	protected void loadBoolean(int location, boolean value)
	{
		//Convert boolean to a 1 or 0 and store it as a float
		float toLoad = value ? 1 : 0;
		GL20.glUniform1f(location, toLoad);
	}
	
	//Temporary FloatBuffer to be used as this class's tool
	private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
	protected void loadMatrix(int location, Matrix4f matrix)
	{
		//Convert matrix to be used in a buffer object which is stored in the GLSL code
		matrix.store(matrixBuffer);
		matrixBuffer.flip();
		//GL20.glUniformMatrix2(location, false, matrixBuffer); //SHOOT ME!!!
		GL20.glUniformMatrix4fv(location, false, matrixBuffer);
	}
	
	public void start()
	{
		//Tell OpenGL to start using this shader
		GL20.glUseProgram(programID);
	}
	
	public void stop()
	{
		//Direct OpenGL to stop using the shader
		GL20.glUseProgram(0);
	}
	
	public void cleanUp()
	{
		stop();
		//Unlink and delete shaders, as well as the shader program
		GL20.glDetachShader(programID, vertexShaderID);
		GL20.glDetachShader(programID, fragmentShaderID);
		GL20.glDeleteShader(vertexShaderID);
		GL20.glDeleteShader(fragmentShaderID);
		GL20.glDeleteProgram(programID);
	}
	
	protected abstract void bindAttributes();
	
	protected void bindAttribute(int attribute, String varName)
	{
		GL20.glBindAttribLocation(programID, attribute, varName);
	}
	
	private static int loadShader(String file, int type)
	{
		StringBuilder shaderSource = new StringBuilder();
		try
		{
			//Read directly from the file line by line and add it to the StringBuilder
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = reader.readLine()) != null)
				shaderSource.append(line).append("\n");
			reader.close();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
		//Create a new Shader object, add and compile the recorded source code (glsl)
		int shaderID = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderID, shaderSource);
		GL20.glCompileShader(shaderID);
		if (GL20.glGetShaderi(shaderID,GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE)
		{
			System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
			System.err.println("Could not compile shader");
			System.exit(-1);
		}
		return shaderID;
	}

}
