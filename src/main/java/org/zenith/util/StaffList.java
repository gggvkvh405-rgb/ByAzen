package org.zenith.util;

import com.google.common.reflect.TypeToken;
import java.util.HashSet;
import java.util.Set;

public class StaffList extends Item<String> {
   public StaffList() {
      super("staffName.json", "", new StaffNameSetTypeToken().getType(), HashSet::new);
   }

   public boolean CrosshairTargetUpdateEvent(String var1) {
      return this.getItems().contains(var1);
   }

   private static final class StaffNameSetTypeToken extends TypeToken<Set<String>> {
   }
}
