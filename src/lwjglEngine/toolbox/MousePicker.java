package lwjglEngine.toolbox;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import lwjglEngine.entities.Camera;
import lwjglEngine.gui.Mouse;
import lwjglEngine.render.DisplayManager;

//Also courtesy of ThinMatrix.

public class MousePicker {

	public Vector3f currentRay;
	
	private Matrix4f projMatrix, viewMatrix;
	public Camera camera;
	
	public MousePicker(Matrix4f p, Camera c) {
		camera = c;
		projMatrix = p;
		viewMatrix = Maths.createViewMatrix(camera);
	}
	
	public void update()
	{
		viewMatrix = Maths.createViewMatrix(camera);
		currentRay = calculateMouseRay();
	}
	
	private Vector3f calculateMouseRay()
	{
		float mouseX = Mouse.getX(), mouseY = Mouse.getY();
		float normalX = 2f*mouseX/DisplayManager.width - 1f;
		float normalY = 2f*mouseY/DisplayManager.height - 1f;
		Vector2f normalized = new Vector2f(normalX, normalY);
		Vector4f clip = new Vector4f(normalized.x, normalized.y, -1f, 1f);
		Vector4f eye = Matrix4f.transform(Matrix4f.invert(projMatrix, null), clip, null);
		eye.z = -1f; eye.w = 0f;
		Vector4f temp = Matrix4f.transform(Matrix4f.invert(viewMatrix, null), eye, null);
		Vector3f rayWorld = new Vector3f(temp.x, temp.y, temp.z);
		return (Vector3f)rayWorld.normalise();
	}

}
