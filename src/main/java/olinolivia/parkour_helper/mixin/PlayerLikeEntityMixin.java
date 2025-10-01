package olinolivia.parkour_helper.mixin;


import net.minecraft.entity.*;
import olinolivia.parkour_helper.settings.ParkourSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin(PlayerLikeEntity.class)
public class PlayerLikeEntityMixin {

    @Redirect(method = "getBaseDimensions", at = @At(value = "INVOKE", target = "Ljava/util/Map;getOrDefault(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
    private Object modifySneakHeight(Map<?, ?> instance, Object key, Object defaultValue) {
        PlayerLikeEntity self = (PlayerLikeEntity) (Object) this;
        assert getPoseDimensions() != null;
        if (key == EntityPose.CROUCHING)
        {
            float height = (float) ParkourSettings.getDependent.apply(self.getEntityWorld().getServer(), "sneaking_player_height");
            return EntityDimensions.changing(
                            ((EntityDimensions) defaultValue).width(),
                            height
                    )
                    .withEyeHeight(height * 0.846666667F)
                    .withAttachments(EntityAttachments.builder().add(EntityAttachmentType.VEHICLE, PlayerLikeEntity.VEHICLE_ATTACHMENT));
        }

        else {return getPoseDimensions().getOrDefault((EntityPose) key, (EntityDimensions) defaultValue);}
    }

    @Accessor(value = "POSE_DIMENSIONS")
    private static Map<EntityPose, EntityDimensions> getPoseDimensions() {
        return null;
    }

}
