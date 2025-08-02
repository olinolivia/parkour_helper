package olinolivia.parkour_helper.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.EntityAttachmentType;
import net.minecraft.entity.EntityAttachments;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import olinolivia.parkour_helper.settings.ParkourSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @ModifyReturnValue(method = "isSwimming", at = @At("RETURN"))
    private boolean modifySwim(boolean r) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        boolean swim = (boolean) ParkourSettings.getDependent.apply(self.getServer(), "allow_swimming");
        return r && swim;
    }

    @Redirect(method = "getBaseDimensions", at = @At(value = "INVOKE", target = "Ljava/util/Map;getOrDefault(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
    private Object modifySneakHeight(Map<?, ?> instance, Object key, Object defaultValue) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        assert getPoseDimensions() != null;
        if (key == EntityPose.CROUCHING)
        {
            float height = (float) ParkourSettings.getDependent.apply(self.getServer(), "sneaking_player_height");
            return EntityDimensions.changing(
                    ((EntityDimensions) defaultValue).width(),
                    height
            )
                    .withEyeHeight(height * 0.846666667F)
                    .withAttachments(EntityAttachments.builder().add(EntityAttachmentType.VEHICLE, PlayerEntity.VEHICLE_ATTACHMENT_POS));
        }

        else {return getPoseDimensions().getOrDefault((EntityPose) key, (EntityDimensions) defaultValue);}
    }

    @Accessor(value = "POSE_DIMENSIONS")
    private static Map<EntityPose, EntityDimensions> getPoseDimensions() {
        return null;
    }

}