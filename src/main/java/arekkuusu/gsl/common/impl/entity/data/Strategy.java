package arekkuusu.gsl.common.impl.entity.data;

import arekkuusu.gsl.common.impl.entity.Strategic;
import net.minecraft.world.entity.EntityDimensions;

public abstract class Strategy {

    private final int id;

    public Strategy(int id) {
        this.id = id;
    }

    public void tick(Strategic strategic) {
    }

    public void particles(Strategic strategic) {
    }

    public abstract EntityDimensions entityDimensions(Strategic strategic);

    public int getId() {
        return id;
    }
}
