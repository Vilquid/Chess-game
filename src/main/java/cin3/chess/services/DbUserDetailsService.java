package cin3.chess.services;

import cin3.chess.domain.User;
import cin3.chess.form.UserForm;
import cin3.chess.repository.AuthorityRepository;
import cin3.chess.repository.UserRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.logging.Logger;

@Service
public class DbUserDetailsService implements UserDetailsService
{
	@Autowired
	private UserRepository users;

	@Autowired
	private AuthorityRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

//	private Logger logger = LoggerFactory.getLogger(DbUserDetailsService.class);
	private Logger logger = (Logger) LoggerFactory.getLogger(DbUserDetailsService.class);

	/**
	 * Create a UserForm object from a User object.
	 *
	 * @param user The user object to be converted to a form.
	 * @return A UserForm object
	 */
	public UserForm createForm(User user)
	{
		UserForm form = new UserForm();
		if (user == null)
		{
			return form;
		}

		form.setId(user.getId());
		form.setEmail(user.getEmail());
		form.setUsername(user.getUsername());
		return form;
	}

	/**
	 * We create a new user, set the id, username, email, password, and authorities, and then save the user
	 *
	 * @param userForm The UserForm object that was submitted by the user.
	 */
	public void save(UserForm userForm)
	{
		User user = new User();
		user.setId(userForm.getId());
		user.setUsername(userForm.getUsername());
		user.setEmail(userForm.getEmail());
		user.setPassword(passwordEncoder.encode(userForm.getPassword()));
		user.setAuthorities(Collections.singletonList(roleRepository.findByAuthority("ROLE_USER")));
		users.save(user);
	}

	// pas sur que j'ai fait le bon import ici
	/**
	 * > The function takes in a username (email) and returns a UserDetails object
	 *
	 * @param email The email of the user that we want to load.
	 * @return UserDetails
	 */
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException
	{
		UserDetails user = users.findByEmail(email);
		if (user == null)
		{
			throw new UsernameNotFoundException("user not found with the corresponding email");
		}
		return user;
	}
}
