package cin3.chess.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerNameTest
{
	User u = new User();

	@Test
	public void testPlayerName()
	{
		u.setUsername("Player1");
	}
}
