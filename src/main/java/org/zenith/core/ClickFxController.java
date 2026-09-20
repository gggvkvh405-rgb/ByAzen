package org.zenith.core;

import java.util.ArrayList;
import java.util.List;

public class ClickFxController {
   public List<ClickFxState> list70 = new ArrayList<>();
   public List<ClickFxPair> list89 = new ArrayList<>();
   public ClickFxPoint var5Var1653 = new ClickFxPoint(0.0F, -1.0F, 0.0F);
   public float float268 = 25.0F;
   public int int380 = 30;
   public final float float269 = 20.0F;
   public boolean boolean147 = false;

   public boolean AttackEntityEvent(int var1) {
      if (this.list70.size() != var1) {
         this.list70.clear();
         this.list89.clear();

         for (int i = 0; i < var1; i++) {
            ClickFxState l111lliil1ilill1l1i11li_ii1il11l111ii11iil = new ClickFxState();
            l111lliil1ilill1l1i11li_ii1il11l111ii11iil.var5Var1652.y = -i;
            l111lliil1ilill1l1i11li_ii1il11l111ii11iil.var5Var1652.x = -i;
            l111lliil1ilill1l1i11li_ii1il11l111ii11iil.boolean109 = i == 0;
            this.list70.add(l111lliil1ilill1l1i11li_ii1il11l111ii11iil);
            if (i > 0) {
               this.list89.add(new ClickFxPair(this.list70.get(i - 1), l111lliil1ilill1l1i11li_ii1il11l111ii11iil, 1.0F));
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public void call229() {
      this.call230();
      this.call234();
      this.call183();
      this.call231();
      this.call183();
      this.call233();
      this.call232();
   }

   public void call230() {
      float f = 0.05F;
      ClickFxPoint l111lliil1ilill1l1i11li_illi1l1l1x = this.var5Var1653.string70().BlockInteractEvent(this.float268 * f);
      l111lliil1ilill1l1i11li_illi1l1l1x = new ClickFxPoint(0.0F, 0.0F, 0.0F);

      for (ClickFxState l111lliil1ilill1l1i11li_ii1il11l111ii11iil : this.list70) {
         if (!l111lliil1ilill1l1i11li_ii1il11l111ii11iil.boolean109) {
            l111lliil1ilill1l1i11li_illi1l1l1x.ItemRegistry(l111lliil1ilill1l1i11li_ii1il11l111ii11iil.var5Var1652);
            l111lliil1ilill1l1i11li_ii1il11l111ii11iil.var5Var1652.Easing(l111lliil1ilill1l1i11li_illi1l1l1x);
            l111lliil1ilill1l1i11li_ii1il11l111ii11iil.var5Var165.ItemRegistry(l111lliil1ilill1l1i11li_illi1l1l1x);
         }
      }
   }

   public void call231() {
      for (int i = 0; i < this.int380; i++) {
         for (int j = this.list89.size() - 1; j >= 0; j--) {
            ClickFxPair l111lliil1ilill1l1i11li_l1i1illlili = this.list89.get(j);
            ClickFxPoint l111lliil1ilill1l1i11li_illi1l1l1x = l111lliil1ilill1l1i11li_l1i1illlili.var5Var159
               .var5Var1652
               .string70()
               .Easing(l111lliil1ilill1l1i11li_l1i1illlili.var5Var1592.var5Var1652)
               .EventClickSlotHook(2.0F);
            l111lliil1ilill1l1i11li_illi1l1l1x = l111lliil1ilill1l1i11li_l1i1illlili.var5Var159
               .var5Var1652
               .string70()
               .ColorAnimator(l111lliil1ilill1l1i11li_l1i1illlili.var5Var1592.var5Var1652)
               .float128();
            if (!l111lliil1ilill1l1i11li_l1i1illlili.var5Var159.boolean109) {
               l111lliil1ilill1l1i11li_l1i1illlili.var5Var159.var5Var1652 = l111lliil1ilill1l1i11li_illi1l1l1x.string70()
                  .Easing(l111lliil1ilill1l1i11li_illi1l1l1x.string70().BlockInteractEvent(l111lliil1ilill1l1i11li_l1i1illlili.float86 / 2.0F));
            }

            if (!l111lliil1ilill1l1i11li_l1i1illlili.var5Var1592.boolean109) {
               l111lliil1ilill1l1i11li_l1i1illlili.var5Var1592.var5Var1652 = l111lliil1ilill1l1i11li_illi1l1l1x.string70()
                  .ColorAnimator(l111lliil1ilill1l1i11li_illi1l1l1x.string70().BlockInteractEvent(l111lliil1ilill1l1i11li_l1i1illlili.float86 / 2.0F));
            }
         }
      }
   }

   public void call232() {
      for (int i = 0; i < this.list89.size(); i++) {
         ClickFxPair l111lliil1ilill1l1i11li_l1i1illlili = this.list89.get(i);
         ClickFxPoint l111lliil1ilill1l1i11li_illi1l1l1 = l111lliil1ilill1l1i11li_l1i1illlili.var5Var159
            .var5Var1652
            .string70()
            .ColorAnimator(l111lliil1ilill1l1i11li_l1i1illlili.var5Var1592.var5Var1652)
            .float128();
         if (!l111lliil1ilill1l1i11li_l1i1illlili.var5Var1592.boolean109) {
            l111lliil1ilill1l1i11li_l1i1illlili.var5Var1592.var5Var1652 = l111lliil1ilill1l1i11li_l1i1illlili.var5Var159
               .var5Var1652
               .string70()
               .ColorAnimator(l111lliil1ilill1l1i11li_illi1l1l1.BlockInteractEvent(l111lliil1ilill1l1i11li_l1i1illlili.float86));
         }
      }
   }

   public void call183() {
      int i = 0;

      boolean flag;
      do {
         flag = false;

         for (int j = 0; j < this.list70.size(); j++) {
            for (int k = j + 1; k < this.list70.size(); k++) {
               ClickFxState l111lliil1ilill1l1i11li_ii1il11l111ii11iilx = this.list70.get(j);
               l111lliil1ilill1l1i11li_ii1il11l111ii11iilx = this.list70.get(k);
               ClickFxPoint l111lliil1ilill1l1i11li_illi1l1l1x = l111lliil1ilill1l1i11li_ii1il11l111ii11iilx.var5Var1652
                  .string70()
                  .ColorAnimator(l111lliil1ilill1l1i11li_ii1il11l111ii11iilx.var5Var1652);
               if (l111lliil1ilill1l1i11li_illi1l1l1x.float126() < 0.99) {
                  flag = true;
                  i++;
                  l111lliil1ilill1l1i11li_illi1l1l1x.float128();
                  l111lliil1ilill1l1i11li_illi1l1l1x = l111lliil1ilill1l1i11li_ii1il11l111ii11iilx.var5Var1652
                     .string70()
                     .Easing(l111lliil1ilill1l1i11li_ii1il11l111ii11iilx.var5Var1652)
                     .EventClickSlotHook(2.0F);
                  if (!l111lliil1ilill1l1i11li_ii1il11l111ii11iilx.boolean109) {
                     l111lliil1ilill1l1i11li_ii1il11l111ii11iilx.var5Var1652 = l111lliil1ilill1l1i11li_illi1l1l1x.string70()
                        .Easing(l111lliil1ilill1l1i11li_illi1l1l1x.string70().BlockInteractEvent(0.5F));
                  }

                  if (!l111lliil1ilill1l1i11li_ii1il11l111ii11iilx.boolean109) {
                     l111lliil1ilill1l1i11li_ii1il11l111ii11iilx.var5Var1652 = l111lliil1ilill1l1i11li_illi1l1l1x.string70()
                        .ColorAnimator(l111lliil1ilill1l1i11li_illi1l1l1x.string70().BlockInteractEvent(0.5F));
                  }
               }
            }
         }
      } while (flag && i < 32);
   }

   public void call233() {
      for (int i = 1; i < this.list70.size() - 2; i++) {
         double d0 = this.on23(this.list70.get(i).var5Var1652, this.list70.get(i - 1).var5Var1652, this.list70.get(i + 1).var5Var1652);
         if (d0 < -20.0F) {
            float f = -20.0F;
            ClickFxPoint l111lliil1ilill1l1i11li_illi1l1l1x = this.on23(this.list70.get(i).var5Var1652, this.list70.get(i - 1).var5Var1652, f * 2.0F);
            this.list70.get(i + 1).var5Var1652 = l111lliil1ilill1l1i11li_illi1l1l1x;
         }

         if (d0 > 20.0) {
            ClickFxPoint l111lliil1ilill1l1i11li_illi1l1l1 = this.on23(this.list70.get(i).var5Var1652, this.list70.get(i - 1).var5Var1652, 20.0F * 2.0F);
            this.list70.get(i + 1).var5Var1652 = l111lliil1ilill1l1i11li_illi1l1l1;
         }
      }
   }

   public void call234() {
      ClickFxState l111lliil1ilill1l1i11li_ii1il11l111ii11iilx = this.list70.get(0);

      for (int i = 1; i < this.list70.size(); i++) {
         l111lliil1ilill1l1i11li_ii1il11l111ii11iilx = this.list70.get(i);
         if (l111lliil1ilill1l1i11li_ii1il11l111ii11iilx.var5Var1652.x - l111lliil1ilill1l1i11li_ii1il11l111ii11iilx.var5Var1652.x > 0.0F) {
            l111lliil1ilill1l1i11li_ii1il11l111ii11iilx.var5Var1652.x = l111lliil1ilill1l1i11li_ii1il11l111ii11iilx.var5Var1652.x;
         }

         float f = (float)i / this.list70.size() * ((float)i / this.list70.size()) * 5.0F;
         float f1 = l111lliil1ilill1l1i11li_ii1il11l111ii11iilx.var5Var1652.z - l111lliil1ilill1l1i11li_ii1il11l111ii11iilx.var5Var1652.z;
         if (f1 > f) {
            l111lliil1ilill1l1i11li_ii1il11l111ii11iilx.var5Var1652.z -= f;
         }

         if (f1 < -f) {
            l111lliil1ilill1l1i11li_ii1il11l111ii11iilx.var5Var1652.z += f;
         }
      }
   }

   public ClickFxPoint on23(ClickFxPoint var1, ClickFxPoint var2, double var3) {
      ClickFxPoint l111lliil1ilill1l1i11li_illi1l1l1 = var1.string70().ColorAnimator(var2);
      l111lliil1ilill1l1i11li_illi1l1l1.CloseScreenEvent((float)var3).Easing(var1);
      return l111lliil1ilill1l1i11li_illi1l1l1;
   }

   public double on23(ClickFxPoint var1, ClickFxPoint var2, ClickFxPoint var3) {
      float f = var2.x - var1.x;
      float f1 = var2.y - var1.y;
      float f2 = var2.x - var3.x;
      float f3 = var2.y - var3.y;
      float f4 = f * f2 + f1 * f3;
      float f5 = f * f3 - f1 * f2;
      double d0 = Math.atan2(f5, f4);
      return d0 * 180.0 / Math.PI;
   }

   public void on23(ClickFxPoint var1) {
      this.var5Var1653 = var1;
   }

   public float boolean106() {
      return this.float268;
   }

   public void EventTriggerKeyEvent(float var1) {
      this.float268 = var1;
   }

   public boolean call141() {
      return this.boolean147;
   }

   public void BotWorldJoinEvent(boolean var1) {
      this.boolean147 = var1;
   }

   public boolean empty() {
      return this.list89.isEmpty();
   }

   public void UiAnimation(ClickFxPoint var1) {
      this.list70.get(0).var5Var165.ItemRegistry(this.list70.get(0).var5Var1652);
      this.list70.get(0).var5Var1652.Easing(var1);
   }

   public List<ClickFxState> int330() {
      return this.list70;
   }

   public static int string69() {
      return 16;
   }
}
