package arekkuusu.gsl.common;

import arekkuusu.gsl.common.proxy.IProxy;
import net.minecraft.entity.player.PlayerEntity;

public class ServerProxy implements IProxy {

    @Override
    public PlayerEntity getPlayer() {
        return null;
    }
}