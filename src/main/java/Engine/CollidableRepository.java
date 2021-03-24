package Engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class CollidableRepository implements Iterable<GameEntity> {

    private static CollidableRepository instance;
    private final List<GameEntity> registeredCollidableEntities;
    
    public static CollidableRepository getInstance() {
        if (instance == null) {
            instance = new CollidableRepository();
        }
        return instance;
    }
    
    public void registerEntity(GameEntity entity) {
        registeredCollidableEntities.add(entity);
    }
    
    public void registerEntities(Collection<GameEntity> entities) {
        registeredCollidableEntities.addAll(entities);
    }
    
    public void unregisterEntity(GameEntity entity) {
        registeredCollidableEntities.remove(entity);
    }
    
    public int count() {
        return registeredCollidableEntities.size();
    }
    
    
    @Override
    public Iterator<GameEntity> iterator() {
        return registeredCollidableEntities.iterator();
    }
    
    private CollidableRepository() {
        registeredCollidableEntities = new ArrayList<>();
    }
}
