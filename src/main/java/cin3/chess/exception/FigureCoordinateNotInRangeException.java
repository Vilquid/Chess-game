package cin3.chess.exception;

public class FigureCoordinateNotInRangeException extends Exception
{
	public FigureCoordinateNotInRangeException(String axisName)
	{
		super(axisName + " must be between 0 and 7");
	}
}

