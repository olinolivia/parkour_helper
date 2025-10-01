package olinolivia.parkour_helper.mixin;

import net.minecraft.entity.LivingEntity;
import olinolivia.parkour_helper.settings.ParkourSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @ModifyConstant(method = "tickMovement", constant = @Constant(doubleValue = 0.003))
    private double modifyThreshold(double value) {
        LivingEntity self = (LivingEntity) (Object) this;
        return (double) ParkourSettings.getDependent.apply(self.getEntityWorld().getServer(), "movement_snap_threshold");
    }

    @Redirect(method = "applyFluidMovingSpeed", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isSprinting()Z"))
    private boolean modifyWaterFalling(LivingEntity instance) {
        LivingEntity self = (LivingEntity) (Object) this;
        return self.isSprinting() && (boolean) ParkourSettings.getDependent.apply(self.getEntityWorld().getServer(), "allow_swimming");
    }

}
