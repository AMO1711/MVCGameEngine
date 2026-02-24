
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import engine.controller.impl.Controller;
import engine.controller.ports.ActionsGenerator;
import engine.model.impl.Model;
import engine.utils.helpers.DoubleVector;
import engine.view.core.View;
import engine.world.ports.WorldDefinition;
import engine.world.ports.WorldDefinitionProvider;
import gameworld.AudioManager;
import gameworld.ProjectAssets;

public class Main {


	private static ProjectAssets projectAssets;
	private static AudioManager audioManager;
	private static final DoubleVector viewDimensions = new DoubleVector(1280, 720);
	private static final DoubleVector worldDimensions = new DoubleVector(4000, 7000);


	public static void main(String[] args) {
		System.setProperty("sun.java2d.uiScale", "1.0");
		System.setProperty("sun.java2d.opengl", "true");
		System.setProperty("sun.java2d.d3d", "false");

		projectAssets = new ProjectAssets();
		audioManager = new AudioManager(projectAssets.musicCatalog);

		audioManager.playMusic("musicaMenu");

		SwingUtilities.invokeLater(Main::createMainMenu);
	}

	private static void createMainMenu()  {
		JFrame menuFrame = new JFrame("FrostByte -- Skate & Slash!");

		menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		menuFrame.setSize((int)viewDimensions.x, (int)viewDimensions.y);
		menuFrame.setLocationRelativeTo(null);
		menuFrame.setResizable(false);

		JPanel bgPanel = new JPanel(new BorderLayout()) {
			private BufferedImage bgImage;

			{


				try {
					bgImage = ImageIO.read(new File("src/resources/images/MainScreen.png"));


				} catch (IOException e) {
					System.out.println("No se encontró la imagen de menu");
				}
			}
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				if (bgImage != null) {
					g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
				} else {
					g.setColor(Color.BLACK);
					g.fillRect(0, 0, getWidth(), getHeight());
				}
			}
		};

		JPanel buttonPanel = new JPanel();
		buttonPanel.setOpaque(false);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 80, 0));


		JButton btnIce = new JButton("Nivel Hielo");
		btnIce.setFont(new Font("Arial", Font.BOLD, 28));
		btnIce.setFocusPainted(false);

		JButton btnGrass = new JButton("Nivel Césped");
		btnGrass.setFont(new Font("Arial", Font.BOLD, 28));
		btnGrass.setFocusPainted(false);

		buttonPanel.add(btnIce);
		buttonPanel.add(Box.createRigidArea(new Dimension(100, 0)));
		buttonPanel.add(btnGrass);

		bgPanel.add(buttonPanel, BorderLayout.SOUTH);
		menuFrame.add(bgPanel);
		menuFrame.setVisible(true);


		btnIce.addActionListener(e -> {
			menuFrame.dispose();

			new Thread(() -> {
				startGame(new gameworld.IceWorldDefinitionProvider(worldDimensions, projectAssets));
			}).start();
		});

		btnGrass.addActionListener(e -> {
			menuFrame.dispose();

			new Thread(() -> {
				startGame(new gameworld.GrassWorldDefinitionProvider(worldDimensions, projectAssets));
			}).start();
		});
	}

	private static void startGame(WorldDefinitionProvider worldProv) {
		int maxBodies = 10;
		int maxAsteroidCreationDelay = 3000;

		ActionsGenerator gameRules = new gamerules.InLimitsGoToCenter();

		Controller controller = new Controller(
				worldDimensions, viewDimensions, maxBodies,
				new View(), new Model(worldDimensions, maxBodies),
				gameRules);

		controller.activate();

		WorldDefinition worldDef = worldProv.provide();

		if (worldDef.musicAssetId != null) {
			audioManager.playMusic(worldDef.musicAssetId);
		}

		new gamelevel.LevelBasic(controller, worldDef);

		new gameai.AIBasicSpawner(controller, worldDef, maxAsteroidCreationDelay).activate();
	}
}
