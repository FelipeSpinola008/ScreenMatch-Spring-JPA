package com.felipe.screenmatch.main;

import com.felipe.screenmatch.model.Episode;
import com.felipe.screenmatch.model.EpisodesData;
import com.felipe.screenmatch.model.SeasonData;
import com.felipe.screenmatch.model.SeriesData;
import com.felipe.screenmatch.service.ApiConsumer;
import com.felipe.screenmatch.service.DataConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    private Scanner scanner = new Scanner(System.in);
    private ApiConsumer apiConsumer = new ApiConsumer();
    private DataConverter converter = new DataConverter();

    private final String ADRESS = "http://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=3a8927ac";

    public void displayMenu() {
        System.out.println("Digite o nome da série para busca:");
        var serieName = scanner.nextLine();
        var json = apiConsumer.getData(ADRESS + serieName.replace(" ", "+") + API_KEY);
        SeriesData seriesData = converter.getData(json, SeriesData.class);
        System.out.println(seriesData);

        List<SeasonData> seasons = new ArrayList<>();

		for(int i = 1; i <=seriesData.totalSeasons(); i++) {
			json = apiConsumer.getData(ADRESS + serieName.replace(" ", "+") + "&Season=" + i + API_KEY);
			SeasonData seasonData = converter.getData(json, SeasonData.class);
			seasons.add(seasonData);
		}
		seasons.forEach(System.out::println);

//        for (int i = 0; i < seriesData.totalSeasons(); i++){
//            List<EpisodesData > episodesSeason = seasons.get(i).episodes();
//            for (int j = 0; j < episodesSeason.size(); j++) {
//                System.out.println(episodesSeason.get(j).title());
//            }
//        }
        seasons.forEach(s -> s.episodes().forEach(e -> System.out.println(e.title())));

        List<EpisodesData> episodesData = seasons.stream()
                .flatMap(s -> s.episodes().stream())
                .collect(Collectors.toList());

//        System.out.println("\nTop 10 episódios");
//        episodesData.stream()
//                .filter(e -> !e.rating().equalsIgnoreCase("N/A"))
//                .peek(e -> System.out.println("Primeiro filtro (N/A): " + e))
//                .sorted(Comparator.comparing(EpisodesData::rating).reversed())
//                .peek(e -> System.out.println("Ordenação: " + e))
//                .limit(10)
//                .peek(e -> System.out.println("limite: " + e))
//                .map(e -> e.title().toUpperCase())
//                .peek(e -> System.out.println("Mapeamento: " + e))
//                .forEach(System.out::println);


        List<Episode> episodes = seasons.stream()
                .flatMap(s -> s.episodes().stream()
                        .map(d -> new Episode(s.season(), d))).collect(Collectors.toList());

        episodes.forEach(System.out::println);

//        System.out.println("Digite o nome do episódio que deseja procurar: ");
//        var excerptTitle = scanner.nextLine();
//        Optional<Episode> searchedEpisode = episodes.stream()
//                .filter(e -> e.getTitle().toUpperCase().contains(excerptTitle.toUpperCase()))
//                .findFirst();
//        if (searchedEpisode.isPresent()) {
//            System.out.println("Episódio encontrado: ");
//            System.out.println("Temporada: "+ searchedEpisode.get().getSeason());
//        } else {
//            System.out.println("Episódio não encontrado");
//        }

//
//        System.out.println("A partir de que ano você deseja ver os episódios?");
//        var year = scanner.nextInt();
//        scanner.nextLine();
//
//        LocalDate searchDate = LocalDate.of(year, 1, 1);
//
//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//        episodes.stream()
//                .filter(e -> e.getReleaseDate() != null && e.getReleaseDate().isAfter(searchDate))
//                .forEach(e -> System.out.println(
//                        "Temporada: " + e.getSeason() +
//                                " Episodio: " + e.getTitle() +
//                                " Data de lançamento: " + e.getReleaseDate().format(formatter)
//
//                ));



        Map<Integer, Double> seasonRating = episodes.stream()
                .filter(e -> e.getRating() > 0.0)
                .collect(Collectors.groupingBy(Episode::getSeason,
                        Collectors.averagingDouble(Episode::getRating)));
        System.out.println(seasonRating);

        DoubleSummaryStatistics est = episodes.stream()
                .filter(e -> e.getRating() > 0.0)
                .collect(Collectors.summarizingDouble(Episode::getRating));
        System.out.println("Média: " + est.getAverage());
        System.out.println("Melhor Episódio: " + est.getMax());
        System.out.println("Pior Episódio: " + est.getMin());
        System.out.println("Quantidade: " + est.getCount());
    }
}
