package org.zenith.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

public final class TradeGuardService {
   public static final int AutoZamok = 3;

   public static BotFeaturesDto TradeGuardService(String var0) {
      try {
         JsonObject jsonobject = JsonParser.parseString(var0).getAsJsonObject();
         int i = on23(jsonobject, "v", -1);
         if (i != 3) {
            throw TextScanner("UNSUPPORTED_PROTOCOL", "Server did not use protocol v3");
         } else {
            UUID uuid = SimpleItemBuilder(jsonobject, "id");
            UUID uuid1 = ItemServiceBase(jsonobject, "replyTo");
            String s = on23(jsonobject, "type");
            JsonObject jsonobject1 = ItemRegistry(jsonobject, "payload");
            if (jsonobject1 == null) {
               throw TextScanner("BAD_PACKET", "Server payload is not an object");
            } else {
               return new BotFeaturesDto(i, uuid, uuid1, on23(s, jsonobject1), jsonobject1);
            }
         }
      } catch (ServiceException illll1l1l1il) {
         throw illll1l1l1il;
      } catch (RuntimeException runtimeexception) {
         throw TextScanner("BAD_PACKET", ItemSpec(runtimeexception));
      }
   }

   public static CloudResponse on23(String var0, JsonObject var1) {
      return (CloudResponse)(
         switch (var0) {
            case "connection.welcome" -> new CloudServerStatsDto(
               UiAnimation(var1, "serverVersion"),
               on23(var1, "serverTime", 0L),
               on23(var1, "heartbeatIntervalMs", 0L),
               on23(var1, "heartbeatTimeoutMs", 0L),
               on23(var1, "maxFrameBytes", 65536),
               on23(var1, "statePublishIntervalMs", 100L),
               Easing(TextScanner(var1, "capabilities"))
            );
            case "connection.close" -> new CloudCodeDto(on23(var1, "code", 1000), UiAnimation(var1, "reason"));
            case "auth.success" -> new CloudPlayerInfoDto(
               SimpleItemBuilder(var1, "sessionId"),
               ModuleSnapshotDto(ColorAnimator(var1, "user")),
               on23(var1, "accessExpiresAt", 0L),
               Easing(TextScanner(var1, "permissions")),
               ItemRegistry(ColorAnimator(var1, "cosmetics"))
            );
            case "cosmetics.access" -> ItemRegistry(var1);
            case "auth.failure" -> new CloudErrorDto(UiAnimation(var1, "code"), UiAnimation(var1, "message"));
            case "system.error" -> new CloudMessageDto(
               UiAnimation(var1, "code"), UiAnimation(var1, "message"), NbtEditor(var1, "retryable"), EnchantItemSpec(var1, "retryAfterMs")
            );
            case "friends.snapshot" -> ItemSpec(var1);
            case "friends.request.received" -> new CloudRelationWrapDto(CloudUserProfile(var1));
            case "friends.request.created" -> new CloudFeaturedDto(on23(var1, "status"), CloudUserProfile(ColorAnimator(var1, "request")));
            case "friends.request.accepted" -> new CloudIdentityDto(SimpleItemBuilder(var1, "requestId"), on23(var1, "status"));
            case "friends.request.declined" -> new CloudAccountDto(SimpleItemBuilder(var1, "requestId"), UiAnimation(var1, "status"));
            case "friends.added" -> new CloudBadgesDto(
               SimpleItemBuilder(var1, "requestId"), UiAnimation(TextScanner(var1, "users")), on23(var1, "createdAt", 0L)
            );
            case "friends.removed" -> new CloudFeatureDto(on23(var1, "friendUserId"), NbtEditor(var1, "removed"));
            case "friends.remove.completed" -> new CloudStatusDto(on23(var1, "friendUserId"), NbtEditor(var1, "removed"));
            case "chat.message.accepted" -> new CloudMediaDto(NbtItemSpec(var1));
            case "chat.message.received" -> TextScanner(var1);
            case "chat.history" -> EnchantItemSpec(var1);
            case "player.watch.result" -> new CloudListsDto(Easing(ItemSpec(var1, "acceptedUserIds")), Easing(ItemSpec(var1, "rejectedUserIds")));
            case "player.state.batch" -> CloudApiClient(var1);
            case "player.inventory.batch" -> MediaTrackInfo(var1);
            case "player.online" -> new CloudAuthAckDto(on23(var1, "userId"), SimpleItemBuilder(var1, "sessionId"), on23(var1, "fence", -1L));
            case "player.offline" -> new CloudSessionAckDto(on23(var1, "userId"), SimpleItemBuilder(var1, "sessionId"), on23(var1, "fence", -1L));
            case "config.upload.ticket" -> SimpleItemBuilder(var1);
            case "config.saved" -> new CloudUserResultDto(ProfileItemBuilder(var1));
            case "config.download.ticket" -> new CloudWhoAmIDto(
               SimpleItemBuilder(var1, "configId"), StringCodec(var1), ProfileItemBuilder(ColorAnimator(var1, "config"))
            );
            case "config.list" -> ItemServiceBase(var1);
            case "config.catalog" -> NbtEditor(var1);
            case "config.updated" -> new CloudUserRefDto(ProfileItemBuilder(var1));
            case "config.codes" -> PotionItemBuilder(var1);
            case "config.access.granted" -> new CloudUserWrapDto(ProfileItemBuilder(var1));
            case "config.code.revoked" -> new CloudPairDto(SimpleItemBuilder(var1, "configId"), SimpleItemBuilder(var1, "codeId"), NbtEditor(var1, "revoked"));
            case "config.deleted" -> new CloudFlagsDto(SimpleItemBuilder(var1, "configId"), NbtEditor(var1, "deleted"), NbtEditor(var1, "blobDeleted"));
            case "config.preview.ticket" -> new CloudLoginDto(SimpleItemBuilder(var1, "configId"), StringCodec(var1), on23(var1, "maxSizeBytes", 0L));
            case "config.like.result" -> new CloudLikeDto(
               SimpleItemBuilder(var1, "configId"), NbtEditor(var1, "liked"), Math.max(0L, on23(var1, "likeCount", 0L))
            );
            case "captcha.solved" -> new CloudNoticeDto(on23(var1, "answer"));
            default -> throw TextScanner("BAD_PACKET", "Unknown server packet type: " + var0);
         }
      );
   }

