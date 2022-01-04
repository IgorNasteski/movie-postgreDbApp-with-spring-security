package com.igor.moviedb.service;

import com.igor.moviedb.model.imdb.ImdbDrugiApiResponse;
import com.igor.moviedb.model.imdb.ImdbResponse;
import com.igor.moviedb.model.movie.MovieResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class MovieDbService {

    private final Logger log = LoggerFactory.getLogger(MovieDbService.class);

    @Autowired
    private RestTemplate restTemplate;

    //ne koristim
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

    public String imdbPutanjaPomocuNazivaFilma(String nazivFilma){
        //OVAKO SKUPLJAM LINK KA IMDB - PRE OVG SAM POZIVAO DRUGI API ZA IMDB - SVAKI PUT SAM IMAO REST POZIV  - AKO IMA 1000 FILMOVA - 1000 POZIVA, PA SAM ONDA CEKAO PO 45 SEKUNDI
        //OVDE IMAM LINK NA DUGME FILMA SA THYMELEAF STRANICE, KAD KLIKNEM OVDE GA SALJEM A JA NA THYMELEAF DOHVATIM I PROSLEDIM OVDE NAZIV TOG FILMA KOJI SAM KLIKNUO DA ME SALJE NA IMDB
        //PA ONDA KONTAKTIRAM IMDB API, PROSLEDIM MU NPR OVAKO https://data-imdb1.p.rapidapi.com/movie/imdb_id/byTitle/Scarface/ - IME FILMA ZALEPIM KOJE SAM DOBIO OD THYMELEAF-A NAKON KLIKA
        //PA ONDA POKUPIM imdb_id NPR OVAKO IZGLEDA "imdb_id": "tt0086250", I SAMO TAJ ID ZALEPIM NA URL KAKO BIH DOSAO NA IMDB STRANICU ZA TAJ FILM NPR https://www.imdb.com/title/tt0086250/
        //prosledim api-ju ime filma, on mi vrati imdb movie id

        //https://data-imdb1.p.rapidapi.com/movie/imdb_id/byTitle/Scarface/
        /*{
            "results": [
            {
                "imdb_id": "tt0086250",
                    "title": "Scarface"
            }
            ]
        }*/
        String urlKaImdb = "https://data-imdb1.p.rapidapi.com/movie/imdb_id/byTitle/" + nazivFilma + "/";

        HttpHeaders headers = new HttpHeaders();
        headers.add("x-rapidapi-host", "data-imdb1.p.rapidapi.com");
        headers.add("x-rapidapi-key", "f0f504f00emshc54fd06ff8496d8p10c439jsn78042f0e7dad");
        //HttpEntity<String> request = new HttpEntity<>("nista",headers);
        HttpEntity<String> request = new HttpEntity<String>(headers);   //kad saljem samo nesto sto sam setovao u hederu (jer npr taj api trazi neke autentifikacione podatke u hederu)

        //DOHVATAM METODOM exchange - SALJEM PRVO URL, PA DA LI JE GET U PITANJU, PA MOJ REQUEST(U OVOM SLUCAJU MI TREBA DA POSALJEM SAMO HEADER PODATKE RADI AUTENTIKACIJE JER ONI
        //PUSTAJU/DAJU PODATKE SAMO ONOM KO IM POSALJE U HEDERU NEKA DVA PARAMETRA), I NA KRAJU NAZIV KLASE KOJA PRIHVATA NAZAD PODATKE KOJI STIZU
        //MORA BITI WRAP-OVANO U ResponseEntity KAD GOD SALJEM NESTO U HEDERU, ILI AKO ZELIM DA PRIHVATIM NJIHOV HEADER RESPONSE, NPR STATUS CODE(NPR DA PROVERIM DA LI JE DOBRO PROSLO - 200 ITD)
        //ILI NPR DA DOHVATIM NJIHOV NEKI HEADER RESPONSE PARAMETAR
        ResponseEntity<ImdbDrugiApiResponse> imdbDrugiApiResponse = restTemplate.exchange(urlKaImdb, HttpMethod.GET, request, ImdbDrugiApiResponse.class);
        System.out.println("Result - status ("+ imdbDrugiApiResponse.getStatusCode() + ") has body: " + imdbDrugiApiResponse.hasBody());

        String imdbMovieId = "";
        //https://www.imdb.com/title/tt0068646/         OVAKO TREBA DA IZGLEDA IMDB LINK ZA NEKI FILM
        String urlKaImdbSaSveMovieId = "";
        if(imdbDrugiApiResponse.getBody().getImdbDrugiApi().size() != 0){   //MORAO DA PROVERIM DA LI NESTO STIZE NAZAD, JER AKO NE STIGNE NISTA - PUCA APLIKACIJA
            imdbMovieId = imdbDrugiApiResponse.getBody().getImdbDrugiApi().get(0).getImdbMovieId();     //AKO STIGNE ODGOVARAJUC IMDB MOVIE ID, ONDA GA SPAKUJ U STRING
            urlKaImdbSaSveMovieId = "https://www.imdb.com/title/" + imdbMovieId + "/";
        } else{                                                             //PA AKO VEC NE STIGNE NISTA, SALJEM IH NA MOJU STRANICU KOJA IH OBAVESTAVA DA NEMA IMDB LINKA ZA TAJ FILM
            //redirectAttributes.addFlashAttribute("message1", "There are no available link to imdb review for this movie."); //NIJE RADILO, NZM STO, PA SAM NA THYMELEAF
            //return "ispisiPorukuDaNemaTogImdbLinka";                                                                                               //STRANICI SAMO ZAKUCAO TEKST
            urlKaImdbSaSveMovieId = "nema"; //ideja je, kada budem hteo da prikazem listu favourites filmova od usera, ako klikne na imdb filma, i ako bude "nema" da ga posaljem na
                                            //stranicu gde pise nema
        }
        //log.info("imdbDrugiApiResponse " + imdbDrugiApiResponse.getBody().getImdbDrugiApi().get(0).getImdbMovieId());
        log.info("IMDB MOVIE " + urlKaImdbSaSveMovieId);

        return urlKaImdbSaSveMovieId;
    }

}
