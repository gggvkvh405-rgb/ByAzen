package org.zenith.core;

public class UiAnimation {
   public long duration;
   public float value;
   public Easing CrosshairTargetUpdateEvent;
   public long startTime;
   public float DataChangedEvent;
   public float EventInjectPlaced;
   public boolean done;
   public boolean ChatMessageEvent;

   public UiAnimation(long var1, float var3, Easing var4) {
      this.duration = var1;
      this.CrosshairTargetUpdateEvent = var4;
      this.value = var3;
      this.DataChangedEvent = var3;
      this.EventInjectPlaced = var3;
      this.done = true;
   }

   public UiAnimation(long var1, Easing var3) {
      this(var1, 0.0F, var3);
   }

   public void on23(boolean var1) {
      this.on23(var1 ? 1.0F : 0.0F);
   }

   public float on23(float var1) {
      long i = System.currentTimeMillis();
      if (var1 != this.EventInjectPlaced) {
         this.DataChangedEvent = this.value;
         this.EventInjectPlaced = var1;
         this.startTime = i;
         this.done = false;
      }

      long j = i - this.startTime;
      if (j >= this.duration) {
         this.value = this.EventInjectPlaced;
         this.done = true;
         return this.value;
      } else {
         float f = (float)j / (float)this.duration;
         float f1 = this.CrosshairTargetUpdateEvent.ease(f, 0.0F, 1.0F, 1.0F);
         this.value = this.DataChangedEvent + (this.EventInjectPlaced - this.DataChangedEvent) * f1;
         return this.value;
      }
   }

   public void setValue(float var1) {
      this.value = var1;
      this.DataChangedEvent = var1;
      this.EventInjectPlaced = var1;
      this.done = true;
   }

   public void UiAnimation(float var1) {
      this.value = var1;
      this.DataChangedEvent = var1;
      this.EventInjectPlaced = var1;
      this.done = true;
   }

   public void reset() {
      this.UiAnimation(0.0F);
   }

   public void Easing(float var1) {
      if (var1 != this.EventInjectPlaced) {
         this.DataChangedEvent = this.value;
         this.EventInjectPlaced = var1;
         this.startTime = System.currentTimeMillis();
         this.done = false;
      }
   }

   public float EmotePlayback() {
      return this.on23(this.EventInjectPlaced);
   }

   public long getDuration() {
      return this.duration;
   }

   public float CancellableEvent() {
      return this.value;
   }

   public Easing Event08() {
      return this.CrosshairTargetUpdateEvent;
   }

   public long getStartTime() {
      return this.startTime;
   }

   public float BotChatEvent() {
      return this.DataChangedEvent;
   }

   public float BotDisconnectEvent() {
      return this.EventInjectPlaced;
   }

   public boolean isDone() {
      return this.done;
   }

   public boolean BotWorldJoinEvent() {
      return this.ChatMessageEvent;
   }

   public void on23(long var1) {
      this.duration = var1;
   }

   public void on23(Easing var1) {
      this.CrosshairTargetUpdateEvent = var1;
   }

   public void setStartTime(long var1) {
      this.startTime = var1;
   }

   public void ColorAnimator(float var1) {
      this.DataChangedEvent = var1;
   }

   public void ItemRegistry(float var1) {
      this.EventInjectPlaced = var1;
   }

   public void UiAnimation(boolean var1) {
      this.done = var1;
   }

   public void Easing(boolean var1) {
      this.ChatMessageEvent = var1;
   }
}
