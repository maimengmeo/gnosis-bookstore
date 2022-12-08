package ca.sheridancollege.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

import javax.sql.DataSource;

@SuppressWarnings("deprecation")
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{

	private LoggingAccessDeniedHandler accessDeniedHandler;
	private BCryptPasswordEncoder passwordEncoder;
	private DataSource dataScource;
	
	public SecurityConfig(LoggingAccessDeniedHandler accessDeniedHandler,
							@Lazy BCryptPasswordEncoder passwordEncoder,
							DataSource dataSource) {
		this.accessDeniedHandler = accessDeniedHandler;
		this.passwordEncoder = passwordEncoder;
		this.dataScource = dataSource;
	}
	
	@Bean
	public BCryptPasswordEncoder createPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public JdbcUserDetailsManager jdbcUserDetailsManager() throws Exception {
		
		JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager();
		
		jdbcUserDetailsManager.setDataSource(dataScource);
		
		return jdbcUserDetailsManager; 
	}
	
	@Override
	public void configure(HttpSecurity http) throws Exception {
		
		http.authorizeRequests()
			.antMatchers("/client/**").hasAnyRole("CLIENT")
			.antMatchers("/secured/**").hasAnyRole("CLIENT","STAFF")
			.antMatchers("/staff/**").hasRole("STAFF")
			.antMatchers("/h2-console/**").permitAll()
			.antMatchers("/", "/**").permitAll()
			.and()
			.formLogin().loginPage("/login")
			.defaultSuccessUrl("/secured")
			.and()
			.logout().invalidateHttpSession(true)
			.clearAuthentication(true)
			.and()
			.exceptionHandling()
			.accessDeniedHandler(accessDeniedHandler);
			
		http.csrf().disable();
		http.headers().frameOptions().disable();

	}
	
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		auth.jdbcAuthentication()
			.dataSource(dataScource)
			.withDefaultSchema()
			.passwordEncoder(passwordEncoder)
			
			.withUser("cleo").password(passwordEncoder.encode("doggo")).roles("CLIENT", "STAFF")
			.and()
			.withUser("beef").password(passwordEncoder.encode("cow")).roles("CLIENT");
	
	
	}
}
