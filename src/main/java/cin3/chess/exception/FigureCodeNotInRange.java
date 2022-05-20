package cin3.chess.exception;

public class FigureCodeNotInRange extends Exception
{
	public FigureCodeNotInRange()
	{
		super("Figure code must be between 0 and 6");
	}
}

