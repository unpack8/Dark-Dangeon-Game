package future.code.dark.dungeon.domen;

import future.code.dark.dungeon.config.Configuration;

import javax.swing.*;

public class Coin extends GameObject {

    private boolean collected = false;

    public Coin(int xPosition, int yPosition) {
        super(xPosition, yPosition, Configuration.COIN_SPRITE);
    }

    public boolean getState(){
        return this.collected;
    }
    public void setStateCollected(){
        this.collected = true;
    }
}
