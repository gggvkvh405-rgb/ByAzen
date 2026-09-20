package org.zenith.base.bot.client;

import java.lang.reflect.Method;

record BotEventBus_MethodData(Object source, Method target, byte priority) {
}