   public static CloudPermissionsDto ItemRegistry(JsonObject var0) {
      return new CloudPermissionsDto(Easing(ItemSpec(var0, "models")), Easing(ItemSpec(var0, "emotes")));
   }

   public static CloudFriendsDto ItemSpec(JsonObject var0) {
      ArrayList arraylist = new ArrayList();

      for (JsonElement jsonelement : ItemSpec(var0, "friends")) {
         JsonObject jsonobject = on23(jsonelement, "friend snapshot item");
         arraylist.add(new CloudFriendDto(ModuleSnapshotDto(ColorAnimator(jsonobject, "user")), NbtEditor(jsonobject, "online")));
      }

      return new CloudFriendsDto(
         on23(var0, "snapshotId"),
         on23(var0, "revision", 0L),
         on23(var0, "chunkIndex", -1),
         NbtEditor(var0, "finalChunk"),
         List.copyOf(arraylist),
         on23(ItemSpec(var0, "incomingRequests")),
         on23(ItemSpec(var0, "outgoingRequests"))
      );
   }

   public static CloudMediaEntryDto TextScanner(JsonObject var0) {
      return new CloudMediaEntryDto(NbtItemSpec(var0));
   }

   public static MediaTrackInfo NbtItemSpec(JsonObject var0) {
      MediaTrackInfo iiilll111l111 = MediaTrackInfo.on23(var0);
      if (iiilll111l111 == null) {
         throw TextScanner("BAD_PACKET", "Invalid chat message payload");
      } else {
         return iiilll111l111;
      }
   }

   public static CloudMediaPageDto EnchantItemSpec(JsonObject var0) {
      ArrayList arraylist = new ArrayList();

      for (JsonElement jsonelement : ItemSpec(var0, "messages")) {
         arraylist.add(NbtItemSpec(on23(jsonelement, "chat history item")));
      }

      JsonObject jsonobject = ItemRegistry(var0, "nextCursor");
      CloudViewDto l1i1li1i11_Var160 = jsonobject == null
         ? null
         : new CloudViewDto(on23(jsonobject, "createdAt", 0L), SimpleItemBuilder(jsonobject, "messageId"));
      return new CloudMediaPageDto(List.copyOf(arraylist), NbtEditor(var0, "hasMore"), l1i1li1i11_Var160);
   }

