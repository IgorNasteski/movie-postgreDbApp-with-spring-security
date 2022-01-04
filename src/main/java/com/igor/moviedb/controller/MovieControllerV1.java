package com.igor.moviedb.controller;

import com.igor.moviedb.model.imdb.ImdbDrugiApiResponse;
import com.igor.moviedb.model.movie.MovieResponse;
import com.igor.moviedb.model.movie.MovieResults;
import com.igor.moviedb.service.MovieDbService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
//@RequestMapping("/movies")
@RequestMapping("/v1")
public class MovieControllerV1 {
    //OVAJ KONTROLER SAM KORISTIO DO PAGINACIJE, SADA GA NE KORISTIM
    //kontaktiram movie db api(npr dodam u url "popular" pa dobijem tu listu) a posle i imdb api(njemu prosledim naziv filma a dobijem id)
    //imacu TOP RATED, POPULAR, UPCOMING, NOW PLAYING   - za svaki endpoint koji se poziva koristim drugu metodu, prva je samo bila za primer/prikaz od 20 filmova

    private final Logger log = LoggerFactory.getLogger(MovieControllerV1.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MovieDbService movieDbService;

    private final String pathSlike = "http://image.tmdb.org/t/p/w500";

    private String url="https://api.themoviedb.org/3/movie/popular?language=en-US&page=1&api_key=79c150f8a75bdf97173bbfac4d0ec280";

                                    //*********************1)TOP RATED FILMOVI IKAD - BICE DEFAULTNO POSTAVLJENI NA HOME STRANICI****************
    //koristim drugi metod @GetMapping("/"), ovaj prvi je samo img gde ispisujem listu od 20 filmova, a na drugom sam namestio da mogu da prikazem od 20 do 1000 filmova
    //sve zavisi koliko setujem iterator u for-u(jer svaka stranica ima 20 filmova u sebi... page=1 ima 20 filmova itd a ima 50 stranica)

    @GetMapping("/topRatedMoviesEver")
    public String getTopRatedMoviesEver(Model theModel){
        //url = https://api.themoviedb.org/3/movie/top_rated?api_key=79c150f8a75bdf97173bbfac4d0ec280&language=en-US&page=1
        String urlTopRatedEver = "https://api.themoviedb.org/3/movie/top_rated?api_key=79c150f8a75bdf97173bbfac4d0ec280&language=en-US&page=1";
        MovieResponse movieResponse = restTemplate.getForObject(urlTopRatedEver, MovieResponse.class);
        //setujem konacnu putanju do slike filma - objasnjenje u metodi koja se nalazi u MovieDbService-u
        movieDbService.setujKonacniPathDoSlikeFilma(movieResponse.getMovieResults());
        theModel.addAttribute("topRatedMovieList", movieResponse.getMovieResults());     //vraca 20 filmova(jer jedan MovieResponse objekat ce uzeti 20 filmova iz page=1)
        return "homeMovies";
    }

    @GetMapping("/")                                            //OVU KORISTIM, GORNJA JE SAMO 20 FILMOVA - RADI TESTA
    public String getTop40RatedMoviesEverProba(Model theModel){
        String urlDoBrojaStranice="https://api.themoviedb.org/3/movie/top_rated?api_key=79c150f8a75bdf97173bbfac4d0ec280&language=en-US&page=";
        //sad dodati brojeve stranica dohvatam filmove sa page-a 1 i page-a 2 - bice dovoljno 40 filmova da prikazem, tako sam ja odlucio
        List<MovieResults> movieResultsList = new ArrayList<>();
        for(int i=1; i<13; i++){    //ako idem iteraciju do 51(to je 1000 filmova - 50 stranica po 20 filmova)
            MovieResponse movieResponse = restTemplate.getForObject(urlDoBrojaStranice + i, MovieResponse.class);
                for(int j=0; j<movieResponse.getMovieResults().size(); j++) {
                    movieResultsList.add(movieResponse.getMovieResults().get(j));//svaki movie response objekat ima 20 filmova, svaki ubaci u listu filmova radi kasnijeg prikaza
                    //movieDbService.setujLinkKaImdb(movieResultsList, j); //izbacio sam pozivanje nekog rest apija za imdb za svaki film
                    //jer necu da zovem api za npr 1000 filmova(traje dugo) imam nov endpoint na koji me thymeleaf salje pa prosledim naziv filma i tamo dohvatim imdb movie id
                }
        }
        //setujem konacnu putanju do slike filma - objasnjenje u metodi koja se nalazi u MovieDbService-u
        movieDbService.setujKonacniPathDoSlikeFilma(movieResultsList);
        theModel.addAttribute("topRatedMovieList", movieResultsList);
        return "homeMovies";
    }



                                  //*****************2)NAJNOVIJI A NAJPOPULARNIJI FILMOVI********************
    //koristim drugi metod @GetMapping("/newPopularMovies"), ovaj prvi je samo img gde ispisujem listu od 20 filmova, a na drugom sam namestio da mogu da prikazem
    //od 20 do 1000 filmova - sve zavisi koliko setujem iterator u for-u(jer svaka stranica ima 20 filmova u sebi... page=1 ima 20 filmova itd a ima 50 stranica)

    @GetMapping("/newPopular20Movies")
    public String getNewMostPopularMovies(Model theModel){
        String url="https://api.themoviedb.org/3/movie/popular?language=en-US&page=1&api_key=79c150f8a75bdf97173bbfac4d0ec280";
        MovieResponse movieResponse = restTemplate.getForObject(url, MovieResponse.class);
        //setujem konacnu putanju do slike filma - objasnjenje u metodi koja se nalazi u MovieDbService-u
        movieDbService.setujKonacniPathDoSlikeFilma(movieResponse.getMovieResults());
        theModel.addAttribute("popularNewMovies", movieResponse.getMovieResults());
        return "newPopularMovies";
    }

    @GetMapping("/newPopularMovies")                                            //OVU KORISTIM, GORNJA JE SAMO 20 FILMOVA - RADI TESTA
    public String getNewPopularMovies(Model theModel){
        //url do novih popularnih=  "https://api.themoviedb.org/3/movie/popular?language=en-US&page=1&api_key=79c150f8a75bdf97173bbfac4d0ec280";
        String urlDoBrojaStranice="https://api.themoviedb.org/3/movie/popular?language=en-US&page=";
        String apiKey = "&api_key=79c150f8a75bdf97173bbfac4d0ec280";
        //sad dodati brojeve stranica dohvatam filmove sa page-a 1 i page-a 2 - bice dovoljno 40 filmova da prikazem, tako sam ja odlucio
        List<MovieResults> movieResultsList = new ArrayList<>();
        for(int i=1; i<13; i++){    //ako idem iteraciju do 51(to je 1000 filmova - 50 stranica po 20 filmova)
            MovieResponse movieResponse = restTemplate.getForObject(urlDoBrojaStranice + i + apiKey, MovieResponse.class);
            for(int j=0; j<movieResponse.getMovieResults().size(); j++) {
                movieResultsList.add(movieResponse.getMovieResults().get(j));
                //movieDbService.setujLinkKaImdb(movieResultsList, j); //izbacio sam pozivanje nekog rest apija za imdb za svaki film
                //jer necu da zovem api za npr 1000 filmova(traje dugo) imam nov endpoint na koji me thymeleaf salje pa prosledim naziv filma i tamo dohvatim imdb movie id
            }
        }
        //setujem konacnu putanju do slike filma - objasnjenje u metodi koja se nalazi u MovieDbService-u
        movieDbService.setujKonacniPathDoSlikeFilma(movieResultsList);
        theModel.addAttribute("popularNewMovies", movieResultsList);
        return "newPopularMovies";
    }



                                    //**********************3)UPCOMING MOVIES***********************************
    //koristim drugi metod @GetMapping("/newPopularMovies"), ovaj prvi je samo img gde ispisujem listu od 20 filmova, a na drugom sam namestio da mogu da prikazem
    //od 20 do 1000 filmova - sve zavisi koliko setujem iterator u for-u(jer svaka stranica ima 20 filmova u sebi... page=1 ima 20 filmova itd a ima 50 stranica)

    @GetMapping("/upcoming20Movies")
    public String get20UpcomingMovies(Model theModel){
        //url = https://api.themoviedb.org/3/movie/upcoming?language=en-US&page=1&api_key=79c150f8a75bdf97173bbfac4d0ec280
        String url = "https://api.themoviedb.org/3/movie/upcoming?language=en-US&page=1&api_key=79c150f8a75bdf97173bbfac4d0ec280";
        MovieResponse movieResponse = restTemplate.getForObject(url, MovieResponse.class);
        //setujem konacnu putanju do slike filma - objasnjenje u metodi koja se nalazi u MovieDbService-u
        movieDbService.setujKonacniPathDoSlikeFilma(movieResponse.getMovieResults());
        theModel.addAttribute("upcomingMovies", movieResponse.getMovieResults());     //vraca 20 filmova(jer jedan MovieResponse objekat ce uzeti 20 filmova iz page=1)
        return "upcomingMovies";
    }

    @GetMapping("/upcomingMovies")                                            //OVU KORISTIM, GORNJA JE SAMO 20 FILMOVA - RADI TESTA
    public String getUpcomingMovies(Model theModel){
        //url = https://api.themoviedb.org/3/movie/upcoming?language=en-US&page=1&api_key=79c150f8a75bdf97173bbfac4d0ec280
        String urlDoPage="https://api.themoviedb.org/3/movie/upcoming?language=en-US&page=";
        //sada mogu inkremetovati page 2 itd itd
        String apiKey="&api_key=79c150f8a75bdf97173bbfac4d0ec280";
        //sad dodati brojeve stranica dohvatam filmove sa page-a 1 i page-a 2 - bice dovoljno 40 filmova da prikazem, tako sam ja odlucio
        List<MovieResults> movieResultsList = new ArrayList<>();
        for(int i=1; i<13; i++){    //ako idem iteraciju do 51(to je 1000 filmova - 50 stranica po 20 filmova)
            MovieResponse movieResponse = restTemplate.getForObject(urlDoPage + i + apiKey, MovieResponse.class);
            for(int j=0; j<movieResponse.getMovieResults().size(); j++) {
                movieResultsList.add(movieResponse.getMovieResults().get(j));
                //movieDbService.setujLinkKaImdb(movieResultsList, j); //izbacio sam pozivanje nekog rest apija za imdb za svaki film
                //jer necu da zovem api za npr 1000 filmova(traje dugo) imam nov endpoint na koji me thymeleaf salje pa prosledim naziv filma i tamo dohvatim imdb movie id
            }
        }
        //setujem konacnu putanju do slike filma - objasnjenje u metodi koja se nalazi u MovieDbService-u
        movieDbService.setujKonacniPathDoSlikeFilma(movieResultsList);
        theModel.addAttribute("upcomingMovies", movieResultsList);
        return "upcomingMovies";
    }


                                   //**********************3)now playing***********************************
    //koristim drugi metod @GetMapping("/newPopularMovies"), ovaj prvi je samo img gde ispisujem listu od 20 filmova, a na drugom sam namestio da mogu da prikazem
    //od 20 do 1000 filmova - sve zavisi koliko setujem iterator u for-u(jer svaka stranica ima 20 filmova u sebi... page=1 ima 20 filmova itd a ima 50 stranica)

    @GetMapping("/nowPlaying")
    public String getNowPlaying(Model theModel){
        //url = https://api.themoviedb.org/3/movie/now_playing?language=en-US&page=1&api_key=79c150f8a75bdf97173bbfac4d0ec280
        String url = "https://api.themoviedb.org/3/movie/now_playing?language=en-US&page=1&api_key=79c150f8a75bdf97173bbfac4d0ec280";
        MovieResponse movieResponse = restTemplate.getForObject(url, MovieResponse.class);
        movieDbService.setujKonacniPathDoSlikeFilma(movieResponse.getMovieResults());
        theModel.addAttribute("nowPlayingMovies", movieResponse.getMovieResults());     //vraca 20 filmova(jer jedan MovieResponse objekat ce uzeti 20 filmova iz page=1)
        return "nowPlaying";
    }

    @GetMapping("/nowPlayingMovies")                                            //OVU KORISTIM, GORNJA JE SAMO 20 FILMOVA - RADI TESTA
    public String getNowPlayingMovies(Model theModel){
        //url = https://api.themoviedb.org/3/movie/now_playing?language=en-US&page=1&api_key=79c150f8a75bdf97173bbfac4d0ec280
        String urlDoPage="https://api.themoviedb.org/3/movie/now_playing?language=en-US&page=";
        //sada mogu inkremetovati page 2 itd itd
        String apiKey="&api_key=79c150f8a75bdf97173bbfac4d0ec280";
        //sad dodati brojeve stranica dohvatam filmove sa page-a 1 i page-a 2 - bice dovoljno 40 filmova da prikazem, tako sam ja odlucio
        List<MovieResults> movieResultsList = new ArrayList<>();
        for(int i=1; i<13; i++){    //ako idem iteraciju do 51(to je 1000 filmova - 50 stranica po 20 filmova)
            MovieResponse movieResponse = restTemplate.getForObject(urlDoPage + i + apiKey, MovieResponse.class);
            for(int j=0; j<movieResponse.getMovieResults().size(); j++) {
                movieResultsList.add(movieResponse.getMovieResults().get(j));
                //movieDbService.setujLinkKaImdb(movieResultsList, j); //izbacio sam pozivanje nekog rest apija za imdb za svaki film
                //jer necu da zovem api za npr 1000 filmova(traje dugo) imam nov endpoint na koji me thymeleaf salje pa prosledim naziv filma i tamo dohvatim imdb movie id
            }
        }
        //setujem konacnu putanju do slike filma - objasnjenje u metodi koja se nalazi u MovieDbService-u
        movieDbService.setujKonacniPathDoSlikeFilma(movieResultsList);
        theModel.addAttribute("nowPlayingMovies", movieResultsList);
        return "nowPlaying";
    }




                                                        //*******************IMDB REDIRECT KAD KLIKNEM NA THYMELEAF DUGME FILMA**************************

    //OVAKO SKUPLJAM LINK KA IMDB - PRE OVG SAM POZIVAO DRUGI API ZA IMDB - SVAKI PUT SAM IMAO REST POZIV  - AKO IMA 1000 FILMOVA - 1000 POZIVA, PA SAM ONDA CEKAO PO 45 SEKUNDI
    //OVDE IMAM LINK NA DUGME FILMA SA THYMELEAF STRANICE, KAD KLIKNEM OVDE GA SALJEM A JA NA THYMELEAF DOHVATIM I PROSLEDIM OVDE NAZIV TOG FILMA KOJI SAM KLIKNUO DA ME SALJE NA IMDB
    //PA ONDA KONTAKTIRAM IMDB API, PROSLEDIM MU NPR OVAKO https://data-imdb1.p.rapidapi.com/movie/imdb_id/byTitle/Scarface/ - IME FILMA ZALEPIM KOJE SAM DOBIO OD THYMELEAF-A NAKON KLIKA
    //PA ONDA POKUPIM imdb_id NPR OVAKO IZGLEDA "imdb_id": "tt0086250", I SAMO TAJ ID ZALEPIM NA URL KAKO BIH DOSAO NA IMDB STRANICU ZA TAJ FILM NPR https://www.imdb.com/title/tt0086250/
    @GetMapping("/posaljiNaImdbKrozKontroler")
    public String posaljiNaImdbKrozKontroler(@RequestParam("nazivFilma")String nazivFilma, RedirectAttributes redirectAttributes){
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

        //ResponseEntity<ImdbDrugiApi> imdbDrugiApiResponse = restTemplate.exchange(urlKaImdb, HttpMethod.GET, request, new ResponseEntity<ImdbDrugiApi>);
        //ImdbDrugiApi imdbDrugiApiResponse = restTemplate.postForObject(urlKaImdb, request, ImdbDrugiApi.class);

        //DOHVATAM METODOM exchange - SALJEM PRVO URL, PA DA LI JE GET U PITANJU, PA MOJ REQUEST(U OVOM SLUCAJU MI TREBA DA POSALJEM SAMO HEADER PODATKE RADI AUTENTIKACIJE JER ONI
        //PUSTAJU/DAJU PODATKE SAMO ONOM KO IM POSALJE U HEDERU NEKA DVA PARAMETRA), I NA KRAJU NAZIV KLASE KOJA PRIHVATA NAZAD PODATKE KOJI STIZU
        //MORA BITI WRAP-OVANO U ResponseEntity KAD GOD SALJEM NESTO U HEDERU, ILI AKO ZELIM DA PRIHVATIM NJIHOV HEADER RESPONSE, NPR STATUS CODE(NPR DA PROVERIM DA LI JE DOBRO PROSLO - 200 ITD)
        //ILI NPR DA DOHVATIM NJIHOV NEKI HEADER RESPONSE PARAMETAR
        ResponseEntity<ImdbDrugiApiResponse> imdbDrugiApiResponse = restTemplate.exchange(urlKaImdb, HttpMethod.GET, request, ImdbDrugiApiResponse.class);
        System.out.println("Result - status ("+ imdbDrugiApiResponse.getStatusCode() + ") has body: " + imdbDrugiApiResponse.hasBody());

        String imdbMovieId = "";
        if(imdbDrugiApiResponse.getBody().getImdbDrugiApi().size() != 0){   //MORAO DA PROVERIM DA LI NESTO STIZE NAZAD, JER AKO NE STIGNE NISTA - PUCA APLIKACIJA
            imdbMovieId = imdbDrugiApiResponse.getBody().getImdbDrugiApi().get(0).getImdbMovieId();     //AKO STIGNE ODGOVARAJUC IMDB MOVIE ID, ONDA GA SPAKUJ U STRING
        } else{                                                             //PA AKO VEC NE STIGNE NISTA, SALJEM IH NA MOJU STRANICU KOJA IH OBAVESTAVA DA NEMA IMDB LINKA ZA TAJ FILM
            redirectAttributes.addFlashAttribute("message1", "There are no available link to imdb review for this movie."); //NIJE RADILO, NZM STO, PA SAM NA THYMELEAF
            return "ispisiPorukuDaNemaTogImdbLinka";                                                                                               //STRANICI SAMO ZAKUCAO TEKST
        }

        log.info("imdbDrugiApiResponse " + imdbDrugiApiResponse.getBody().getImdbDrugiApi().get(0).getImdbMovieId());

        //https://www.imdb.com/title/tt0068646/         OVAKO TREBA DA IZGLEDA IMDB LINK ZA NEKI FILM
        String urlKaImdbSaSveMovieId = "https://www.imdb.com/title/" + imdbMovieId + "/";
        log.info("IMDB MOVIE " + urlKaImdbSaSveMovieId);
        //String redirectUrl = "http://www.yahoo.com" + imdbId;
        return "redirect:" + urlKaImdbSaSveMovieId;                 // - KADA ZELIM DA REDIREKTUJEM NA NEKU STRANICU NA NETU(A NE NA NEKU THYMELEAF STRANICU MOJU) ONDA MORAM DODATI "https://www" ITD
    }

    /*      GLEDAO NEKE PRIMERE JER MI JE TREBAO PRIMER KADA SE U REQUESTU SALJU SAMO PODACI HEDERA A NE I SAM OBJEKAT POPUNJEN
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        ResponseEntity<List<Employee>> response = restTemplate.exchange("http://hello-server/rest/employees", HttpMethod.GET,entity, new ParameterizedTypeReference<List<Employee>>() {});
        return response.getBody(); //this returns List of Employee



        String url = "https://data-imdb1.p.rapidapi.com/movie/imdb_id/byTitle/Scarface/";
            //System.out.println("URL " + url);
        //ocekuju nesto u hederu radi autentifikacije
        HttpHeaders headers = new HttpHeaders();
            headers.add("x-rapidapi-host", "data-imdb1.p.rapidapi.com");
            headers.add("x-rapidapi-key", "f0f504f00emshc54fd06ff8496d8p10c439jsn78042f0e7dad");
        HttpEntity<String> request = new HttpEntity<>("nista",headers);
        ImdbMovieIdResponse imdbMovieIdResponse = restTemplate.postForObject(url, request, ImdbMovieIdResponse.class);*/










                               //****************************PRIMER SA MOG NEKOG REST APP-A, OVAKO SAM DOHVATAO FILMOVE*********************************
                                                        //2)NAJNOVIJI A NAJPOPULARNIJI FILMOVI  -   sa rest-a primer kako ih uzimam
    //vraca 20 najnovihih i najpopularnijih filmova
    @GetMapping("/mostRecentAndPopularMovieList")
    public List<MovieResults> getMostPopularMovies(){
        MovieResponse movieResponse = restTemplate.getForObject(url, MovieResponse.class);
        return movieResponse.getMovieResults();
    }

    //vraca 40,60 itd najnovijih i najpopularnijih filmova, zavisi koliko namestim u for petlji
    //posto ima vise page-a, na svakom page-u ima samo 20 filmova... a ima 50 page-a, pa da probam da vracam nekoliko
    @GetMapping("/mostRecentAndPopularMovieListWithFewPagesMore")
    public List<MovieResponse> getMostPopularMoviesWithFewPages(){
        String urlDoBrojaStranice="https://api.themoviedb.org/3/movie/popular?language=en-US&page=";
        //sad dodati brojeve stranica
        //pa dodati drugi deo url-a
        String urlDrugiDeoSaApiKeyem="&api_key=79c150f8a75bdf97173bbfac4d0ec280";
        List<MovieResponse> movieResponseList = new ArrayList<>();
        MovieResponse movieResponse = new MovieResponse();
        for(int i=1; i<4; i++) {    //i mora da krece od 1 jer broj stranica krece od 1, pa hocu da dohvatim podatke prvih 3 stranica(svaka ima 20 filmova)
            movieResponse = restTemplate.getForObject(urlDoBrojaStranice + i + urlDrugiDeoSaApiKeyem, MovieResponse.class);
            movieResponseList.add(movieResponse);
        }
        return movieResponseList;
    }




}
