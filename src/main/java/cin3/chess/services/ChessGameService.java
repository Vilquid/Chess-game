package cin3.chess.services;

import cin3.chess.domain.Figure;
import cin3.chess.domain.FigureName;
import cin3.chess.domain.Game;
import cin3.chess.domain.PlayerName;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@Service
public class ChessGameService
{
	// to convert the time from milliseconds to seconds.
	private static final int S_CONVERT = 1000;

	// logger
//	private Logger logger = LoggerFactory.getLogger(ChessGameService.class);
	private Logger logger = (Logger) LoggerFactory.getLogger(ChessGameService.class);

	/**
	 * "Add a figure to the grid."
	 *
	 * The function is private, so it can only be called from within the class
	 *
	 * @param grid The list of figures that will be added to the game.
	 * @param game The game object that the figure is being added to.
	 * @param x The x coordinate of the figure
	 * @param y The y coordinate of the figure
	 * @param code The code of the figure.
	 * @param name The name of the figure.
	 * @param owner 0 = white, 1 = black
	 */
	private void addFigureToGrid(List<Figure> grid, Game game, int x, int y, int code, String name, int owner)
	{
		Figure fig = new Figure();

		fig.setCode(code);
		fig.setX(x);
		fig.setY(y);
		fig.setName(name);
		fig.setOwner(owner);
		fig.setGame(game);
		grid.add(fig);
	}

	/**
	 * generate a grid of chess with all the pawns
	 *
	 * @param game game that contains the grid
	 */
	public void generateGrid(final Game game)
	{
		// create a list of Figures
		List<Figure> grid = new ArrayList<>();

		for (int i = 0; i < Game.NUMBER_OF_PLAYER_IN_GAME; i++)
		{
			for (int j = 0; j < Game.WIDTH; j++)
			{
				String figName = Game.FIGURES_PLACEMENT.get(j);

				addFigureToGrid(grid, game, j, 5 * (1 - i) + 1, FigureName.PAWN.ordinal(), "pawn", i);
				addFigureToGrid(grid, game, j, 7 * (1 - i), FigureName.valueOf(figName.toUpperCase()).ordinal(), figName, i);
			}
		}
		// add the grid to the game
		game.setGrid(grid);
		logger.info("grid successfully generated");
	}

	/**
	 * Return the sign of a number
	 *
	 * @param v number to test
	 * @return 1 if positive, -1 if negative, 0 otherwise
	 */
	public int isPositive(int v)
	{
		return Integer.compare(v, 0);
	}

	/**
	 * It checks if the segment between two points is free of obstacles
	 *
	 * @param game the game object
	 * @param x1 the x coordinate of the first cell
	 * @param y1 the y coordinate of the first cell
	 * @param x2 the x coordinate of the destination cell
	 * @param y2 the y coordinate of the destination cell
	 * @return A boolean value.
	 */
	public boolean isSegmentFree(Game game, int x1, int y1, int x2, int y2)
	{
		int dx = isPositive(x2 - x1);
		int dy = isPositive(y2 - y1);

		// ignore the last cell
		x2 -= dx;
		y2 -= dy;

		while (x1 != x2 || y1 != y2)
		{
			x1 += dx;
			y1 += dy;
			if (!game.isCellFree(x1, y1))
			{
				return false;
			}
		}

		return true;
	}

	/**
	 * If the absolute value of the difference between the x and y coordinates of the starting position and the x and y
	 * coordinates of the ending position is the same, then the segment between the starting position and the ending position
	 * is free
	 *
	 * @param game The game object
	 * @param x The x coordinate of the piece you want to move
	 * @param y The y coordinate of the piece
	 * @param nx new x position
	 * @param ny new y position
	 * @return A boolean value.
	 */
	public boolean checkBishop(Game game, int x, int y, int nx, int ny)
	{
		if (Math.abs(x - nx) == Math.abs(y - ny))
		{
			return isSegmentFree(game, x, y, nx, ny);
		}

		return false;
	}

	/**
	 * "If the rook is moving along a row or column, and there are no pieces in the way, then the move is valid."
	 *
	 * The first if statement checks if the rook is moving along a row or column. If it is, then the function calls
	 * isSegmentFree to check if there are any pieces in the way. If there are no pieces in the way, then the move is valid
	 *
	 * @param game The game object
	 * @param x The x coordinate of the piece
	 * @param y The y coordinate of the piece
	 * @param nx new x position
	 * @param ny new y position
	 * @return A boolean value.
	 */
	public boolean checkRook(Game game, int x, int y, int nx, int ny)
	{
		if (x == nx || y == ny)
		{
			return isSegmentFree(game, x, y, nx, ny);
		}

		return false;
	}

