import Engine.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Scanner;

public class Player extends Camera {
    private String SPRITE_PATH = "CharacterSprites.png";
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
    private boolean doubleAttackOn;

    public Player(MovementController controller) {
        super(controller);
        setDimension(30, 60);
        setSpeed(16);
        health = 100.0;
        deck = new CardDeck(true);
        doubleAttackOn = false;
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

    public Card fire() {
        deck.setCurrentCooldownToAttackCooldown();
        deck.removeCard();
        return new Card(this, doubleAttackOn);
    }

    public boolean canFire() {
        return deck.canFire();
    }

    public void reload() {
        deck.shuffle();
    }

    public String deckInfo() {
        return "Cartes : " + deck.getCardAmount() + " / " + deck.getCardCapacity();
    }

    public double getHealth() {
        return health;
    }

    public void setDoubleAttackOn(boolean bool) {
        deck.activateDoubleAttack();
        doubleAttackOn = bool;
    }

    public boolean isDoubleAttackOn() {
        return doubleAttackOn;
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

    public void update() {
        super.moveAccordingToHandler();
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
        if (!deck.isNotReloading()) {
            buffer.changeFontSize(15);
            buffer.drawText("Shuffling...", x, y - 20, Color.WHITE);
            buffer.changeFontSize(10);
        }
    }

}
