package ch.agilesolutions.krb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.kerberos.authentication.KerberosAuthenticationProvider;
import org.springframework.security.kerberos.authentication.KerberosServiceAuthenticationProvider;
import org.springframework.security.kerberos.authentication.sun.SunJaasKerberosClient;
import org.springframework.security.kerberos.authentication.sun.SunJaasKerberosTicketValidator;
import org.springframework.security.kerberos.web.authentication.SpnegoAuthenticationProcessingFilter;
import org.springframework.security.kerberos.web.authentication.SpnegoEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import ch.agilesolutions.krb.services.UserInfoService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Value("${security.krb.keytab}")
	private String keytab;

	@Value("${security.krb.spn}")
	private String spn;
	
	@Bean(name = BeanIds.AUTHENTICATION_MANAGER)
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
 		return super.authenticationManagerBean();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.exceptionHandling().authenticationEntryPoint(spnegoEntryPoint()).and().authorizeRequests().antMatchers("/resources/**").permitAll().anyRequest().authenticated().and().formLogin().loginPage("/login").failureUrl("/login?error")
				.permitAll().and().logout().permitAll().and().addFilterBefore(spnegoAuthenticationProcessingFilter(authenticationManagerBean()), BasicAuthenticationFilter.class);
	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(kerberosAuthenticationProvider()).authenticationProvider(kerberosServiceAuthenticationProvider());
	}

	@Bean
	public KerberosAuthenticationProvider kerberosAuthenticationProvider() {
		KerberosAuthenticationProvider provider = new KerberosAuthenticationProvider();
		SunJaasKerberosClient client = new SunJaasKerberosClient();
		client.setDebug(true);
		provider.setKerberosClient(client);
		provider.setUserDetailsService(userInfoService());
		return provider;
	}

	@Bean
	public SpnegoEntryPoint spnegoEntryPoint() {
		return new SpnegoEntryPoint("/login");
	}

	@Bean
	public SpnegoAuthenticationProcessingFilter spnegoAuthenticationProcessingFilter(AuthenticationManager authenticationManager) {
		SpnegoAuthenticationProcessingFilter filter = new SpnegoAuthenticationProcessingFilter();
		filter.setAuthenticationManager(authenticationManager);
		return filter;
	}

	@Bean
	public KerberosServiceAuthenticationProvider kerberosServiceAuthenticationProvider() {
		KerberosServiceAuthenticationProvider provider = new KerberosServiceAuthenticationProvider();
		provider.setTicketValidator(sunJaasKerberosTicketValidator());
		provider.setUserDetailsService(userInfoService());
		return provider;
	}

	@Bean
	public SunJaasKerberosTicketValidator sunJaasKerberosTicketValidator() {
		SunJaasKerberosTicketValidator ticketValidator = new SunJaasKerberosTicketValidator();
		ticketValidator.setServicePrincipal(spn);
		ticketValidator.setKeyTabLocation(new FileSystemResource(keytab));
		ticketValidator.setDebug(true);
		return ticketValidator;
	}

	@Bean
	public UserInfoService userInfoService() {
		return new UserInfoService();
	}

}
