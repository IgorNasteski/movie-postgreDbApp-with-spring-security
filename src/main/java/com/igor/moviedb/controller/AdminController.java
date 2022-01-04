package com.igor.moviedb.controller;

import com.igor.moviedb.model.user.Authorities;
import com.igor.moviedb.model.user.Users;
import com.igor.moviedb.repository.AuthoritiesRepository;
import com.igor.moviedb.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthoritiesRepository authoritiesRepository;

    @GetMapping("/homeAdmin")
    public String adminPage(){
        System.out.println("USAO U ADMIN KONTROLER");
        return "adminPage";
    }

    @GetMapping("/sendToCrudUsersPage")
    public String crudUsers(Model theModel){
        List<Users> usersList = userRepository.findAll();
        List<Authorities> authoritiesList = authoritiesRepository.findAll();

        List<Users> listOfUsers = new ArrayList<>();
        for(int i=0; i<usersList.size(); i++){
            for(int j=0; j<authoritiesList.size(); j++){
                if(usersList.get(i).getUsername().equalsIgnoreCase(authoritiesList.get(j).getUsername())){
                    Users user = new Users();
                    user.setUserId(usersList.get(i).getUserId());
                    user.setUsername(usersList.get(i).getUsername());
                    user.setPassword(usersList.get(i).getPassword());
                    user.setEnabled(usersList.get(i).isEnabled());
                    user.setFirstName(usersList.get(i).getFirstName());
                    user.setLastName(usersList.get(i).getLastName());
                    user.setAuthority(authoritiesList.get(j).getAuthority());
                    listOfUsers.add(user);
                }
            }
        }
        theModel.addAttribute("users", listOfUsers);
        return "crudUsers";
    }

    /*@GetMapping("/showFormForUpdate")
    public String showFormForUpdate(@RequestParam("username")String username, Model theModel){
        Users user = userRepository.findByUsername(username);
        //UsersAndAuthorities usersAndAuthorities = new UsersAndAuthorities();
        theModel.addAttribute("user",user);
        return "addUser";
    }*/

    @GetMapping("/showFormForUpdate")
    public String showFormForUpdate(@RequestParam("userId")int userId, Model theModel){
        Optional<Users> userFromDb = userRepository.findById(userId);
        Users user = userFromDb.get();
        System.out.println("U METODI ZA UPDATE USER ID " + user.getUserId() + "    USERNAME " + user.getUsername() + "     PASSWORD " + user.getPassword() + "      ENABLED " + user.isEnabled());
        //UsersAndAuthorities usersAndAuthorities = new UsersAndAuthorities();
        theModel.addAttribute("user",user);

        //posto ne smem da menjam username korisniku kada izaberem update user u crudUsers.html, a hocu da mi samo ispise username u input polju(read only) onda ovde setujem neki
        //string npr readOnly u npr "readOnly" a u addUser.html u input polju za username ucinim polje read only ako se poklapa sa ovim stringom th:readonly="${makeUsernameReadOnly=='readOnly'}"
        String readOnly = "readOnly";
        theModel.addAttribute("makeUsernameReadOnly", readOnly);

        return "addUser";
    }

    //@RequestMapping(value = "/classesTable/saveProfessor/{professorId}/{classesId}")
    //public ModelAndView saveClassesProfessor(@RequestParam("classesId") long classesId,
                                             //@RequestParam("professorId") long professorId) {

    //public String showFormForDelete(@RequestParam(value = "userId", required = false)Integer userId, @RequestParam(value = "userAuthority", required = false)String userAuthority){

    @GetMapping("/showFormForDelete/{userId}/{userAuthority}")//POSTO SALJEM 2 PARAMETRA MORAM DA DODAM OVAJ DEO!!!!! {userId}/{userAuthority}
    public String showFormForDelete(@PathVariable(value = "userId")Integer userId, @PathVariable(value = "userAuthority")String userAuthority){
        System.out.println("        USER ID ZA BRISANJE " + userId + "     user authority " + userAuthority);
        Users users = userRepository.getById(userId);
        //List<Authorities> authorities = authoritiesRepository.findByUsername(users.getUsername());
        List<Authorities> authorities = authoritiesRepository.findByUsernameAndAuthority(users.getUsername(), userAuthority);
        for(Authorities authority : authorities){
            authoritiesRepository.delete(authority);    //obrisace svakako samo jedan record iz tabele authorities jer sam ovde sa thymeleaf-a prosledio rolu na cije dugme sam kliknu za delete
        }                                               //pa cu da obrisem samo taj record u tabeli authorities ako je admin, a user da ostavim
        if(userAuthority.equalsIgnoreCase("ROLE_USER"))
            userRepository.deleteById(userId);
        //return "redirect:/admin/homeAdmin";
        return "redirect:/admin/sendToCrudUsersPage";
    }

    @RequestMapping(value = "/save", method = { RequestMethod.GET, RequestMethod.POST })
    public String saveCustomer(/*@Valid*/ @ModelAttribute("user")Users user/*, BindingResult bindingResult*/, RedirectAttributes ra){
        System.out.println("USER U SAVE-U    USER ID " + user.getUserId() + "    USERNAME " + user.getUsername() + "     PASSWORD " + user.getPassword() + "      ENABLED " + user.isEnabled() + "     FIRST NAME " + user.getFirstName() + "      LAST NAME " + user.getLastName());
         if(user.getUserId() == 0) {         //ako je novi user id ce mu biti 0, ako je update nece (tj stici ce sa thymeleaf-a 0)
             user.setPassword("{noop}" + user.getPassword());
             userRepository.save(user);
             Authorities authorities = new Authorities();
             authorities.setUsername(user.getUsername());
             authorities.setAuthority("ROLE_USER");
             System.out.println("U IF-U SAVE-A ADMINA AUTHORITIES  " + authorities.getId() + "   " + authorities.getUsername() + "   " + authorities.getAuthority());
             authoritiesRepository.save(authorities);
         }else{//ako vec ima authorities za tog usera, nadji ga i promeni
             //user.setPassword("{noop}" + user.getPassword()); ako vec ima usera nema potrebe da mu se dodaje {noop} jer ga vec ima - dodace dvaput
             userRepository.save(user);
             List<Authorities> authorities = authoritiesRepository.findByUsername(user.getUsername());
             Authorities authority = authorities.get(0);
             System.out.println("U ELSU-U SAVE-A ADMINA AUTHORITIES  " + authority.getId() + "   " + authority.getUsername() + "   " + authority.getAuthority());
             authoritiesRepository.save(authority);
         }
        //return "redirect:/admin/homeAdmin";
        return "redirect:/admin/sendToCrudUsersPage";
    }

    @GetMapping("/showFormForAddUser")
    public String showAddForm(Model theModel){
        Users user = new Users();
        theModel.addAttribute("user", user);
        return "addUser";
    }

    @GetMapping("/searchUser")                //dodao zbog search-a
    public String adminsListCrudCustomers(@RequestParam("searchedUsername")String username, /*BindingResult bindingResult, */RedirectAttributes ra, Model theModel){
        System.out.println("USAO U ADMINS SEARCH USERS");

        //if(bindingResult.hasErrors()){
        if(username == "" || username == null){
            ra.addFlashAttribute("message1", "Username field cannot be empty!");
            return "redirect:/admin/sendToCrudUsersPage";
        }

        //Users searchedUser = userRepository.findByUsername(username);
        //List<Authorities> authorities = authoritiesRepository.findByUsername(username);
        Users searchedUser = userRepository.findByUsernameContaining(username);                     //bolje ovako jer cu moci da za admina unesem npr dm i ispisace mi ga
        List<Authorities> authorities = authoritiesRepository.findByUsernameContaining(username);   //moram obe tabele jer nisu povezane direktno, ali skladistim username u obe

        List<Users> listOfUsers = new ArrayList<>();
        for(int i=0; i<authorities.size(); i++){
            Users user = new Users(searchedUser.getUsername(), searchedUser.getPassword(), searchedUser.isEnabled(), searchedUser.getFirstName(), searchedUser.getLastName(),
                                                                                searchedUser.getUserMovies(), authorities.get(i).getAuthority());
            //searchedUser.setAuthority(authorities.get(i).getAuthority());
            listOfUsers.add(user);
        }

        theModel.addAttribute("users", listOfUsers);
        return "crudUsers";
    }

    @GetMapping("/showFormForAddRole")
    public String showFormForAddRole(@RequestParam("userId")int userId, Model theModel){
        //bice samo 2 polja, prvo je input polje username da bih video kome dodajem novu rolu, a drugo je input polje gde unosim rolu, a hocu da username bude read only
        String readOnly = "readOnly";
        theModel.addAttribute("makeUsernameReadOnly", readOnly);

        //Users user = new Users();
        Optional<Users> userFromDb = userRepository.findById(userId);
        Users user = userFromDb.get();
        theModel.addAttribute("user", user);
        return "adminAddRoleToUser";
    }

    //saveNewRole
    //kad useru dodam novu rolu, samo dodajem novi zapis u tabeli authorities(jer nije povezana sa tabelom users)
    @RequestMapping(value = "/saveNewRole", method = { RequestMethod.GET, RequestMethod.POST })
    public String saveNewRoleForUser(/*@Valid*/ @ModelAttribute("user")Users user/*, BindingResult bindingResult*/, RedirectAttributes ra){
        if(user.getAuthority() == "" || user.getAuthority() ==null){
            ra.addFlashAttribute("message10", "Authority field cannot be empty!");
            return "redirect:/admin/sendToCrudUsersPage";
        }else{
            Authorities authorities = new Authorities();
            authorities.setUsername(user.getUsername());
            authorities.setAuthority(user.getAuthority());
            authoritiesRepository.save(authorities);
            return "redirect:/admin/sendToCrudUsersPage";
            //return "crudUsers";
        }
    }

}
