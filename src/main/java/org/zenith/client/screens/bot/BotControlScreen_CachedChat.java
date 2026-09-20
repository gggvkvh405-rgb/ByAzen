package org.zenith.client.screens.bot;

import java.util.List;
import net.minecraft.text.OrderedText;

record BotControlScreen_CachedChat(long seenAt, List<OrderedText> lines) {
}
