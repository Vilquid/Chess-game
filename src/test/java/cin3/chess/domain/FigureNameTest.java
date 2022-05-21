package cin3.chess.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FigureNameTest
{
	@Test
	public void stringToFigureNameTest() {
		assertThat(FigureName.stringToFigureName("queen")).isEqualTo(FigureName.QUEEN);
		assertThat(FigureName.stringToFigureName("king")).isEqualTo(FigureName.KING);
		assertThat(FigureName.stringToFigureName("rook")).isEqualTo(FigureName.ROOK);
		assertThat(FigureName.stringToFigureName("bishop")).isEqualTo(FigureName.BISHOP);
		assertThat(FigureName.stringToFigureName("knight")).isEqualTo(FigureName.KNIGHT);
		assertThat(FigureName.stringToFigureName("pawn")).isEqualTo(FigureName.PAWN);
		assertThat(FigureName.stringToFigureName("King")).isNull();
	}
}
