package future.code.dark.dungeon.domen;

import future.code.dark.dungeon.config.Configuration;

import java.util.List;
import java.util.stream.Collectors;

public class Player extends DynamicObject {
    private static final int stepSize = 1;
    private enum State {
        ALIVE, DEAD
    }
    private State state = State.ALIVE;
    private int score = 0;

    public Player(int xPosition, int yPosition) {
        super(xPosition, yPosition, Configuration.PLAYER_SPRITE);
    }

    public void move(Direction direction) {
        super.move(direction, stepSize);
    }

    @Override
    public String toString() {
        return "Score: " + this.score;
    }
//    public String getScore() {
//        return "Player{[" + this.xPosition + ":" + this.yPosition + "]}";
//    }
    public State getState() {
        return this.state;
    }
    public void setStateDead() {
        this.state = State.DEAD;
    }

//    public List<GameObject> listOfCollisions(List<GameObject> gameObjects){
//        return gameObjects.stream()
//                .filter(gameObject -> (gameObject instanceof Coin || gameObject instanceof Enemy) &&
//                        gameObject.getXPosition() == this.getXPosition() &&
//                        gameObject.getYPosition() == this.getYPosition())
//                .collect(Collectors.toList());
//    }

    public boolean checkPlayerState(List<GameObject> collisionObject) {
        for (GameObject co: collisionObject) {
            if (co instanceof Coin && !((Coin) co).getState()) {
                ((Coin) co).setStateCollected();
                score++;
            }
            if (co instanceof Enemy) {
                this.setStateDead();
            }
        }
        return this.getState() == State.DEAD;
    }
}
