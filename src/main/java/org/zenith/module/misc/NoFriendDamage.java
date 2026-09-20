package org.zenith.module.misc;

import org.zenith.module.Category;
import org.zenith.module.Module;
import org.zenith.module.ModuleInfo;
import org.zenith.module.ModuleManager;
import org.zenith.module.combat.*;
import org.zenith.module.movement.*;
import org.zenith.module.player.*;
import org.zenith.module.render.*;
import org.zenith.module.misc.*;

@ModuleInfo(name = "NoFriendDamage", category = Category.MISC, description = "Дает рейкасту проходить сквозь друзей")
public final class NoFriendDamage extends Module {
   public static final NoFriendDamage noFriendDamage = new NoFriendDamage();
}
