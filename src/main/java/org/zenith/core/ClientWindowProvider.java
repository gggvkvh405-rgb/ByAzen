package org.zenith.core;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;

public interface ClientWindowProvider extends ClientProvider {
   MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   Window val214 = minecraftClient3.getWindow();
}
