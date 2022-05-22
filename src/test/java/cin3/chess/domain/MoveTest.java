package cin3.chess.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MoveTest {
	private Move m;

	@BeforeEach
	public void before() {
		m = new Move();
	}

	/**
	 * It tests that the getters and setters work
	 */
	@Test
	public void gettersTest() {
		Long time = System.currentTimeMillis();
		Game g = new Game();


		m.setId(1L);
		m.setTime(time);
		m.setPlayer(0);
		m.setPositionEnd("c1");
		m.setPositionStart("c3");
		m.setGame(g);

		assertThat(m.getId()).isEqualTo(1L);
		assertThat(m.getGame()).isEqualTo(g);
		assertThat(m.getPlayer()).isEqualTo(0);
		assertThat(m.getPositionEnd()).isEqualTo("c1");
		assertThat(m.getPositionStart()).isEqualTo("c3");
		assertThat(m.getTime()).isEqualTo(time);
	}
}
