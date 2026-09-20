package org.zenith.event;

public final class ChatMessageEvent extends CancellableEvent {
   public String message;
   public final ChatMessageEvent.Source direction;
   public final ChatMessageEvent.Direction phase;

   public ChatMessageEvent(String var1, ChatMessageEvent.Source var2, ChatMessageEvent.Direction var3) {
      this.message = var1;
      this.direction = var2;
      this.phase = var3;
   }

   public String getMessage() {
      return this.message;
   }

   public boolean NoInteract() {
      return this.direction == ChatMessageEvent.Source.call205;
   }

   public boolean OpenWals() {
      return this.phase == ChatMessageEvent.Direction.call206;
   }


   public enum Direction {
      call206,
      call270;
   }

   public enum Source {
      call264,
      call205;
   }
}
