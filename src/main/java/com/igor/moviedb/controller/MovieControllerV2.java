package com.igor.moviedb.controller;

import com.igor.moviedb.model.imdb.ImdbDrugiApiResponse;
import com.igor.moviedb.model.movie.MovieResponse;
import com.igor.moviedb.model.movie.MovieResults;
import com.igor.moviedb.model.movie.favourites.UserFavouriteMovie;
import com.igor.moviedb.model.user.Authorities;
import com.igor.moviedb.model.user.Users;
import com.igor.moviedb.repository.UserFavouriteMovieRepository;
import com.igor.moviedb.repository.UserRepository;
import com.igor.moviedb.service.MovieDbService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
//@RequestMapping("/movies")
@RequestMapping("/")
public class MovieControllerV2 {
    //KORISTIM OVAJ KONTROLER, IMPLEMENTIRAO SAM PAGINACIJU NA NEKI MOJ NACIN


    //kontaktiram movie db api(npr dodam u url "popular" pa dobijem tu listu) a posle i imdb api(njemu prosledim naziv filma a dobijem id)
    //imacu TOP RATED, POPULAR, UPCOMING, NOW PLAYING   - za svaki endpoint koji se poziva koristim drugu metodu, prva je samo bila za primer/prikaz od 20 filmova

