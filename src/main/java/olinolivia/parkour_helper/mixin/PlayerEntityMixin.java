package olinolivia.parkour_helper.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.player.PlayerEntity;
import olinolivia.parkour_helper.settings.ParkourSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @ModifyReturnValue(method = "isSwimming", at = @At("RETURN"))
    private boolean modifySwim(boolean r) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        boolean swim = (boolean) ParkourSettings.getDependent.apply(self.getEntityWorld().getServer(), "allow_swimming");
        return r && swim;
    }

}