package cin3.chess.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GameListTest {
	private GameList g;

	@BeforeEach
	public void before() {
		g = new GameList();
	}

	/**
	 * It tests that the getters and setters work
	 */
	@Test
	public void gettersTest() {
		g.setId(0L);
		g.setWinner("winner");
		g.setLooser("looser");
		g.setGameId(2048L);

		assertThat(g.getId()).isEqualTo(0L);
		assertThat(g.getWinner()).isEqualTo("winner");
		assertThat(g.getLooser()).isEqualTo("looser");
		assertThat(g.getGameId()).isEqualTo(2048L);
	}
}