   public static CloudFullUserDto SimpleItemBuilder(JsonObject var0) {
      boolean flag = NbtEditor(var0, "alreadyReady");
      JsonObject jsonobject = ItemRegistry(var0, "config");
      return new CloudFullUserDto(
         SimpleItemBuilder(var0, "uploadId"),
         flag,
         flag ? null : StringCodec(var0),
         on23(var0, "maxSizeBytes", 0L),
         jsonobject == null ? null : ProfileItemBuilder(jsonobject)
      );
   }

   public static CloudUsersPageDto ItemServiceBase(JsonObject var0) {
      ArrayList arraylist = new ArrayList();

      for (JsonElement jsonelement : ItemSpec(var0, "configs")) {
         arraylist.add(ProfileItemBuilder(on23(jsonelement, "config list item")));
      }

      Integer integer = var0.has("nextOffset") ? on23(var0, "nextOffset", 0) : null;
      return new CloudUsersPageDto(on23(var0, "offset", 0), List.copyOf(arraylist), NbtEditor(var0, "hasMore"), integer);
   }

   public static CloudConfigsPageDto NbtEditor(JsonObject var0) {
      ArrayList arraylist = new ArrayList();

      for (JsonElement jsonelement : ItemSpec(var0, "configs")) {
         JsonObject jsonobject = on23(jsonelement, "config catalog item");
         JsonObject jsonobject1 = ColorAnimator(jsonobject, "author");
         JsonObject jsonobject2 = ColorAnimator(jsonobject, "access");
         arraylist.add(
            new CloudConfigDetailsDto(
               ProfileItemBuilder(jsonobject),
               new CloudConfigMetaDto(on23(jsonobject1, "userId"), on23(jsonobject1, "nickname")),
               new CloudTagDto(on23(jsonobject2, "primarySource")),
               Math.max(0L, on23(jsonobject, "likeCount", 0L)),
               NbtEditor(jsonobject, "liked")
            )
         );
      }

      Integer integer = var0.has("nextOffset") ? on23(var0, "nextOffset", 0) : null;
      return new CloudConfigsPageDto(on23(var0, "scope"), on23(var0, "offset", 0), List.copyOf(arraylist), NbtEditor(var0, "hasMore"), integer);
   }

   public static CloudSessionsDto PotionItemBuilder(JsonObject var0) {
      ArrayList arraylist = new ArrayList();

      for (JsonElement jsonelement : ItemSpec(var0, "codes")) {
         JsonObject jsonobject = on23(jsonelement, "config access code");
         arraylist.add(
            new CloudEntitlementsDto(
               SimpleItemBuilder(jsonobject, "codeId"),
               SimpleItemBuilder(jsonobject, "configId"),
               on23(jsonobject, "code"),
               on23(jsonobject, "status"),
               on23(jsonobject, "createdAt", 0L),
               NbtItemSpec(jsonobject, "redeemedByUserId"),
               EnchantItemSpec(jsonobject, "redeemedAt"),
               EnchantItemSpec(jsonobject, "revokedAt")
            )
         );
      }

      return new CloudSessionsDto(SimpleItemBuilder(var0, "configId"), List.copyOf(arraylist));
   }

   public static CloudUserDto ProfileItemBuilder(JsonObject var0) {
      Long olong = var0.has("readyAt") ? on23(var0, "readyAt", 0L) : null;
      JsonObject jsonobject = ItemRegistry(var0, "preview");
      return new CloudUserDto(
         SimpleItemBuilder(var0, "configId"),
         on23(var0, "ownerUserId"),
         on23(var0, "name"),
         on23(var0, "serverAddress"),
         on23(var0, "sizeBytes", -1L),
         on23(var0, "sha256"),
         on23(var0, "visibility"),
         on23(var0, "status"),
         on23(var0, "version", -1L),
         on23(var0, "fileName"),
         on23(var0, "createdAt", 0L),
         olong,
         on23(var0, "updatedAt", 0L),
         NbtItemSpec(var0, "description"),
         jsonobject == null ? null : new CloudSessionExtDto(StringCodec(jsonobject), on23(jsonobject, "sha256"), on23(jsonobject, "sizeBytes", 0L))
      );
   }

