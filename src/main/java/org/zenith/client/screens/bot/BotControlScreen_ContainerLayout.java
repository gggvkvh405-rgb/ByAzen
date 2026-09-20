package org.zenith.client.screens.bot;

import net.minecraft.util.Identifier;

record BotControlScreen_ContainerLayout(Identifier texture, int width, int height, int chestRows, int titleX, boolean centerTitle, boolean playerInventoryLabel) {
}
