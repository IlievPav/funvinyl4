package bg.softuni.funvinyl.config;

import bg.softuni.funvinyl.service.impl.VinylUserService;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

    private final VinylUserService vinylUserService;
    private final PasswordEncoder passwordEncoder;

    public ApplicationSecurityConfig(VinylUserService vinylUserService, PasswordEncoder passwordEncoder) {
        this.vinylUserService = vinylUserService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {


        http.authorizeRequests()
                // allow access to static resources to anyone
                .antMatchers("/js/**", "/css/**", "/image/**").permitAll()
                //  requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()==("/js/**", "/css/**", "/images/**").permitAll()
                // allow access to index, user login and registration to anyone
                .antMatchers("/", "/users/login", "/users/register", "/about").permitAll()
                // protect all other pages
                .antMatchers("/**").authenticated()
                .and()
                // configure login with HTML form
                .formLogin()
                // our login page will be served by the controller with mapping /users/login
                .loginPage("/users/login")
                // the name of the user name input field in OUR login form is username (optional)
                .usernameParameter(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY)
                // the name of the user password input field in OUR login form is password (optional)
                .passwordParameter(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_PASSWORD_KEY)
                // on login success redirect here
                .defaultSuccessUrl("/")
                // on login failure redirect here
                .failureForwardUrl("/users/login-error")
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID");

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(vinylUserService)
                .passwordEncoder(passwordEncoder);
    }
}