   public static CloudSessionDto StringCodec(JsonObject var0) {
      return new CloudSessionDto(on23(var0, "method"), on23(var0, "url"), FileLogger(ColorAnimator(var0, "headers")), on23(var0, "expiresAt", 0L));
   }

   public static Map<String, String> FileLogger(JsonObject var0) {
      HashMap hashmap = new HashMap();

      for (Entry<String, JsonElement> entry : var0.entrySet()) {
         if (!entry.getValue().isJsonPrimitive()) {
            throw TextScanner("BAD_PACKET", "HTTP ticket header must be a string");
         }

         hashmap.put(entry.getKey(), entry.getValue().getAsString());
      }

      return Map.copyOf(hashmap);
   }

   public static CloudLogsDto CloudApiClient(JsonObject var0) {
      ArrayList arraylist = new ArrayList();

      for (JsonElement jsonelement : ItemSpec(var0, "states")) {
         JsonObject jsonobject = on23(jsonelement, "state batch item");
         arraylist.add(new CloudLogEntryDto(on23(jsonobject, "userId"), on23(jsonobject, "receivedAt", 0L), ColorAnimator(jsonobject, "state")));
      }

      return new CloudLogsDto(on23(var0, "generatedAt", 0L), List.copyOf(arraylist));
   }

   public static CloudStatsDto MediaTrackInfo(JsonObject var0) {
      ArrayList arraylist = new ArrayList();

      for (JsonElement jsonelement : ItemSpec(var0, "inventories")) {
         JsonObject jsonobject = on23(jsonelement, "inventory batch item");
         arraylist.add(new CloudStatEntryDto(on23(jsonobject, "userId"), on23(jsonobject, "receivedAt", 0L), ColorAnimator(jsonobject, "inventory")));
      }

      return new CloudStatsDto(on23(var0, "generatedAt", 0L), List.copyOf(arraylist));
   }

   public static List<CloudRelationDto> on23(JsonArray var0) {
      ArrayList arraylist = new ArrayList();

      for (JsonElement jsonelement : var0) {
         arraylist.add(CloudUserProfile(on23(jsonelement, "friend request")));
      }

      return List.copyOf(arraylist);
   }

   public static CloudRelationDto CloudUserProfile(JsonObject var0) {
      return new CloudRelationDto(
         SimpleItemBuilder(var0, "requestId"),
         ModuleSnapshotDto(ColorAnimator(var0, "from")),
         ModuleSnapshotDto(ColorAnimator(var0, "to")),
         on23(var0, "status"),
         on23(var0, "createdAt", 0L)
      );
   }

   public static List<CloudBadgeDto> UiAnimation(JsonArray var0) {
      if (var0 == null) {
         throw TextScanner("BAD_PACKET", "users is missing");
      }

      ArrayList arraylist = new ArrayList();

      for (JsonElement jsonelement : var0) {
         arraylist.add(ModuleSnapshotDto(on23(jsonelement, "user")));
      }

      return List.copyOf(arraylist);
   }

   public static CloudBadgeDto ModuleSnapshotDto(JsonObject var0) {
      return new CloudBadgeDto(on23(var0, "id"), UiAnimation(var0, "nickname"), UiAnimation(var0, "role"));
   }

   public static List<String> Easing(JsonArray var0) {
      if (var0 == null) {
         return List.of();
      }

      ArrayList arraylist = new ArrayList();

      for (JsonElement jsonelement : var0) {
         if (!jsonelement.isJsonPrimitive()) {
            throw TextScanner("BAD_PACKET", "Expected a string array");
         }

         arraylist.add(jsonelement.getAsString());
      }

      return List.copyOf(arraylist);
   }

   public static JsonObject ColorAnimator(JsonObject var0, String var1) {
      JsonObject jsonobject = ItemRegistry(var0, var1);
      if (jsonobject == null) {
         throw TextScanner("BAD_PACKET", var1 + " is missing");
      } else {
         return jsonobject;
      }
   }

