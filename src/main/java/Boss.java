import Engine.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class Boss extends MovableGameEntity {
    private String SPRITE_PATH = "Boss.png";
    private BufferedImage spriteSheet;
    // No animation because he's a divine entity
    private double health;
    private int playerX;
    private int playerY;
    private int attackCooldown;
    private int currentCooldown;
    private int currentDirectionCooldown;
    private int directionCooldown;


    public Boss(int x, int y) {
        teleport(x, y);
        this.x = x;
        this.y = y;
        setDimension(120, 200);
        setSpeed(6);
        health = 400.0;
        attackCooldown = 80;
        currentCooldown = 0;
        directionCooldown = 10;
        currentDirectionCooldown = 0;
        loadSpriteSheet();
        CollidableRepository.getInstance().registerEntity(this);
    }

    private void loadSpriteSheet() {
        try {
            spriteSheet = scaleImage(ImageIO.read(this.getClass().getResource(SPRITE_PATH)));
        }catch (IOException e) {

        }
    }

    private BufferedImage scaleImage(Image image) {
        int width = 120;
        int height = 200;

        BufferedImage resizedImg = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(image, 0, 0, width, height, null);
        g2.dispose();
        return resizedImg;
    }

    public void getPlayerCoordinates(int x, int y) {
        this.playerX = x;
        this.playerY = y;
    }

    public void moveAccordingToPlayer() {
        if (playerInRange()) {
            moveTowardPlayer();
        }
    }

    private void moveTowardPlayer() {
        if (x < playerX) {
            moveRight();
        }
        if (x > playerX) {
            moveLeft();
        }
        if (y < playerY) {
            moveDown();
        }
        if (y - 1 > playerY) {
            moveUp();
        }
    }

    private void setRandomDirection() {
        if (getDirection() == Direction.UP) {
            setDirection(Direction.RIGHT);
        } else if (getDirection() == Direction.RIGHT) {
            setDirection(Direction.DOWN);
        } else if (getDirection() == Direction.DOWN) {
            setDirection(Direction.LEFT);
        } else if (getDirection() == Direction.LEFT) {
            setDirection(Direction.UP);
        }
        currentDirectionCooldown = directionCooldown;
    }

    private void lowerDirectionCooldown() {
        currentDirectionCooldown--;
        if (currentDirectionCooldown <= 0) {
            currentDirectionCooldown = 0;
        }
    }

    private void lowerAttackCooldown() {
        currentCooldown--;
        if (currentCooldown < 0) {
            currentCooldown = 0;
        }
    }

    public void hit() {
        health -= 25.0;
        if (health < 0) {
            health = 0;
        }
    }

    public boolean isDead() {
        return health == 0;
    }

    public boolean isInAttackRange() {
        return playerInRange();
    }

    private boolean playerInRange() {
        int xDifference = playerX - x;
        int yDifference = playerY - y;
        xDifference *= xDifference < 0 ? -1 : 1;
        yDifference *= yDifference < 0 ? -1 : 1;

        return xDifference < 190 || yDifference < 190;
    }

    public boolean canAttack() {
        return currentCooldown == 0;
    }

    public ArrayList<Card> attack() {
        setCurrentCooldownToAttackCooldown();
        return createCards();
    }

    private ArrayList<Card> createCards() {
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(new Card(this, Direction.UP));
        cards.add(new Card(this, Direction.DOWN));
        cards.add(new Card(this, Direction.LEFT));
        cards.add(new Card(this, Direction.RIGHT));
        return cards;
    }

    private void setCurrentCooldownToAttackCooldown() {
        currentCooldown = attackCooldown;
    }

    @Override
    public void update() {
        lowerAttackCooldown();
        lowerDirectionCooldown();
    }

    @Override
    public void draw(Buffer buffer) {
        buffer.drawRectangle(x , y - 30, (int)((health / 400) * width), 10, Color.RED);

        buffer.drawImage(spriteSheet, x, y);
    }

}
