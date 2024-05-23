package config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

// TODO-10: Enable method security
// - Add @EnableMethodSecurity annotation to this class

@Configuration
@EnableMethodSecurity
public class RestSecurityConfig {

	private static final String ADMIN = "ADMIN";
	private static final String SUPER_ADMIN = "SUPERADMIN";
	private static final String USER = "USER";

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		// @formatter:off
        http.authorizeHttpRequests((authz) -> authz
                // TODO-04: Configure authorization using requestMatchers method
                // - Allow DELETE on the /accounts resource (or any sub-resource)
                //   for "SUPERADMIN" role only
                // - Allow POST or PUT on the /accounts resource (or any sub-resource)
                //   for "ADMIN" or "SUPERADMIN" role only
                // - Allow GET on the /accounts resource (or any sub-resource)
                //   for all roles - "USER", "ADMIN", "SUPERADMIN"
        		// - Allow GET on the /authorities resource
                //   for all roles - "USER", "ADMIN", "SUPERADMIN"

                // Deny any request that doesn't match any authorization rule
				.requestMatchers(HttpMethod.DELETE, "/accounts/**").hasRole(SUPER_ADMIN)
				.requestMatchers(HttpMethod.POST, "/accounts/**").hasAnyRole(ADMIN, SUPER_ADMIN)
				.requestMatchers(HttpMethod.PUT, "/accounts/**").hasAnyRole(ADMIN, SUPER_ADMIN)
				.requestMatchers(HttpMethod.GET, "/accounts/**").hasAnyRole(ADMIN, SUPER_ADMIN, USER)
				.requestMatchers(HttpMethod.GET, "/authorities").hasAnyRole(ADMIN, SUPER_ADMIN, USER)
                .anyRequest().denyAll())
        .httpBasic(withDefaults())
        .csrf(CsrfConfigurer::disable);
        // @formatter:on

        return http.build();
	}

	// TODO-14b (Optional): Remove the InMemoryUserDetailsManager definition
	// - Comment the @Bean annotation below
	
	//@Bean
    public InMemoryUserDetailsManager userDetailsService(PasswordEncoder passwordEncoder) {

		// TODO-05: Add three users with corresponding roles:
		// - "user"/"user" with "USER" role (example code is provided below)
		// - "admin"/"admin" with "USER" and "ADMIN" roles
		// - "superadmin"/"superadmin" with "USER", "ADMIN", and "SUPERADMIN" roles
		// (Make sure to store the password in encoded form.)
    	// - pass all users in the InMemoryUserDetailsManager constructor
		UserDetails user = User.withUsername("user").password(passwordEncoder.encode("user")).roles(USER).build();
		var adminUser = User.withUsername("admin").password(passwordEncoder().encode("admin")).roles(ADMIN, USER).build();
		var supserAdminUser = User.withUsername("superadmin").password(passwordEncoder.encode("superadmin")).roles(SUPER_ADMIN, ADMIN, USER).build();

		return new InMemoryUserDetailsManager(user, adminUser, supserAdminUser);
	}
    
    @Bean
    public PasswordEncoder passwordEncoder() {
    	return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