   public static JsonObject ItemRegistry(JsonObject var0, String var1) {
      JsonElement jsonelement = var0.get(var1);
      return jsonelement != null && jsonelement.isJsonObject() ? jsonelement.getAsJsonObject() : null;
   }

   public static JsonObject on23(JsonElement var0, String var1) {
      if (var0 != null && var0.isJsonObject()) {
         return var0.getAsJsonObject();
      } else {
         throw TextScanner("BAD_PACKET", var1 + " is not an object");
      }
   }

   public static JsonArray ItemSpec(JsonObject var0, String var1) {
      JsonArray jsonarray = TextScanner(var0, var1);
      if (jsonarray == null) {
         throw TextScanner("BAD_PACKET", var1 + " is missing");
      } else {
         return jsonarray;
      }
   }

   public static JsonArray TextScanner(JsonObject var0, String var1) {
      JsonElement jsonelement = var0.get(var1);
      return jsonelement != null && jsonelement.isJsonArray() ? jsonelement.getAsJsonArray() : null;
   }

   public static String on23(JsonObject var0, String var1) {
      String s = UiAnimation(var0, var1);
      if (s.isBlank()) {
         throw TextScanner("BAD_PACKET", var1 + " is missing");
      } else {
         return s;
      }
   }

   public static String UiAnimation(JsonObject var0, String var1) {
      JsonElement jsonelement = var0.get(var1);
      return jsonelement != null && jsonelement.isJsonPrimitive() ? jsonelement.getAsString() : "";
   }

   public static String NbtItemSpec(JsonObject var0, String var1) {
      JsonElement jsonelement = var0.get(var1);
      return jsonelement != null && !jsonelement.isJsonNull() && jsonelement.isJsonPrimitive() ? jsonelement.getAsString() : null;
   }

   public static Long EnchantItemSpec(JsonObject var0, String var1) {
      JsonElement jsonelement = var0.get(var1);
      return jsonelement != null && !jsonelement.isJsonNull() ? on23(var0, var1, 0L) : null;
   }

   public static UUID SimpleItemBuilder(JsonObject var0, String var1) {
      UUID uuid = ItemServiceBase(var0, var1);
      if (uuid == null) {
         throw TextScanner("BAD_PACKET", var1 + " must be a UUID");
      } else {
         return uuid;
      }
   }

   public static UUID ItemServiceBase(JsonObject var0, String var1) {
      JsonElement jsonelement = var0.get(var1);
      if (jsonelement != null && !jsonelement.isJsonNull()) {
         try {
            return UUID.fromString(jsonelement.getAsString());
         } catch (RuntimeException runtimeexception) {
            throw TextScanner("BAD_PACKET", var1 + " must be a UUID");
         }
      } else {
         return null;
      }
   }

   public static int on23(JsonObject var0, String var1, int var2) {
      try {
         JsonElement jsonelement = var0.get(var1);
         return jsonelement != null && jsonelement.isJsonPrimitive() ? jsonelement.getAsInt() : var2;
      } catch (RuntimeException runtimeexception) {
         throw TextScanner("BAD_PACKET", var1 + " must be an integer");
      }
   }

   public static long on23(JsonObject var0, String var1, long var2) {
      try {
         JsonElement jsonelement = var0.get(var1);
         return jsonelement != null && jsonelement.isJsonPrimitive() ? jsonelement.getAsLong() : var2;
      } catch (RuntimeException runtimeexception) {
         throw TextScanner("BAD_PACKET", var1 + " must be an integer");
      }
   }

   public static boolean NbtEditor(JsonObject var0, String var1) {
      try {
         JsonElement jsonelement = var0.get(var1);
         return jsonelement != null && jsonelement.isJsonPrimitive() && jsonelement.getAsBoolean();
      } catch (RuntimeException runtimeexception) {
         throw TextScanner("BAD_PACKET", var1 + " must be boolean");
      }
   }

   public static ServiceException TextScanner(String var0, String var1) {
      return new ServiceException(var0, var1, false);
   }

   public static String ItemSpec(Throwable var0) {
      String s = var0.getMessage();
      return s != null && !s.isBlank() ? s : var0.getClass().getSimpleName();
   }
}
