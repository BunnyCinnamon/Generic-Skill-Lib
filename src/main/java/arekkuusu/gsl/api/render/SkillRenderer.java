package arekkuusu.gsl.api.render;

import arekkuusu.gsl.api.registry.data.SerDes;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

@OnlyIn(Dist.CLIENT)
public abstract class SkillRenderer<E extends SerDes> {

    public final EntityRenderDispatcher renderManager = Minecraft.getInstance().getEntityRenderDispatcher();
    public final Random rand = new Random();
    public SkillRendererDispatcher dispatcher;

    public void render(LivingEntity entity, E context, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        //For Rent
    }

    public void bindTexture(ResourceLocation location) {
        Minecraft.getInstance().textureManager.bindForSetup(location);
    }

    public void setDispatcher(SkillRendererDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }
}
