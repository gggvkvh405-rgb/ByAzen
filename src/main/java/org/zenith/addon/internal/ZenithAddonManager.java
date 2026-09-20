package org.zenith.addon.internal;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import org.zenith.ZenithClient;
import org.zenith.addon.api.ZenithAddon;
import org.zenith.addon.api.render.AddonRender;
import org.zenith.addon.runtime.AddonFrontendServices;
import org.zenith.addon.runtime.AddonLoadReport;
import org.zenith.addon.runtime.EntitlementProvider;
import org.zenith.addon.runtime.FabricAddonLoader;
import org.zenith.addon.runtime.ModuleRegistry;
import org.zenith.addon.runtime.RegisteredModule;
import org.zenith.core.ClientSession;
import org.zenith.event.EventRenderScreenHook;

public final class ZenithAddonManager implements AutoCloseable {
   public static final Logger LOGGER = Logger.getLogger("ZenithAddonManager");
   public static final String MODS_ENDPOINT = "https://api.zenithdlc.org/mods/list/";
   public static final Pattern ADDON_ID = Pattern.compile("[a-z0-9][a-z0-9_-]*");
   public final ModuleRegistry registry = new ModuleRegistry();
   public final Set<String> reportedRenderFailures = new HashSet<>();
   public AddonLoadReport report = new AddonLoadReport(List.of(), List.of(), List.of());

   public ZenithAddonManager() {
      EventManager.register(this);
   }

   public void load() {
      AddonRender.installBackend(new ZenithImmediateRenderBackend());
      AddonFrontendServices addonfrontendservices = new AddonFrontendServices(new ZenithModuleCatalog(), new ZenithConfigCatalog(), new ZenithFriendCatalog());
      this.report = FabricAddonLoader.load(this.registry, this.entitlementProvider(), addonfrontendservices);

      for (RegisteredModule registeredmodule : this.registry.modules()) {
         ZenithClient.on23().ColorAnimator().UiAnimation(new AddonBackedModule(registeredmodule));
      }
   }

   public ModuleRegistry registry() {
      return this.registry;
   }

   public AddonLoadReport report() {
      return this.report;
   }

   @EventTarget
   public void onHudRender(EventRenderScreenHook var1) {
      if (ZenithClient.on23().ColorAnimator() != null && MinecraftClient.getInstance().world != null && !MinecraftClient.getInstance().options.hudHidden) {
         ZenithAddonRenderContext zenithaddonrendercontext = new ZenithAddonRenderContext(var1.WarpFarm());

         for (RegisteredModule registeredmodule : this.registry.enabledModules()) {
            try {
               registeredmodule.renderHud(zenithaddonrendercontext);
            } catch (RuntimeException runtimeexception) {
               if (this.reportedRenderFailures.add(registeredmodule.id())) {
                  LOGGER.log(Level.WARNING, "Addon HUD render failed in " + registeredmodule.id(), runtimeexception);
               }
            }
         }
      }
   }

   public EntitlementProvider entitlementProvider() {
      if (!hasLocalAddons()) {
         LOGGER.info("No zenith:addon entrypoints found, skipping entitlement check");
         return EntitlementProvider.allowAll();
      } else {
         ClientSession ii1il11l111ii11iil_ii1il11l111ii11iil_ii1il11l111ii11iil = ZenithClient.on23().CommandManager();
         String s = ii1il11l111ii11iil_ii1il11l111ii11iil_ii1il11l111ii11iil == null
            ? ""
            : ii1il11l111ii11iil_ii1il11l111ii11iil_ii1il11l111ii11iil.getUsername();
         if (s != null && !s.isBlank()) {
            Set<String> set = this.requestAllowedAddons(s.trim());
            LOGGER.info(() -> "Allowed addons for " + s + ": " + set);
            return set::contains;
         } else {
            LOGGER.warning("Addon entitlement check denied: Zenith nickname is unavailable");
            return EntitlementProvider.denyAll();
         }
      }
   }

   private static boolean hasLocalAddons() {
      try {
         return !FabricLoader.getInstance().getEntrypointContainers("zenith:addon", ZenithAddon.class).isEmpty();
      } catch (Throwable var1) {
         return true;
      }
   }

   public Set<String> requestAllowedAddons(String var1) {
      Set<String> hashset = new HashSet<>();
      HttpURLConnection httpurlconnection = null;

      HashSet hashset1;
      try {
         String s = URLEncoder.encode(var1, StandardCharsets.UTF_8).replace("+", "%20");
         httpurlconnection = (HttpURLConnection)URI.create("https://api.zenithdlc.org/mods/list/" + s).toURL().openConnection();
         httpurlconnection.setRequestMethod("GET");
         httpurlconnection.setConnectTimeout(5000);
         httpurlconnection.setReadTimeout(5000);
         httpurlconnection.setRequestProperty("Accept", "application/json");
         httpurlconnection.setRequestProperty("User-Agent", "Zenith-Addon-Loader/1.0");
         if (httpurlconnection.getResponseCode() != 200) {
            LOGGER.warning("Could not request addons for " + var1 + ": HTTP " + httpurlconnection.getResponseCode());
            return hashset;
         }

         InputStreamReader inputstreamreader = new InputStreamReader(httpurlconnection.getInputStream(), StandardCharsets.UTF_8);

         JsonElement jsonelement;
         try {
            jsonelement = JsonParser.parseReader(inputstreamreader);
         } catch (Throwable throwable1) {
            try {
               inputstreamreader.close();
            } catch (Throwable throwable) {
               throwable1.addSuppressed(throwable);
            }

            throw throwable1;
         }

         inputstreamreader.close();
         JsonArray jsonarray = extractMods(jsonelement);
         if (jsonarray != null) {
            for (JsonElement jsonelement1 : jsonarray) {
               if (jsonelement1.isJsonPrimitive() && jsonelement1.getAsJsonPrimitive().isString()) {
                  String s1 = jsonelement1.getAsString().trim();
                  if (ADDON_ID.matcher(s1).matches()) {
                     hashset.add(s1);
                  }
               }
            }

            return hashset;
         }

         hashset1 = (HashSet)hashset;
      } catch (Exception exception) {
         LOGGER.log(Level.WARNING, "Could not request addons for " + var1, exception);
         return hashset;
      } finally {
         if (httpurlconnection != null) {
            httpurlconnection.disconnect();
         }
      }

      return hashset1;
   }

   public static JsonArray extractMods(JsonElement var0) {
      if (var0 == null || var0.isJsonNull()) {
         return null;
      } else if (var0.isJsonArray()) {
         return var0.getAsJsonArray();
      } else if (!var0.isJsonObject()) {
         return null;
      } else {
         JsonObject jsonobject = var0.getAsJsonObject();
         if (jsonobject.has("mods") && jsonobject.get("mods").isJsonArray()) {
            return jsonobject.getAsJsonArray("mods");
         } else {
            return jsonobject.has("data") && jsonobject.get("data").isJsonArray() ? jsonobject.getAsJsonArray("data") : null;
         }
      }
   }

   @Override
   public void close() {
      EventManager.unregister(this);
      this.registry.close();
   }
}
