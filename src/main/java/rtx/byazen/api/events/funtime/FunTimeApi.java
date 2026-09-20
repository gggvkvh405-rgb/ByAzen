package rtx.byazen.api.events.funtime;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import rtx.byazen.api.events.funtime.FunTimeApiException;
import rtx.byazen.api.events.funtime.FunTimeEvent;
import rtx.byazen.api.events.funtime.FunTimeMine;
import rtx.byazen.utils.net.Endpoints;

public final class FunTimeApi {
    public static final FunTimeApi INSTANCE = new FunTimeApi();
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5L)).build();
    private volatile String baseUrl = Endpoints.funtime();

    private FunTimeApi() {
    }

    private static JsonObject parse(String string) {
        try {
            JsonElement jsonElement = JsonParser.parseString((String)(string == null ? "" : string));
            return jsonElement.isJsonObject() ? jsonElement.getAsJsonObject() : new JsonObject();
        }
        catch (RuntimeException runtimeException) {
            return new JsonObject();
        }
    }

    private static String string(JsonObject jsonObject, String string, String string2) {
        if (jsonObject == null) {
            return string2;
        }
        JsonElement jsonElement = jsonObject.get(string);
        if (jsonElement == null || !jsonElement.isJsonPrimitive()) {
            return string2;
        }
        String string3 = jsonElement.getAsString().trim();
        return string3.isEmpty() ? string2 : string3;
    }

    private static long number(JsonObject jsonObject, String string, long l) {
        if (jsonObject == null) {
            return l;
        }
        JsonElement jsonElement = jsonObject.get(string);
        if (jsonElement == null || !jsonElement.isJsonPrimitive() || !jsonElement.getAsJsonPrimitive().isNumber()) {
            return l;
        }
        return jsonElement.getAsLong();
    }

    private JsonObject request(String string) throws FunTimeApiException {
        HttpResponse<String> httpResponse;
        String string2 = this.baseUrl + string;
        HttpRequest httpRequest = HttpRequest.newBuilder(URI.create(string2)).timeout(Duration.ofSeconds(10L)).header("Accept", "application/json").GET().build();
        try {
            httpResponse = this.http.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        }
        catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            throw new FunTimeApiException(-1, "interrupted", "\u0417\u0430\u043f\u0440\u043e\u0441 \u043f\u0440\u0435\u0440\u0432\u0430\u043d");
        }
        catch (IOException iOException) {
            throw new FunTimeApiException(-1, "network", "\u0421\u0435\u0442\u044c \u043d\u0435\u0434\u043e\u0441\u0442\u0443\u043f\u043d\u0430: " + iOException.getClass().getSimpleName());
        }
        JsonObject jsonObject = FunTimeApi.parse(httpResponse.body());
        if (httpResponse.statusCode() == 200) {
            return jsonObject;
        }
        throw new FunTimeApiException(httpResponse.statusCode(), "http", "HTTP " + httpResponse.statusCode());
    }

    public List<FunTimeEvent> events() throws FunTimeApiException {
        JsonObject jsonObject = FunTimeApi.servers(this.request("events"));
        ArrayList<FunTimeEvent> arrayList = new ArrayList<FunTimeEvent>();
        for (Map.Entry entry : jsonObject.entrySet()) {
            int n = FunTimeApi.anarchyOf((String)entry.getKey());
            if (!((JsonElement)entry.getValue()).isJsonArray()) continue;
            for (JsonElement jsonElement : ((JsonElement)entry.getValue()).getAsJsonArray()) {
                if (!jsonElement.isJsonObject()) continue;
                JsonObject jsonObject2 = jsonElement.getAsJsonObject();
                arrayList.add(new FunTimeEvent(FunTimeApi.string(jsonObject2, "name", "\u0421\u043e\u0431\u044b\u0442\u0438\u0435"), n, 0, FunTimeApi.string(jsonObject2, "id", ""), FunTimeApi.string(jsonObject2, "rarity", "")));
            }
        }
        return arrayList;
    }

    private static JsonObject servers(JsonObject jsonObject) {
        JsonObject jsonObject2 = jsonObject.getAsJsonObject("servers");
        return jsonObject2 == null ? new JsonObject() : jsonObject2;
    }

    public List<FunTimeMine> mines() throws FunTimeApiException {
        JsonObject jsonObject = FunTimeApi.servers(this.request("mines"));
        ArrayList<FunTimeMine> arrayList = new ArrayList<FunTimeMine>();
        for (Map.Entry entry : jsonObject.entrySet()) {
            String string = (String)entry.getKey();
            if (!((JsonElement)entry.getValue()).isJsonArray()) continue;
            for (JsonElement jsonElement : ((JsonElement)entry.getValue()).getAsJsonArray()) {
                if (!jsonElement.isJsonObject()) continue;
                JsonObject jsonObject2 = jsonElement.getAsJsonObject();
                arrayList.add(new FunTimeMine(string, FunTimeApi.serverLabel(string), FunTimeApi.string(jsonObject2, "name", ""), FunTimeApi.string(jsonObject2, "rarity", ""), "", FunTimeApi.number(jsonObject2, "refillAt", 0L)));
            }
        }
        return arrayList;
    }

    private static String serverLabel(String string) {
        if (string == null || string.isBlank()) {
            return "";
        }
        int n = FunTimeApi.anarchyOf(string);
        if (n <= 0) {
            return string;
        }
        String string2 = string.toUpperCase(Locale.ROOT);
        String string3 = string2.contains("LITE") ? "\u041b\u0430\u0439\u0442-\u0410\u043d\u0430\u0440\u0445\u0438\u044f" : "\u0410\u043d\u0430\u0440\u0445\u0438\u044f";
        String string4 = string2.contains("NEW") ? " (1.21)" : "";
        return string3 + " " + n + string4;
    }

    public void setBaseUrl(String string) {
        if (string != null && !string.isBlank()) {
            this.baseUrl = string.endsWith("/") ? string.trim() : string.trim() + "/";
        }
    }

    private static int anarchyOf(String string) {
        if (string == null) {
            return 0;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < string.length(); ++i) {
            char c = string.charAt(i);
            if (c < '0' || c > '9') continue;
            stringBuilder.append(c);
        }
        try {
            return stringBuilder.isEmpty() ? 0 : Integer.parseInt(stringBuilder.toString());
        }
        catch (NumberFormatException numberFormatException) {
            return 0;
        }
    }
}

