package Searchers;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class SearchSightsData {
    public static SightInfo search(String xid, String token) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.opentripmap.com/0.1/ru/places/xid/" + xid + "?apikey=" + token)
                .get()
                .build();
        var responseFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return client.newCall(request).execute();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            return null;
        }, ThreadPool.getInstance().getPool());
        Response response = null;
        try {
            response = responseFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        if (response == null) {
            throw new IOException();
        }

        if (response.code() != 200) {
            System.out.println(response.body().string());
            throw new IOException("Response is not 200");
        }
        SightInfo result = new SightInfo();
        try {
            Object obj = new JSONParser().parse(Objects.requireNonNull(response.body()).string());
            result.json = obj;
            result.xid = (String) ((JSONObject) obj).get("xid");
            result.info = ((JSONObject) obj).get("info") == null? "Нет описания" : (String) ((JSONObject) ((JSONObject) obj).get("info")).get("descr");
            result.name = ((JSONObject) obj).get("name").equals("") ? "Без имени" : (String) ((JSONObject) obj).get("name");
            result.kinds = (String) ((JSONObject) obj).get("kinds");
            result.kinds = result.kinds.replace(",", ", ").replace("_", " ");
            result.lat = (Double) ((JSONObject) ((JSONObject) obj).get("point")).get("lat");
            result.lng = (Double) ((JSONObject) ((JSONObject) obj).get("point")).get("lon");
            JSONObject address = (JSONObject) ((JSONObject) obj).get("address");

            if (address != null) {
                result.address = address.get("country") == null || address.get("country").equals("") ? "" : address.get("country") + ", ";
                result.address += address.get("state") == null || address.get("state").equals("") ? "" : address.get("state") + ", ";
                result.address += address.get("county") == null || address.get("county").equals("") ? "" : address.get("county") + ", ";
                result.address += address.get("city") == null || address.get("city").equals("") ? "" : address.get("city") + ", ";
                result.address += address.get("city_district") == null || address.get("city_district").equals("") ? "" : (String) address.get("city_district") + ", ";
                result.address += address.get("road") == null || address.get("road").equals("") ? "" : address.get("road") + ", ";
                result.address += address.get("house_number") == null || address.get("house_number").equals("") ? "" : address.get("house_number") + ", ";
                result.address += address.get("house") == null || address.get("house").equals("") ? "" : address.get("house") + ", ";
                result.address += address.get("postcode") == null || address.get("postcode").equals("") ? "" : address.get("postcode");
            }

        } catch (ParseException e) {
            throw new IOException(e);
        }
        return result;
    }
}
