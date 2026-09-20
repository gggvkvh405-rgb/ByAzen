package org.zenith.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zenith.ZenithClient;

public final class ProfileCacheStore {
   public static final Logger logger2 = LoggerFactory.getLogger("ZenithSortLayouts");
   public static final Gson gson2 = new GsonBuilder().setPrettyPrinting().create();
   public static final Type type2 = (new TypeToken<List<CachedProfile>>() {}).getType();
   public final Path path15 = ZenithClient.ColorAnimator.toPath().resolve("sort-layouts.json");
   public final List<CachedProfile> list114 = new ArrayList<>();

   public ProfileCacheStore() {
      this.load();
   }

   public List<CachedProfile> abstractClientPlayerEntity() {
      return List.copyOf(this.list114);
   }

   public CachedProfile EventWindowSizeChanged(String var1) {
      return var1 == null ? null : this.list114.stream().filter(var1xx -> var1xx.getName().equalsIgnoreCase(var1)).findFirst().orElse(null);
   }

   public boolean on23(String var1, Collection<SlotRenderRule> var2) {
      ArrayList arraylist = new ArrayList<>(this.list114);
      this.list114.removeIf(var1xx -> var1xx.getName().equalsIgnoreCase(var1));
      this.list114.add(new CachedProfile(var1, System.currentTimeMillis(), var2));
      if (this.itemStack5()) {
         return true;
      }

      this.list114.clear();
      this.list114.addAll(arraylist);
      return false;
   }

   public boolean delete(String var1) {
      ArrayList arraylist = new ArrayList<>(this.list114);
      if (!this.list114.removeIf(var1xx -> var1xx.getName().equalsIgnoreCase(var1))) {
         return false;
      }

      if (this.itemStack5()) {
         return true;
      }

      this.list114.clear();
      this.list114.addAll(arraylist);
      return false;
   }

   public void load() {
      this.list114.clear();
      if (Files.isRegularFile(this.path15)) {
         try {
            String s = Files.readString(this.path15, StandardCharsets.UTF_8);
            if (s.isBlank()) {
               return;
            }

            List<CachedProfile> list = (List<CachedProfile>)gson2.fromJson(s, type2);
            if (list == null) {
               return;
            }

            for (CachedProfile lll111iiili1il1l1ill1il1l_l1i1illlili : list) {
               if (lll111iiili1il1l1ill1il1l_l1i1illlili != null && lll111iiili1il1l1ill1il1l_l1i1illlili.matrixStack()) {
                  this.list114.add(lll111iiili1il1l1ill1il1l_l1i1illlili);
               }
            }
         } catch (Exception exception) {
            logger2.warn("Не удалось загрузить раскладки сортировки из {}", this.path15, exception);
         }
      }
   }

   public boolean itemStack5() {
      Path path = this.path15.resolveSibling(this.path15.getFileName() + ".tmp");

      try {
         Files.createDirectories(this.path15.getParent());
         Files.writeString(path, gson2.toJson(this.list114), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

         try {
            Files.move(path, this.path15, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
         } catch (AtomicMoveNotSupportedException atomicmovenotsupportedexception) {
            Files.move(path, this.path15, StandardCopyOption.REPLACE_EXISTING);
         }

         return true;
      } catch (IOException ioexception) {
         logger2.warn("Не удалось сохранить раскладки сортировки в {}", this.path15, ioexception);
         return false;
      }
   }
}
