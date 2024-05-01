package br.com.alura.screenmatch.model;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "episodios")
public class Episodio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Integer temporada;
    private String titulo;
    private Integer numeroEpisodio;
    private Double avaliacao;
    private LocalDate dataLancamento;

    @ManyToOne
    private Serie serie;

    public Episodio() {}
    
    public Episodio(Integer numeroTemporada, DadosEpisodio dadosEpisodio) {
        this.temporada = numeroTemporada;
        this.titulo = dadosEpisodio.titulo();
        this.numeroEpisodio = dadosEpisodio.numero();
        
        try {
            this.avaliacao = Double.valueOf(dadosEpisodio.avaliacao());
        } catch (NumberFormatException ex) {
            this.avaliacao = 0.0;
        }
        
        try {
            this.dataLancamento = LocalDate.parse(dadosEpisodio.dataLancamento());
        } catch (DateTimeParseException ex) {
            this.dataLancamento = null;
        }
    }
    
    public Integer getTemporada() {
        return temporada;
    }
    
    public void setTemporada(Integer temporada) {
        this.temporada = temporada;
    }
    
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    public Integer getNumeroEpisodio() {
        return numeroEpisodio;
    }
    
    public void setNumeroEpisodio(Integer numeroEpisodio) {
        this.numeroEpisodio = numeroEpisodio;
    }

    public Double getAvaliacao() {
        return avaliacao;
    }
    
    public void setAvaliacao(Double avaliacao) {
        this.avaliacao = avaliacao;
    }
    
    public LocalDate getDataLancamento() {
        return dataLancamento;
    }
    
    public void setDataLancamento(LocalDate dataLancamento) {
        this.dataLancamento = dataLancamento;
    }
    
    public Serie getSerie() {
        return serie;
    }
    
    public void setSerie(Serie serie) {
        this.serie = serie;
    }
    
    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        String frase;
        if(this.dataLancamento != null) {
            Date date = Date.valueOf(this.dataLancamento);
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyy");
            String data = formatter.format(date);
    
            frase = "Temporada: %d; Titulo: %s; Numero episódio: %d; Avaliação: %.2f; Data lançamento: %s"
            .formatted(this.temporada, this.titulo, this.numeroEpisodio, this.avaliacao, data);

        } else {
            frase = "Temporada: %d; Titulo: %s; Numero episódio: %d; Avaliação: %.2f; Data lançamento: %s"
            .formatted(this.temporada, this.titulo, this.numeroEpisodio, this.avaliacao, this.dataLancamento);
        }

        return frase;
    }
}
