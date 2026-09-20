package org.zenith.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatTagParser {
   public final ChatTag var110Var159;
   public final String string84;
   public final long long142;
   public final String string85;

   public ChatTagParser(String var1, String var2, long var3) {
      this.var110Var159 = ChatTag.GuiWalkEvent(var1);
      this.string84 = var2;
      this.long142 = var3;
      this.string85 = this.var1184();
   }

   public String getDisplayName() {
      return this.int370().getDisplayName();
   }

   public String getIcon() {
      return this.int370().getIcon();
   }

   public boolean float21() {
      Pattern pattern = Pattern.compile("(\\([^)]+\\))?\\s*#(\\d+)");
      Matcher matcher = pattern.matcher(this.string84);

      while (matcher.find()) {
         String s = matcher.group(2);
         if (s != null && s.length() > 0) {
            try {
               int i = Integer.parseInt(s);
               if (i > 0) {
                  return true;
               }
            } catch (NumberFormatException numberformatexception) {
               return true;
            }
         }
      }

      return false;
   }

   public String var1184() {
      List<BuildInfo> arraylist = new ArrayList<>();
      Pattern pattern = Pattern.compile("(\\([^)]+\\))?\\s*#(\\d+)");
      Matcher matcher = pattern.matcher(this.string84);

      while (matcher.find()) {
         String s = matcher.group(1);
         String s1 = matcher.group(2);
         if (s1 != null && s1.length() > 0) {
            try {
               int i = Integer.parseInt(s1);
               if (i > 0) {
                  arraylist.add(new BuildInfo(i, s));
               }
            } catch (NumberFormatException var14) {
            }
         }
      }

      if (arraylist.isEmpty()) {
         return "";
      }

      arraylist.sort(Comparator.comparingInt(BuildInfo::int202));
      int k = arraylist.size();
      List<BuildInfo> arraylist3 = new ArrayList<>();
      List<BuildInfo> arraylist4 = new ArrayList<>();
      List<BuildInfo> arraylist1 = new ArrayList<>();
      List<BuildInfo> arraylist2 = new ArrayList<>();

      for (BuildInfo lilli1lllliii1_l1i1illlilix : arraylist) {
         if (lilli1lllliii1_l1i1illlilix.int202() >= 1 && lilli1lllliii1_l1i1illlilix.int202() <= 14) {
            arraylist3.add(lilli1lllliii1_l1i1illlilix);
         } else if (lilli1lllliii1_l1i1illlilix.int202() >= 15 && lilli1lllliii1_l1i1illlilix.int202() <= 32) {
            arraylist4.add(lilli1lllliii1_l1i1illlilix);
         } else if (lilli1lllliii1_l1i1illlilix.int202() >= 33 && lilli1lllliii1_l1i1illlilix.int202() <= 47) {
            arraylist1.add(lilli1lllliii1_l1i1illlilix);
         } else if (lilli1lllliii1_l1i1illlilix.int202() >= 48) {
            arraylist2.add(lilli1lllliii1_l1i1illlilix);
         }
      }

      ArrayList arraylist5 = new ArrayList();
      byte b0 = 4;
      if (k <= 4) {
         for (BuildInfo lilli1lllliii1_l1i1illlilix : arraylist) {
            arraylist5.add(this.on23(lilli1lllliii1_l1i1illlilix));
         }
      } else {
         List<List<BuildInfo>> arraylist6 = new ArrayList<>();
         arraylist6.add(arraylist3);
         arraylist6.add(arraylist4);
         arraylist6.add(arraylist1);
         arraylist6.add(arraylist2);
         int[] aint = new int[4];

         for (int j = 0; j < 4 && arraylist5.size() < b0; j++) {
            if (!arraylist6.get(j).isEmpty()) {
               arraylist5.add(this.on23(arraylist6.get(j).get(0)));
               aint[j] = 1;
            }
         }

         for (int l = 0; l < 4 && arraylist5.size() < b0; l++) {
            while (aint[l] < arraylist6.get(l).size() && arraylist5.size() < b0) {
               arraylist5.add(this.on23(arraylist6.get(l).get(aint[l])));
               aint[l]++;
            }
         }
      }

      String s2 = String.join(", ", arraylist5);
      if (k > 4) {
         s2 = s2 + "...";
      }

      return s2;
   }

   public String on23(BuildInfo var1) {
      return var1.string68() != null && !var1.string68().isEmpty() ? var1.int202() + " " + var1.string68() : String.valueOf(var1.int202());
   }

   public ChatTag int370() {
      return this.var110Var159;
   }

   public String getMessage() {
      return this.string84;
   }

   public long call452() {
      return this.long142;
   }

   public String call440() {
      return this.string85;
   }
}
