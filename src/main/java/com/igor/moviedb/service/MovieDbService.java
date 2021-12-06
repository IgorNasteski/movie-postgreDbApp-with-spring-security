package com.igor.moviedb.service;

import com.igor.moviedb.model.imdb.ImdbResponse;
import com.igor.moviedb.model.movie.MovieResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class MovieDbService {

    @Autowired
    private RestTemplate restTemplate;

    public void setujLinkKaImdb(List<MovieResults> movieResultsList, int j){
        String prviDeoImdbPutanje = "https://v2.sg.media-imdb.com/suggestion/";
        String pocetnoSlovoFilma = movieResultsList.get(j).getTitle().substring(0,1).toLowerCase();//imdb api ocekuje pre poziva kada prosledjujem ime filma, da dodam prvo slovo filma, mora biti malo
        //npr   t/thegoodfather     takodje ako film ima 2 reci moraju biit spojene
        String imeFilma = movieResultsList.get(j).getTitle();
        String imeFilmaAkoIma2ReciBiceSpojene = imeFilma.replaceAll("\\s+","").toLowerCase(); //konacno ime filma - ako ima 2 reci bice spojene(moraju u pozivu prema apiju da budu spojene)
        //skloni space izmedju dva stringa - spoji ih String resenje = str.replaceAll("\\s+","");  moraju biti mala slova

        //ako ima "?" bacace error
        //if(imeFilmaAkoIma2ReciBiceSpojene.contains("?")){ //ovako nije htelo, bacalo je error
        if(imeFilmaAkoIma2ReciBiceSpojene.indexOf('?') >= 0){   //ovako mogu da pitam da li se bilo gde u stringu nalazi "?"
            // System.out.println("USAO U IF ZA ???? ");
            imeFilmaAkoIma2ReciBiceSpojene = imeFilmaAkoIma2ReciBiceSpojene.replaceAll("\\p{Punct}","");
            //System.out.println("SKLANJAM ? " + imeFilmaAkoIma2ReciBiceSpojene);
        }
        //log.info("prvo slovo filma " + pocetnoSlovoFilma + "      " + "naziv filma - ako ima 2 reci spojene su " + imeFilmaAkoIma2ReciBiceSpojene);
        String imdbUrlPutanjaSaPocetnimSlovomFilmaPaNazivomFilma = prviDeoImdbPutanje+pocetnoSlovoFilma+"/" + imeFilmaAkoIma2ReciBiceSpojene + ".json";
        //log.info("Broj filma " + i + "cela url putanja ka imdb-u " + imdbUrlPutanjaSaPocetnimSlovomFilmaPaNazivomFilma);

        String movieId = "";
        ImdbResponse imdbResponse = restTemplate.getForObject(imdbUrlPutanjaSaPocetnimSlovomFilmaPaNazivomFilma, ImdbResponse.class);
        if(imdbResponse != null && imdbResponse.getImdbDetails() != null){//bacalo NullPointerException ako vrati slucajno null za objekat
            movieId = imdbResponse.getImdbDetails().get(0).getMovieId();
        }else{
            movieId = "unknown";
        }

        System.out.println("MOVIE ID " + movieId);
        movieResultsList.get(j).setImdbLink("https://www.imdb.com/title/" + movieId);
    }

    public void setujKonacniPathDoSlikeFilma(List<MovieResults> movieResultsList){
        //posto dobijam kao putanju do slike filma ovako npr: "/2CAL2433ZeIihfX1Hb2139CX0pW.jpg", a na netu sam iskopao info da za moviedb api
        //mora da se prvo ispise "http://image.tmdb.org/t/p/w500" pa tek na to doda taj path do slike koji dobijam kad kontaktiram moviedb api
        //preko rest template-a, dobijem listu objekta MovieResult, i svaki njegov objekat(filma) imace String polje moviePostrePath koji ce imati putanju
        //npr "/2CAL2433ZeIihfX1Hb2139CX0pW.jpg", a da bih prosledio thymeleaf-u ceo url do slike - pre toga moram dodati "http://image.tmdb.org/t/p/w500"
        String pathSlike = "http://image.tmdb.org/t/p/w500";   //npr putanja do slike http://image.tmdb.org/t/p/w500/your_poster_path   your_poster_path=putanja
        //dobijam iz polja "moviePosterPath", moram da setujem celu putanju na to polje
        for(int i=0; i<movieResultsList.size(); i++){
            String konacanPathSlike = pathSlike + movieResultsList.get(i).getMoviePosterPath();
            movieResultsList.get(i).setMoviePosterPath(konacanPathSlike);
        }
    }

}
