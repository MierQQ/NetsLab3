import Searchers.*;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class App {
    static public void main(String[] args) {
        int radius = 1000;
        try (Scanner input = new Scanner(System.in)){
            System.out.println("Enter search request:");
            String searchStr = input.nextLine();
            int i = 0;
            List<PlaceInfo> listOfPlaces = SearcherByPlace.search(searchStr, "41ee9e3f-88ea-4f30-81af-e56da4e631eb");
            if (listOfPlaces.size() == 0) {
                System.out.println("No such places");
                return;
            }
            System.out.println("Possible places:");
            for(var it: listOfPlaces){
                System.out.println(i++ + "---------------------" );
                System.out.println(it.lat + "\t" + it.lng);
                System.out.println(it.str);
            }
            System.out.println("----------------------\nEnter number of place");

            int placeNumber = input.nextInt();
            while (listOfPlaces.size() <= placeNumber || placeNumber < 0) {
                System.out.println("Number of place is not permitted, enter again");
                placeNumber = input.nextInt();
            }
            System.out.println("----------------------");
            int finalPlaceNumber = placeNumber;
            CompletableFuture<String> weatherFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    return GetWeatherByCoords.getWeather(listOfPlaces.get(finalPlaceNumber).lat, listOfPlaces.get(finalPlaceNumber).lng, "ea73f732ee7e6a674ad4dec7a198739d");
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
                return null;
            });

            var sightsFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    return SearchSightsByCoords.search(listOfPlaces.get(finalPlaceNumber).lat, listOfPlaces.get(finalPlaceNumber).lng, radius, "5ae2e3f221c38a28845f05b6e34ca70daf257c8fe3614b0410e8dc3e");
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
                return null;
            });


            String weather = weatherFuture.get();
            var sights = sightsFuture.get();

            if (weather == null || sights == null) {
                throw new IOException();
            }

            System.out.println(weather);

            System.out.println("----------------------\nSights:");
            i = 0;

            for(var it: sights){
                SightInfo sight = it.get();
                if (sight == null) {
                    continue;
                }
                System.out.println(i++ + "---------------------" );
                System.out.println("lat: " + sight.lat + "\t lng: " + sight.lng);
                System.out.println("Name: " + sight.name);
                System.out.println("Address: " + sight.address);
                System.out.println("Kind of place: " + sight.kinds);
                System.out.println("info: " + sight.info);

            }
            System.out.println("----------------------");

        } catch (IOException | InterruptedException | ExecutionException exception) {
            exception.printStackTrace();
        }
        ThreadPool.getInstance().getPool().shutdown();
    }
}
