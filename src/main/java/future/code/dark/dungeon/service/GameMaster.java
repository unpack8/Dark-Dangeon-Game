package future.code.dark.dungeon.service;

import future.code.dark.dungeon.config.Configuration;
import future.code.dark.dungeon.domen.Coin;
import future.code.dark.dungeon.domen.DynamicObject;
import future.code.dark.dungeon.domen.Enemy;
import future.code.dark.dungeon.domen.Exit;
import future.code.dark.dungeon.domen.GameObject;
import future.code.dark.dungeon.domen.Map;
import future.code.dark.dungeon.domen.Player;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static future.code.dark.dungeon.config.Configuration.*;

public class GameMaster {

    private static GameMaster instance;
    private final Map map;
    private final List<GameObject> gameObjects;
    private static final Image Victory = new ImageIcon(VICTORY).getImage();
    private static final Image GameOver = new ImageIcon(GAME_OVER).getImage();

    public static synchronized GameMaster getInstance() {
        if (instance == null) {
            instance = new GameMaster();
        }
        return instance;
    }

    private GameMaster() {
        try {
            this.map = new Map(Configuration.MAP_FILE_PATH);
            this.gameObjects = initGameObjects(map.getMap());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private List<GameObject> initGameObjects(char[][] map) {
        List<GameObject> gameObjects = new ArrayList<>();
        Consumer<GameObject> addGameObject = gameObjects::add;
        Consumer<Enemy> addEnemy = enemy -> {if (ENEMIES_ACTIVE) gameObjects.add(enemy);};

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                switch (map[i][j]) {
                    case EXIT_CHARACTER -> addGameObject.accept(new Exit(j, i));
                    case COIN_CHARACTER -> addGameObject.accept(new Coin(j, i));
                    case ENEMY_CHARACTER -> addEnemy.accept(new Enemy(j, i));
                    case PLAYER_CHARACTER -> addGameObject.accept(new Player(j, i));
                }
            }
        }

        return gameObjects;
    }

    public void renderFrame(Graphics graphics) {
        getMap().render(graphics);
        getStaticObjects().forEach(gameObject -> gameObject.render(graphics));
        getEnemies().forEach(gameObject -> gameObject.render(graphics));
        getPlayer().render(graphics);
        removeCollectedCoins();
        if(getPlayer().checkPlayerState(listOfCollisions(gameObjects))) {
            graphics.drawImage(GameOver,0, 0, map.getWidth() * SPRITE_SIZE,map.getHeight() * SPRITE_SIZE, null);
        }
        if(checkLevel(gameObjects)) {
            graphics.drawImage(Victory,0, 0, map.getWidth() * SPRITE_SIZE,map.getHeight() * SPRITE_SIZE, null);
        }
        graphics.setColor(Color.WHITE);
        graphics.drawString(getPlayer().toString(), 10, 20);
    }

    public Player getPlayer() {
        return (Player) gameObjects.stream()
                .filter(gameObject -> gameObject instanceof Player)
                .findFirst()
                .orElseThrow();
    }

    private List<GameObject> getStaticObjects() {
        return gameObjects.stream()
                .filter(gameObject -> !(gameObject instanceof DynamicObject))
                .collect(Collectors.toList());
    }

    private List<Enemy> getEnemies() {
        return gameObjects.stream()
                .filter(gameObject -> gameObject instanceof Enemy)
                .map(gameObject -> (Enemy) gameObject)
                .collect(Collectors.toList());
    }
    private boolean checkLevel(List<GameObject> gameObjects){
        return gameObjects.stream()
                .filter(gameObject -> gameObject instanceof Coin)
                .toList().size() == 0;
    }
    private void removeCollectedCoins(){
        for(GameObject go : gameObjects.stream()
                .filter(gameObject -> gameObject instanceof Coin && ((Coin) gameObject).getState())
                .toList()) {
            gameObjects.remove(go);
        }
    }

    private List<GameObject> listOfCollisions(List<GameObject> gameObjects){
        return gameObjects.stream()
                .filter(gameObject -> (gameObject instanceof Coin || gameObject instanceof Enemy) &&
                        gameObject.getXPosition() == getPlayer().getXPosition() &&
                        gameObject.getYPosition() == getPlayer().getYPosition())
                .collect(Collectors.toList());
    }

    public Map getMap() {
        return map;
    }
}
