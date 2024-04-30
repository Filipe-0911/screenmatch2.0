package br.com.alura.screenmatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.Serie;

import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long>{
    Optional<Serie> findByTituloContainingIgnoreCase(String nomeSerie);
    List<Serie> findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(String nomeAtor, Double avaliacao);
    List<Serie> findTop5ByOrderByAvaliacaoDesc();
    List<Serie> findByGenero(Categoria categoria);
}
