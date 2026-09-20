package org.zenith.core;

import org.zenith.rotation.RotationEasingBase;

public class MenuEaseA extends RotationEasingBase {
   @Override
   public BotActivity call110() {
      return BotActivity.call412;
   }

   MenuEaseA() {
   }

   public static MenuEaseAPayload screen() {
      return new MenuEaseAPayload();
   }
}
