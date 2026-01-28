package game;

import java.awt.Dimension;

import controller.impl.Controller;
import game.actionsgen.*;
import game.aigen.*;
import game.levelgen.*;
import game.worlddef.RandomWorldDefinitionProvider;
import model.impl.Model;
import utils.assets.impl.ProjectAssets;
import view.core.View;
import world.ports.WorldDefinition;
import world.ports.WorldDefinitionProvider;

public class Main {

	public static void main(String[] args) {

		System.setProperty("sun.java2d.uiScale", "1.0");

		Dimension worldDimension = new Dimension(9000, 6000);
		Dimension viewDimension = new Dimension(1800, 1800);
		int maxDynamicBodies = 2000;
		int maxAsteroidCreationDelay = 500;

		//
		// CORE ENGINE SETUP =>
		// world dimension +
		// view dimension +
		// max dynamic bodies +
		// CONTROLLER +
		// VIEW +
		// MODEL +
		// ACTIONS GENERATOR
		//

		Controller controller = new Controller(
				worldDimension,
				viewDimension,
				maxDynamicBodies,
				new View(),
				new Model(),
				new ActionsReboundCollisionPlayerImmunity());

		controller.activate();

		//
		// SCENE SETUP =>
		// world definition +
		// level generator +
		// AI generator
		//

		// 1) World definition
		ProjectAssets projectAssets = new ProjectAssets();
		WorldDefinitionProvider world = new RandomWorldDefinitionProvider(
			worldDimension, projectAssets);
		WorldDefinition worldDef = world.provide();

		// 2) Level generator (Level***)
		new LevelBasic(controller, worldDef);

		// 3) AI generator (AI***)
		new AIBasicSpawner(controller, worldDef, maxAsteroidCreationDelay).activate();

	}
}
