package cin3.chess.config;


import cin3.chess.services.DbUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter
{
	@Autowired
	private DbUserDetailsService userDetailsService;

	/**
	 * If the user is not authenticated, then redirect to the login page. Otherwise, allow access to the requested resource;
	 *
	 * The first line of the function is a call to the authorizeRequests() method. This method is used to configure the
	 * authorization rules for the application
	 *
	 * @param http This is the main object that is used to configure the security of the application.
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception
	{
		http
				.authorizeRequests()
				.antMatchers("/resources/**", "/register").permitAll()
				.anyRequest().authenticated()
				.and()
				.formLogin()
				.loginPage("/login")
				.usernameParameter("email")
				.permitAll()
				.defaultSuccessUrl("/", true)
				.and()
				.logout()
				.logoutUrl("/logout")
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
				.invalidateHttpSession(true)
				.permitAll();
	}

	/**
	 * This function is called by the Spring Security framework to configure the authentication provider.
	 *
	 * @param auth This is the AuthenticationManagerBuilder object that is used to create an AuthenticationManager object.
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception
	{
		auth.authenticationProvider(authenticationProvider());
	}

	/**
	 * This function creates a new DaoAuthenticationProvider object, sets the userDetailsService to the userDetailsService
	 * object we created earlier, and sets the passwordEncoder to the encoder() function we created earlier
	 *
	 * @return A DaoAuthenticationProvider object.
	 */
	@Bean
	public DaoAuthenticationProvider authenticationProvider()
	{
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(encoder());

		return authProvider;
	}

	/**
	 * It creates a password encoder that uses the BCrypt algorithm.
	 *
	 * @return A PasswordEncoder object.
	 */
	@Bean
	public PasswordEncoder encoder()
	{
		// hashable Bcrypt 10 iterations
		return new BCryptPasswordEncoder(10);
	}

	/**
	 * The SpringSecurityDialect class is a dialect for Spring Security that provides a way to use the Spring Security tags
	 * inside of Thymeleaf
	 *
	 * @return A SpringSecurityDialect object.
	 */
	@Bean
	public SpringSecurityDialect springSecurityDialect()
	{
		return new SpringSecurityDialect();
	}

}
