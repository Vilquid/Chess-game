package cin3.chess.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FriendRequestTest {
	private FriendRequest f;

	@BeforeEach
	public void before() {
		f = new FriendRequest();
	}

	/**
	 * It tests that the getters of the FriendRequest class return the values that were set by the setters
	 */
	@Test
	public void gettersTest() {
		User u = new User();

		f.setId(2048L);
		f.setSender("Sender");
		f.setReceiver(u);
		f.setAccepted(true);

		assertThat(f.getId()).isEqualTo(2048L);
		assertThat(f.getSender()).isEqualTo("Sender");
		assertThat(f.getAccepted()).isTrue();
		assertThat(f.getReceiver()).isEqualTo(u);

	}
}
