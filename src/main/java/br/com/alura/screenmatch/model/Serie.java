package br.com.alura.screenmatch.model;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "series")
public class Serie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String titulo;
    private Integer totalTemporadas;
    private Double avaliacao;
    @Enumerated(EnumType.STRING)
    private Categoria genero;
    private String atores;
    
    private String poster;
    private String siponse;
    
    @OneToMany(mappedBy = "serie", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Episodio> episodios = new ArrayList<>();
    
    public Serie(DadosSerie dadosSerie) {
        this.titulo = dadosSerie.titulo();
        this.totalTemporadas = dadosSerie.totalTemporadas();
        this.avaliacao = OptionalDouble.of(Double.valueOf(dadosSerie.avaliacao())).orElse(0);
        this.genero = Categoria.fromString(dadosSerie.genero().split(",")[0].trim());
        this.atores = dadosSerie.atores();
        this.poster = dadosSerie.poster();
        this.siponse = dadosSerie.sinopse();
    }
    
    public Serie() {}
    
    public String getTitulo() {
        return titulo;
    }
    
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getTotalTemporadas() {
        return totalTemporadas;
    }
    
    public void setTotalTemporadas(Integer totalTemporadas) {
        this.totalTemporadas = totalTemporadas;
    }
    
    public Double getAvaliacao() {
        return avaliacao;
    }
    
    public void setAvaliacao(Double avaliacao) {
        this.avaliacao = avaliacao;
    }
    
    public Categoria getGenero() {
        return genero;
    }
    
    public void setGenero(Categoria genero) {
        this.genero = genero;
    }
    
    public String getAtores() {
        return atores;
    }
    
    public void setAtores(String atores) {
        this.atores = atores;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }
    
    public String getSiponse() {
        return siponse;
    }
    
    public void setSiponse(String siponse) {
        this.siponse = siponse;
    }
    
    public Long getId() {
        return id;
    }
    
    @Override
    public String toString() {
        return  "genero=" + genero + "\n"
                + "titulo=" + titulo + "\n"
                + "totalTemporadas=" + totalTemporadas + "\n"
                + "avaliacao=" + avaliacao+ "\n"
                + "atores=" + atores + "\n"
                + "poster=" + poster + "\n"
                + "siponse=" + siponse + "\n"
                + "episodios=" + __criaStringEpisodios(episodios);
    }
    
    public List<Episodio> getEpisodio() {
        return episodios;
    }
    public void setEpisodio(List<Episodio> episodio) {
        episodio.forEach(e -> e.setSerie(this));
        this.episodios = episodio;
    }

    private String __criaStringEpisodios(List<Episodio> listaSeries) {
        String texto = "";
        
        for(Episodio ep : listaSeries) {
            texto += ep + ", " + "\n";
        }


        return texto;
    }
    
}