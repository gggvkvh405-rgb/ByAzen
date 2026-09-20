package org.zenith.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.function.Supplier;
import org.zenith.ZenithClient;

public class Item<T> {
   protected Collection<T> items;
   public final String fileName;
   public final String shifr;
   public final Type type;
   public final Supplier<Collection<T>> collectionSupplier;

   public Item(String var1, String var2, Type var3, Supplier<Collection<T>> var4) {
      this.fileName = var1;
      this.shifr = var2;
      this.type = var3;
      this.collectionSupplier = var4;
      File file1 = new File(ZenithClient.ColorAnimator, var1);
      if (!file1.exists()) {
         try {
            file1.createNewFile();
            this.items = var4.get();
         } catch (Exception exception) {
            this.items = var4.get();
         }
      } else {
         this.load();
      }
   }

   protected Gson createGson() {
      return new GsonBuilder().create();
   }

   public void save() {
      Gson gson = this.createGson();
      String s = gson.toJson(this.items);

      try (FileWriter filewriter = new FileWriter(new File(ZenithClient.ColorAnimator, this.fileName))) {
         filewriter.write(this.shifr.isEmpty() ? s : Base64.getEncoder().encodeToString(CryptoUtils.on23(s.getBytes(), this.shifr)));
      } catch (Exception var8) {
      }
   }

   public void load() {
      try (BufferedReader bufferedreader = new BufferedReader(new FileReader(new File(ZenithClient.ColorAnimator, this.fileName)))) {
         Gson gson = this.createGson();
         if (!this.shifr.isEmpty()) {
            String s = bufferedreader.readLine();
            if (s != null && !s.isEmpty()) {
               byte[] abyte = Base64.getDecoder().decode(s);
               byte[] abyte1 = CryptoUtils.UiAnimation(abyte, this.shifr);
               String s1 = new String(abyte1, StandardCharsets.UTF_8);
               this.items = (Collection<T>)gson.fromJson(s1, this.type);
            } else {
               this.items = this.collectionSupplier.get();
            }
         } else {
            this.items = (Collection<T>)gson.fromJson(bufferedreader, this.type);
         }

         if (this.items == null) {
            this.items = this.collectionSupplier.get();
         }
      } catch (Exception exception) {
         this.items = this.collectionSupplier.get();
      }
   }

   public void add(T var1) {
      this.items.add(var1);
   }

   public void remove(T var1) {
      this.items.remove(var1);
   }

   public Collection<T> getItems() {
      return this.items;
   }
}
