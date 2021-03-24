import Engine.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Enemy extends MovableGameEntity {
    private String SPRITE_PATH = "EnemySprites.png";
    private static final int ANIMATION_SPEED = 4;
    private BufferedImage spriteSheet;
    private BufferedImage[] leftFrames;
    private BufferedImage[] rightFrames;
    private BufferedImage[] upFrames;
    private BufferedImage[] downFrames;
    private int animation = 0;
    private int nextFrame = ANIMATION_SPEED;

    private double health;
    private CardDeck deck;
    private int playerX;
    private int playerY;
    private int currentDirectionCooldown;
    private int directionCooldown;

    public Enemy(int x, int y) {
        teleport(x, y);
        this.x = x;
        this.y = y;
        setDimension(30, 60);
        setSpeed(8);
        health = 100.0;
        deck = new CardDeck(false);
        currentDirectionCooldown = 0;
        directionCooldown = 10;
        loadSpriteSheet();
        loadAnimationFrames();
        CollidableRepository.getInstance().registerEntity(this);
    }

    private void loadSpriteSheet() {
        try {
            spriteSheet = ImageIO.read(this.getClass().getResource(SPRITE_PATH));
        }catch (IOException e) {

        }
    }

    private void loadAnimationFrames() {
        upFrames = new BufferedImage[3];
        downFrames = new BufferedImage[3];
        leftFrames = new BufferedImage[3];
        rightFrames = new BufferedImage[3];

        upFrames[0] = scaleImage(spriteSheet.getSubimage(0, 0, 32, 54));
        upFrames[1] = scaleImage(spriteSheet.getSubimage(32, 0, 32, 54));
        upFrames[2] = scaleImage(spriteSheet.getSubimage(64, 0, 32, 54));

        downFrames[0] = scaleImage(spriteSheet.getSubimage(0, 55, 32, 54));
        downFrames[1] = scaleImage(spriteSheet.getSubimage(32, 55, 32, 54));
        downFrames[2] = scaleImage(spriteSheet.getSubimage(64, 55, 32, 54));

        leftFrames[0] = scaleImage(spriteSheet.getSubimage(96, 0, 32, 54));
        leftFrames[1] = scaleImage(spriteSheet.getSubimage(128, 0, 32, 54));
        leftFrames[2] = scaleImage(spriteSheet.getSubimage(160, 0, 32, 54));

        rightFrames[0] = scaleImage(spriteSheet.getSubimage(96, 55, 32, 54));
        rightFrames[1] = scaleImage(spriteSheet.getSubimage(128, 55, 32, 54));
        rightFrames[2] = scaleImage(spriteSheet.getSubimage(160, 55, 32, 54));
    }

    private BufferedImage scaleImage(Image image) {
        int width = 48;
        int height = 81;

        BufferedImage resizedImg = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(image, 0, 0, width, height, null);
        g2.dispose();
        return resizedImg;
    }

    public boolean isDead() {
        return health == 0;
    }

    public void hit() {
        health -= 25.0;
        if (health < 0) {
            health = 0;
        }
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

    public boolean isInAttackRange() {
        return playerInRange();
    }

    private boolean playerInRange() {
        int xDifference = playerX - x;
        int yDifference = playerY - y;
        xDifference *= xDifference < 0 ? -1 : 1;
        yDifference *= yDifference < 0 ? -1 : 1;

        return xDifference < 70 || yDifference < 70;
    }

    public boolean canAttack() {
        return deck.canFire();
    }

    public Card attack() {
        deck.removeCard();
        deck.setCurrentCooldownToAttackCooldown();
        return new Card(this, false);
    }

    public boolean shouldReload() {
        return deck.getCardAmount() == 0;
    }

    public void reload() {
        deck.shuffle();
    }

    @Override
    public void update() {
        lowerDirectionCooldown();
        if (super.hasMoved()) {
            nextFrame--;
            if (nextFrame == 0) {
                ++animation;
                if (animation > 1) {
                    animation = 0;
                }
                nextFrame = ANIMATION_SPEED;
            }
        } else {
            animation = 2;
        }
        deck.lowerCooldowns();
    }

    @Override
    public void draw(Buffer buffer) {
        if (getDirection() == Direction.UP) {
            buffer.drawImage(upFrames[animation], x, y);
        } else if (getDirection() == Direction.DOWN) {
            buffer.drawImage(downFrames[animation], x, y);
        } else if (getDirection() == Direction.LEFT) {
            buffer.drawImage(leftFrames[animation], x, y);
        } else if (getDirection() == Direction.RIGHT) {
            buffer.drawImage(rightFrames[animation], x, y);
        }
    }
}
