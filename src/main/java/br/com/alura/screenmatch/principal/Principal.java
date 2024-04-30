package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.GetApiKey;
import br.com.alura.screenmatch.service.ConverteDados;
import br.com.alura.screenmatch.model.Categoria;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String ombdKey = GetApiKey.getKey("OMBD_KEY");
    private final String API_KEY = "&apikey=%s".formatted(ombdKey);
    
    @Autowired
    private SerieRepository repositorio;

    private List<Serie> series = new ArrayList<>();

    public Principal(SerieRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void exibeMenu() {
        var opcao = -1;
        while (opcao != 0) {
            System.out.println();
            var menu = """
                    1 - Buscar séries
                    2 - Buscar episódios
                    3 - Listar séries buscadas
                    4 - Buscar série por título
                    5 - Buscar Série por ator
                    6 - Top 5 Séries
                    7 - Buscar Séries por Categoria

                    0 - Sair
                    """;

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarSeriePorAtor();
                    break;
                case 6:
                    buscarTop5Series();
                    break;
                case 7:
                    buscarSeriesPorCategoria();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }

        }
    }


    private void buscarTop5Series() {
        List<Serie> listaSeriesTop = repositorio.findTop5ByOrderByAvaliacaoDesc();

        listaSeriesTop.forEach(s -> System.out.println(
            "Nome: %s; Avaliação: %.2f".formatted(s.getTitulo(), s.getAvaliacao())
        ));
    }

    private void buscarSeriePorAtor() {
        System.out.println("Escreva o nome de um ator: ");
        var nomeAtor = leitura.nextLine();

        System.out.println("Avaliações a partir de qual valor? ");
        var avaliacao = leitura.nextDouble();

        List<Serie> listaSeriesPorAtor = repositorio.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);

        listaSeriesPorAtor
        .forEach(s -> System.out.println(
            "Nome: %s; Avaliação: %.2f".formatted(s.getTitulo(), s.getAvaliacao())
        ));
    }

    private void buscarSeriePorTitulo() {
        System.out.println("Escolha uma série pelo nome: ");
        var nomeSerie = leitura.nextLine();

        Optional<Serie> serieBuscada = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if(serieBuscada.isPresent()) {
            System.out.println("Série encontrada: ");
            System.out.println(serieBuscada.get());
        } else {
            System.out.println("Série não encontrada!");
        }
    }

    private void listarSeriesBuscadas() {
        
        series = repositorio.findAll();

        series.stream().sorted(Comparator.comparing(Serie::getGenero)).forEach(System.out::println);;
    }

    private void buscarSerieWeb() {
        try {
            DadosSerie dados = getDadosSerie();
            Serie serie = new Serie(dados);
            repositorio.save(serie);
            
        } catch (Exception e) {
            System.out.println("Série não encontrada!");
            e.printStackTrace();
        }
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie() {
        listarSeriesBuscadas();

        System.out.println("Escolha uma série pelo nome: ");
        var nomeSerie = leitura.nextLine();

        Optional<Serie> serie = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if(serie.isPresent()) {
            var serieEncontrada = serie.get();

            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }

            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                .flatMap(d -> d.episodios()
                        .stream()
                        .map(e -> new Episodio(d.numero(), e)))
                .collect(Collectors.toList());
            
            serieEncontrada.setEpisodio(episodios);
            
            repositorio.save(serieEncontrada);


        } else {
            System.out.println("Série não encontrada!");
        }


    }

    private void buscarSeriesPorCategoria() {
        System.out.println("Deseja buscar séries de que categoria / genero? ");
        var nomeGenero = leitura.next();

        Categoria categoria = Categoria.fromPortugues(nomeGenero);

        List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria);
        System.out.println("Séries da categoria %s: ".formatted(nomeGenero));

        seriesPorCategoria.forEach(System.out::println);

    }

}