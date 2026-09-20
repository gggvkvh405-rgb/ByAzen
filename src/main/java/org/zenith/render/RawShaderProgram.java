package org.zenith.render;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL20;

public class RawShaderProgram {
   public final int int333;
   public final Map<String, Integer> map35 = new HashMap<>();
   public final String string82;
   public final String string83;

   public RawShaderProgram(String var1, String var2) {
      this(var1, var2, var2);
   }

   public RawShaderProgram(String var1, String var2, String var3) {
      this.string82 = "/assets/gl/shaders/" + var1 + "/" + var3 + ".vsh";
      this.string83 = "/assets/gl/shaders/" + var1 + "/" + var2 + ".fsh";
      int i = this.ItemRegistry(this.string82, 35633);
      int j = this.ItemRegistry(this.string83, 35632);
      this.int333 = GL20.glCreateProgram();
      GL20.glAttachShader(this.int333, i);
      GL20.glAttachShader(this.int333, j);
      GL20.glBindAttribLocation(this.int333, 0, "Position");
      GL20.glBindAttribLocation(this.int333, 1, "UV0");
      GL20.glBindAttribLocation(this.int333, 2, "Color");
      GL20.glLinkProgram(this.int333);
      if (GL20.glGetProgrami(this.int333, 35714) == 0) {
         throw new RuntimeException("Failed to link shader program: " + GL20.glGetProgramInfoLog(this.int333));
      }

      GL20.glDeleteShader(i);
      GL20.glDeleteShader(j);
   }

   public int ItemRegistry(String var1, int var2) {
      try {
         String s = this.KeybindsHud(var1);
         int i = GL20.glCreateShader(var2);
         GL20.glShaderSource(i, s);
         GL20.glCompileShader(i);
         if (GL20.glGetShaderi(i, 35713) == 0) {
            throw new RuntimeException("Failed to compile shader: " + var1 + "\n" + GL20.glGetShaderInfoLog(i));
         } else {
            return i;
         }
      } catch (IOException ioexception) {
         throw new RuntimeException("Failed to load shader: " + var1, ioexception);
      }
   }

   public String KeybindsHud(String var1) throws IOException {
      InputStream inputstream = RawShaderProgram.class.getResourceAsStream(var1);
      if (inputstream == null) {
         throw new IOException("Shader file not found: " + var1);
      }

      StringBuilder stringbuilder = new StringBuilder();
      BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8));

      try {
         String s;
         while ((s = bufferedreader.readLine()) != null) {
            stringbuilder.append(s).append("\n");
         }

         bufferedreader.close();
      } catch (Throwable throwable1) {
         try {
            bufferedreader.close();
         } catch (Throwable throwable) {
            throwable1.addSuppressed(throwable);
         }

         throw throwable1;
      }

      if (stringbuilder.length() > 0 && stringbuilder.charAt(0) == '\ufeff') {
         stringbuilder.deleteCharAt(0);
      }

      return stringbuilder.toString();
   }

   public void bind() {
      GL20.glUseProgram(this.int333);
   }

   public void unbind() {
      GL20.glUseProgram(0);
   }

   public void ItemSpec(String var1, int var2) {
      int i = this.HudInfoBoxPrimary(var1);
      if (i != -1) {
         GL20.glUniform1i(i, var2);
      }
   }

   public void on23(String var1, float var2) {
      int i = this.HudInfoBoxPrimary(var1);
      if (i != -1) {
         GL20.glUniform1f(i, var2);
      }
   }

   public void on23(String var1, FloatBuffer var2) {
      int i = this.HudInfoBoxPrimary(var1);
      if (i != -1) {
         GL20.glUniform1fv(i, var2);
      }
   }

   public void on23(String var1, float var2, float var3) {
      int i = this.HudInfoBoxPrimary(var1);
      if (i != -1) {
         GL20.glUniform2f(i, var2, var3);
      }
   }

   public void on23(String var1, Vector2f var2) {
      this.on23(var1, var2.x, var2.y);
   }

   public void on23(String var1, float var2, float var3, float var4, float var5) {
      int i = this.HudInfoBoxPrimary(var1);
      if (i != -1) {
         GL20.glUniform4f(i, var2, var3, var4, var5);
      }
   }

   public void on23(String var1, float var2, float var3, float var4) {
      int i = this.HudInfoBoxPrimary(var1);
      if (i != -1) {
         GL20.glUniform3f(i, var2, var3, var4);
      }
   }

   public void on23(String var1, Vector3f var2) {
      this.on23(var1, var2.x, var2.y, var2.z);
   }

   public void ItemSpec(String var1, boolean var2) {
      this.ItemSpec(var1, var2 ? 1 : 0);
   }

   public void delete() {
      GL20.glDeleteProgram(this.int333);
   }

   public int HudInfoBoxPrimary(String var1) {
      return this.map35.computeIfAbsent(var1, var1x -> GL20.glGetUniformLocation(this.int333, var1x));
   }
}
