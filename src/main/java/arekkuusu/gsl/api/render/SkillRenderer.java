package arekkuusu.gsl.api.render;

import arekkuusu.gsl.api.registry.data.SerDes;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

@OnlyIn(Dist.CLIENT)
public abstract class SkillRenderer<E extends SerDes> {

    public final EntityRendererManager renderManager = Minecraft.getInstance().getRenderManager();
    public final Random rand = new Random();
    public SkillRendererDispatcher dispatcher;

    public void render(LivingEntity entity, E context, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        //For Rent
    }

    public void bindTexture(ResourceLocation location) {
        Minecraft.getInstance().getRenderManager().textureManager.bindTexture(location);
    }

    public void setDispatcher(SkillRendererDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }
}
