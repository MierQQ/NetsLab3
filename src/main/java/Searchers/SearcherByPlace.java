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

public class SearcherByPlace {
    public static List<PlaceInfo> search(String str, String token) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://graphhopper.com/api/1/geocode?q=" + str + "&locale=en&debug=false&key=" + token)
                .get()
                .build();
        Response response = client.newCall(request).execute();
        if (response.code() != 200) {
            throw new IOException("Response is not 200");
        }
        List<PlaceInfo> result = new LinkedList<PlaceInfo>();

        try {
            Object obj = new JSONParser().parse(response.body().string());
            JSONObject json = (JSONObject) obj;
            JSONArray places = (JSONArray) json.get("hits");
            for (var it : places) {
                PlaceInfo hit = new PlaceInfo();
                hit.json = it;
                hit.lat = (Double)(((JSONObject)((JSONObject)it).get("point"))).get("lat");
                hit.lng = (Double)(((JSONObject)((JSONObject)it).get("point"))).get("lng");
                hit.str = (((String)((JSONObject)it).get("name")) != null? ((String)((JSONObject)it).get("name")): "");
                hit.str += (((String)((JSONObject)it).get("country")) != null? ", " + ((String)((JSONObject)it).get("country")): "");
                hit.str += (((String)((JSONObject)it).get("state")) != null? ", " + ((String)((JSONObject)it).get("state")): "");
                hit.str += (((String)((JSONObject)it).get("street")) != null? ", " + ((String)((JSONObject)it).get("street")): "");
                hit.str += (((String)((JSONObject)it).get("housenumber")) != null? ", " + ((String)((JSONObject)it).get("housenumber")): "");
                hit.str += (((String)((JSONObject)it).get("postcode")) != null? ", " + ((String)((JSONObject)it).get("postcode")): "");
                result.add(hit);
            }
        } catch (ParseException e) {
            throw new IOException(e);
        }
        return result;
    }
}
