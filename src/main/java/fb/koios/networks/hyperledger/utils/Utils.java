package fb.koios.networks.hyperledger.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import java.nio.charset.StandardCharsets;


public class Utils {
    public static String prettyJson(final byte[] json) {
        return prettyJson(new String(json, StandardCharsets.UTF_8));
    }

    private static String prettyJson(final String json) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        var parsedJson = JsonParser.parseString(json);
        return gson.toJson(parsedJson);
    }
}
