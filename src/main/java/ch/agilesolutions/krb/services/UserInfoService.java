package ch.agilesolutions.krb.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserInfoService implements UserDetailsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserInfoService.class);

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		LOGGER.info(username);
		return new User(username, "notUsed", true, true, true, true, AuthorityUtils.createAuthorityList("ROLE_USER", "ROLE_ADMIN"));
	}
}
