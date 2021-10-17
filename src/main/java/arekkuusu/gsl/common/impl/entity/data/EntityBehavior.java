package arekkuusu.gsl.common.impl.entity.data;

import net.minecraft.world.entity.Entity;

public abstract class EntityBehavior<T extends Entity> {

    private final int id;

    public EntityBehavior(int id) {
        this.id = id;
    }

    public void tick(T strategic) {
    }

    public int getId() {
        return id;
    }
}
