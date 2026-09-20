package org.zenith.core;

import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;

/** A typed mutable value in Zenith's legacy shader uniform block. */
public final class LegacyUniform {
   final String name;
   final int count;
   final boolean integer;
   final float[] values;

   LegacyUniform(String name, int count, boolean integer, float[] values) {
      this.name = name;
      this.count = count;
      this.integer = integer;
      this.values = values;
   }

   public void set(float x) { if (values.length > 0) values[0] = x; }
   public void set(int x) { if (values.length > 0) values[0] = x; }
   public void set(float x, float y) { set(x); if (values.length > 1) values[1] = y; }
   public void set(float x, float y, float z) { set(x, y); if (values.length > 2) values[2] = z; }
   public void set(float x, float y, float z, float w) { set(x, y, z); if (values.length > 3) values[3] = w; }

   void addSize(Std140SizeCalculator size) {
      if (integer) size.putInt();
      else if (count == 1) size.putFloat();
      else if (count == 2) size.putVec2();
      else if (count == 3) size.putVec3();
      else size.putVec4();
   }

   void write(Std140Builder writer) {
      if (integer) writer.putInt((int)values[0]);
      else if (count == 1) writer.putFloat(values[0]);
      else if (count == 2) writer.putVec2(values[0], values[1]);
      else if (count == 3) writer.putVec3(values[0], values[1], values[2]);
      else writer.putVec4(values[0], values[1], values[2], values[3]);
   }
}
