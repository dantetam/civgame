package render;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.util.vector.Vector2f;

import lwjglEngine.entities.Camera;
import lwjglEngine.fontRendering.TextMaster;
import lwjglEngine.gui.GuiTexture;
import lwjglEngine.gui.Mouse;
import lwjglEngine.render.DisplayManager;
import lwjglEngine.tests.MainGameLoop;
import data.Color;
import data.ColorImage;
import data.EntityData;
import terrain.*;
import vector.Point;
import vector.Vector3f;
import system.*;
import entity.Player;
import game.BaseEntity;
import game.Civilization;
import game.GameEntity;
import game.Grid;
import game.LwjglGrid;
import game.Pathfinder;
import game.Tile;

public class CivGame {

	public Game game;
	public boolean enabled = true;

	public BaseTerrain map;
	public String challengeType, terrainType, civChoice;
	public int numCivs, numCityStates, difficultyLevel;
	public double[][] terrain;
	// public boolean[][] rivers;
	public Erosion erosion;

	public LwjglGrid grid;
	public long seed;

	public Player player = new Player();
	public boolean showAll = false;

	public ArrayList<BaseSystem> systems;
	// private RenderSystem renderSystem = new RenderSystem(this);
	public MainGameLoop lwjglSystem;
	public Camera camera; // double reference for quick fix
	public float width = 1500, height = 900;
	public float centerX = width / 2, centerY = height / 2; // for rendering purposes, to determine how the position of
															// the mouse affects the camera

	public MenuSystem menuSystem = new MenuSystem(this);
	public ArrayList<GuiTexture> guis = new ArrayList<GuiTexture>();

	public InputSystem inputSystem = new InputSystem(this);
	public CivilizationSystem civilizationSystem = new CivilizationSystem(this);
	public RenderSystem renderSystem = new RenderSystem(this);
	// public ChunkSystem chunkSystem;

	public boolean testing = false, tacticalView = false, keyMenu = false, forceCursor = false; // tacticalView ->
																								// display special tile
																								// yield and tile
																								// ownership GUIs

	public CivGame(Game game, int numCivs, int numCityStates, int difficultyLevel, String challengeType,
			String terrainType, String civChoice, long seed) {
		this.game = game;
		this.numCivs = numCivs;
		this.numCityStates = numCityStates;
		this.difficultyLevel = difficultyLevel;
		this.challengeType = challengeType;
		this.terrainType = terrainType;
		this.civChoice = civChoice;

		this.seed = seed;

		systems = new ArrayList<BaseSystem>();

		// original order, civ, render, (menu), input

		systems.add(civilizationSystem);
		systems.add(inputSystem);
		systems.add(renderSystem);
		// systems.add(lwjglSystem);
		systems.add(menuSystem);

		setup();
	}

	public void options(boolean autoSelect, boolean t, boolean fc) {
		inputSystem.autoSelect = autoSelect;
		testing = t;
		forceCursor = fc;
	}

