package olinolivia.parkour_helper;

import net.fabricmc.api.ClientModInitializer;
import olinolivia.parkour_helper.settings.ClientParkourSettings;

public class ParkourHelperClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {

		ClientParkourSettings.init();

	}
}