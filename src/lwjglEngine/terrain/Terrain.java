package lwjglEngine.terrain;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.lwjgl.util.vector.Vector3f;

import terrain.BicubicInterpolator;
import lwjglEngine.models.RawModel;
import lwjglEngine.render.Loader;
import lwjglEngine.textures.ModelTexture;
import lwjglEngine.textures.TerrainTexture;
import lwjglEngine.textures.TerrainTexturePack;

public class Terrain {

	public static final float SIZE = 1600;
	protected static final float MAX_HEIGHT = 40;
	protected static final float MIN_HEIGHT = -40;
	protected static final float MAX_PIXEL_COLOR = 256 * 256 * 256;
	
	public float x,z;
	public RawModel model;
	public TerrainTexturePack texturePack;
	public TerrainTexture blendMap;

	public Terrain(int gridX, int gridZ, Loader loader, TerrainTexturePack texturePack,
			TerrainTexture blendMap, String heightMap)
	{
		this.texturePack = texturePack;
		this.blendMap = blendMap;
		x = (float)gridX * SIZE;
		z = (float)gridZ * SIZE;
		model = generateTerrain(loader, heightMap);
	}

	protected RawModel generateTerrain(Loader loader, String heightMap)
	{
		if (heightMap == null) return null;
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File("res/"+heightMap+".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int VERTEX_COUNT = image.getHeight();
		
		int count = VERTEX_COUNT * VERTEX_COUNT;
		float[] vertices = new float[count*3];
		float[] normals = new float[count*3];
		float[] textureCoords = new float[count*2];
		int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT*1)];
		int vertexPointer = 0;
		for (int i = 0; i < VERTEX_COUNT; i++) {
			for (int j = 0; j < VERTEX_COUNT; j++) {
				vertices[vertexPointer*3] = (float)j/((float)VERTEX_COUNT-1) * SIZE;
				vertices[vertexPointer*3+1] = getHeight(j, i, image);
				vertices[vertexPointer*3+2] = (float)i/((float)VERTEX_COUNT-1) * SIZE;
				Vector3f normal = calculateNormal(j, i, image);
				normals[vertexPointer*3] = normal.x;
				normals[vertexPointer*3+1] = normal.y;
				normals[vertexPointer*3+2] = normal.z;
				textureCoords[vertexPointer*2] = (float)j/((float)VERTEX_COUNT-1);
				textureCoords[vertexPointer*2+1] = (float)i/((float)VERTEX_COUNT-1);
				vertexPointer++;
			}
		}
		int pointer = 0;
		for (int gz = 0; gz < VERTEX_COUNT-1; gz++) {
			for (int gx = 0; gx < VERTEX_COUNT-1; gx++) {
				int topLeft = (gz*VERTEX_COUNT) + gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz+1)*VERTEX_COUNT) + gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
		for (int i = 0; i < vertices.length; i+=3)
		{
			System.out.println(vertices[i] + " " + vertices[i+1] + " " + vertices[i+2]);
		}
		return loader.loadToVAO(vertices, textureCoords, normals, indices);
	}
	
	protected Vector3f calculateNormal(int x, int y, BufferedImage image)
	{
		float hl = getHeight(x-1, y, image);
		float hr = getHeight(x+1, y, image);
		float hd = getHeight(x, y-1, image);
		float hu = getHeight(x, y+1, image);
		Vector3f normal = new Vector3f(hl-hr, 2, hd-hu);
		normal.normalise();
		return normal;
	}
	
	private float getHeight(int x, int y, BufferedImage image)
	{
		if (x < 0 || x >= image.getHeight() || y < 0 || y >= image.getHeight())
		{
			return 0;
		}
		float height = image.getRGB(x, y);
		height += MAX_PIXEL_COLOR/2f;
		height /= MAX_PIXEL_COLOR/2f;
		height *= MAX_HEIGHT;
		return height;
	}

}
