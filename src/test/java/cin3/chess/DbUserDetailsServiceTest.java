package cin3.chess;

import cin3.chess.domain.User;
import cin3.chess.form.UserForm;
import cin3.chess.repository.AuthorityRepository;
import cin3.chess.repository.UserRepository;
import cin3.chess.services.DbUserDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public class DbUserDetailsServiceTest
{
	@Mock
	private UserRepository users;

	@Mock
	private AuthorityRepository roles;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private DbUserDetailsService service;

	@Test
	public void createFormNullTest()
	{
		UserForm result = service.createForm(null);

		assertThat(result).isNotNull();
	}

	@Test
	public void createFormTest()
	{
		User u = new User();
		u.setId(3L);
		u.setEmail("foo@gmail.com");
		u.setUsername("john.doe");

		UserForm result = service.createForm(u);
		assertThat(result.getEmail()).isEqualTo("foo@gmail.com");
		assertThat(result.getId()).isEqualTo(3L);
		assertThat(result.getUsername()).isEqualTo("john.doe");
	}

	@Test
	public void saveUserTest()
	{
		User u = new User();
		u.setId(0L);
		u.setEmail("foo@gmail.com");
		u.setUsername("john.doe");

		UserForm result = service.createForm(u);
		result.setPassword("azerty123$");
		result.setConfirmPassword("azerty123$");

		service.save(result);

		lenient().when(users.save(Mockito.any(User.class))).thenReturn(u);
	}
}

