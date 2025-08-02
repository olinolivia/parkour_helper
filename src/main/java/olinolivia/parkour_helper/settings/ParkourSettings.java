package olinolivia.parkour_helper.settings;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class ParkourSettings extends PersistentState {

    private static final HashMap<String, ParkourSettingField<?>> fields = new HashMap<>();

    static class ParkourSettingField<T> {

        @FunctionalInterface interface SettingSetter {void apply(ParkourSettings settings, Object value);}

        String name;
        Codec<T> codec;
        Function<ParkourSettings, T> getter;
        SettingSetter setter;
        Supplier<ArgumentType<T>> argumentTypeSupplier;
        private ParkourSettingField(String name, Codec<T> codec, Function<ParkourSettings, T> getter, SettingSetter setter, Supplier<ArgumentType<T>> argumentTypeSupplier) {
            this.name = name;
            this.codec = codec;
            this.getter = getter;
            this.setter = setter;
            this.argumentTypeSupplier = argumentTypeSupplier;
            fields.put(this.name, this);
        }
    }

    public boolean sprintLeniency = true;
    public boolean allowSwimming = true;
    public float sneakingPlayerHeight = 1.5F;
    public double movementSnapThreshold = 0.003D;
    public boolean allowSprintSneak = true;

    private static final ParkourSettingField<Boolean> SPRINT_LENIENCY_FIELD = new ParkourSettingField<>("sprint_leniency", Codec.BOOL, (instance) -> instance.sprintLeniency, (settings, x) -> settings.sprintLeniency = (boolean) x, BoolArgumentType::bool);
    private static final ParkourSettingField<Boolean> ALLOW_SWIMMING_FIELD = new ParkourSettingField<>("allow_swimming", Codec.BOOL, (instance) -> instance.allowSwimming, (settings, x) -> settings.allowSwimming = (boolean) x, BoolArgumentType::bool);
    private static final ParkourSettingField<Float> SNEAKING_PLAYER_HEIGHT_FIELD = new ParkourSettingField<>("sneaking_player_height", Codec.FLOAT, (instance) -> instance.sneakingPlayerHeight, (settings, x) -> settings.sneakingPlayerHeight = (float) x, FloatArgumentType::floatArg);
    private static final ParkourSettingField<Double> MOVEMENT_SNAP_THRESHOLD_FIELD = new ParkourSettingField<>("movement_snap_threshold", Codec.DOUBLE, (instance) -> instance.movementSnapThreshold, (settings, x) -> settings.movementSnapThreshold = (double) x, DoubleArgumentType::doubleArg);
    private static final ParkourSettingField<Boolean> ALLOW_SPRINT_SNEAK_FIELD = new ParkourSettingField<>("allow_sprint_sneak", Codec.BOOL, (instance) -> instance.allowSprintSneak, (settings, x) -> settings.allowSprintSneak = (boolean) x, BoolArgumentType::bool);

    private static <T> App<RecordCodecBuilder.Mu<ParkourSettings>, T> getApp(ParkourSettingField<T> field) {
        return field.codec.fieldOf(field.name).forGetter(object -> field.getter.apply(object));
    }

    protected static final Codec<ParkourSettings> CODEC = RecordCodecBuilder.create(parkourSettingsInstance ->
            parkourSettingsInstance.group(
                    getApp(SPRINT_LENIENCY_FIELD),
                    getApp(ALLOW_SWIMMING_FIELD),
                    getApp(SNEAKING_PLAYER_HEIGHT_FIELD),
                    getApp(MOVEMENT_SNAP_THRESHOLD_FIELD),
                    getApp(ALLOW_SPRINT_SNEAK_FIELD)
            ).apply(parkourSettingsInstance, ParkourSettings::new)
    );

    private static final PersistentStateType<ParkourSettings> TYPE = new PersistentStateType<>(
            "parkour_settings",
            ParkourSettings::new,
            CODEC,
            null
    );

    public static BiFunction<MinecraftServer, String, Object> getDependent = (server, fieldName) -> getCurrent(server).get(fieldName);
    static void setGetDependent(BiFunction<MinecraftServer, String, Object> newGetDependent) {
        getDependent = newGetDependent;
    }

    public static ParkourSettings getCurrent(MinecraftServer server) {
        ServerWorld world = server.getWorld(World.OVERWORLD);
        assert world != null;
        ParkourSettings r = world.getPersistentStateManager().getOrCreate(TYPE);
        r.markDirty();
        return r;
    }

    static void iterateFields(BiConsumer<String, ParkourSettingField<?>> iteration) {
        fields.forEach(iteration);
    }

    static {
        ServerWorldEvents.LOAD.register((server, world) ->
                getCurrent(server)
        );
        ServerPlayerEvents.JOIN.register((player) ->
            SettingsUpdatedPayload.send(getCurrent(Objects.requireNonNull(player.getServer())), player)
        );
    }

    public Object get(String id) {return fields.get(id).getter.apply(this);}

    public void set(String id, Object value) {fields.get(id).setter.apply(this, value);}

    public ParkourSettings() {}

    public ParkourSettings(
            boolean sprintLeniency,
            boolean allowSwimming,
            float sneakingPlayerHeight,
            double movementSnapThreshold,
            boolean allowSprintSneak
            ) {
        this.sprintLeniency = sprintLeniency;
        this.allowSwimming = allowSwimming;
        this.sneakingPlayerHeight = sneakingPlayerHeight;
        this.movementSnapThreshold = movementSnapThreshold;
        this.allowSprintSneak = allowSprintSneak;
    }

    public static void init() {}

}