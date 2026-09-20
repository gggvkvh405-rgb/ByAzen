package org.zenith.base.comand.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.CommandSource;
import org.zenith.ZenithClient;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.base.comand.impl.args.NeuroArgumentType;
import org.zenith.core.StyledTextBuilder;
import org.zenith.core.TextAccent;

public class NeuroCommand extends CommandAbstract {
   public static final File neuroDirectory = new File(ZenithClient.ColorAnimator, "neuro");

   public NeuroCommand() {
      super("neuro");
      ensureNeuroDirectory();
   }

   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> var1) {
      var1.then(literal("load").then(arg("name", NeuroArgumentType.create()).executes(var0 -> {
         String s = (String)var0.getArgument("name", String.class);
         File file1 = findNeuroFile(s);
         if (file1 == null) {
            StyledTextBuilder.on23(TextAccent.call013, "Файл нейромодели не найден");
            return 1;
         }

         try {
            ZenithClient.on23().ServiceException().Easing(file1);
            StyledTextBuilder.on23(TextAccent.call002, "Нейромодель загружена: " + file1.getName());
         } catch (IOException ioexception) {
            StyledTextBuilder.on23(TextAccent.call013, "Ошибка при загрузке нейромодели");
         }

         return 1;
      })));
      var1.then(literal("dir").executes(var0 -> {
         if (!ensureNeuroDirectory()) {
            StyledTextBuilder.on23(TextAccent.call013, "Ошибка при создании папки нейромоделей");
            return 1;
         }

         try {
            Runtime.getRuntime().exec(new String[]{"explorer", neuroDirectory.getAbsolutePath()});
         } catch (IOException ioexception) {
            StyledTextBuilder.on23(TextAccent.call013, "Ошибка при открытии папки нейромоделей");
         }

         return 1;
      }));
   }

   public static List<String> neuroNames() {
      if (!ensureNeuroDirectory()) {
         return new ArrayList<>();
      }

      File[] afile = neuroDirectory.listFiles();
      List<String> arraylist = new ArrayList<>();
      if (afile != null) {
         for (File file1 : afile) {
            if (file1.isFile()) {
               arraylist.add(file1.getName());
            }
         }
      }

      arraylist.sort(String.CASE_INSENSITIVE_ORDER);
      return arraylist;
   }

   public static boolean ensureNeuroDirectory() {
      return neuroDirectory.isDirectory() || !neuroDirectory.exists() && neuroDirectory.mkdirs();
   }

   public static File findNeuroFile(String var0) {
      if (var0 != null && !var0.isBlank()) {
         try {
            File file1 = neuroDirectory.getCanonicalFile();
            File file2 = new File(file1, var0).getCanonicalFile();
            return file2.toPath().startsWith(file1.toPath()) && file2.isFile() ? file2 : null;
         } catch (IOException ioexception) {
            return null;
         }
      } else {
         return null;
      }
   }
}