	public void setup() {
		try {
			System.setProperty("org.lwjgl.librarypath", "lib/natives");

			DisplayManager.createDisplay();
			DisplayManager.main = this;
			// setMouseCallback();
			// setKeyCallback();
			GLFW.glfwShowWindow(DisplayManager.window);

			generate(terrainType);
			takeBlendMap(sendBlendMap(grid), "res/generatedBlendMap.png");
			takeBlendMap(sendHighlightMap(grid), "res/generatedHighlightMap.png");

			// Force update
			menuSystem.rbox = grid.civs[0].revealedBox();

			lwjglSystem = new MainGameLoop(this);
			camera = lwjglSystem.camera;

			// grid.setManager(lwjglSystem.levelManager); //Manually assign this since the
			// levelmanager is created after the grid

			// makeRivers(terrain);
			/*
			 * for (int r = 0; r < terrain.length; r++) { for (int c = 0; c <
			 * terrain[0].length; c++) { print((int)terrain[r][c] + " "); } println(); }
			 */

			// chunkSystem = new ChunkSystem(this);
			// systems.add(chunkSystem);
			erosion = new Erosion(terrain, 1);
			erode();
			// chunkSystem.tick();

			// Set it manually
			player.civ = grid.civs[0];
			player.civ.name = "Player";
			player.orient(grid, lwjglSystem.widthBlock);
			inputSystem.on = false;
			menuSystem.select(null); // Fix the selection menu
			// chunkSystem.update(); //Update once
			Tile t = grid.civs[0].units.get(0).location; // First settler
			fixCamera(t.row, t.col); // Center the camera at the appropriate location

			for (int i = 0; i < grid.civs.length; i++) {
				Civilization civ = grid.civs[i];
				civ.war = Math.min(1, civ.war * 2);
				civ.tallwide = Math.min(0, civ.tallwide / 2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean newLine = false;
	public float rMouseX = -1, rMouseY = -1;

	public void mousePressed() {

	}

	public void mouseDragged() {
		// println("Dragging " + mouseX + "," + mouseY);
		menuSystem.menuActivated = true;
		// newMenuSystem.mouseDragged(mouseX, mouseY);
	}

	public void mouseReleased() {
		/*
		 * if (mouseButton == RIGHT) { inputSystem.queueRightClick(rMouseX, rMouseY);
		 * rMouseX = -1; rMouseY = -1; menuSystem.movementChoices.clear();
		 * menuSystem.pathToHighlighted.clear(); //menuSystem.menuActivated = false;
		 * newMenuSystem.mouseReleased(rMouseX, rMouseY); }
		 */
	}

	public void stop() {
		game.exit();
		// super.stop();
	}

	public void fixCamera(int r, int c) {
		Vector3f tilePosition = grid.get3DPositionFromTile(r, c);
		camera.focusCamera(tilePosition.x, tilePosition.z, -35);
		/*
		 * if (camera.position.y < 25)
		 * camera.focusCamera((r-0.5F)*1600F/(float)grid.rows,
		 * (c+0.5F)*1600F/(float)grid.cols, -10); else
		 * camera.focusCamera((r-0.5F)*1600F/(float)grid.rows,
		 * (c+0.5F)*1600F/(float)grid.cols + camera.position.y, -35);
		 */
		/*
		 * lwjglSystem.camera.position.x = r*lwjglSystem.widthBlock;
		 * lwjglSystem.camera.position.y = 60; lwjglSystem.camera.position.x =
		 * (c-2)*lwjglSystem.widthBlock;
		 */
		// player.rotY = 0;
		// player.rotVertical = 0;
		// player.update();
	}

	public void resetCamera() {
		// centerX = mouseX/(1 - player.rotY/(float)Math.PI);
		// centerY = mouseY/(1 + 4*player.rotVertical/(float)Math.PI);
	}

	private static final int blendMapWidth = 256, blendMapHeight = 256;

	private BufferedImage sendBlendMap(Grid grid) {
		try {
			BufferedImage img = new BufferedImage(blendMapWidth, blendMapHeight, BufferedImage.TYPE_INT_RGB);
			int chunkWidth = (int) ((float) blendMapWidth / (float) grid.rows),
					chunkHeight = (int) ((float) blendMapHeight / (float) grid.cols);
			int[][] colors = new int[blendMapWidth][blendMapHeight];
			for (int r = 0; r < grid.rows; r++) {
				for (int c = 0; c < grid.cols; c++) {
					// Get the raw color by brick color by biome
					// Color color =
					// EntityData.brickColorMap.get(EntityData.groundColorMap.get(grid.getTile(r,c).biome));
					// int intColor = getIntColor((int)(color.r*255), (int)(color.g*255),
					// (int)(color.b*255));
					int gray = (int) Math.max(1, (float) grid.getTile(r, c).biome / 6F * 255F);
					int intColor = getIntColor(gray, gray, gray);
					for (int rr = r * chunkWidth; rr < (r + 1) * chunkWidth; rr++) {
						for (int cc = c * chunkHeight; cc < (c + 1) * chunkHeight; cc++) {
							if (rr >= colors.length || cc >= colors[0].length)
								break;
							if (rr < 0 || cc < 0)
								continue;
							colors[rr][cc] = intColor;
						}
					}
				}
			}
			for (int r = 0; r < colors.length; r++) {
				for (int c = 0; c < colors[0].length; c++) {
					img.setRGB(r, c, colors[r][c]);
				}
			}
			return img;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean availableUpdate = true;

	public BufferedImage sendHighlightMap(Grid grid) {
		if (!availableUpdate)
			return null;
		availableUpdate = false;
		try {
			BufferedImage img = new BufferedImage(blendMapWidth * 2, blendMapHeight * 2, BufferedImage.TYPE_INT_RGB);
			int chunkWidth = (int) (((float) blendMapWidth * 2) / ((float) grid.rows)),
					chunkHeight = (int) (((float) blendMapHeight * 2) / ((float) grid.cols));
			int[][] colors = new int[blendMapWidth * 2][blendMapHeight * 2];
			// System.out.println(chunkWidth + " " + chunkWidth*grid.rows + " " +
			// grid.rows);
			/*
			 * int width = 16; BufferedImage img = new BufferedImage(width*grid.rows,
			 * width*grid.cols, BufferedImage.TYPE_INT_RGB); int chunkWidth = 16,
			 * chunkHeight = 16; int[][] colors = new int[width*grid.rows][width*grid.cols];
			 */
			for (int r = 0; r < grid.rows; r++) {
				for (int c = 0; c < grid.cols; c++) {
					int red = 0, green = 0, blue = 0;
					Tile t = grid.getTile(r, c);
					if (t.owner != null) {
						red = (int) t.owner.r;
						green = (int) t.owner.g;
						blue = (int) t.owner.b;
					}
					if (menuSystem != null) {
						if (menuSystem.getSelected() != null) {
							if (t.equals(menuSystem.getSelected().location)
									|| t.dist(menuSystem.getSelected().location) < 2) {
								if (t.owner == null) {
									red = 255;
									green = 255;
									blue = 255;
								} else {
									red += 50;
									green += 50;
									blue += 50;
								}
							}
						}
						if (t.equals(menuSystem.getMouseHighlighted())
								|| t.dist(menuSystem.getMouseHighlighted()) < 2) {
							if (t.owner == null) {
								red = 255;
								green = 255;
								blue = 255;
							} else {
								red += 20;
								green += 20;
								blue += 20;
							}
						}
					}

					int intColor = getIntColor(red, green, blue);
					for (int rr = r * chunkWidth; rr < (r + 1) * chunkWidth; rr++) {
						for (int cc = c * chunkHeight; cc < (c + 1) * chunkHeight; cc++) {
							if (rr >= colors.length || cc >= colors[0].length)
								break;
							if (rr < 0 || cc < 0)
								continue;
							colors[rr][cc] = intColor;
						}
					}
				}
			}
			for (int r = 0; r < colors.length; r++)
				for (int c = 0; c < colors[0].length; c++)
					img.setRGB(r, c, colors[r][c]);
			availableUpdate = true;
			return img;
		} catch (Exception e) {
			availableUpdate = true;
			e.printStackTrace();
			return null;
		}
	}

	private int getIntColor(int r, int g, int b) {
		r = r < 0 ? 0 : r;
		g = g < 0 ? 0 : g;
		b = b < 0 ? 0 : b; // Ternary hell...
		r = r > 255 ? 255 : r;
		g = g > 255 ? 255 : g;
		b = b > 255 ? 255 : b;
		int col = (r << 16) | (g << 8) | b;
		return col;
	}

	public void takeBlendMap(BufferedImage image, String fileName) {
		if (image == null)
			return;
		try {
			File file = new File(fileName);
			if (!file.exists())
				file.createNewFile();
			ImageIO.write(image, "png", file);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return; // ?? Did this method return something originally?
	}

	// Use the appropriate terrain to make a table and then render it by making some
	// entities
	// Then make a grid out of it
	public float cutoff;

	public void generate(String terrainType) {
		float con;
		if (terrainType.equals("terrain1")) {
			map = new PerlinNoise(seed);
			terrain = map.generate(new double[] { 32, 32, 150, 8, 1, 0.8, 6, 256, 55 });
			con = 1F;
			cutoff = 55;
		} else if (terrainType.equals("terrain2")) {
			map = new RecursiveBlock(seed);
			terrain = map.generate(new double[] { 10, 0 });
			con = 1F;
			cutoff = 1;
		} else if (terrainType.equals("terrain10")) {
			int len = 128;
			double[][] temp = DiamondSquare.makeTable(50, 50, 50, 50, len + 1);
			map = new DiamondSquare(temp);
			map.seed(seed);
			// ds.diamond(0, 0, 4);
			// displayTables = ds.dS(0, 0, len, 40, 0.7)
			// map.seed(seed);
			terrain = map.generate(new double[] { 0, 0, len, 40, 0.7 });
			// System.out.println(terrain);
			con = 1F;
			cutoff = 60;
		} else if (terrainType.equals("terrain11")) {
			int len = 128;
			double[][] temp = DiamondSquare.makeTable(50, 50, 50, 50, len + 1);
			temp[temp.length / 2][temp[0].length / 2] = 200;
			temp[0][temp[0].length / 2] = 50;
			temp[temp.length - 1][temp[0].length / 2] = 50;
			temp[temp[0].length / 2][0] = 50;
			temp[temp[0].length / 2][temp.length - 1] = 50;
			map = new DiamondSquare(temp);
			map.seed(seed);
			// ds.diamond(0, 0, 4);
			// displayTables = ds.dS(0, 0, len, 40, 0.7)
			// map.seed(seed);
			terrain = map.generate(new double[] { 0, 0, len, 40, 0.7 });
			// System.out.println(terrain);
			con = 1F;
			cutoff = 100;
		} else if (terrainType.equals("terrain4")) {
			map = new RecursiveBlock(seed);
			terrain = map.generate(new double[] { 10, 1 });
			con = 3F;
			cutoff = 1;
		} else if (terrainType.equals("terrain5")) {
			map = new PerlinNoise(seed);
			terrain = map.generate(new double[] { 32, 32, 150, 8, 1, 0.8, 6, 64, -150 });
			con = 1F;
			cutoff = -150;
		} else {
			System.err.println("No map!");
			int[] err = new int[5];
			err[10] = 0;
			con = 1F;
			cutoff = 0;
		}
		int n = 1;
		if (numCivs <= 4) {
			n = 4;
		} else if (numCivs <= 7) {
			n = 3;
		} else if (numCivs <= 10) {
			n = 2;
		} else {
			n = 1;
			// Don't sample and downsize it
		}
		if (!terrainType.equals("terrain11"))
			n *= 2;
		terrain = downSample(terrain, n);
		menuSystem.multiplier = n;
		menuSystem.markedTiles = new boolean[terrain.length][terrain[0].length];
		int[][] biomes = assignBiome(terrain);
		grid = new LwjglGrid(civChoice, terrain, biomes, assignResources(biomes), assignFields(biomes), numCivs,
				numCityStates, difficultyLevel, 3, (int) cutoff, seed);
		civilizationSystem.theGrid = grid;
		// player = new Player(grid.civs[0]);
		makeRivers(biomes);
	}

	public int[][] assignFields(int[][] biomes) {
		int[][] temp = new int[biomes.length][biomes[0].length];
		Random rand = new Random(seed + 1000000000);
		for (int r = 0; r < biomes.length; r++) {
			for (int c = 0; c < biomes[0].length; c++) {
				if (biomes[r][c] == -1)
					temp[r][c] = 0;
				else {
					double random = rand.nextDouble();
					if (random < 0.05 * biomes[r][c] / 3) {
						temp[r][c] = 3;
					} else if (random < 0.1 * biomes[r][c] / 3) {
						temp[r][c] = 2;
					} else if (random < 0.25 * biomes[r][c] / 3) {
						temp[r][c] = 1;
					} else {
						temp[r][c] = 0;
					}
				}
			}
		}
		return temp;
	}

	public int[][] assignBiome(double[][] terrain) {
		int[][] temp = new int[terrain.length][terrain[0].length];
		double width = Math.max(Math.pow(2, Math.floor(Math.log10(terrain.length) / Math.log10(2)) + 1),
				Math.pow(2, Math.floor(Math.log10(terrain[0].length) / Math.log10(2)) + 1));
		double[][] temperature = assignTemperature(width);
		double[][] rain = assignRain(temperature);
		for (int r = 0; r < temp.length; r++) {
			for (int c = 0; c < temp[0].length; c++) {
				if (terrain[r][c] >= cutoff) {
					/*
					 * System.out.println("------"); System.out.println(temp.length + " " +
					 * temp[0].length); System.out.println(terrain.length + " " +
					 * terrain[0].length); System.out.println(temperature.length + " " +
					 * rain.length);
					 */
					temp[r][c] = returnBiome(temperature[r][c], rain[r][c]);
				} else
					temp[r][c] = -1;
			}
		}
		/*
		 * for (int r = 0; r < temp.length; r++) { for (int c = 0; c < temp[0].length;
		 * c++) { System.out.print(temp[r][c] + " "); } System.out.println(); }
		 */
		return temp;
	}

	public void makeRivers(int[][] biomes) {
		grid.verticalRivers = new boolean[biomes.length][biomes.length - 1];
		grid.horizontalRivers = new boolean[biomes.length - 1][biomes.length];
		for (int r = 0; r < grid.verticalRivers.length; r++) {
			for (int c = 0; c < grid.verticalRivers[0].length; c++) {
				if (biomes[r][c] >= 1 && biomes[r][c + 1] >= 1) {
					if (Math.random() < 0.02 * biomes[r][c]) {
						grid.verticalRivers[r][c] = true;
					}
				}
			}
		}
		for (int r = 0; r < grid.horizontalRivers.length; r++) {
			for (int c = 0; c < grid.horizontalRivers[0].length; c++) {
				if (biomes[r][c] >= 1 && biomes[r + 1][c] >= 1) {
					if (Math.random() < 0.02 * biomes[r][c]) {
						grid.horizontalRivers[r][c] = true;
					}
				}
			}
		}
		// ^ Set them directly from here
		// grid.verticalRivers = verticalRivers;
		// grid.horizontalRivers = horizontalRivers;
	}

	public int[][] assignResources(int[][] biomes) {
		int[][] resources = new int[biomes.length][biomes[0].length];
		for (int r = 0; r < biomes.length; r++) {
			for (int c = 0; c < biomes[0].length; c++) {
				int b = biomes[r][c];
				boolean[] candidates = new boolean[100];
				if (b == -1) {
					candidates[10] = true;
					candidates[11] = true;
				} else if (b == 0) {
					candidates[20] = true;
					candidates[21] = true;
					candidates[22] = true;
					candidates[40] = true;
				} else if (b == 1) {
					candidates[20] = true;
					candidates[21] = true;
					candidates[22] = true;
					candidates[40] = true;
				} else if (b == 2) {
					candidates[20] = true;
					candidates[21] = true;
					candidates[22] = true;
					candidates[40] = true;
				} else if (b == 3) {
					candidates[1] = true;

					candidates[20] = true;
					candidates[22] = true;
					candidates[40] = true;
				} else if (b == 4) {
					candidates[1] = true;

					candidates[20] = true;
					candidates[22] = true;
					candidates[30] = true;
					candidates[40] = true;
				} else if (b == 5) {
					candidates[1] = true;

					candidates[22] = true;
					candidates[30] = true;
					candidates[40] = true;
				} else if (b == 6) {
					candidates[1] = true;
					candidates[2] = true;

					candidates[22] = true;
					candidates[30] = true;
					candidates[40] = true;
				} else if (b == 8) {

				}
				for (int i = 0; i < candidates.length; i++) {
					if (candidates[i]) {
						if (Math.random() < EntityData.resourceLevels[i]) {
							resources[r][c] = i;
						}
						/*
						 * else if (Math.random() < 0.06) { resources[r][c] = 1; }
						 */
					}
				}
			}
		}
		/*
		 * for (int r = 0; r < resources.length; r++) { for (int c = 0; c <
		 * resources[0].length; c++) { System.out.print(resources[r][c] + " "); }
		 * System.out.println(); }
		 */
		return resources;
	}

	// The three methods below stolen from "blockgame"
	// Returns an interpolated map which gives each chunk a temperature, 0 to 4
	// (arctic to tropical)
	public double[][] assignTemperature(double nDiv) {
		// int chunkLength = rows; //chunkService.returnChunkLength();
		// double[][] oldSource = new
		// PerlinNoise(870).makePerlinNoise((int)nDiv,(int)nDiv,3,8,3,0.5,2);
		// return PerlinNoise.expand(oldSource,nDiv*2);
		double[][] oldSource = new PerlinNoise(seed).generate(new double[] { 32, 32, 3, 16, 3, 1, 3, nDiv });
		return oldSource;
	}

	// Returns an interpolated map which gives each chunk a level of rain, based on
	// temperature
	// Arctic climates do not have rain, tropical climates can have any level
	public double[][] assignRain(double[][] temperature) {
		Random rainRandom = new Random(seed);
		double[][] returnThis = new double[temperature.length][temperature[0].length];
		for (int i = 0; i < temperature.length; i++) {
			for (int j = 0; j < temperature[0].length; j++) {
				returnThis[i][j] = rainRandom.nextDouble() * temperature[i][j] + rainRandom.nextDouble();
			}
		}
		// returnThis = PerlinNoise.recurInter(returnThis,1,returnThis.length/2);
		return returnThis;
	}

	// returns the biome based on temperature, t, and rain, r
	/*
	 * 0 ice 1 taiga 2 desert 3 savannah 4 dry forest 5 forest 6 rainforest 7 beach
	 * (outdated)
	 */
	public int returnBiome(double t, double r) {
		if (t > 3) {
			if (r > 3.5)
				return 6;
			else if (r > 2.5)
				return 5;
			else if (r > 1.5)
				return 4;
			/*
			 * else if (r > 1.25) return 3;
			 */
			else
				return 2;
		} else if (t > 2) {
			if (r > 2)
				return 5;
			else if (r > 1.5)
				return 4;
			else if (r > 0.75)
				return 3;
			else
				return 2;
		} else if (t > 1) {
			if (r > 1)
				return 4;
			if (r > 0.5)
				return 3;
			else
				return 2;
		} else {
			if (r > 0.5)
				return 1;
			else {
				// System.out.println(t + " " + r);
				return 0;
			}
		}
	}

	public double[][] downSample(double[][] terrain, int num) {
		double[][] temp = new double[terrain.length / num + 1][terrain.length / num + 1];
		for (int r = 0; r < terrain.length; r += num)
			for (int c = 0; c < terrain[0].length; c += num)
				temp[r / num][c / num] = terrain[r][c];
		return temp;
	}

	public void erode() {
		for (int i = 0; i < 100; i++) {
			int r = 0;
			int c = 0;
			do {
				r = (int) (terrain.length * Math.random());
				c = (int) (terrain.length * Math.random());
			} while (terrain[r][c] < erosion.cutoff);
			erosion.flood(r, c, 15);
		}
		boolean done = false;
		while (!done) {
			done = !erosion.tick();
		}
	}

	public float widthBlock() {
		return lwjglSystem.widthBlock;
	}
	// public void setUpdateFrame(int frames) {chunkSystem.updateFrame = frames;}

}
