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
	public Vector3f rayCastHit;
	public static float constant = 1.0f;

	private Matrix4f projMatrix, viewMatrix, transformMatrix;
	public Camera camera;

	public MousePicker(Matrix4f p, Camera c) {
		camera = c;
		projMatrix = p;
		viewMatrix = MatrixMathUtil.createViewMatrix(camera);
	}

	public void update() {
		viewMatrix = MatrixMathUtil.createViewMatrix(camera);
		currentRay = calculateMouseRay(Mouse.getX(), DisplayManager.height - Mouse.getY());

		rayCastHit = new Vector3f(camera.position.x - camera.position.y / currentRay.y * currentRay.x, 0,
				camera.position.z - camera.position.y / currentRay.y * currentRay.z);
		rayCastHit.scale(constant);
	}

	private Vector3f calculateMouseRay(float mouseX, float mouseY) {
		// float mouseX = Mouse.getX(), mouseY = Mouse.getY();
		float normalX = 2f * mouseX / DisplayManager.width - 1f;
		float normalY = 2f * mouseY / DisplayManager.height - 1f;
		Vector2f normalized = new Vector2f(normalX, normalY);
		Vector4f clip = new Vector4f(normalized.x, normalized.y, -1f, 1f);
		Vector4f eye = Matrix4f.transform(Matrix4f.invert(projMatrix, null), clip, null);
		eye.z = -1f;
		eye.w = 0f;
		Vector4f temp = Matrix4f.transform(Matrix4f.invert(viewMatrix, null), eye, null);
		Vector3f rayWorld = new Vector3f(temp.x, temp.y, temp.z);
		return (Vector3f) rayWorld.normalise();
	}

	// Reverse of the transformation in the previous function. Although that was the
	// reverse,
	// so I guess this is the "normal" forward directed transformation?
	public Vector2f calculateScreenPos(float posX, float posZ) {
		// Create a new transformation matrix for the different position
		Matrix4f transformMatrix = MatrixMathUtil.createTransformMatrix(new Vector3f(posX, 0, posZ), 0, 0, 0, 1);
		Vector4f worldPosition = Matrix4f.transform(transformMatrix, new Vector4f(posX, 0, posZ, 1.0f), null);

		// equivalent: glPosition = projectionMatrix * (viewMatrix * worldPosition);
		Vector4f glPosition = Matrix4f.transform(projMatrix, Matrix4f.transform(viewMatrix, worldPosition, null), null);
		Vector2f normalized = new Vector2f(glPosition.x, glPosition.y);

		// Reverse: y = 2x/width - 1, reverse's inverse: (width/2)(y + 1) = x
		return new Vector2f((normalized.x + 1f) * DisplayManager.width / 2f,
				(normalized.y + 1f) * DisplayManager.height / 2f);
	}

}
