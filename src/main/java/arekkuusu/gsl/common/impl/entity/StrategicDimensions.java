package arekkuusu.gsl.common.impl.entity;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.phys.AABB;

public class StrategicDimensions extends EntityDimensions {

    private final Type box;

    public StrategicDimensions(Type box, float p_20381_, float p_20382_, boolean p_20383_) {
        super(p_20381_, p_20382_, p_20383_);
        this.box = box;
    }

    @Override
    public AABB makeBoundingBox(double x, double y, double z) {
        float f = this.width / 2.0F;
        float f1 = this.height / 2.0F;
        return this.box.aabb(f, f1, x, y, z);
    }

    @Override
    public EntityDimensions scale(float pWidthFactor, float pHeightFactor) {
        return !this.fixed && (pWidthFactor != 1.0F || pHeightFactor != 1.0F) ? scalable(this.box, this.width * pWidthFactor, this.height * pHeightFactor) : this;
    }

    public static EntityDimensions scalable(Type box, float pWidth, float pHeight) {
        return new StrategicDimensions(box, pWidth, pHeight, false);
    }

    public static EntityDimensions fixed(Type box, float pWidth, float pHeight) {
        return new StrategicDimensions(box, pWidth, pHeight, true);
    }

    public enum Type implements StringRepresentable {
        CENTER {
            @Override
            AABB aabb(double w, double h, double x, double y, double z) {
                return new AABB(x - w, y - h, z - w, x + w, y + h, z + w);
            }
        },
        BOTTOM {
            @Override
            AABB aabb(double w, double h, double x, double y, double z) {
                return new AABB(x - w, y, z - w, x + w, y + h * 2, z + w);
            }
        };

        abstract AABB aabb(double w, double h, double x, double y, double z);

        @Override
        public String getSerializedName() {
            return name();
        }
    }
}
