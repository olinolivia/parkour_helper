package olinolivia.parkour_helper.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.network.ClientPlayerEntity;
import olinolivia.parkour_helper.settings.ClientParkourSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {

	@ModifyReturnValue(method = "hasCollidedSoftly", at = @At("RETURN"))
	private boolean modifySoftCollision(boolean r) {
		return r && ClientParkourSettings.sprintLeniency;
	}

	@Redirect(method = "shouldStopSprinting", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isTouchingWater()Z"))
	private boolean modifySwim1(ClientPlayerEntity instance) {
		return instance.isTouchingWater() && ClientParkourSettings.allowSwimming;
	}
	@Redirect(method = "canStartSprinting", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isTouchingWater()Z"))
	private boolean modifySwim2(ClientPlayerEntity instance) {
		return instance.isTouchingWater() && ClientParkourSettings.allowSwimming;
	}

	@ModifyReturnValue(method = "shouldStopSprinting", at = @At("RETURN"))
	private boolean modifySprintSneak(boolean r) {
		ClientPlayerEntity self = (ClientPlayerEntity) (Object) this;
		return r || (self.isSneaking() && !ClientParkourSettings.allowSprintSneak);
	}

}