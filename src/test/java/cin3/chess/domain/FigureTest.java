package cin3.chess.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FigureTest
{
	private Figure fig;

	@BeforeEach
	public void before()
	{
		fig = new Figure();
	}

	/**
	 * The function `setCodeTest()` tests the `setCode()` function
	 */
	@Test
	public void setCodeTest()
	{
		fig.setCode(-1);
		assertThat(fig.getCode()).isNull();

		fig.setCode(6);
		assertThat(fig.getCode()).isNull();

		fig.setCode(0);
		assertThat(fig.getCode()).isEqualTo(0);

		fig.setCode(1);
		assertThat(fig.getCode()).isEqualTo(1);

		fig.setCode(4);
		assertThat(fig.getCode()).isEqualTo(4);

		fig.setCode(5);
		assertThat(fig.getCode()).isEqualTo(5);
	}

	/**
	 * The function `setNameTest` tests the `setName` function
	 */
	@Test
	public void setNameTest()
	{
		fig.setName("dddd");
		assertThat(fig.getName()).isNull();

		fig.setName("king");
		assertThat(fig.getName()).isEqualTo("king");
	}


	/**
	 * This function tests the getCountPlayed() function in the Figurine class
	 */
	@Test
	public void getCountPlayedTest()
	{
		assertThat(fig.getCountPlayed()).isEqualTo(0);
	}

	/**
	 * It tests that the getters and setters work
	 */
	@Test
	public void gettersTest()
	{
		Game g = new Game();

		fig.setOwner(0);
		fig.setId(0L);
		fig.setGame(g);
		fig.setY(0);
		fig.setX(1);

		assertThat(fig.getOwner()).isEqualTo(0);
		assertThat(fig.getId()).isEqualTo(0L);
		assertThat(fig.getGame()).isEqualTo(g);
		assertThat(fig.getY()).isEqualTo(0);
		assertThat(fig.getX()).isEqualTo(1);
	}

	/**
	 * The function `getMoveCode()` returns a string that represents the move code of the figure
	 */
	@Test
	public void getMoveCodeTest()
	{
		fig.setName("king");
		fig.setCode(FigureName.stringToFigureName("king").ordinal());
		fig.setX(1);
		fig.setY(2);

		assertThat(fig.getMoveCode()).isEqualTo("B6");
	}

	/**
	 * The function `updateCountPlayed()` should increment the countPlayed variable by 1
	 */
	@Test
	public void updateCountPlayed()
	{
		fig.updateCountPlayed();
		assertThat(fig.getCountPlayed()).isEqualTo(1);
	}

	/**
	 * It tests if the html code of a figure is correct
	 */
	@Test
	public void getHtmlCodeTest()
	{
		fig.setCode(FigureName.stringToFigureName("king").ordinal());
		fig.setOwner(PlayerName.WHITE.ordinal());
		assertThat(fig.getHtmlCode()).isEqualTo("&#9812;");

		fig.setOwner(PlayerName.BLACK.ordinal());
		assertThat(fig.getHtmlCode()).isEqualTo("&#9818;");
	}
}
