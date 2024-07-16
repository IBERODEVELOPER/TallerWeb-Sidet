package com.ibero.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;


@Configuration
@EnableWebSecurity
public class SpringSecurityConf {
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests((authz) -> authz.requestMatchers("/", "/css/**", "/js/**", "/images/**","/error/**", "/peoples/listPeople").permitAll()
				.requestMatchers("/peoples/listPeople").hasAnyRole("USER")
				.requestMatchers("/user/userReg").hasAnyRole("ADMIN")
				.requestMatchers("/peoples/**").hasAnyRole("ADMIN")
				.requestMatchers("/rol/**").hasAnyRole("ADMIN")
				.anyRequest().authenticated())
				.formLogin(login -> login.loginPage("/login") .permitAll())
				.logout(logout -> logout.permitAll());

		return http.build();
	}
	
	@Bean
	public static BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Autowired
	public void configurerGlobal (AuthenticationManagerBuilder builder) throws Exception{
		PasswordEncoder encoder = passwordEncoder();
		UserBuilder users = User.builder().passwordEncoder(encoder::encode);
		builder.inMemoryAuthentication()
		.withUser(users.username("Jose").password("12345").roles("ADMIN","USER"))
		.withUser(users.username("Jack").password("12345").roles("USER"));
	}

	

}
