package lwjglEngine.toolbox;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import lwjglEngine.entities.Camera;

//Courtesy of ThinMatrix. Hope his kickstarter goes well.

public class MatrixMathUtil {

	//Rotate normally by a certain translation
	public static Matrix4f createTransformMatrix(Vector3f translation, float rx, float ry, float rz, float scale)
	{
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		//Writing matrix twice accesses 'matrix' and rewrites the new 'transformed' matrix in 'matrix'
		Matrix4f.translate(translation, matrix, matrix);
		//Rotate it by angles around the axes
		Matrix4f.rotate((float)Math.toRadians(rx), new Vector3f(1,0,0), matrix, matrix);
		Matrix4f.rotate((float)Math.toRadians(ry), new Vector3f(0,1,0), matrix, matrix);
		Matrix4f.rotate((float)Math.toRadians(rz), new Vector3f(0,0,1), matrix, matrix);
		Matrix4f.scale(new Vector3f(scale,scale,scale), matrix, matrix);
		return matrix;
	}
	
	//For 2D GUIs
	public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.scale(new Vector3f(scale.x, scale.y, 1f), matrix, matrix);
		return matrix;
	}
	
	//Rotate in the opposite direction with respect to a camera's orientation
	public static Matrix4f createViewMatrix(Camera camera)
	{
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		//Rotate first and then translate; opposite of normal translation process
		Matrix4f.rotate((float)Math.toRadians(camera.pitch), new Vector3f(1,0,0), matrix, matrix);
		Matrix4f.rotate((float)Math.toRadians(camera.yaw), new Vector3f(0,1,0), matrix, matrix);
		Vector3f negative = new Vector3f(-camera.position.x, -camera.position.y, -camera.position.z);
		Matrix4f.translate(negative, matrix, matrix);
		return matrix;
	}
	
}
