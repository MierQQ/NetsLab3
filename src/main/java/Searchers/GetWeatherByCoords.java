package Searchers;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class GetWeatherByCoords {
    public static String getWeather(double lat, double lng, String token) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.openweathermap.org/data/2.5/weather?lat=" + String.valueOf(lat) + "&lon=" + String.valueOf(lng) + "&appid=" + token + "&lang=ru&units=metric")
                .get()
                .build();
        Response response = client.newCall(request).execute();
        if (response.code() != 200) {
            throw new IOException("Response is not 200");
        }
        String result;
        try {
            Object obj = new JSONParser().parse(response.body().string());
            JSONObject json = (JSONObject) obj;
            JSONObject placeWeather = (JSONObject) json.get("main");
            result =
                    "Weather: " + (String) ((JSONObject)(((JSONArray)json.get("weather")).get(0))).get("description") + "\n" +
                    "Temp: " + (double)placeWeather.get("temp") + "\n" +
                    "Feels: " + (double)placeWeather.get("feels_like") + "\n" +
                    "Min: " + (double)placeWeather.get("temp_min") + "\n" +
                    "Max: " + (double)placeWeather.get("temp_max") + "\n" +
                    "Pressure: " + (long)placeWeather.get("pressure") + "\n" +
                    "Humidity: " + (long)placeWeather.get("humidity");
        } catch (ParseException e) {
            throw new IOException(e);
        }
        return result;
    }
}
