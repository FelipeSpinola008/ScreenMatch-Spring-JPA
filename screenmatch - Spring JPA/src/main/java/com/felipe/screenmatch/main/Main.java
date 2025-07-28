package com.felipe.screenmatch.main;

import com.felipe.screenmatch.model.Episode;
import com.felipe.screenmatch.model.SeasonData;
import com.felipe.screenmatch.model.Serie;
import com.felipe.screenmatch.model.SeriesData;
import com.felipe.screenmatch.repository.SerieRepository;
import com.felipe.screenmatch.service.ApiConsumer;
import com.felipe.screenmatch.service.DataConverter;

import java.util.*;
import java.util.stream.Collectors;

public class Main {

    private Scanner scanner = new Scanner(System.in);
    private ApiConsumer apiConsumer = new ApiConsumer();
    private DataConverter converter = new DataConverter();

    private final String ADRESS = "http://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=3a8927ac";

    private List<SeriesData> seriesData = new ArrayList<>();
    private SerieRepository repository;

    private List<Serie> series = new ArrayList<>();

    public Main(SerieRepository repository) {
        this.repository = repository;
    }

    public void displayMenu() {
        var option = -1;
        while (option != 0) {
            try {
                var menu = """
                        1 - Buscar séries
                        2 - Buscar episódios
                        3 - Listar séries buscadas
                        
                        0 - Sair
                        """;

                System.out.println(menu);
                option = scanner.nextInt();
                scanner.nextLine();

                switch (option) {
                    case 1:
                        searchSerieWeb();
                        break;
                    case 2:
                        searchSerieEpisode();
                        break;
                    case 3:
                        listSearchedSeries();
                        break;
                    case 0:
                        System.out.println("Saindo...");
                        break;
                    default:
                        System.out.println("Opção inválida");
                }

            } catch (InputMismatchException e) {
                System.out.println("\nNão consegui entender, digite um número válido:\n");
                scanner.nextLine();
            }
        }
    }
    private void searchSerieWeb() {
        SeriesData data = getSeriesData();
        //seriesData.add(data);
        Serie serie = new Serie(data);
        repository.save(serie);
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
        listSearchedSeries();
        System.out.println("Escolha uma série pelo nome: ");
        var serieName = scanner.nextLine();
        Optional<Serie> serie = series.stream()
                .filter(s -> s.getTitle().toLowerCase().contains(serieName.toLowerCase()))
                .findFirst();
        if (serie.isPresent()) {

            var foundSerie = serie.get();
            List<SeasonData> seasons = new ArrayList<>();
            for (int i = 1; i <= foundSerie.getTotalSeasons(); i++) {
                var json =  apiConsumer.getData(ADRESS + foundSerie.getTitle().replace(" ", "+") + "&season=" + i + API_KEY);
                SeasonData seasonData = converter.getData(json, SeasonData.class);
                seasons.add(seasonData);
                seasons.forEach(System.out::println);

                List<Episode> episodes = seasons.stream()
                        .flatMap(d -> d.episodes().stream()
                                .map(e -> new Episode(e.numero(), e)))
                        .collect(Collectors.toList());
                foundSerie.setEpisodes(episodes);
                repository.save(foundSerie);
            }
        } else {
            System.out.println("série não encontrada!");
        }

    }
    private void listSearchedSeries() {
        series =  repository.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenre))
                .forEach(System.out::println);
    }
}
