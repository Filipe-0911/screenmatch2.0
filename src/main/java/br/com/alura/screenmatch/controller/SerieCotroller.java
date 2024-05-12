package br.com.alura.screenmatch.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.alura.screenmatch.service.SerieService;
import br.com.alura.screenmatch.DTO.SerieDTO;

@RestController
public class SerieCotroller {

    @Autowired
    private SerieService serieServico;

    @GetMapping("/series")
    public List<SerieDTO> obterSeries() {
        return serieServico.obterTodasAsSeries();
    }

    @GetMapping("series/top5")
    public List<SerieDTO> seriesTop5() {
        return serieServico.obterTop5Series();
    }

}
