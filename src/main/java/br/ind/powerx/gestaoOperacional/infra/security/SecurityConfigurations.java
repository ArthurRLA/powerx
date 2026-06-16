package br.ind.powerx.gestaoOperacional.infra.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import br.ind.powerx.gestaoOperacional.services.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfigurations {

	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		return httpSecurity.authorizeHttpRequests(authorize -> authorize
				.requestMatchers("/styles/**", "/images/**", "/js/**").permitAll()
				.requestMatchers("/forgot-password", "/reset-password**").permitAll()
				.requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
				.requestMatchers(HttpMethod.GET, "/auth/login").permitAll()
				.requestMatchers(HttpMethod.GET, "/home").hasAnyRole("ADMIN", "USER")
				.requestMatchers(HttpMethod.GET, "/incentives").hasAnyRole("ADMIN", "USER")
				.requestMatchers(HttpMethod.GET, "/user/*").hasAnyRole("ADMIN", "USER")
				.requestMatchers(HttpMethod.GET, "/user/**").hasRole("ADMIN")
				.requestMatchers(HttpMethod.POST, "/user/**").hasRole("ADMIN")
				.requestMatchers(HttpMethod.GET, "/contacts").hasAnyRole("ADMIN", "USER")
				.requestMatchers(HttpMethod.GET, "/incentives/*").hasAnyRole("ADMIN", "USER")
				.requestMatchers(HttpMethod.GET, "/customers/*/employees").hasAnyRole("ADMIN", "USER")
				.requestMatchers(HttpMethod.POST, "/sale/calcule").hasAnyRole("ADMIN", "USER")
				.requestMatchers(HttpMethod.GET, "/reports/sale/*").hasAnyRole("ADMIN", "USER")
				.requestMatchers(HttpMethod.GET, "/reports/last").hasAnyRole("ADMIN", "USER")
				.requestMatchers(HttpMethod.GET, "/adm").hasRole("ADMIN")
				.requestMatchers(HttpMethod.GET, "/customers").hasRole("ADMIN")
				.requestMatchers(HttpMethod.POST, "/customers/**").hasRole("ADMIN")
				.requestMatchers(HttpMethod.GET, "/customers/**").hasRole("ADMIN")
				.requestMatchers(HttpMethod.GET, "/employees").hasRole("ADMIN")
				.requestMatchers(HttpMethod.POST, "/employees/**").hasRole("ADMIN")
				.requestMatchers(HttpMethod.GET, "/employees/**").hasRole("ADMIN")
				.requestMatchers(HttpMethod.GET, "/flags").hasRole("ADMIN")
				.requestMatchers(HttpMethod.POST, "/flags/**").hasRole("ADMIN")
				.requestMatchers(HttpMethod.GET, "/industry").hasRole("ADMIN")
				.requestMatchers(HttpMethod.POST, "/industry/**").hasRole("ADMIN")
				.requestMatchers(HttpMethod.GET, "/order").hasRole("ADMIN")
				.requestMatchers(HttpMethod.GET, "/order/state").hasRole("ADMIN")
				.requestMatchers(HttpMethod.GET, "/partner-groups").hasRole("ADMIN")
				.requestMatchers(HttpMethod.POST, "/partnet-groups/**").hasRole("ADMIN")
				.requestMatchers(HttpMethod.GET, "/payments").hasRole("ADMIN")
				.requestMatchers(HttpMethod.POST, "/payments/**").hasRole("ADMIN")
				.requestMatchers(HttpMethod.GET, "/payment-order").hasRole("ADMIN")
				.requestMatchers(HttpMethod.GET, "/payment-order/state").hasRole("ADMIN")
				.requestMatchers(HttpMethod.GET, "/products").hasRole("ADMIN")
				.requestMatchers(HttpMethod.GET, "/product-incentive-distribution").hasRole("ADMIN")
				.requestMatchers(HttpMethod.POST, "/products/**").hasRole("ADMIN")
				.requestMatchers(HttpMethod.GET, "/table-prices").hasRole("ADMIN")
				.requestMatchers(HttpMethod.POST, "/table-prices").hasRole("ADMIN")
				.requestMatchers(HttpMethod.GET, "/users").hasRole("ADMIN")
				.requestMatchers(HttpMethod.POST, "/users/**").hasRole("ADMIN")
				.requestMatchers(HttpMethod.GET, "/users/**").hasRole("ADMIN")
				.requestMatchers(HttpMethod.GET, "/revenues").hasRole("ADMIN")
				.anyRequest().authenticated())

				.formLogin(form -> form.loginPage("/auth/login").loginProcessingUrl("/auth/login")
						.usernameParameter("email").passwordParameter("password").defaultSuccessUrl("/home", true)
						.failureUrl("/auth/login?error=true").permitAll())

				.logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/auth/login?logout=true")
						.invalidateHttpSession(true).deleteCookies("JSESSIONID").permitAll())
				
				.exceptionHandling(exception -> exception.accessDeniedPage("/access-denied")).build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());

		return authProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
		return http.getSharedObject(AuthenticationManagerBuilder.class).authenticationProvider(authenticationProvider())
				.build();
	}
}
