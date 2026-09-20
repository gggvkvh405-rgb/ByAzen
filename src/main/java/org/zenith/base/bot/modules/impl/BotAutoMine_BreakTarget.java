package org.zenith.base.bot.modules.impl;

import net.minecraft.util.hit.BlockHitResult;
import org.zenith.rotation.Rotation;

record BotAutoMine_BreakTarget(Rotation rotation, BlockHitResult hitResult, int drillCoverage, double distanceSquared) {
}
