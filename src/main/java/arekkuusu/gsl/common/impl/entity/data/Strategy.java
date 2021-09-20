package arekkuusu.gsl.common.impl.entity.data;

import arekkuusu.gsl.common.impl.entity.Strategic;

public abstract class Strategy<T extends Strategic> {

    private final int id;

    public Strategy(int id) {
        this.id = id;
    }

    public void tick(T strategic) {
    }

    public int getId() {
        return id;
    }
}
