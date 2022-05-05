package cinnamon.gsl.client;

import cinnamon.gsl.common.proxy.IProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientProxy implements IProxy {

    @Override
    public Player getPlayer() {
        return Minecraft.getInstance().player;
    }
}