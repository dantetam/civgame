package system;

import org.lwjgl.util.vector.Vector3f;

import game.Tile;
import lwjglEngine.terrain.Terrain;
import lwjglEngine.textures.TerrainTexture;
import lwjglEngine.toolbox.MousePicker;
import render.CivGame;

public class RenderSystem extends BaseSystem {

	public MousePicker mousePicker;
	
	public RenderSystem(CivGame civGame) {
		super(civGame);
		// TODO Auto-generated constructor stub
	}

	public void tick() 
	{
		mousePicker.update();
		
		//float dist = (float)Math.sqrt(Math.pow(ray.x, 2) + Math.pow(ray.z, 2));
		//float angle = (float)Math.atan2(ray.z, ray.x);
		//Vector3f rayCast = new Vector3f(camPos.x + camPos.y/ray.y*dist*(float)Math.cos(angle),0,camPos.z + camPos.y/ray.y*dist*(float)Math.sin(angle));
		Tile t = main.grid.getTile(
				(int)Math.floor(mousePicker.rayCastHit.x/Terrain.SIZE/0.9f*(float)main.grid.rows),
				(int)Math.floor(mousePicker.rayCastHit.z/Terrain.SIZE/0.9f*(float)main.grid.cols)
				);
		main.menuSystem.setMouseHighlighted(t);
		//main.lwjglSystem.terrain0.blendMap2 = new TerrainTexture(main.lwjglSystem.loader.loadTexture("generatedHighlightMap"));
		/*if (main != null)
			if (main.lwjglSystem != null)
				if (main.lwjglSystem.frameCount % 50 == 25)
					System.out.println(rayCast);*/
		//main.menuSystem.mouseHighlighted
	}
	
}
