package br.com.alura.screenmatch.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.alura.screenmatch.DTO.SerieDTO;
import br.com.alura.screenmatch.repository.SerieRepository;

@Service
public class SerieService {
    @Autowired
    private SerieRepository serieRepositorio;

    public List<SerieDTO> obterTodasAsSeries() {
        return serieRepositorio.findAll()
                .stream()
                .map(s -> new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getAvaliacao(),
                        s.getGenero(), s.getAtores(), s.getPoster(), s.getSiponse()))
                .collect(Collectors.toList());
    }

    public List<SerieDTO> obterTop5Series() {
        return serieRepositorio.findTop5ByOrderByAvaliacaoDesc().stream()
                .map(s -> new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getAvaliacao(),
                        s.getGenero(), s.getAtores(), s.getPoster(), s.getSiponse()))
                .collect(Collectors.toList());

    }
}