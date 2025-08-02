package olinolivia.parkour_helper;

import net.fabricmc.api.ModInitializer;

import olinolivia.parkour_helper.settings.ParkourCommand;
import olinolivia.parkour_helper.settings.ParkourSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParkourHelper implements ModInitializer {

	public static final String MOD_ID = "parkour_helper";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		ParkourSettings.init();
		ParkourCommand.init();

	}
}