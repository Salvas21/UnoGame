import Engine.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class UnoGame extends Game {
    private final String COLLISIONS_PATH = "src/main/resources/CollisionData.json";

    private Player player;
    private Boss boss;
    private PlayerController controller;
    private ArrayList<Enemy> enemies;
    private ArrayList<CollisionBlock> blocks;
    private ArrayList<CollisionBlock> curtainBLocks;
    private ArrayList<CollisionBlock> objects;
    private ArrayList<Card> cards;

    private int drawDelay = 0;
    private int maxDrawDelay = 135;

    public UnoGame(int width, int height) {
        super(width, height);
    }

    protected void initialize() {
        setTitle("UNO : 2020 Apocalypse Edition");
        createPlayer();
        initArrayLists();
        generateEnemies();
        createBoss();
        getAllCollisionsData();
        SoundEffect music = SoundEffectFactory.music();
        music.setVolume(SoundEffect.Volume.LOW);
        music.play();
    }

    private void createPlayer() {
        controller = new PlayerController();
        player = new Player(controller);
        player.teleport(1920/2 - 200, 1080/2 - 100);
    }

    private void initArrayLists() {
        enemies = new ArrayList<>();
        cards = new ArrayList<>();
        blocks = new ArrayList<>();
        curtainBLocks = new ArrayList<>();
        objects = new ArrayList<>();
    }

    private void generateEnemies() {
        enemies.add(new Enemy(300, -3000)); // with boss
        enemies.add(new Enemy(400, -300)); // hall left
        enemies.add(new Enemy(3300, -400)); // hall right
        enemies.add(new Enemy(3600, -1100)); // class right
        enemies.add(new Enemy(1900, -1800)); // hall top
        enemies.add(new Enemy(2700, -1800)); // hall top
    }

    private void createBoss() {
        boss = new Boss(300, -3400);
    }

    private void getAllCollisionsData() {
        final int COLLISIONS_INDEX = 3;
        final int CURTAIN_INDEX = 4;
        final int OBJECTS_INDEX = 5;
        JSONObject jsonObject = parseJSONFile();
        createCollisionBlocks(getIterator(jsonObject, COLLISIONS_INDEX));
        createCurtainBlocks(getIterator(jsonObject, CURTAIN_INDEX));
        createHourglassesAndChestBlocks(getIterator(jsonObject, OBJECTS_INDEX));
    }

    private JSONObject parseJSONFile() {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) jsonParser.parse(new FileReader(COLLISIONS_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private Iterator<Long> getIterator(JSONObject jsonObject, int index) {
        JSONArray array;
        array = getJSONArray(jsonObject, index);
        return array.iterator();
    }

    private JSONArray getJSONArray(JSONObject jsonObject, int index) {
        JSONArray layersArray = (JSONArray) jsonObject.get("layers");
        JSONObject collisionsLayer = (JSONObject) layersArray.get(index);
        return (JSONArray) collisionsLayer.get("data");
    }

    private void createCollisionBlocks(Iterator<Long> iterator) {
        long nb = 0;
        for (int y = 0; y < 75 && iterator.hasNext(); y++) {
            for (int x = 0; x < 75 && iterator.hasNext(); x++) {
                nb = iterator.next();
                if (nb == 2333) {
                    blocks.add(new CollisionBlock(x * 64, y * 64, "Collision"));
                } else if (nb == 2315) {
                    blocks.add(new CollisionBlock(x * 64, y * 64, "Sewer"));
                }
            }
        }
    }

    private void createCurtainBlocks(Iterator<Long> iterator) {
        for (int y = 0; y < 75 && iterator.hasNext(); y++) {
            for (int x = 0; x < 75 && iterator.hasNext(); x++) {
                if (iterator.next() == 2292) {
                    curtainBLocks.add(new CollisionBlock(x * 64, y * 64, "Curtain"));
                }
            }
        }
    }

    private void createHourglassesAndChestBlocks(Iterator<Long> iterator) {
        //2407 == hourglass
        //2409 == chest
        long nb = 0;
        for (int y = 0; y < 75 && iterator.hasNext(); y++) {
            for (int x = 0; x < 75 && iterator.hasNext(); x++) {
                nb = iterator.next();
                if (nb == 2407) {
                    objects.add(new CollisionBlock(x * 64, y * 64, "Hourglass"));
                } else if (nb == 2409) {
                    objects.add(new CollisionBlock(x * 64, y * 64, "Chest"));
                }
            }
        }
    }

    protected void update() {
        if (!player.isDead()) {
            updateController();
            player.update();
            updateBoss();
            updateDoubleAttack();
            updateEnemies();
            updateCards();
            ArrayList<GameEntity> entitiesToKill = determinateEntitiesToKill();
            killEntities(entitiesToKill);
            updateDrawDelay();
        } else {
            updateController();
        }
    }

    protected void draw(Buffer buffer) {
        if (!player.isDead()) {
            Map.getInstance().draw(buffer);
            player.draw(buffer);
            if (boss != null) {
                boss.draw(buffer);
            }
            drawEnemies(buffer);
            //drawCollisions(buffer);
            drawCards(buffer);
            drawHUD(buffer);
            conditionalDraws(buffer);
        } else {
            drawIfDead(buffer);
        }
    }

    protected void conclude() {
        super.stop();
    }

    private void updateController() {
        if (controller.isQuitPressed()) {
            super.stop();
        }
        if (controller.isFirePressed() && player.canFire()) {
            cards.add(player.fire());
            SoundEffectFactory.throwCard().play();
            if (player.isDoubleAttackOn()) {
                SoundEffectFactory.throwCard().play();
                cards.add(player.fire());
            }
        }
        if (controller.isReloadPressed()) {
            player.reload();
            SoundEffectFactory.shuffle().play();
        }
        if (controller.isFullScreenPressed()) {
            RenderingEngine.getInstance().getScreen().toggleFullScreen();
        }
    }

    private void updateDoubleAttack() {
        if (doubleAttackConditionTrue()) {
            player.setDoubleAttackOn(true);
            drawDelay = maxDrawDelay;
        }
    }

    private boolean doubleAttackConditionTrue() {
        return objects.size() == 0 && !player.isDoubleAttackOn();
    }

    private void updateBoss() {
        if (boss != null) {
            boss.getPlayerCoordinates(player.getX(), player.getY());
            if (boss.isInAttackRange() && boss.canAttack()) {
                cards.addAll(boss.attack());
                for (int i = 0; i < 4; i++) {
                    SoundEffectFactory.throwCard().play();
                }
            }
            boss.moveAccordingToPlayer();
            boss.update();
        }
    }

    private void updateEnemies() {
        for (Enemy enemy : enemies) {
            enemy.getPlayerCoordinates(player.getX(), player.getY());
            if (enemy.isInAttackRange() && enemy.canAttack()) {
                cards.add(enemy.attack());
                SoundEffectFactory.throwCard().play();
            } else if (enemy.shouldReload()) {
                enemy.reload();
            }
            enemy.moveAccordingToPlayer();
            enemy.update();
        }
    }

    private void updateCards() {
        for (Card card : cards) {
            card.update();
        }
    }

    private ArrayList<GameEntity> determinateEntitiesToKill() {
        ArrayList<GameEntity> killedEntities = new ArrayList<>();
        for (Card card : cards) {
            card.update();
            if (card.collisionBoundIntersectWith(player)) {
                player.hit();
                if (player.isDead()) {
                    SoundEffectFactory.death().play();
                    //conclude();
                }
                killedEntities.add(card);
            }
            if (card.collisionBoundIntersectWith(boss)) {
                boss.hit();
                if (boss.isDead()) {
                    SoundEffectFactory.murloc().play();
                    CollidableRepository.getInstance().unregisterEntity(boss);
                    boss = null;
                }
                killedEntities.add(card);
            }
            for (CollisionBlock block : blocks) {
                if (card.collisionBoundIntersectWith(block)) {
                    killedEntities.add(card);
                }
            }
            for (Enemy enemy : enemies) {
                if (card.collisionBoundIntersectWith(enemy)) {
                    killedEntities.add(card);
                    enemy.hit();
                    if (enemy.isDead()) {
                        SoundEffectFactory.murloc().play();
                        killedEntities.add(enemy);
                    }
                }
            }
            for (Card card1 : cards) {
                if (card1.collisionBoundIntersectWith(card)) {
                    killedEntities.add(card);
                    killedEntities.add(card1);
                }
            }
            for (CollisionBlock object : objects) {
                if (card.collisionBoundIntersectWith(object)) {
                    killedEntities.add(card);
                }
            }
            for (CollisionBlock curtainBLock : curtainBLocks) {
                if (card.collisionBoundIntersectWith(curtainBLock)) {
                    killedEntities.add(card);
                }
            }
        }
        for (CollisionBlock object : objects) {
            if (player.collisionBoundIntersectWith(object)) {
                if (object.nameIs("Hourglass") && controller.isActionPressed()) {
                    killedEntities.add(object);
                } else if (object.nameIs("Chest") && controller.isActionPressed()) {
                    killedEntities.add(object);

                }
            }
        }
        if (curtainBLocks.size() > 0) {
            for (CollisionBlock curtainBLock : curtainBLocks) {
                if (objects.size() == 1) {
                    killedEntities.add(curtainBLock);
                    Map.getInstance().changeMap();
                    drawDelay = maxDrawDelay;
                }
            }
        }
        return killedEntities;
    }

    private void killEntities(ArrayList<GameEntity> entitiesToKill) {
        for (GameEntity entity : entitiesToKill) {
            if (entity instanceof CollisionBlock) {
                if (((CollisionBlock) entity).nameIs("Collision")) {
                    blocks.remove(entity);
                } else if (((CollisionBlock) entity).nameIs("Curtain")) {
                    curtainBLocks.remove(entity);
                } else {
                    objects.remove(entity);
                }
            } else if (entity instanceof Card) {
                cards.remove(entity);
            } else if (entity instanceof Enemy) {
                enemies.remove(entity);
            }
            CollidableRepository.getInstance().unregisterEntity(entity);
        }
    }

    private void updateDrawDelay() {
        drawDelay--;
        if (drawDelay < 0) {
            drawDelay = 0;
        }
    }

    private void drawEnemies(Buffer buffer) {
        for (Enemy enemy : enemies) {
            enemy.draw(buffer);
        }
    }

    private void drawCollisions(Buffer buffer) {
        for (CollisionBlock block : blocks) {
            block.draw(buffer);
        }
    }

    private void drawCards(Buffer buffer) {
        for (Card card : cards) {
            card.draw(buffer);
        }
    }

    private void drawHUD(Buffer buffer) {
        buffer.changeFontSize(25);
        buffer.drawRectangle(1350, 700, 240, 130, Color.BLACK);
        buffer.drawText("FPS : " + GameTime.getCurrentFps(), 10, 30, Color.WHITE);
        buffer.drawText("PV : " + player.getHealth(), 1400, 750, Color.WHITE);
        buffer.drawText(player.deckInfo(),1400, 800, Color.WHITE);
        buffer.changeFontSize(10);
    }

    private void conditionalDraws(Buffer buffer) {
        drawIfPlayerCollisionsWithObject(buffer);
        drawIfPlayerCollisionsWithToilet(buffer);
        drawIfPlayerCollisionsWithCurtain(buffer);
        drawIfCurtainIsUp(buffer);
        drawIfDoubleAttackOn(buffer);
    }

    private void drawIfPlayerCollisionsWithObject(Buffer buffer) {
        for (CollisionBlock object : objects) {
            if (player.collisionBoundIntersectWith(object)) {
                if (object.nameIs("Hourglass")) {
                    drawHourglassText(buffer);
                } else if (object.nameIs("Chest")) {
                    drawChestText(buffer);
                } else {
                    drawSewersEasterEggText(buffer);
                }
            }
        }
    }

    private void drawIfPlayerCollisionsWithToilet(Buffer buffer) {
        for (CollisionBlock block : blocks) {
            if (player.collisionBoundIntersectWith(block)) {
                if (block.nameIs("Sewer")) {
                    drawSewersEasterEggText(buffer);
                }
            }
        }
    }

    private void drawIfPlayerCollisionsWithCurtain(Buffer buffer) {
        for (CollisionBlock block : curtainBLocks) {
            if (player.collisionBoundIntersectWith(block)) {
                drawCurtainText(buffer);
            }
        }
    }

    private void drawHourglassText(Buffer buffer) {
        buffer.drawRectangle(player.getX() + player.getWidth(), player.getY() + player.getHeight(), 420, 50, Color.BLACK);
        buffer.changeFontSize(15);
        buffer.drawText("     Vous avez trouvé un sablier éclatant de son sable doré,", player.getX() + player.getWidth(), player.getY() + player.getHeight() + 20, Color.WHITE);
        buffer.drawText("                pesez sur 'e' pour le secouer.", player.getX() + player.getWidth(), player.getY() + player.getHeight() + 40, Color.WHITE);
        buffer.changeFontSize(10);
    }

    private void drawChestText(Buffer buffer) {
        buffer.drawRectangle(player.getX() + player.getWidth(), player.getY() + player.getHeight(), 420, 50, Color.BLACK);
        buffer.changeFontSize(15);
        buffer.drawText("     Vous avez trouvé un coffre contenant un immense secret de l'ancien monde à l'intérieur,", player.getX() + player.getWidth(), player.getY() + player.getHeight() + 20, Color.WHITE);
        buffer.drawText("                                   pesez sur 'e' pour l'ouvrir.", player.getX() + player.getWidth(), player.getY() + player.getHeight() + 40, Color.WHITE);
        buffer.changeFontSize(10);
    }

    private void drawSewersEasterEggText(Buffer buffer) {
        buffer.drawRectangle(player.getX() + player.getWidth(), player.getY() + player.getHeight(), 490, 100, Color.BLACK);
        buffer.changeFontSize(15);
        buffer.drawText("     As you get closer to the toilet, you hear sounds incoming from beneath...", player.getX() + player.getWidth(), player.getY() + player.getHeight() + 20, Color.WHITE);
        buffer.drawText("                         As if there was gunshots from the sewers...", player.getX() + player.getWidth(), player.getY() + player.getHeight() + 40, Color.WHITE);
        buffer.drawText("      Perhaps these sounds feels familiar to you, or they will soon enough...", player.getX() + player.getWidth(), player.getY() + player.getHeight() + 60, Color.WHITE);
        buffer.changeFontSize(10);
    }

    private void drawCurtainText(Buffer buffer) {
        buffer.drawRectangle(player.getX() + player.getWidth(), player.getY() + player.getHeight(), 470, 50, Color.BLACK);
        buffer.changeFontSize(15);
        buffer.drawText("     Le rideau est completement descendu et donc trop lourd pour lever,", player.getX() + player.getWidth(), player.getY() + player.getHeight() + 20, Color.WHITE);
        buffer.drawText("       il doit y avoir un moyen de faire marcher ses poulies comme avant.", player.getX() + player.getWidth(), player.getY() + player.getHeight() + 40, Color.WHITE);
        buffer.changeFontSize(10);
    }

    private void drawIfCurtainIsUp(Buffer buffer) {
        if (drawDelay > 0 && objects.size() == 1) {
            buffer.drawRectangle(player.getX() + player.getWidth(), player.getY() + player.getHeight(), 400, 50, Color.BLACK);
            buffer.changeFontSize(15);
            buffer.drawText("       Vous entendez des poulies qui s'actionnent,", player.getX() + player.getWidth(), player.getY() + player.getHeight() + 20, Color.WHITE);
            buffer.drawText("               le rideau doit s'etre levé ! ", player.getX() + player.getWidth(), player.getY() + player.getHeight() + 40, Color.WHITE);
            buffer.changeFontSize(10);
        }
    }

    private void drawIfDoubleAttackOn(Buffer buffer)    {
        if (drawDelay > 0 && objects.size() == 0) {
            buffer.drawRectangle(player.getX() + player.getWidth(), player.getY() + player.getHeight(), 460, 50, Color.BLACK);
            buffer.changeFontSize(15);
            buffer.drawText("     Vous avez appris la capacité d'attaquer deux fois en meme temps !", player.getX() + player.getWidth(), player.getY() + player.getHeight() + 20, Color.WHITE);
            buffer.changeFontSize(10);
        }
    }

    private void drawIfDead(Buffer buffer) {
        if (player.isDead()) {
            drawEndScreen(buffer);
        }
    }

    private void drawEndScreen(Buffer buffer) {
        buffer.drawRectangle(0, 0, RenderingEngine.getInstance().getViewportWidth(), RenderingEngine.getInstance().getViewportHeight(), Color.BLACK);
        buffer.changeFontSize(35);
        buffer.drawText("YOU DIED", 1920/2 - 200, 1080/2 - 100, Color.RED);
        buffer.changeFontSize(25);
        buffer.drawText("Press SHIFT + ESC to leave", 1920/2 - 265, 800 - 100, Color.WHITE);
        buffer.changeFontSize(10);
    }
}
