package olinolivia.parkour_helper.settings;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClientParkourSettings {

    public static boolean sprintLeniency;
    public static boolean allowSwimming;
    public static float sneakingPlayerHeight;
    public static double movementSnapThreshold;
    public static boolean allowSprintSneak;

    private static void sync(SettingsUpdatedPayload payload) {
        ParkourSettings settings = payload.settings();
        sprintLeniency = settings.sprintLeniency;
        allowSwimming = settings.allowSwimming;
        sneakingPlayerHeight = settings.sneakingPlayerHeight;
        movementSnapThreshold = settings.movementSnapThreshold;
        allowSprintSneak = settings.allowSprintSneak;
    }

    static {
        ClientPlayNetworking.registerGlobalReceiver(SettingsUpdatedPayload.ID, (payload, context) -> sync(payload));
        ParkourSettings.setGetDependent((server, fieldName) -> switch (fieldName) {
            case "sprint_leniency" -> sprintLeniency;
            case "allow_swimming" -> allowSwimming;
            case "sneaking_player_height" -> sneakingPlayerHeight;
            case "movement_snap_threshold" -> movementSnapThreshold;
            case "allow_sprint_sneak" -> allowSprintSneak;
            default -> null;
        });
    }

    public static void init() {}

}
