package cin3.chess.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GameTest {
	private Game g;

	@BeforeEach
	public void before() {
		g = new Game();
	}

	/**
	 * It tests the getters and setters of the Game class
	 */
	@Test
	public void gettersTest() {
		User u1 = new User();
		User u2 = new User();

		Long time = System.currentTimeMillis();

		g.setId(0L);
		g.setEchec(0);
		g.setGameTime();
		g.setTimeWhitePlayer(time);
		g.setTimeBlackPlayer(time);
		g.setWhitePlayer(u1);
		g.setBlackPlayer(u2);
		g.setCurrentPlayer(0);
		g.setTimeCurrentPlayer(time);
		assertThat(g.getCurrentUser()).isEqualTo(u1);
		g.setCurrentPlayer(99);
		assertThat(g.getCurrentUser()).isEqualTo(null);
		g.setCurrentPlayer(1);
		g.setTimeCurrentPlayer(time);
		assertThat(g.getCurrentUser()).isEqualTo(u2);


		assertThat(g.getId()).isEqualTo(0L);
		assertThat(g.getWhitePlayer()).isEqualTo(u1);
		assertThat(g.getBlackPlayer()).isEqualTo(u2);
		assertThat(g.getEchec()).isEqualTo(0);
		assertThat(g.getGameTime()).isEqualTo(time);
		assertThat(g.getTimeWhitePlayer()).isEqualTo(time);
		assertThat(g.getTimeBlackPlayer()).isEqualTo(time);

	}

	/**
	 * > The function `changePlayer` should change the current player to the next player
	 */
	@Test
	public void changePlayerTest() {
		g.setCurrentPlayer(0);
		g.changePlayer();
		assertThat(g.getCurrentPlayer()).isEqualTo(1);
	}
}
