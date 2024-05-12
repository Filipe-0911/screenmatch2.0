package br.com.alura.screenmatch.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.alura.screenmatch.DTO.EpisodioDTO;
import br.com.alura.screenmatch.DTO.SerieDTO;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;

@Service
public class SerieService {
    @Autowired
    private SerieRepository serieRepositorio;

    public List<SerieDTO> obterTodasAsSeries() {
        return __converteDados(serieRepositorio.findAll());
    }

    public List<SerieDTO> obterTop5Series() {
        return __converteDados(serieRepositorio.findTop5ByOrderByAvaliacaoDesc());
    }

    public List<SerieDTO> obterSeriesMaisRecentes() {
        return __converteDados(serieRepositorio.encontrarEpisodiosMaisRecentes());
    }

    public SerieDTO obterPorId(Long id) {
        Optional<Serie> serieOpcional = serieRepositorio.findById(id);

        if (serieOpcional.isPresent()) {
            Serie s = serieOpcional.get();

            return new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getAvaliacao(),
                    s.getGenero(), s.getAtores(), s.getPoster(), s.getSiponse());

        } else {
            return null;
        }
    }

    public List<EpisodioDTO> obterTodasAsTemporadasPorId(Long id) {
        Optional<Serie> serieOpcional = serieRepositorio.findById(id);

        if (serieOpcional.isPresent()) {
            Serie s = serieOpcional.get();
            return __converteDadosEpisodio(s.getEpisodio());
            
        } else {
            return null;
        }
    }

    public List<EpisodioDTO> obterEpisodiosPorNumero(Long id, Integer numeroTemporada) {
        List<Episodio> listaEpisodios = serieRepositorio.buscaEpisodiosQueASeriePossuiNoBancoPorId(id, numeroTemporada);

        return __converteDadosEpisodio(listaEpisodios);
    }

    private List<SerieDTO> __converteDados(List<Serie> series) {
        return series.stream()
                .map(s -> new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getAvaliacao(),
                        s.getGenero(), s.getAtores(), s.getPoster(), s.getSiponse()))
                .collect(Collectors.toList());
    }

    private List<EpisodioDTO> __converteDadosEpisodio(List<Episodio> episodios) {
        return episodios.stream()
                .map(e -> new EpisodioDTO(e.getTemporada(), e.getTitulo(), e.getNumeroEpisodio()))
                .collect(Collectors.toList());
    }

}
