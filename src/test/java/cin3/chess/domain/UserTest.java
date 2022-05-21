package cin3.chess.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserTest {
	private User u;

	@BeforeEach
	public void before() {
		u = new User();
	}

	/**
	 * This function tests the getters and setters of the User class
	 */
	@Test
	public void gettersTest() {

		u.setId(2048L);
		u.setLogIn(true);
		u.setUsername("User");
		u.setPlaying(false);
		u.setEmail("user@test.com");
		u.setPassword("password");
		u.setConfirmPassword("password");

		assertThat(u.getId()).isEqualTo(2048L);
		assertThat(u.getLogIn()).isTrue();
		assertThat(u.getUsername()).isEqualTo("User");
		assertThat(u.getEmail()).isEqualTo("user@test.com");
		assertThat(u.getPlaying()).isFalse();
		assertThat(u.getPassword()).isEqualTo("password");
		assertThat(u.getConfirmPassword()).isEqualTo("password");
	}

	/**
	 * `isAccountNonExpiredTest()` tests that the `isAccountNonExpired()` method returns true
	 */
	@Test
	public void isAccountNonExpiredTest() {
		assertThat(u.isAccountNonExpired()).isTrue();
	}

	/**
	 * `isAccountNonLockedTest()` tests that the `isAccountNonLocked()` method returns true
	 */
	@Test
	public void isAccountNonLockedTest() {
		assertThat(u.isAccountNonLocked()).isTrue();
	}

	/**
	 * `isCredentialsNonExpiredTest()` tests that the `isCredentialsNonExpired()` function returns true
	 */
	@Test
	public void isCredentialsNonExpiredTest() {
		assertThat(u.isCredentialsNonExpired()).isTrue();
	}

	/**
	 * > This function tests that the user is enabled
	 */
	@Test
	public void isEnabledTest() {
		assertThat(u.isEnabled()).isTrue();
	}
}
