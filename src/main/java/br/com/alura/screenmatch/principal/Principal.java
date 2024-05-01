package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.model.SerieEpisodioProjection;
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
    private Optional<Serie> serieBusca;

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
                    8 - Busca por quantidade temporada
                    9 - Busca por trecho de nome
                    10 - Quantidade de ep por série
                    11 - Top episódios por série
                    12 - Buscar episódios a partir de um ano

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
                case 8:
                    buscarSeriesPorNumeroTemporadaEAvaliacao();
                    break;
                case 9:
                    buscarEpisodioPorTrecho();
                    break;
                case 10:
                    buscarQuantidadeEpPorSerie();
                    break;
                case 11:
                    topEpPorSerie();
                    break;
                case 12:
                    buscarEpisodiosAPartirDeUmaData();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }

        }
    }

    private void buscarSerieWeb() {
        try {
            System.out.println("Digite o nome da série para busca");
            var nomeSerie = leitura.nextLine();
            var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
            DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
            Serie serie = new Serie(dados);
            repositorio.save(serie);
        } catch (Exception e) {
            System.out.println("Série não encontrada!");
            e.printStackTrace();
        }
    }

    private void buscarEpisodioPorSerie() {
        listarSeriesBuscadas();

        System.out.println("Escolha uma série pelo nome: ");
        var nomeSerie = leitura.nextLine();

        Optional<Serie> serie = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (serie.isPresent()) {
            var serieEncontrada = serie.get();

            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(
                        ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
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

    private void listarSeriesBuscadas() {
        series = repositorio.findAll();
        series.stream().sorted(Comparator.comparing(Serie::getGenero)).forEach(System.out::println);
    }

    private void buscarSeriePorTitulo() {
        System.out.println("Escolha uma série pelo nome: ");
        var nomeSerie = leitura.nextLine();
        List<Serie> listaSeriesEncontradas = repositorio.findAllByTituloContainingIgnoreCase(nomeSerie);

        if (listaSeriesEncontradas.size() > 1) {
            System.out.println("Existe mais de uma série com nomes parecidos no banco, escolha uma: ");
            listaSeriesEncontradas.forEach(s -> System.out.println(s.getTitulo()));
            var novoNomeSerie = leitura.nextLine();
            serieBusca = repositorio.findByTitulo(novoNomeSerie);
        } else {
            serieBusca = repositorio.findByTituloContainingIgnoreCase(nomeSerie);
        }

        if (serieBusca.isPresent()) {
            System.out.println("Série encontrada: " + serieBusca.get().getTitulo());
            System.out.println();
        } else {
            System.out.println("Série não encontrada!");
        }
    }

    private void buscarSeriePorAtor() {
        System.out.println("Escreva o nome de um ator: ");
        var nomeAtor = leitura.nextLine();

        System.out.println("Avaliações a partir de qual valor? ");
        var avaliacao = leitura.nextDouble();

        List<Serie> listaSeriesPorAtor = repositorio
                .findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);

        listaSeriesPorAtor
                .forEach(s -> System.out.println(
                        "Nome: %s; Avaliação: %.2f".formatted(s.getTitulo(), s.getAvaliacao())));
    }

    private void buscarTop5Series() {
        List<Serie> listaSeriesTop = repositorio.findTop5ByOrderByAvaliacaoDesc();

        listaSeriesTop.forEach(s -> System.out.println(
                "Nome: %s; Avaliação: %.2f".formatted(s.getTitulo(), s.getAvaliacao())));
    }

    private void buscarSeriesPorCategoria() {
        System.out.println("Deseja buscar séries de que categoria / genero? ");
        var nomeGenero = leitura.next();

        Categoria categoria = Categoria.fromPortugues(nomeGenero);

        List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria);
        System.out.println("Séries da categoria %s: ".formatted(nomeGenero));

        seriesPorCategoria.forEach(System.out::println);

    }

    private void buscarSeriesPorNumeroTemporadaEAvaliacao() {
        System.out.println("Digite o número máximo de temporadas da série que deseja ver: ");
        var numeroMaxTemporadas = leitura.nextInt();

        System.out.println("Digite a nota mínima da série que deseja ver: ");
        var notaMinima = leitura.nextDouble();

        List<Serie> listaSeries = repositorio.buscaPorTotalTemporadasEAvaliacao(numeroMaxTemporadas, notaMinima);

        listaSeries.forEach(System.out::println);
    }

    private void buscarEpisodioPorTrecho() {
        System.out.println("Escreva o nome do episódio para busca: ");
        var trechoEpisodio = leitura.nextLine();

        List<Episodio> episodiosEncontrados = repositorio.episodiosPorTrecho(trechoEpisodio);

        episodiosEncontrados.forEach(System.out::println);
    }

    private void buscarQuantidadeEpPorSerie() {
        System.out.println("Quantidade de episodios por série: ");
        List<SerieEpisodioProjection> listaEp = repositorio.quantidadeEpisodiosPorSerie();

        listaEp.forEach(
                r -> System.out.println("Nome da Série: %s; Quantidade %d".formatted(r.titulo(), r.quantidadeEp())));
    }

    private void topEpPorSerie() {
        buscarSeriePorTitulo();

        if (serieBusca.isPresent()) {
            Serie serie = serieBusca.get();
            List<Episodio> topEpisodios = repositorio.topEpisodiosPorSerie(serie);
            topEpisodios.forEach(e -> System.out.println(
                    "Temporada: %d ;Nome do episodio: %s; Número do episódio %d; Avaliação: %.2f"
                            .formatted(e.getTemporada(), e.getTitulo(), e.getNumeroEpisodio(), e.getAvaliacao())));
        }
    }

    private void buscarEpisodiosAPartirDeUmaData() {
        buscarSeriePorTitulo();
        if(serieBusca.isPresent()) {
            System.out.println("Digite o ano mínimo de limite de lançamento: ");
            int anoLancamento = leitura.nextInt();
            leitura.nextLine();

            Serie serie = serieBusca.get();
            
            List<Episodio> listaEpisodiosData = repositorio.episodiosPorSerieEAno(serie, anoLancamento);
            listaEpisodiosData.forEach(System.out::println);

        }
    }
}
