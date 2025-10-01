package olinolivia.parkour_helper.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.network.ClientPlayerEntity;
import olinolivia.parkour_helper.settings.ClientParkourSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {

	@ModifyReturnValue(method = "hasCollidedSoftly", at = @At("RETURN"))
	private boolean modifySoftCollision(boolean r) {
		return r && ClientParkourSettings.sprintLeniency;
	}

	@ModifyExpressionValue(method = "canSprint(Z)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isPartlyTouchingWater()Z"))
	private boolean modifySwim(boolean original) {
		return original && ClientParkourSettings.allowSwimming;
	}

	@ModifyReturnValue(method = "shouldStopSprinting", at = @At("RETURN"))
	private boolean modifySprintSneak(boolean r) {
		ClientPlayerEntity self = (ClientPlayerEntity) (Object) this;
		return r || (self.isSneaking() && !ClientParkourSettings.allowSprintSneak);
	}

}