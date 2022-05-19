package cin3.chess.domain;

public enum FigureName
{
	KING(0),
	QUEEN(1),
	ROOK(2),
	BISHOP(3),
	KNIGHT(4),
	PAWN(5);


	private final int value;

	FigureName(int value)
	{
		this.value = value;
	}

	public static FigureName stringToFigureName(String name)
	{
		FigureName out = switch (name) {
			case "rook" -> FigureName.ROOK;
			case "king" -> FigureName.KING;
			case "queen" -> FigureName.QUEEN;
			case "bishop" -> FigureName.BISHOP;
			case "pawn" -> FigureName.PAWN;
			case "knight" -> FigureName.KNIGHT;
			default -> null;
		};

		return out;
	}
}

