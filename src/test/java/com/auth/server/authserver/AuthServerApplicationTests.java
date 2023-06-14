package com.auth.server.authserver;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.provisioning.UserDetailsManager;

import java.util.UUID;

@SpringBootTest
class AuthServerApplicationTests {

	/**
	 * 初始化客户端信息
	 */
	@Autowired
	private UserDetailsManager userDetailsManager;

	/**
	 * 创建用户信息
	 */
	@Test
	void testSaveUser() {
		UserDetails userDetails = User.builder().passwordEncoder(s -> "{bcrypt}" + new BCryptPasswordEncoder().encode(s))
//		UserDetails userDetails = User.builder().passwordEncoder(s -> new BCryptPasswordEncoder().encode(s))
				.username("user")
				.password("password")
				.roles("ADMIN")
				.build();
		userDetailsManager.createUser(userDetails);

		new BCryptPasswordEncoder().matches("password", "$10$Nx6ro8ZfWaqUUH6vvonm.uKD7XCqCob9jHQegn7/sZZL0ZlSZUG7G");
	}

	@Test
	void testPassword() {
		boolean result = new BCryptPasswordEncoder().matches("password",
				"{bcrypt}$2a$10$Ws5J80WQald/nPeFsMpCYe3u5aAMNjdiF5bORfy/ot4OJH/cUfb3.");

		System.out.println(result);

		boolean result1 = new BCryptPasswordEncoder().matches("password",
				"$2a$10$Ws5J80WQald/nPeFsMpCYe3u5aAMNjdiF5bORfy/ot4OJH/cUfb3.");
		System.out.println(result1);
	}

	/**
	 * 创建clientId信息
	 */
	@Autowired
	private RegisteredClientRepository registeredClientRepository;

	@Test
	void testSaveClient() {
		RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
				.clientId("messaging-client")
				// 这里加上{bcrypt}存入数据库， 就不用在SecurityConfig里注入PasswordEncoder了, 如果注入了反而不生效
				.clientSecret("{bcrypt}" + new BCryptPasswordEncoder().encode("secret"))
//				.clientSecret(new BCryptPasswordEncoder().encode("secret"))
				.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
				.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
				.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
				.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
				.redirectUri("http://127.0.0.1:8080/login/oauth2/code/messaging-client-oidc")
				.redirectUri("http://127.0.0.1:8080/authorized")
				.scope(OidcScopes.OPENID).scope("message.read")
				.scope("message.write")
				.clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
				.build();
		registeredClientRepository.save(registeredClient);
	}

}
