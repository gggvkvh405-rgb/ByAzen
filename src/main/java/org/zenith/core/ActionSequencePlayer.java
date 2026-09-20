package org.zenith.core;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.BooleanSupplier;
import org.zenith.util.StopWatch;

public class ActionSequencePlayer {
   public final StopWatch stopWatch3 = new StopWatch();
   public final List<StepConditionHost> list107 = Lists.newCopyOnWriteArrayList();
   public final List<TimedStep> list108 = Lists.newCopyOnWriteArrayList();
   public int int435;
   public int int436;
   public boolean boolean190;
   public SequenceStep zClass071Var165 = new SeqStepB(1);

   public ActionSequencePlayer() {
      this.int206();
   }

   public ActionSequencePlayer on23(int var1, TickGate var2) {
      return this.on23(var1, var2, () -> true, 0);
   }

   public ActionSequencePlayer on23(int var1, TickGate var2, BooleanSupplier var3) {
      return this.on23(var1, var2, var3, 0);
   }

   public ActionSequencePlayer on23(int var1, TickGate var2, int var3) {
      return this.on23(var1, var2, () -> true, var3);
   }

   public ActionSequencePlayer on23(int var1, TickGate var2, BooleanSupplier var3, int var4) {
      this.list107.add(new StepConditionHost(var1, var2, var3, var4));
      this.list107.sort(StepConditionHost::UiAnimation);
      return this;
   }

   public ActionSequencePlayer UiAnimation(int var1, TickGate var2) {
      return this.UiAnimation(var1, var2, () -> true, 0);
   }

   public ActionSequencePlayer UiAnimation(int var1, TickGate var2, BooleanSupplier var3) {
      return this.UiAnimation(var1, var2, var3, 0);
   }

   public ActionSequencePlayer UiAnimation(int var1, TickGate var2, int var3) {
      return this.UiAnimation(var1, var2, () -> true, var3);
   }

   public ActionSequencePlayer UiAnimation(int var1, TickGate var2, BooleanSupplier var3, int var4) {
      this.list108.add(new TimedStep(var1, var2, var3, var4));
      this.list108.sort(TimedStep::UiAnimation);
      return this;
   }

   public void call040() {
      this.stopWatch3.reset();
   }

   public void call041() {
      this.int435 = 0;
      this.int436 = 0;
   }

   public ActionSequencePlayer int205() {
      if (this.ImageEncoder()) {
         this.int206();
      }

      return this;
   }

   public ActionSequencePlayer int206() {
      this.list107.clear();
      this.list108.clear();
      this.call040();
      this.call041();
      return this;
   }

   public void update() {
      if ((!this.list107.isEmpty() || !this.list108.isEmpty()) && !this.boolean190) {
         this.list107.forEach(var1 -> {
            if (this.int435 < this.list107.size()) {
               StepConditionHost l1l1i1ill1ll_Var160 = this.list107.get(this.int435);
               if (l1l1i1ill1ll_Var160.double86().getAsBoolean() && this.stopWatch3.BotFeatureRegistry(l1l1i1ill1ll_Var160.double84())) {
                  l1l1i1ill1ll_Var160.double85().string81();
                  this.int435++;
                  this.call040();
                  if (this.zClass071Var165.CloudApiClient(this.int435, this.list107.size())) {
                     this.call041();
                     this.zClass071Var165.double83();
                  }
               }
            }
         });
         this.list108.forEach(var1 -> {
            if (this.int436 < this.list108.size()) {
               TimedStep l1l1i1ill1ll_liil11l111liil1ll = this.list108.get(this.int436);
               if (l1l1i1ill1ll_liil11l111liil1ll.double86().getAsBoolean() && l1l1i1ill1ll_liil11l111liil1ll.getTicks() <= 0) {
                  l1l1i1ill1ll_liil11l111liil1ll.double85().string81();
                  this.int436++;
                  this.call040();
                  if (this.zClass071Var165.CloudApiClient(this.int436, this.list108.size())) {
                     this.call041();
                     this.zClass071Var165.double83();
                  }
               }

               l1l1i1ill1ll_liil11l111liil1ll.int332();
            }
         });
         this.int435 = Math.min(this.int435, this.list107.size());
         this.int436 = Math.min(this.int436, this.list108.size());
      }
   }

   public ActionSequencePlayer on23(SequenceStep var1) {
      this.zClass071Var165 = var1;
      return this;
   }

   public boolean ImageEncoder() {
      return this.int435 >= this.list107.size() && this.int436 >= this.list108.size() && !this.boolean190 && this.zClass071Var165.ImageEncoder();
   }

   public StopWatch call274() {
      return this.stopWatch3;
   }

   public List<StepConditionHost> call275() {
      return this.list107;
   }

   public List<TimedStep> double74() {
      return this.list108;
   }

   public int double75() {
      return this.int435;
   }

   public int double76() {
      return this.int436;
   }

   public boolean double77() {
      return this.boolean190;
   }

   public SequenceStep double78() {
      return this.zClass071Var165;
   }

   public void Event14(int var1) {
      this.int435 = var1;
   }

   public void HealthUpdateEvent(int var1) {
      this.int436 = var1;
   }

   public void BotTickEvent(boolean var1) {
      this.boolean190 = var1;
   }
}
