package olinolivia.parkour_helper.settings;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class ParkourCommand {

    private static int getSubCommand(String name, Object value, ServerCommandSource source) {
        source.sendFeedback(() -> Text.literal(name + " is set to: " + value), false);
        return 1;
    }

    protected static int setSubCommand(String name, CommandContext<ServerCommandSource> context) {
        ParkourSettings settings = ParkourSettings.getCurrent(context.getSource().getServer());
        Object value = context.getArgument("value", Object.class);
        settings.set(name, value);
        SettingsUpdatedPayload.sendToAll(settings, context.getSource().getServer());

        context.getSource().sendFeedback(() -> Text.literal(name + " has been set to " + value), true);
        return 1;
    }
    static {

        CommandRegistrationCallback.EVENT.register(
                (
                        commandDispatcher,
                        commandRegistryAccess,
                        registrationEnvironment
                ) -> {

                    LiteralArgumentBuilder<ServerCommandSource> parkour = literal("parkour");

                    ParkourSettings.iterateFields((name, field) ->
                        parkour.then(
                                literal(name)
                                        .executes(context -> getSubCommand(name, ParkourSettings.getCurrent(context.getSource().getServer()).get(name), context.getSource()))
                                                .then(
                                                        argument("value", field.argumentTypeSupplier.get())
                                                                .executes(context -> setSubCommand(name, context))
                                                                .requires((source) -> source.hasPermissionLevel(2))
                                                )
                        )
                    );
                    commandDispatcher.register(parkour);
                });
    }

    public static void init() {}

}
