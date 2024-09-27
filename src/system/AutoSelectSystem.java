package system;

import game.BaseEntity;
import game.Civilization;
import game.GameEntity;
import game.Tile;
import lwjglEngine.fontRendering.TextMaster;
import lwjglEngine.render.DisplayManager;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import org.lwjgl.glfw.GLFW;

import processing.core.*;
import data.EntityData;
import data.Field;
import render.CivGame;
import render.TextBox;
import system.MenuSystem.Click;
import units.City;
import units.Settler;

public class AutoSelectSystem extends BaseSystem {

	public AutoSelectSystem(CivGame civGame) {
		super(civGame);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void tick() {
		if (main.menuSystem.getSelected() == null) {
			if (main.autoSelectFramesWait <= 0) {
				main.resetAutoSelectWait();
				if (main.inputSystem.availableUnit() != null) {
					main.inputSystem.selectAvailableUnit();
					main.menuSystem.forceUpdate();
				}
			}
			else if (main.autoSelectFramesWait > 0) {
				main.autoSelectFramesWait--;
			}
		}
	}

}
