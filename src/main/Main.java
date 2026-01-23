package main;

import assets.implementations.ProjectAssets;
import controller.implementations.Controller;
import generators.implementations.DefaultActionsGenerator;
import generators.implementations.DefaultIAGenerator;
import generators.implementations.DefaultLevelGenerator;
import generators.ports.LifeConfigDTO;
import model.implementations.Model;
import view.core.View;
import world.implementations.EarthInCenterWorldDefinitionProvider;
import world.ports.WorldDefinition;
import world.ports.WorldDefinitionProvider;

public class Main {

    public static void main(String[] args) {

        System.setProperty("sun.java2d.uiScale", "1.0");
        int worldWidth = 2450;
        int worldHeight = 1450;
        int maxDynamicBodies = 2000;
        int maxAsteroidCreationDelay = 500;
        int minAsteroidSize = 55;
        int maxAsteroidSize = 10;
        int maxAsteroidMass = 1000;
        int minAsteroidMass = 10;
        int maxAsteroidSpeedModule = 175;
        int maxAsteroidAccModule = 0;

        ProjectAssets projectAssets = new ProjectAssets();

        WorldDefinitionProvider world = new EarthInCenterWorldDefinitionProvider(
                worldWidth,
                worldHeight,
                projectAssets);

        WorldDefinition worldDef = world.provide();

        Controller controller = new Controller(
                worldWidth,
                worldHeight,
                new View(),
                new Model(worldWidth, worldHeight, maxDynamicBodies),
                new DefaultActionsGenerator());

        controller.activate();

        DefaultLevelGenerator worldGenerator = new DefaultLevelGenerator(
                controller,
                worldDef);

        LifeConfigDTO lifeConfig = new LifeConfigDTO(
                maxAsteroidCreationDelay,
                maxAsteroidSize,
                minAsteroidSize,
                maxAsteroidMass,
                minAsteroidMass,
                maxAsteroidSpeedModule,
                maxAsteroidAccModule);

        DefaultIAGenerator lifeGenerator = new DefaultIAGenerator(
                controller,
                worldDef,
                lifeConfig);

        lifeGenerator.activate();
    }
}
