package cinnamon.gsl.api.render;

import cinnamon.gsl.api.registry.Effect;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

@OnlyIn(Dist.CLIENT)
public abstract class EffectRenderer<E extends Effect> {

    public final EntityRenderDispatcher renderManager = Minecraft.getInstance().getEntityRenderDispatcher();
    public final Random rand = new Random();
    public EffectRendererDispatcher dispatcher;

    public void render(E effect, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        //For Rent
    }

    public void bindTexture(ResourceLocation location) {
        Minecraft.getInstance().textureManager.bind(location);
    }

    public void setDispatcher(EffectRendererDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }
}
