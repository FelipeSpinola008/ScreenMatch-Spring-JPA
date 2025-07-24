package com.felipe.screenmatch.main;

import com.felipe.screenmatch.model.SeasonData;
import com.felipe.screenmatch.model.SeriesData;
import com.felipe.screenmatch.service.ApiConsumer;
import com.felipe.screenmatch.service.DataConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private Scanner scanner = new Scanner(System.in);
    private ApiConsumer apiConsumer = new ApiConsumer();
    private DataConverter converter = new DataConverter();

    private final String ADRESS = "http://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=3a8927ac";

    public void displayMenu() {
        var menu = """
                1 - Buscar séries
                2 - Buscar episódios
                
                0 - Sair
                """;

        System.out.println(menu);
        var option = scanner.nextInt();
        scanner.nextLine();

        switch (option) {
            case 1:
                searchSerieWeb();
                break;
            case 2:
                searchSerieEpisode();
                break;
            case 0:
                System.out.println("Saindo...");
                break;
            default:
                System.out.println("Opção inválida");
        }
    }
    private void searchSerieWeb() {
        SeriesData data = getSeriesData();
        System.out.println(data);

    }

    private SeriesData getSeriesData() {
        System.out.println("Digite o nome da série para busca: ");
        var serieName = scanner.nextLine();
        var json = apiConsumer.getData(ADRESS + serieName.replace(" ", "+") + API_KEY);
        SeriesData data = converter.getData(json, SeriesData.class);
        return data;
    }

    private void searchSerieEpisode() {
        SeriesData seriesData = getSeriesData();
        List<SeasonData> seasons = new ArrayList<>();

        for (int i = 1; i <= seriesData.totalSeasons(); i++) {
            var json =  apiConsumer.getData(ADRESS + seriesData.title().replace(" ", "+") + "&season=" + i + API_KEY);
            SeasonData seasonData = converter.getData(json, SeasonData.class);
            seasons.add(seasonData);
        }
        seasons.forEach(System.out::println);
    }
}