    private final Logger log = LoggerFactory.getLogger(MovieControllerV2.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MovieDbService movieDbService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserFavouriteMovieRepository userFavouriteMovieRepository;

    //bice deljena vrednost, setovacu je svaki put kad bude user menjao stranicu, da bih mogao u drugoj metodi kontrolera kada dodajem u favourites da redirektujem
    //bas na tu istru stranicu/da ostavim usera na istoj stranici
    private static int brojStraniceNaKojuJeUserKliknuo;

    private final String pathSlike = "http://image.tmdb.org/t/p/w500";

    private String url="https://api.themoviedb.org/3/movie/popular?language=en-US&page=1&api_key=79c150f8a75bdf97173bbfac4d0ec280";

                                    //*********************1)TOP RATED FILMOVI IKAD - BICE DEFAULTNO POSTAVLJENI NA HOME STRANICI****************
    //koristim drugi metod @GetMapping("/"), ovaj prvi je samo img gde ispisujem listu od 20 filmova, a na drugom sam namestio da mogu da prikazem od 20 do 1000 filmova
    //sve zavisi koliko setujem iterator u for-u(jer svaka stranica ima 20 filmova u sebi... page=1 ima 20 filmova itd a ima 50 stranica)

    /*@GetMapping("/topRatedMoviesEver")
    public String getTopRatedMoviesEver(Model theModel){
        //url = https://api.themoviedb.org/3/movie/top_rated?api_key=79c150f8a75bdf97173bbfac4d0ec280&language=en-US&page=1
        String urlTopRatedEver = "https://api.themoviedb.org/3/movie/top_rated?api_key=79c150f8a75bdf97173bbfac4d0ec280&language=en-US&page=1";
        MovieResponse movieResponse = restTemplate.getForObject(urlTopRatedEver, MovieResponse.class);
        //setujem konacnu putanju do slike filma - objasnjenje u metodi koja se nalazi u MovieDbService-u
        movieDbService.setujKonacniPathDoSlikeFilma(movieResponse.getMovieResults());
        theModel.addAttribute("topRatedMovieList", movieResponse.getMovieResults());     //vraca 20 filmova(jer jedan MovieResponse objekat ce uzeti 20 filmova iz page=1)
        return "homeMovies";
    }*/

    /*@GetMapping("/")                                            //OVU KORISTIM, GORNJA JE SAMO 20 FILMOVA - RADI TESTA
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
    }*/

    @GetMapping("/")                                            //OVU KORISTIM, GORNJA JE SAMO 20 FILMOVA - RADI TESTA
    public String getTopRatedMoviesEverProba(Model theModel){
        return proveriBrojStraniceZaPaginacijuTopRatedMoviesHomePage(1, theModel);//cim pokrenem app, gadja ovaj endpoint a on kaze metodi ispod daj sve sa page-a 1(tih 20 filmova)
    }

    //svaki put kada neki korisnik izabere npr stranicu broj 40(to je dugme koje ce da taj broj prosledi ovde) ja cu ovde to uhvatiti i vratiti tih 20 filmova sa te stranice
    @GetMapping("/proveriBrojStraniceZaPaginacijuTopRatedMoviesHomePage")
    public String proveriBrojStraniceZaPaginacijuTopRatedMoviesHomePage(@RequestParam("brojStranice")int brojStranice, Model theModel){
        System.out.println("BROJ STRANICE NA KOJU SAM KLIKNUO TOP RATED MOVIES " + brojStranice);
        //url ka podacima   "https://api.themoviedb.org/3/movie/top_rated?api_key=79c150f8a75bdf97173bbfac4d0ec280&language=en-US&page=1"   ima 50 page-a, na svakom 20 filmova
        String urlTopRatedEver = "https://api.themoviedb.org/3/movie/top_rated?api_key=79c150f8a75bdf97173bbfac4d0ec280&language=en-US&page=";
        MovieResponse movieResponse = restTemplate.getForObject(urlTopRatedEver+brojStranice, MovieResponse.class);
        //setujem konacnu putanju do slike filma - objasnjenje u metodi koja se nalazi u MovieDbService-u
        movieDbService.setujKonacniPathDoSlikeFilma(movieResponse.getMovieResults());
        theModel.addAttribute("topRatedMovieList", movieResponse.getMovieResults());     //vraca 20 filmova(jer jedan MovieResponse objekat ce uzeti 20 filmova iz page=1)
        List<Integer> brojeviStranica = new ArrayList<>();
        for(int i=1; i<51; i++){
            brojeviStranica.add(i);
        }
        int brojTrenutneStranice = brojStranice;
        theModel.addAttribute("brojeviStranica", brojeviStranica);
        brojStraniceNaKojuJeUserKliknuo=brojStranice;//dodao da mogu da kada user klikne add to favourites - da prosledim i broj stranice pa da
                                                     //ga redirektujem na bas tu stranicu iz metode gde odradjujem add to favourties a zove se /dodajFilmUFavourites
        return "homeMovies";
    }

                                  //*****************2)NAJNOVIJI A NAJPOPULARNIJI FILMOVI********************
    //koristim drugi metod @GetMapping("/newPopularMovies"), ovaj prvi je samo img gde ispisujem listu od 20 filmova, a na drugom sam namestio da mogu da prikazem
    //od 20 do 1000 filmova - sve zavisi koliko setujem iterator u for-u(jer svaka stranica ima 20 filmova u sebi... page=1 ima 20 filmova itd a ima 50 stranica)

   /* @GetMapping("/newPopular20Movies")
    public String getNewMostPopularMovies(Model theModel){
        String url="https://api.themoviedb.org/3/movie/popular?language=en-US&page=1&api_key=79c150f8a75bdf97173bbfac4d0ec280";
        MovieResponse movieResponse = restTemplate.getForObject(url, MovieResponse.class);
        //setujem konacnu putanju do slike filma - objasnjenje u metodi koja se nalazi u MovieDbService-u
        movieDbService.setujKonacniPathDoSlikeFilma(movieResponse.getMovieResults());
        theModel.addAttribute("popularNewMovies", movieResponse.getMovieResults());
        return "newPopularMovies";
    }*/

    /*@GetMapping("/newPopularMovies")                                            //OVU KORISTIM, GORNJA JE SAMO 20 FILMOVA - RADI TESTA
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
    }*/

    @GetMapping("/newPopularMovies")
    public String getNewMostPopularMovies(Model theModel){
        return proveriBrojStraniceZaPaginacijuPopularMovies(1, theModel);
    }

    @GetMapping("/proveriBrojStraniceZaPaginacijuPopularMovies")
    public String proveriBrojStraniceZaPaginacijuPopularMovies(@RequestParam("brojStranice")int brojStranice, Model theModel){
        System.out.println("BROJ STRANICE NA KOJU SAM KLIKNUO POPULAR MOVIES " + brojStranice);
        //url za most popular   "https://api.themoviedb.org/3/movie/popular?language=en-US&page=1&api_key=79c150f8a75bdf97173bbfac4d0ec280"
        String urlDoPage = "https://api.themoviedb.org/3/movie/popular?language=en-US&page=";
        String apiKey = "&api_key=79c150f8a75bdf97173bbfac4d0ec280";
        MovieResponse movieResponse = restTemplate.getForObject(urlDoPage + brojStranice + apiKey, MovieResponse.class);
        //setujem konacnu putanju do slike filma - objasnjenje u metodi koja se nalazi u MovieDbService-u
        movieDbService.setujKonacniPathDoSlikeFilma(movieResponse.getMovieResults());
        theModel.addAttribute("popularNewMovies", movieResponse.getMovieResults());
        List<Integer> brojeviStranica = new ArrayList<>();
        for(int i=1; i<51; i++){
            brojeviStranica.add(i);
        }
        theModel.addAttribute("brojeviStranica", brojeviStranica);
        brojStraniceNaKojuJeUserKliknuo=brojStranice;//dodao da mogu da kada user klikne add to favourites - da prosledim i broj stranice pa da
        //ga redirektujem na bas tu stranicu iz metode gde odradjujem add to favourties a zove se /dodajFilmUFavourites
        return "newPopularMovies";  //vraca 20 filmova(jer jedan MovieResponse objekat ce uzeti 20 filmova iz page=1)
    }



                                    //**********************3)UPCOMING MOVIES***********************************
    //koristim drugi metod @GetMapping("/newPopularMovies"), ovaj prvi je samo img gde ispisujem listu od 20 filmova, a na drugom sam namestio da mogu da prikazem
    //od 20 do 1000 filmova - sve zavisi koliko setujem iterator u for-u(jer svaka stranica ima 20 filmova u sebi... page=1 ima 20 filmova itd a ima 50 stranica)

    /*@GetMapping("/upcoming20Movies")
    public String get20UpcomingMovies(Model theModel){
        //url = https://api.themoviedb.org/3/movie/upcoming?language=en-US&page=1&api_key=79c150f8a75bdf97173bbfac4d0ec280
        String url = "https://api.themoviedb.org/3/movie/upcoming?language=en-US&page=1&api_key=79c150f8a75bdf97173bbfac4d0ec280";
        MovieResponse movieResponse = restTemplate.getForObject(url, MovieResponse.class);
        //setujem konacnu putanju do slike filma - objasnjenje u metodi koja se nalazi u MovieDbService-u
        movieDbService.setujKonacniPathDoSlikeFilma(movieResponse.getMovieResults());
        theModel.addAttribute("upcomingMovies", movieResponse.getMovieResults());     //vraca 20 filmova(jer jedan MovieResponse objekat ce uzeti 20 filmova iz page=1)
        return "upcomingMovies";
    }*/

    /*@GetMapping("/upcomingMovies")                                            //OVU KORISTIM, GORNJA JE SAMO 20 FILMOVA - RADI TESTA
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
    }*/

    @GetMapping("/upcomingMovies")
    public String getUpcomingMovies(Model theModel){
        return proveriBrojStraniceZaPaginacijuUpcomingMovies(1, theModel);
    }

    @GetMapping("/proveriBrojStraniceZaPaginacijuUpcomingMovies")
    public String proveriBrojStraniceZaPaginacijuUpcomingMovies(@RequestParam("brojStranice")int brojStranice, Model theModel){
        System.out.println("BROJ STRANICE NA KOJU SAM KLIKNUO UPCOMING MOVIES " + brojStranice);
        //url = https://api.themoviedb.org/3/movie/upcoming?language=en-US&page=1&api_key=79c150f8a75bdf97173bbfac4d0ec280
        String urlDoPage = "https://api.themoviedb.org/3/movie/upcoming?language=en-US&page=";
        String apiKey = "&api_key=79c150f8a75bdf97173bbfac4d0ec280";
        MovieResponse movieResponse = restTemplate.getForObject(urlDoPage + brojStranice + apiKey, MovieResponse.class);
        //setujem konacnu putanju do slike filma - objasnjenje u metodi koja se nalazi u MovieDbService-u
        movieDbService.setujKonacniPathDoSlikeFilma(movieResponse.getMovieResults());
        theModel.addAttribute("upcomingMovies", movieResponse.getMovieResults());     //vraca 20 filmova(jer jedan MovieResponse objekat ce uzeti 20 filmova iz page=1)
        List<Integer> brojeviStranica = new ArrayList<>();
        for(int i=1; i<51; i++){
            brojeviStranica.add(i);
        }
        theModel.addAttribute("brojeviStranica", brojeviStranica);
        brojStraniceNaKojuJeUserKliknuo=brojStranice;//dodao da mogu da kada user klikne add to favourites - da prosledim i broj stranice pa da
        //ga redirektujem na bas tu stranicu iz metode gde odradjujem add to favourties a zove se /dodajFilmUFavourites
        return "upcomingMovies";
    }


                                   //**********************3)now playing***********************************
    //koristim drugi metod @GetMapping("/newPopularMovies"), ovaj prvi je samo img gde ispisujem listu od 20 filmova, a na drugom sam namestio da mogu da prikazem
    //od 20 do 1000 filmova - sve zavisi koliko setujem iterator u for-u(jer svaka stranica ima 20 filmova u sebi... page=1 ima 20 filmova itd a ima 50 stranica)

    /*@GetMapping("/nowPlaying")
    public String getNowPlaying(Model theModel){
        //url = https://api.themoviedb.org/3/movie/now_playing?language=en-US&page=1&api_key=79c150f8a75bdf97173bbfac4d0ec280
        String url = "https://api.themoviedb.org/3/movie/now_playing?language=en-US&page=1&api_key=79c150f8a75bdf97173bbfac4d0ec280";
        MovieResponse movieResponse = restTemplate.getForObject(url, MovieResponse.class);
        movieDbService.setujKonacniPathDoSlikeFilma(movieResponse.getMovieResults());
        theModel.addAttribute("nowPlayingMovies", movieResponse.getMovieResults());     //vraca 20 filmova(jer jedan MovieResponse objekat ce uzeti 20 filmova iz page=1)
        return "nowPlaying";
    }*/

    /*@GetMapping("/nowPlayingMovies")                                            //OVU KORISTIM, GORNJA JE SAMO 20 FILMOVA - RADI TESTA
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
    }*/

    @GetMapping("/nowPlayingMovies")
    public String getNowPlaying(Model theModel){
        return proveriBrojStraniceZaPaginacijuNowPlayingMovies(1, theModel);
    }

    @GetMapping("/proveriBrojStraniceZaPaginacijuNowPlayingMovies")
    public String proveriBrojStraniceZaPaginacijuNowPlayingMovies(@RequestParam("brojStranice")int brojStranice, Model theModel){
        System.out.println("BROJ STRANICE NA KOJU SAM KLIKNUO NOW PLAYING MOVIES " + brojStranice);
        //url = https://api.themoviedb.org/3/movie/now_playing?language=en-US&page=1&api_key=79c150f8a75bdf97173bbfac4d0ec280
        String urlDoPage = "https://api.themoviedb.org/3/movie/now_playing?language=en-US&page=";
        String apiKey = "&api_key=79c150f8a75bdf97173bbfac4d0ec280";
        MovieResponse movieResponse = restTemplate.getForObject(urlDoPage+brojStranice+apiKey, MovieResponse.class);
        movieDbService.setujKonacniPathDoSlikeFilma(movieResponse.getMovieResults());
        theModel.addAttribute("nowPlayingMovies", movieResponse.getMovieResults());     //vraca 20 filmova(jer jedan MovieResponse objekat ce uzeti 20 filmova iz page=1)
        List<Integer> brojeviStranica = new ArrayList<>();
        for(int i=1; i<51; i++){
            brojeviStranica.add(i);
        }
        theModel.addAttribute("brojeviStranica", brojeviStranica);
        brojStraniceNaKojuJeUserKliknuo=brojStranice;//dodao da mogu da kada user klikne add to favourites - da prosledim i broj stranice pa da
        //ga redirektujem na bas tu stranicu iz metode gde odradjujem add to favourties a zove se /dodajFilmUFavourites
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
    /*@GetMapping("/mostRecentAndPopularMovieList")
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
    }*/


                                    //*************DEO ZA FAVOURITES MOVIES**************
    //http://localhost:8088/dodajFilmUFavourites?nazivFilma=Dilwale%20Dulhania%20Le%20Jayenge   kada kliknem na fovourites
    @PostMapping("/dodajFilmUFavourites")   //spakovao sam ove vrednosti u thymeleaf-u da bih detalje uneo u bazu za tog usera
    public String addToFavourites(@RequestParam("title") String title, @RequestParam("moviePosterPath") String moviePosterPath, @RequestParam("releaseDate") String releaseDate, @RequestParam("popularity") int popularity, @RequestParam("movieVotesAverage") double movieVotesAverage, @RequestParam("numberOfPeopleVoted") int numberOfPeopleVoted, @RequestParam("vrstaFilmova") String vrstaFilmova, Authentication authentication, Model theModel, RedirectAttributes ra){
        System.out.println("MOVIE OBJEKAT IZ THYMELEAF-A , TITLE " + title + "     MOVIE POSTER PATH " + moviePosterPath);
        System.out.println("BROJ TRENUTNE STRANICE NA KOJU JE USER KLIKNUO " + brojStraniceNaKojuJeUserKliknuo);
        System.out.println("DA LI JE HOME MOVIES " + vrstaFilmova);

        //ako vec imam taj film u favourites-ima, ne dodaj ga
        Users userFromDb = userRepository.findByUsername(authentication.getName());
        UserFavouriteMovie daLiPostojiUBaziUserFavouriteMovie = userFavouriteMovieRepository.findByTitleContainingAndUsers(title, userFromDb);
        if(daLiPostojiUBaziUserFavouriteMovie != null){
            ra.addFlashAttribute("message100", "Movie is already on the list!");
        }else {
            String username = authentication.getName();
            System.out.println("USER USERNAME UZET PREKO AUTHENTICATION KLASE         " + username);
            Users user = userRepository.findByUsername(username);
            int userId = user.getUserId();
            System.out.println("USER ID " + userId);
            UserFavouriteMovie userFavouriteMovie = new UserFavouriteMovie();
            userFavouriteMovie.setUsers(user);
            userFavouriteMovie.setTitle(title);
            userFavouriteMovie.setMoviePosterPath(moviePosterPath);
            userFavouriteMovie.setReleaseDate(releaseDate);
            userFavouriteMovie.setPopularity(popularity);
            userFavouriteMovie.setMovieVotesAverage(movieVotesAverage);
            userFavouriteMovie.setNumberOfPeopleVoted(numberOfPeopleVoted);
            String imdbMovieId = movieDbService.imdbPutanjaPomocuNazivaFilma(title);
            userFavouriteMovie.setMovieImdbId(imdbMovieId);
            userFavouriteMovieRepository.save(userFavouriteMovie);
        }

        if(vrstaFilmova.equalsIgnoreCase("homeMovies"))
            return proveriBrojStraniceZaPaginacijuTopRatedMoviesHomePage(brojStraniceNaKojuJeUserKliknuo, theModel);//redirektuje(ostavlja)me na stranici na kojoj sam film dodao u favourites
        else if(vrstaFilmova.equalsIgnoreCase("newPopularMovies"))
            return proveriBrojStraniceZaPaginacijuPopularMovies(brojStraniceNaKojuJeUserKliknuo, theModel);//redirektuje(ostavlja)me na stranici na kojoj sam film dodao u favourites
        else if(vrstaFilmova.equalsIgnoreCase("upcomingMovies"))
            return proveriBrojStraniceZaPaginacijuUpcomingMovies(brojStraniceNaKojuJeUserKliknuo, theModel);//redirektuje(ostavlja)me na stranici na kojoj sam film dodao u favourites
        //else if(vrstaFilmova.equalsIgnoreCase("nowPlayingMovies"))
        else
            return proveriBrojStraniceZaPaginacijuNowPlayingMovies(brojStraniceNaKojuJeUserKliknuo, theModel);//redirektuje(ostavlja)me na stranici na kojoj sam film dodao u favourites
        //return "redirect:/";
        //SAMO SAM POSLAO NAZIV FILMA, MOZDA DA DIREKTNO KONTAKTIRAM MOVIE DB API PA DA POMOCU NAZIVA FILMA POKUPIM I OSTALE PODATKE VEZANE ZA FILM,
        //PA DA SACUVAM U BAZI A ONDA DA SMESTIM TOM USERU OVE PODATKE
        //return "";
    }

    @GetMapping("/showMyProfile")
    public String showMyProfile(Authentication authentication, Model theModel, RedirectAttributes ra){
        //ne moram ovo da hendlujem jer se nece desiti da neko ko nije ulogovan moze da dodje na putanju za svoje favourites filmove, a sve i da proba da rucno
        //ukuca ovaj endpoint izbacice mu error page
        /*if(authentication.getName() == "" || authentication.getName() == null){    //ako neko nasilno proba da udje na putanju ovu da vidi svoje filmove a nije ulogovan, bacice error jer nema username-a za njega u bazi
            //ra.addFlashAttribute("message100", "Movie is already on the list!");              //pa cu samo da ga redirektujem na home page
            return "redirect:/";
        }*/
        Users user = userRepository.findByUsername(authentication.getName());
        //List<UserFavouriteMovie> userFavouriteMovieList = userFavouriteMovieRepository.findByUsers(user.getUserId());
        List<UserFavouriteMovie> userFavouriteMovieList = userFavouriteMovieRepository.findByUsers(user);
        theModel.addAttribute("userFavouriteMovies", userFavouriteMovieList);
        return "userProfile";//userProfile
    }

    @GetMapping("/searchMovie")                //dodao zbog search-a
    public String searchMovie(@RequestParam("searchedMovieName")String searchedMovieName, /*BindingResult bindingResult, */RedirectAttributes ra, Model theModel, Authentication authentication){
        System.out.println("USAO U MOVIES SEARCH MOVIE, MOVIE NAME " + searchedMovieName);

        if(searchedMovieName == "" || searchedMovieName == null){
            ra.addFlashAttribute("message1", "Movie name cannot be empty!");
            return "redirect:/showMyProfile";
        }

        Users user = userRepository.findByUsername(authentication.getName());
        UserFavouriteMovie userFavouriteMovie = userFavouriteMovieRepository.findByTitleContainingAndUsers(searchedMovieName, user);
        //ako nema tog filma koji sam trazio vratice null i pucace
        if(userFavouriteMovie == null){
            ra.addFlashAttribute("message10", "Movie is not in list!");
            return "redirect:/showMyProfile";
        }

        List<UserFavouriteMovie> userFavouriteMovieList = new ArrayList<>();
        userFavouriteMovieList.add(userFavouriteMovie);

        theModel.addAttribute("userFavouriteMovies", userFavouriteMovieList);
        return "userProfile";
    }

    @GetMapping("/removeMovieFromFavourites")
    public String removeMovieFromFavourites(@RequestParam("nazivFilma")String nazivFilma, Authentication authentication){
        Users user = userRepository.findByUsername(authentication.getName());
        UserFavouriteMovie userFavouriteMovie = userFavouriteMovieRepository.findByTitleContainingAndUsers(nazivFilma, user);
        userFavouriteMovieRepository.delete(userFavouriteMovie);
        //userFavouriteMovieRepository.deleteByTitle(nazivFilma);
        return "redirect:/showMyProfile";
    }

}
