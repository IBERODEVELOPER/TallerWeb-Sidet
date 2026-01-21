package com.ibero.demo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ibero.demo.auth.handler.LoginSuccesHandler;
import com.ibero.demo.interceptors.TemporaryPasswordFilter;
import com.ibero.demo.interceptors.TurnOffEmployeeFilter;
import com.ibero.demo.service.IUserServiceImpl;


@Configuration
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SpringSecurityConf {
	
	@Autowired
	private LoginSuccesHandler successHandler;
	
	@Autowired
	private IUserServiceImpl userDetailsService;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
    private TurnOffEmployeeFilter turnOffEmployeeFilter;
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
		.addFilterBefore(new TemporaryPasswordFilter(), UsernamePasswordAuthenticationFilter.class)
		.addFilterBefore(turnOffEmployeeFilter, UsernamePasswordAuthenticationFilter.class)
		.authorizeHttpRequests((authz) -> authz.requestMatchers("/login", "/css/**", "/js/**", "/images/**","/error/**","/send-email").permitAll()
				.anyRequest().authenticated())
				.formLogin(login -> login
						.successHandler(successHandler)
						.loginPage("/login") 
						.permitAll())
				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl("/login?logout")
						.permitAll());
				
		return http.build();
	}

	
	@Autowired
	public void configurerGlobal (AuthenticationManagerBuilder builder) throws Exception{
		builder.userDetailsService(userDetailsService)
		.passwordEncoder(passwordEncoder);
		}

}
