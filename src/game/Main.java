package game;

import assets.impl.ProjectAssets;
import controller.impl.Controller;
import controller.ports.ActionsGenerator;
import game.actionsgen.*;
import game.aigen.*;
import game.levelgen.*;
import game.worlddef.RandomWorldDefinitionProvider;
import model.impl.Model;
import utils.helpers.DoubleVector;
import view.core.View;
import world.ports.WorldDefinition;
import world.ports.WorldDefinitionProvider;

public class Main {

	public static void main(String[] args) {

		System.setProperty("sun.java2d.uiScale", "1.0");

		DoubleVector worldDimension = new DoubleVector(18000, 18000);
		DoubleVector viewDimension = new DoubleVector(2700, 1450);
		int maxBodies = 1000;
		int maxAsteroidCreationDelay = 100;

		ProjectAssets projectAssets = new ProjectAssets();
		ActionsGenerator actionsGenerator = new ActionsInLimitsGoToCenter();
		WorldDefinitionProvider world = new RandomWorldDefinitionProvider(
				worldDimension, projectAssets);

		// *** CORE ENGINE ***

		// region Controller
		Controller controller = new Controller(
				worldDimension,
				viewDimension,
				maxBodies,
				new View(),
				new Model(),
				actionsGenerator);

		controller.activate();
		// endregion

		// *** SCENE ***

		// region World definition
		WorldDefinition worldDef = world.provide();
		// endregion

		// region Level generator (Level***)
		new LevelBasic(controller, worldDef);
		// endregion

		// region AI generator (AI***)
		new AIBasicSpawner(controller, worldDef, maxAsteroidCreationDelay).activate();
		// endregion

	}
}
