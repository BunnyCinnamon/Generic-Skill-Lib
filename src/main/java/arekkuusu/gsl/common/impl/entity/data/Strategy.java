package arekkuusu.gsl.common.impl.entity.data;

import arekkuusu.gsl.common.impl.entity.Strategic;
import net.minecraft.world.entity.EntityDimensions;

public abstract class Strategy<T extends Strategic> {

    private final int id;

    public Strategy(int id) {
        this.id = id;
    }

    public void tick(T strategic) {
    }

    public void particles(T strategic) {
    }

    public abstract EntityDimensions entityDimensions(T strategic);

    public int getId() {
        return id;
    }
}
