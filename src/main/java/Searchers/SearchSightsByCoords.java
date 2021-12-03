package Searchers;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class SearchSightsByCoords {
    public static List<CompletableFuture<SightInfo>> search(double lat, double lng, double radiusInMeters, String token) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.opentripmap.com/0.1/ru/places/radius?format=json&radius=" + radiusInMeters + "&lon=" + lng +"&lat=" + lat + "&apikey=" + token)
                .get()
                .build();
        Response response = client.newCall(request).execute();
        if (response.code() != 200) {
            throw new IOException("Response is not 200");
        }
        List<CompletableFuture<SightInfo>> result = new LinkedList<>();
        try {
            Object obj = new JSONParser().parse(Objects.requireNonNull(response.body()).string());
            JSONArray sights = (JSONArray) obj;
            for (var it : sights) {
                String xid = (String) ((JSONObject)it).get("xid");
                var sight = CompletableFuture.supplyAsync(() -> {
                    try {
                        return SearchSightsData.search(xid, token);
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                    return null;
                });
                result.add(sight);
            }
        } catch (ParseException e) {
            throw new IOException(e);
        }
        return result;
    }
}
