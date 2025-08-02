package olinolivia.parkour_helper.settings;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import olinolivia.parkour_helper.ParkourHelper;

public record SettingsUpdatedPayload(ParkourSettings settings) implements CustomPayload {

    private static final Identifier SETTINGS_UPDATED_ID = Identifier.of(ParkourHelper.MOD_ID, "settings_updated_payload");
    public static final Id<SettingsUpdatedPayload> ID = new Id<>(SETTINGS_UPDATED_ID);
    private static final PacketCodec<? super RegistryByteBuf, SettingsUpdatedPayload> PACKET_CODEC = PacketCodec.of(
            SettingsUpdatedPayload::write,
            buf ->
            new SettingsUpdatedPayload(ParkourSettings.CODEC.decode(NbtOps.INSTANCE, buf.readNbt()).getOrThrow().getFirst()));
    private static final CustomPayload.Type<? super RegistryByteBuf, SettingsUpdatedPayload> TYPE = PayloadTypeRegistry.playS2C().register(ID, PACKET_CODEC);

    private void write(PacketByteBuf buf) {
        buf.writeNbt(ParkourSettings.CODEC.encodeStart(NbtOps.INSTANCE, settings).getOrThrow());
    }

    public static void send(ParkourSettings settings, ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, new SettingsUpdatedPayload(settings));
    }

    public static void sendToAll(ParkourSettings settings, MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            ServerPlayNetworking.send(player, new SettingsUpdatedPayload(settings));
        }
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}