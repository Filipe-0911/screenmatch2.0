package br.com.alura.screenmatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.model.SerieEpisodioProjection;

import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long>{
    Optional<Serie> findByTituloContainingIgnoreCase(String nomeSerie);

    Optional<Serie> findByTitulo(String novoNomeSerie);

    List<Serie> findAllByTituloContainingIgnoreCase(String nomeSerie);
    List<Serie> findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(String nomeAtor, Double avaliacao);
    List<Serie> findTop5ByOrderByAvaliacaoDesc();
    List<Serie> findByGenero(Categoria categoria);
    List<Serie> findByTotalTemporadasLessThanEqualAndAvaliacaoGreaterThanEqual(int totalTemporadas, Double avaliacao);
    
    // JPQL
    @Query("select s from Serie s where s.totalTemporadas <= :totalTemporadas and s.avaliacao >= :avaliacao")
    List<Serie> buscaPorTotalTemporadasEAvaliacao(int totalTemporadas, Double avaliacao);

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE e.titulo ILIKE %:trechoEpisodio%")
    List<Episodio> episodiosPorTrecho(String trechoEpisodio);

    // Para consultas mais avançadas no banco, deve ser criada uma classe/record com os dados
    // que serão retornados do banco para que a JPA saiba como mapear o dado retornado.
    // Neste caso decidi criar um record para que já tenha os metodos getter embutidos por padrão.
    // Ao fazer a query, deve ser instanciado a classe/record com todo o caminho das pastas para que a jpa
    // saiba como encontrar a classe/record.
    @Query("select new br.com.alura.screenmatch.model.SerieEpisodioProjection(s.titulo, count(e)) from Serie s join s.episodios e group by s.titulo order by (count(e)) desc")
    List<SerieEpisodioProjection> quantidadeEpisodiosPorSerie();

    @Query("SELECT e FROM Episodio e INNER JOIN e.serie s WHERE s.titulo = :nomeSerie")
    List<Episodio> buscaEpisodiosQueASeriePossuiNoBanco(String nomeSerie);

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s = :serie order by e.avaliacao DESC LIMIT 5")
    List<Episodio> topEpisodiosPorSerie(Serie serie);

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s = :serie AND YEAR(e.dataLancamento) >= :anoLancamento")
    List<Episodio> episodiosPorSerieEAno(Serie serie, int anoLancamento);

    @Query("select e FROM Episodio e WHERE e.titulo = :nomeEpisodio")
    List<Optional<Episodio>> buscaEpisodioPorTitulo(String nomeEpisodio);
    
}