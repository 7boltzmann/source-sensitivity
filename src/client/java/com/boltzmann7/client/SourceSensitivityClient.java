package com.boltzmann7.client;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class SourceSensitivityClient implements ClientModInitializer {

	double convertSourceSensitivityToMinecraft(double sensitivity) {
		double x = (1.0 / 0.6) * ((Math.pow((0.022 * sensitivity) / (0.15 * 8.0), 1.0 / 3.0)) - 0.2);
		x = Mth.clamp(x, 0, 1);
		return x;
	}

	@Override
	public void onInitializeClient() {
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, buildContext) -> {

			dispatcher.register(
					ClientCommands.literal("sensitivity")
							.then(
									ClientCommands.argument(
													"sourceSensitivity",
													DoubleArgumentType.doubleArg(0.0, 30)
											)
											.executes(context -> {
												double sourceSensitivity = DoubleArgumentType.getDouble(context, "sourceSensitivity");
												double minecraftSensitivity = convertSourceSensitivityToMinecraft(sourceSensitivity);
												Minecraft.getInstance().options.sensitivity().set(minecraftSensitivity);
												context.getSource()
														.getPlayer()
														.sendOverlayMessage(
																Component.literal(
																		"Sensitivity Set to " + sourceSensitivity + " (" + Math.round(minecraftSensitivity * 200) + "%)"
																)
														);
												return 1;
											})
							)
			);

		});
	}
}