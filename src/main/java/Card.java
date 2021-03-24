import Engine.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Card extends MovableGameEntity {
    private Direction playerDirection;
    private String SPRITE_PATH = "AttackSprites.png";
    private BufferedImage spriteSheet;
    private BufferedImage image;
    private double angle;


    public Card(MovableGameEntity entity, boolean doubleAttackOn) {
        playerDirection = entity.getDirection();
        setSpeed(16);
        angle = 90.0;
        loadSpriteSheet();
        setDimension(12, 18);
        setImageAndTeleportByDirection(entity, doubleAttackOn);
        CollidableRepository.getInstance().registerEntity(this);
    }

    public Card(MovableGameEntity entity, Direction direction) {
        playerDirection = direction;
        setSpeed(12);
        angle = 90.0;
        loadSpriteSheet();
        setDimension(12, 18);
        setImageAndTeleportByDirection(entity, false);
        CollidableRepository.getInstance().registerEntity(this);
    }

    private void setImageAndTeleportByDirection(MovableGameEntity entity, boolean doubleAttackOn) {
        if (playerDirection == Direction.RIGHT) {
            int y = (doubleAttackOn) ? entity.getY() + 16 : entity.getY() + 15 - 2;
            super.teleport(entity.getX() + entity.getWidth() + 1, y);
            image = scaleImage(spriteSheet.getSubimage(0, 0, 12, 18));
        } else if (playerDirection == Direction.LEFT) {
            int y = (doubleAttackOn) ? entity.getY() + 30 : entity.getY() + 10;
            super.teleport(entity.getX() - 9, y);
            image = scaleImage(spriteSheet.getSubimage(12, 0, 12, 18));
        } else if (playerDirection == Direction.DOWN) {
            int x = (doubleAttackOn) ? entity.getX() + 16 : entity.getX() + 15 - 2;
            super.teleport(x, entity.getY() + entity.getHeight() + 1);
            image = scaleImage(spriteSheet.getSubimage(24, 0, 12, 18));
        } else if (playerDirection == Direction.UP) {
            int x = (doubleAttackOn) ? entity.getX() + 16 : entity.getX() + 15 - 2;
            super.teleport(x, entity.getY() - 9);
            image = scaleImage(spriteSheet.getSubimage(36, 0, 12, 18));
        }
    }

    private void loadSpriteSheet() {
        try {
            spriteSheet = ImageIO.read(this.getClass().getResource(SPRITE_PATH));
        }catch (IOException e) {

        }
    }

    private BufferedImage scaleImage(Image image) {
        int width = 18; //12
        int height = 27; //18

        BufferedImage resizedImg = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(image, 0, 0, width, height, null);
        g2.dispose();
        return resizedImg;
    }

    @Override
    public void update() {
        move(playerDirection);
    }

    public void draw(Buffer buffer) {
        buffer.drawImage(rotate(image), x, y);
    }

    private BufferedImage rotate(BufferedImage image) {
        int w = image.getWidth();
        int h = image.getHeight();

        BufferedImage rotated = new BufferedImage(w, h, image.getType());
        Graphics2D graphic = rotated.createGraphics();
        graphic.rotate(Math.toRadians(angle), w/2, h/2);
        graphic.drawImage(image, null, 0, 0);
        graphic.dispose();
        angle += 6;
        return rotated;
    }


}
