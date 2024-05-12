package br.com.alura.screenmatch.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.alura.screenmatch.service.SerieService;
import br.com.alura.screenmatch.DTO.EpisodioDTO;
import br.com.alura.screenmatch.DTO.SerieDTO;
import br.com.alura.screenmatch.model.Categoria;

@RestController
@RequestMapping("/series")
public class SerieCotroller {

    @Autowired
    private SerieService serieServico;

    @GetMapping
    public List<SerieDTO> obterSeries() {
        return serieServico.obterTodasAsSeries();
    }

    @GetMapping("/top5")
    public List<SerieDTO> seriesTop5() {
        return serieServico.obterTop5Series();
    }

    @GetMapping("lancamentos")
    public List<SerieDTO> seriesMaisRecentes() {
        return serieServico.obterSeriesMaisRecentes();
    }

    @GetMapping("/{id}")
    public SerieDTO obterPorId(@PathVariable Long id) {
        return serieServico.obterPorId(id);
    }

    @GetMapping("/{id}/temporadas/todas")
    public List<EpisodioDTO> obterTodasAsTemporadas(@PathVariable Long id) {
        return serieServico.obterTodasAsTemporadasPorId(id);
    }

    @GetMapping("/{id}/temporadas/{numeroTemporada}")
    public List<EpisodioDTO> obterEpisodiosPorTemporada(@PathVariable Long id, @PathVariable int numeroTemporada) {
        return serieServico.obterEpisodiosPorNumero(id, numeroTemporada);
    }

    @GetMapping("/categoria/{nomeCategoria}")
    public List<SerieDTO> obterSeriesPorCategoria(@PathVariable String nomeCategoria) {
        return serieServico.obterSeriesPorCategoria(Categoria.fromPortugues(nomeCategoria));
    }

    @GetMapping("/{id}/temporadas/top")
    public List<EpisodioDTO> obterTop5EpisodiosPorSeriePorId(@PathVariable Long id) {
        return serieServico.obterTop5EpisodiosPorSeriePorId(id);
    }
}