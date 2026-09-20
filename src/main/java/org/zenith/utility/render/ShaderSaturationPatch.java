package org.zenith.utility.render;

import java.util.regex.Pattern;

public final class ShaderSaturationPatch {
   public static final Pattern COLOR_SAMPLE = Pattern.compile("(?m)^(\\s*vec4\\s+color\\s*=\\s*texture\\(Sampler0\\s*,\\s*texCoord0\\)[^;]*;)");
   public static final Pattern DIFFUSE_COLOR = Pattern.compile("(?m)^(\\s*diffuseColor\\s*\\*=\\s*v_Color\\s*;)");

   private ShaderSaturationPatch() {
   }
}
