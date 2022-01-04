package com.igor.moviedb.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
//@EnableWebMvc             //OVO JE DO SPRING BOOT-A. U SPRING BOOT-U AKO OVO STAVIM SPRING CE DA ODRADI NEKU SVOJU KONFIGURACIJU ZA NEKE STVARI, A MOJU KONFIGURACIJU
public class Config extends WebSecurityConfigurerAdapter {//CE DA IGNORISE. DOK NISAM ZAKOMENTARISAO NPR NISAM MOGAO DA UCITAM SLIKU NIGDE

    @Qualifier("dataSource")    //nije htelo da radi bez dodavanja bas ovog Qualifier-a
    @Autowired
    DataSource dataSource;

    //Enable jdbc authentication    -   mora se navesti dataSource odozgo
    @Autowired
    public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication().dataSource(dataSource);
    }

    //deprecated
    /*@Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }*/

    //koristio
    /*@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }*/

    private String[] PUBLIC_MATCHERS = {//ovde stavljam one resurse(npr foldere sa slikama) ili putanje endopinta koje zelim da svi mogu da vide
            "/img/**",                  //a posto u ovoj aplikaciji zelim da regi/logi bude opcion, dozvoljavam im da sve vide, sem endpointa koje sam
                                        //naveo dole u konfiguraciji koje ce dozvoliti samo adminu da vidi sve u njegovom kontroleru sa /admin/**
            //"/css/**",		//BITAN MI JE FOLDER img DA BIH DOZVOLIO SLIKAMA DA SE PRIKAZUJU NA LOGIN STRANICI fancy-login
            "/js/**",
            "/"
    };
//"/users/showMyLoginPage/**",      //ova 3 reda su bila kada nisam dozvolio svima da vide sve "/**", da bi mogli cim se pokrene app da vide login formu i da proverim njihove kredencijale
//"/users/signUp/**",
//"/users/processNewSignUpUser/**",

    @Override
    protected void configure(HttpSecurity http) throws Exception {
            http
                //.httpBasic().and()
                .csrf().disable()
                .authorizeRequests()
//.antMatchers("/").permitAll()   //SVE JE RADILO OK, ALI JE BACALO NA LOGIN OBAVEZAN DOK NISAM UVEO DA SVI MOGU DA VIDE SVE, ALI SAMO CE USERI KOJI SE REGISTRUJU PA LOGINUJU
                                  //MOCI DA VIDE NPR OPCIJU ZA FAVOURITES, ADMIN DEO ZA ADMINA JER SAM TO NAVEO DOLE. DODAO IPAK U PUBLIC_MATCHERS DA NE DODAJEM DODATNO OVDE
                .antMatchers(PUBLIC_MATCHERS).permitAll()
                //.anyRequest().authenticated()	//svaki request koji "dolazi" na app mora biti autentifikovan
                //kada je ovo bilo aktivirano, svi KOJI SU BILI AUTENTIKOVANI su mogli da pristupaju npr linku za samo managere
                //a onda sam dodao ispod OVE REDOVE KO GDE SME, KAKO BI OGRANICIO KO GDE SME DA PRISTUPI.
                .antMatchers("/admin/**").hasRole("ADMIN")//svima dozvoli da se krecu svuda sem na ovoj putanji i nadalje(admin controller), to je za mene
                                                        //samo meni dozvoli da pristupim na ovaj i sve ostale endpointe(stranicu gde mogu da brisem koga zelim npr)
                                                        //u bazi se tabela mora zvati "authorities" i mora imati kolonu "authority" tu ce biti
                    .and()
                .formLogin()//.permitAll()
                    .loginPage("/users/showMyLoginPage").permitAll()
				.loginProcessingUrl("/users/checkUserCredentialsAtLogin")
				//.permitAll()*/	//dozvoljavamo svima da vide login page
                .and()
                    //.logout().permitAll()
                    //.and()
                    //.exceptionHandling().accessDeniedPage("/access-denied");	//morao da dodam u kontroler jer gadja "access-denied" putanju
                .logout()
                .logoutUrl("/logout").permitAll()	//DODAJEM MOGUCNOST/OPCIJU ZA LOGOUT !!!!!
                .clearAuthentication(true)      //nisam siguran koliko je ovo bitno
                .invalidateHttpSession(true)    //"onesposobi trenutnu sesiju" - nisam siguran koliko je ovo bitno
                .deleteCookies("JSESSIONID")
                .and()
                .exceptionHandling().accessDeniedPage("/access-denied");
    }

}
