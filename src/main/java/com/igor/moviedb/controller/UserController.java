package com.igor.moviedb.controller;

import com.igor.moviedb.model.user.Authorities;
import com.igor.moviedb.model.user.Users;
import com.igor.moviedb.repository.AuthoritiesRepository;
import com.igor.moviedb.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthoritiesRepository authoritiesRepository;

    /*@Autowired
    private PasswordEncoder passwordEncoder;*/

    //SPRING SECURITY NAS PRVO SALJE NA OVU PUTANJU /showMyLoginPage A TO SMO DEFINISALI U config klasi SecurityConfiguration.java, DA NAS PRVO SALJE TAMO PRI LOGIN-U
    //vise ne jer sam u config security klasi setovao da svi mogu da vide sve, a da kliknu na regi/logi dugme samo ako zele(dodacu deo za mene/admina da samo ja vidim)
    @GetMapping("/showMyLoginPage")
    public String showLoginPage(Model theModel){
        //AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        //theModel.addAttribute("authRequest", authenticationRequest);
        System.out.println("U login metodi kontrolera UserController");
        theModel.addAttribute("users", new Users());
        System.out.println("FANCY LOGIN USAO");
        return "fancy-login";
    }

    //dolazi ovde sa login forme, ako je uneo dobre kredencijale baci ga na home page "/", a to je top rated movies
    @PostMapping("/checkUserCredentialsAtLogin")
    public String checkUserCredentialsAfterLogin(@ModelAttribute("users")Users users/*, BindingResult bindingResult*/, RedirectAttributes ra){
        System.out.println("USAO U /checkUserCredentialsAtLogin, username " + users.getUsername() + " password " + users.getPassword());
        Users usersFromDb = userRepository.findByUsername(users.getUsername());
        System.out.println("USER IZ BAZE " + usersFromDb.getUsername() + " " + usersFromDb.getPassword());
        String thymeleafStranica = "";

        if(usersFromDb == null){
            //ne postoji taj users, mora opet da se loguje ili registruje
            //thymeleafStranica = "nijeUneoDobarPassPriLoginuIliGaNemaUBazi";
            System.out.println("NEMA USERA U BAZI PRI LOGINU, OPET LOGOVANJE");
            ra.addFlashAttribute("message1", "Invalid username, try again!");
            thymeleafStranica = "redirect:/users/showMyLoginPage";
        }
        //ako postoji users u bazi sa ovim username-om i ako je uneo dobar password
        else if(usersFromDb != null && usersFromDb.getPassword().equals(users.getPassword())){
            System.out.println("UNEO JE DOBAR PASSWORD, ULOGUJ GA");
            thymeleafStranica = "redirect:/";    //baci ga na home screen - na ispis top filmova
        }
        else if(usersFromDb != null && !usersFromDb.getPassword().equals(users.getPassword())){
            System.out.println("NIJE UNEO DOBAR PASSWORD");
            //thymeleafStranica = "nijeUneoDobarPassPriLoginuIliGaNemaUBazi";
            ra.addFlashAttribute("message2", "Invalid password, try again!");
            thymeleafStranica = "redirect:/users/showMyLoginPage";
        }

        return thymeleafStranica;
    }

    //SIGN UP - SALJEM NA FORMU ZA REGISTRACIJU USERA
    //ODRADJUJEM SIGNUP PRICU, U config klasi SecurityConfiguration.java sam dodao u private String[] PUBLIC_MATCHERS = {...,"/signUp/**"} i tako dozvolio svima da odrade SIGNUP
    @GetMapping("/signUp")
    public String signUp(Model theModel){
        System.out.println("U SIGN UP-U SAM");
        Users users = new Users();
        theModel.addAttribute("users", users);
        return "signUpForma";
    }

    //OVDE DOLAZIM SA REGISTRACIONE FORME, AKO TAJ USERNAME NIJE ZAUZET U BAZI, SACUVAJ GA(I SVIMA PO DEFAULTU SETUJEM ROLU "users", SAMO SAM JA ADMIN PA CU MOCI
    //DA VIDIM NEKE STVARI KOJE OSTALI NE MOGU, MOCI CU DA NPR BRISEM I UPDATE-UJEM USERE. KADA SE REGISTRUJE BACAM GA NA LOGIN PAGE
    @PostMapping("/processNewSignUpUser")
    public String processNewSignUpUser(@Valid @ModelAttribute("users")Users users, BindingResult bindingResult, RedirectAttributes ra){
        //ne radi validator ne znam zasto??? Pa sam morao da obradim i to - PRORADIO JER SAM OTISAO NA SPRING INITIALIZER SAJT I ODATLE MAZNUO DEPENDENCY ZA VALIDATION
        String odgovor = "";

        if(bindingResult.hasErrors()) {
            odgovor = "signUpForma";
        }

        /*if((((users.getUsername() == null) || "".equals(users.getUsername())) || (users.getPassword() == null) || "".equals(users.getPassword()))){
            ra.addFlashAttribute("message4", "Username and password cannot be empty!");
            odgovor = "redirect:/users/signUp";
        } else if((users.getUsername().length() < 4 || users.getUsername().length() > 15) || (users.getPassword().length() < 4 || users.getPassword().length() > 15)){
            ra.addFlashAttribute("message5", "Username and password must contains between 4 and 15 characters!");
            odgovor = "redirect:/users/signUp";
        }*/
        //System.out.println("IME, PASSWORD, I MAIL " + users.getUsername() + " " + users.getPassword() + " " + users.getEmail());
        else if((users.getFirstName().equalsIgnoreCase("")  || users.getFirstName() == null) || (users.getLastName().equalsIgnoreCase("") || users.getLastName() == null)){
            ra.addFlashAttribute("message6", "You must enter First and Last Name!");
            odgovor = "redirect:/users/signUp";
        }else if(userRepository.findByUsername(users.getUsername()) == null){
            System.out.println("NEMA USERA U BAZI, SACUVAJ GA");
            System.out.println("USERNAME " + users.getUsername() + " PASSWORD " + users.getPassword());
            //List<Authorities> listAuthorities = new ArrayList<>();
            Authorities authorities = new Authorities();
            authorities.setAuthority("ROLE_USER");              //zakucavam svima koji se registruju rolu users-a u tabeli authorities, ako zelim ja mogu dati nekom
            //authorities.setUsers(users);                          //admina tako sto cu da mu setujem u tabeli authorities authority na "ROLE_ADMIN"
            authorities.setUsername(users.getUsername());
            //listAuthorities.add(authorities);
            //users.setAuthorities(listAuthorities);
            users.setEnabled(true);
            users.setUsername(users.getUsername());
            String setujPrvoVelikoSlovoImenaAkoNeUneseUser = users.getFirstName().substring(0, 1).toUpperCase() + users.getFirstName().substring(1);
            users.setFirstName(setujPrvoVelikoSlovoImenaAkoNeUneseUser);
            String setujPrvoVelikoSlovoPrezimenaAkoNeUneseUser = users.getLastName().substring(0, 1).toUpperCase() + users.getLastName().substring(1);
            users.setLastName(setujPrvoVelikoSlovoPrezimenaAkoNeUneseUser);
            //users.setPassword(passwordEncoder.encode(users.getPassword())); //bcrypt - bice npr ovako $2a$10$qPsz9vvwJv7EcLWvgrTHc.R./PQfCQKf7845kONAbnNezcTSPS6Ha
            users.setPassword("{noop}"+users.getPassword());              //ali ne mogu da dekodiram tj vidim koji je password... nisam pronasao jos nigde
            //tako da cu samo da zakucam {noop} defaultni encoder - bice u bazi {noop}password
            //ja navedem password, spring kad vidi {noop} to ignorise, cita samo taj pass posle

            authoritiesRepository.save(authorities);
            userRepository.save(users);
            ra.addFlashAttribute("message", "Success! Your registration is now complete.");
            odgovor = "redirect:/users/showMyLoginPage";
        }else if(userRepository.findByUsername(users.getUsername()).getUsername().equalsIgnoreCase(users.getUsername())){
            bindingResult.addError(new FieldError("users", "username", "Username is already in use."));
            ra.addFlashAttribute("message2", "Username is already taken!");
            odgovor = "redirect:/users/signUp";
        /*else if(userRepository.findByUsername(users.getUsername()).getEmail().equalsIgnoreCase(users.getEmail())){
            ra.addFlashAttribute("message3", "Email is already taken!");
            odgovor = "redirect:/users/signUp";
        }*/
        }


        //ra.addFlashAttribute("message", "Success! Your registration is now complete.");
        //userRepository.save(users);

        //success-registration(link to login) or error-registration(link to login)
        //return "redirect:/users/showMyLoginPage";

        return odgovor;
    }

    @GetMapping("/userPath")
    public String userPath(){
        return "userPage";
    }

}
