package Engine;

import java.awt.Rectangle;

public class Collision {
    
    private final MovableGameEntity entity;
    
    public Collision(MovableGameEntity entity) {
        this.entity = entity;
    }
    
    public int getAllowedSpeed(Direction direction) {
        switch (direction) {
            case UP: return getAllowedUpSpeed();
            case DOWN: return getAllowedDownSpeed();
            case LEFT: return getAllowedLeftSpeed();
            case RIGHT: return getAllowedRightSpeed();
        }
        return 0;
    }
    
    private int getAllowedUpSpeed() {
        return distance((GameEntity other) -> entity.y - (other.y + other.height));
    }
    
    private int getAllowedDownSpeed() {
        return distance((GameEntity other) -> other.y - (entity.y + entity.height));
    }
    
    private int getAllowedLeftSpeed() {
        return distance((GameEntity other) -> entity.x - (other.x + other.width));
    }
    
    private int getAllowedRightSpeed() {
        return distance((GameEntity other) -> other.x - (entity.x + entity.width));
    }
    
    private int distance(AllowedDistanceCalculator calculator) {
        Rectangle collisionBound = entity.getCollisionBound();
        int allowedDistance = entity.getSpeed();
        for (GameEntity other: CollidableRepository.getInstance()) {
            if (collisionBound.intersects(other.getBounds())) {
                allowedDistance = Math.min(allowedDistance, 
                        calculator.calculateWith(other));            
            }
        }
        return allowedDistance;
    }
    
    private interface AllowedDistanceCalculator {
        public int calculateWith(GameEntity other);
    }
}
