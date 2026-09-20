package org.zenith.core;

import java.util.List;
import net.minecraft.util.math.Vec3d;
import org.zenith.module.combat.Aura;
public interface CloudResult {
   double zenithDLC_getPrevServerX();

   double zenithDLC_getPrevServerY();

   double zenithDLC_getPrevServerZ();

   List<Aura.Service> zenithDLC_getPositionHistory();

   void zenithDLC_recordServerPosition(Vec3d position);
}
