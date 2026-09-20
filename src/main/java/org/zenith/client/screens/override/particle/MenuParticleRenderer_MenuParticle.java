package org.zenith.client.screens.override.particle;

class MenuParticleRenderer_MenuParticle {
   public float x;
   public float y;
   public float size;
   public float speed;
   public float time;
   public float maxTime;
   public float alpha;

   public MenuParticleRenderer_MenuParticle(MenuParticleRenderer var1, float var2, float var3, boolean var4) {
      this.reset(var2, var3, var4);
   }

   public void reset(float var1, float var2, boolean var3) {
      this.size = 2 + (int)(Math.random() * 4.0);
      this.speed = 2.0F + (float)(Math.random() * 0.8F);
      this.maxTime = 100.0F + (float)(Math.random() * 200.0);
      this.time = var3 ? (float)(Math.random() * this.maxTime) : 0.0F;
      if (var3) {
         this.x = (float)(Math.random() * var1);
         this.y = (float)(Math.random() * var2);
      } else if (Math.random() > 0.5) {
         this.x = -20.0F;
         this.y = (float)(Math.random() * var2);
      } else {
         this.x = (float)(Math.random() * var1);
         this.y = -20.0F;
      }
   }

   public void update(float var1, float var2, float var3) {
      this.x = this.x + this.speed * var3;
      this.y = this.y + this.speed * var3;
      this.time += var3;
      float f = this.maxTime * 0.2F;
      if (this.time < f) {
         this.alpha = this.time / f;
      } else if (this.time > this.maxTime - f) {
         this.alpha = (this.maxTime - this.time) / f;
      } else {
         this.alpha = 1.0F;
      }

      this.alpha = Math.max(0.0F, Math.min(1.0F, this.alpha));
      if (this.time >= this.maxTime || this.x > var1 + 50.0F || this.y > var2 + 50.0F) {
         this.reset(var1, var2, false);
      }
   }
}
