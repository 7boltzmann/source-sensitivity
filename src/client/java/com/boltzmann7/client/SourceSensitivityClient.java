package com.boltzmann7.client;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.Mth;

public class SourceSensitivityClient implements ClientModInitializer {

	double convertSourceSensitivityToMinecraft(double sensitivity) {
        return Mth.clamp((1.0 / 0.6) * ((Math.pow((0.022 * sensitivity) / (0.15 * 8.0), 1.0 / 3.0)) - 0.2), 0, 1);
	}

	@Override
	public void onInitializeClient() {
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, buildContext) -> {

			dispatcher.register(
					ClientCommands.literal("sensitivity")
							.then(
									ClientCommands.argument(
													"sourceSensitivity",
													DoubleArgumentType.doubleArg(0.43636, 27.92727) // comes from intersection of y = 0 and y = 1
											)
											.executes(context -> {
												LocalPlayer player = context.getSource().getPlayer();

												double sourceSensitivity = DoubleArgumentType.getDouble(context, "sourceSensitivity");
												double minecraftSensitivity = convertSourceSensitivityToMinecraft(sourceSensitivity);

												Minecraft.getInstance().options.sensitivity().set(minecraftSensitivity);
												player.sendOverlayMessage(
														Component.literal(
																String.format("Sensitivity Set to %f (%.3f%%)", sourceSensitivity, minecraftSensitivity * 200)
														)
												);
												return 1;
											})
							)
			);

		});
	}
}