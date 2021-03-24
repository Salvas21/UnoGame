public class CardDeck {
    private final double attackCooldown;
    private double currentAttackCooldown;
    private final double reloadTime;
    private double currentReloadTime;
    private int cardCapacity;
    private int cardAmount;

    public CardDeck(boolean player) {
        if (player) {
            attackCooldown = 10.0;
            reloadTime = 45.0;
        } else {
            attackCooldown = 15.0;
            reloadTime = 75.0;
        }
        currentAttackCooldown = 0.0;
        currentReloadTime = 0.0;
        cardCapacity = 7;
        cardAmount = 7;
    }

    public void lowerCooldowns() {
        lowerAttackCooldown();
        lowerReloadCooldown();
    }

    private void lowerAttackCooldown() {
        currentAttackCooldown--;
        if (currentAttackCooldown <= 0) {
            currentAttackCooldown = 0;
        }
    }

    private void lowerReloadCooldown() {
        currentReloadTime--;
        if (currentReloadTime <= 0) {
            currentReloadTime = 0;
        }
    }

    public boolean canFire() {
        return currentAttackCooldown == 0.0 && cardAmount > 0 && isNotReloading();
    }

    public void removeCard() {
        cardAmount--;
    }

    public boolean isNotReloading() {
        return currentReloadTime <= 0;
    }

    public void shuffle() {
        cardAmount = cardCapacity;
        currentReloadTime = reloadTime;
    }

    public void setCurrentCooldownToAttackCooldown() {
        currentAttackCooldown = attackCooldown;
    }

    public int getCardCapacity() {
        return cardCapacity;
    }

    public int getCardAmount() {
        return cardAmount;
    }

    public void activateDoubleAttack() {
        cardCapacity *= 2;
        cardAmount = cardCapacity;
    }
}