	/**
	 * If the move is a bishop move or a rook move, then it's a valid queen move
	 *
	 * @param game The game object
	 * @param x The x coordinate of the piece you want to move
	 * @param y The y coordinate of the piece you want to move
	 * @param nx new x position
	 * @param ny new y position
	 * @return A boolean value.
	 */
	public boolean checkQueen(Game game, int x, int y, int nx, int ny)
	{
		return checkBishop(game, x, y, nx, ny) || checkRook(game, x, y, nx, ny);
	}

	/**
	 * If the king is moving two spaces, it's castling. Otherwise, it's moving one space
	 *
	 * @param game The game object
	 * @param f1 The figure that is being moved
	 * @param nx new x position
	 * @param ny new y position
	 * @return A boolean value.
	 */
	public boolean checkKing(Game game, Figure f1, int nx, int ny)
	{
		if (checkBigCastling(game, f1, nx, ny))
		{
			startCastling(game, 0, 3);
			return true;
		} else if (checkSmallCastling(game, f1, nx, ny))
		{
			startCastling(game, 7, 5);
			return true;
		} else
		{
			return Math.abs(f1.getX() - nx) <= 1 && Math.abs(f1.getY() - ny) <= 1;
		}
	}

	/**
	 * If the absolute value of the difference between the x coordinates is 1 and the absolute value of the difference between
	 * the y coordinates is 2, or if the absolute value of the difference between the x coordinates is 2 and the absolute
	 * value of the difference between the y coordinates is 1, then return true.
	 *
	 * @param x The x coordinate of the piece you're moving
	 * @param y The y coordinate of the piece
	 * @param nx new x position
	 * @param ny new y position
	 * @return A boolean value.
	 */
	public boolean checkKnight(int x, int y, int nx, int ny)
	{
		return (Math.abs(x - nx) == 1 && Math.abs(y - ny) == 2) ||
				(Math.abs(x - nx) == 2 && Math.abs(y - ny) == 1);
	}

	/**
	 * If the pawn is moving forward one square, and the square is empty, then the move is valid. If the pawn is moving
	 * forward two squares, and the first square is empty, and the second square is empty, then the move is valid. If the pawn
	 * is moving diagonally one square, and the square is occupied by an enemy piece, then the move is valid.
	 *
	 * @param game the game object
	 * @param f the figure to move
	 * @param nx new x position
	 * @param ny the new y position of the figure
	 * @return A boolean value.
	 */
	public boolean checkPawn(Game game, Figure f, int nx, int ny)
	{
		// get the owner of the figure
		int owner = f.getOwner();
		// compute the direction according to the owner
		int dy = Arrays.asList(-1, 1).get(owner);
		// y offset
		int py = f.getY() + dy;
		// get x of the figure
		int x = f.getX();

		// the move is strictly forward
		if (nx == x)
		{
			// verify the target cell is free and the pawn move by +1 on;y
			if (game.isCellFree(nx, ny) && ny == py)
			{
				return true;
			} else if (f.getY() == (5 * (1 - owner) + 1) && ny == (py + dy))
			{ // manage the move +2 at the beginning
				return game.isCellFree(x, py) && game.isCellFree(x, py + dy);
			}

		} else if (Math.abs(nx - x) == 1 && ny == py)
		{ // the move is in diagonal
			return game.getFigureAt(nx, ny) != null;
		}
		return false;
	}


	/**
	 * If the pawn is on the 4th or 3rd line, and the target cell is free, and the opposite pawn played only once and it
	 * position is on the right of the pawn, then the move is an en passant
	 *
	 * @param game The current game.
	 * @param pawn The pawn that is moving.
	 * @param nx The new x position of the pawn.
	 * @param ny the y coordinate of the target cell
	 * @return A boolean value.
	 */
	public boolean checkEnPassant(Game game, Figure pawn, int nx, int ny)
	{
		int player = game.getCurrentPlayer();
		// Theoretical pawn position to realize an en passant.
		int yPawn = (pawn.getOwner() == PlayerName.BLACK.ordinal()) ? 4 : 3;
		if (pawn.getY() == yPawn)
		{
			// verify the target cell is free
			if (game.getFigureAt(nx, ny) == null)
			{
				Figure oppositePawn = null;
				if(pawn.getX() - nx > 0 )
				{
					// verify the opposite pawn played only once and it position is on the right of the pawn
					oppositePawn = game.getFigureAt(pawn.getX() - 1, pawn.getY());
				}else if(pawn.getX() - nx < 0 )
				{
					oppositePawn = game.getFigureAt(pawn.getX() + 1, pawn.getY());
				}

				if (oppositePawn != null)
				{
					return oppositePawn.getOwner() == (1 - player) && oppositePawn.getCountPlayed() == 1;
				} else
				{
					return false;
				}
			}
		}

		return false;
	}

}
