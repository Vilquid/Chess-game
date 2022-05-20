package cin3.chess.config;

import cin3.chess.domain.Authority;
import cin3.chess.domain.User;
import cin3.chess.repository.AuthorityRepository;
import cin3.chess.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent>
{
	boolean alreadySetup = true;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AuthorityRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * @brief This method is called when the application context is refreshed.
	 * @param contextRefreshedEvent
	 * @return void
	 */
	@Override
	@Transactional // j'ai chopé le @Transactional de gradle
	public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent)
	{
		if (alreadySetup)
		{
			return;
		}

		createRoleIfNotFound("ROLE_ADMIN");
		createRoleIfNotFound("ROLE_USER");

		Authority adminRole = roleRepository.findByAuthority("ROLE_ADMIN");

		User user = new User();
		user.setUsername("springchess");
		user.setPassword(passwordEncoder.encode("springchess"));
		user.setEmail("spring.chess@domain.org");
		user.setAuthorities(Collections.singletonList(adminRole));
		userRepository.save(user);

		alreadySetup = true;
	}

	@Transactional
	void createRoleIfNotFound(String name)
	{
		Authority role = roleRepository.findByAuthority(name);
		if (role == null) {
			role = new Authority();
			role.setAuthority(name);
			roleRepository.save(role);
		}
	}
}
