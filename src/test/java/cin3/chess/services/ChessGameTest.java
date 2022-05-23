package cin3.chess.services;

import cin3.chess.domain.Figure;
import cin3.chess.domain.FigureName;
import cin3.chess.domain.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ChessGameTest
{
	private ChessGameService service;

	private Game game;

	@BeforeEach
	public void before()
	{
		service = new ChessGameService();
		game = new Game();
		service.generateGrid(game);
	}

	@Test
	public void isPositiveTest()
	{
		assertThat(service.isPositive(3)).isEqualTo(1);
		assertThat(service.isPositive(-23)).isEqualTo(-1);
		assertThat(service.isPositive(0)).isEqualTo(0);
	}

	@Test
	public void isSegmentFreeTest()
	{
		assertThat(service.isSegmentFree(game, 0, 2, 7, 2)).isTrue();
		assertThat(service.isSegmentFree(game, 2, 2, 5, 5)).isTrue();
		assertThat(service.isSegmentFree(game, 0, 7, 0, 2)).isFalse();
		assertThat(service.isSegmentFree(game, 2, 7, 7, 2)).isFalse();
	}

	@Test
	public void checkBishopTest()
	{
		// first diagonal
		assertThat(service.checkBishop(game, 2, 7, 0, 5)).isFalse();
		// move the pawn that is on the path of the bishop
		game.getFigureAt(1, 6).setY(5);
		assertThat(service.checkBishop(game, 2, 7, 0, 5)).isTrue();

		// second diagonal
		assertThat(service.checkBishop(game, 2, 7, 4, 5)).isFalse();
		// move the pawn that is on the path of the bishop
		game.getFigureAt(3, 6).setY(5);
		assertThat(service.checkBishop(game, 2, 7, 4, 5)).isTrue();

		// can't go in front even if with move the pawn in front of the bishop
		game.getFigureAt(2, 6).setY(3);
		assertThat(service.checkBishop(game, 2, 7, 2, 5)).isFalse();

		// can't go outside the map
		assertThat(service.checkBishop(game, 2, 7, -1, 8)).isFalse();
	}

	@Test
	public void checkRookTest()
	{
		// move the pawn in front of the left rook for testing
		game.getFigureAt(0, 6).setY(4);

		// can go forward
		assertThat(service.checkRook(game, 0, 7, 0, 5)).isTrue();

		// can go back
		assertThat(service.checkRook(game, 3, 4, 3, 5)).isTrue();

		// can go left
		assertThat(service.checkRook(game, 3, 4, 2, 4)).isTrue();

		// can go right
		assertThat(service.checkRook(game, 3, 4, 4, 4)).isTrue();

		// can't go outside the map
		assertThat(service.checkRook(game, 4, 4, -1, -1)).isFalse();

		// can't go in diagonal
		assertThat(service.checkRook(game, 3, 3, 4, 4)).isFalse();
	}

	@Test
	public void checkQueenTest()
	{
		// move the pawn in front of the left rook for testing
		game.getFigureAt(0, 6).setY(4);

		// can go forward
		assertThat(service.checkQueen(game, 0, 7, 0, 5)).isTrue();

		// can go back
		assertThat(service.checkQueen(game, 3, 4, 3, 5)).isTrue();

		// can go left
		assertThat(service.checkQueen(game, 3, 4, 2, 4)).isTrue();

		// can go right
		assertThat(service.checkQueen(game, 3, 4, 4, 4)).isTrue();

		// first diagonal
		assertThat(service.checkQueen(game, 2, 7, 0, 5)).isFalse();

		// move the pawn that is on the path of the bishop
		game.getFigureAt(1, 6).setY(5);
		assertThat(service.checkQueen(game, 2, 7, 0, 5)).isTrue();

		// second diagonal
		assertThat(service.checkQueen(game, 2, 7, 4, 5)).isFalse();
		// move the pawn that is on the path of the bishop
		game.getFigureAt(3, 6).setY(5);
		assertThat(service.checkQueen(game, 2, 7, 4, 5)).isTrue();

		// can't go outside the map
		assertThat(service.checkQueen(game, 4, 4, -1, -1)).isFalse();

	}

	@Test
	public void checkKingTest()
	{
		Figure f = game.getFigureAt(4, 7);
		f.setY(4);

		// can go forward
		assertThat(service.checkKing(game, f, 4, 3)).isTrue();

		// can go back
		assertThat(service.checkKing(game, f, 4, 5)).isTrue();

		// can go left
		assertThat(service.checkKing(game, f, 3, 4)).isTrue();

		// can go right
		assertThat(service.checkKing(game, f, 5, 4)).isTrue();

		// can go in diagonal top-right
		assertThat(service.checkKing(game, f, 5, 5)).isTrue();

		// can go in diagonal top-left
		assertThat(service.checkKing(game, f, 3, 5)).isTrue();

		// can go in diagonal bottom-right
		assertThat(service.checkKing(game, f, 5, 3)).isTrue();

		// can go in diagonal bottom-left
		assertThat(service.checkKing(game, f, 3, 3)).isTrue();

		// can't go outside the map
		assertThat(service.checkKing(game, f, -1, -1)).isFalse();
	}

	/**
	 * If the king is in check, it can't move to a square that is in check.
	 */
	@Test
	public void smallCastlingTest()
	{
		Figure f = game.getFigureAt(4, 7);

		// Small castling
		// Move knight and bishop
		game.getFigureAt(5, 7).setY(5);
		game.getFigureAt(6, 7).setY(5);

		assertThat(service.checkKing(game, f, 6, 7)).isTrue();
	}

	/**
	 * If the king is in the right position, and the rook is in the right position, and the king hasn't moved, and the rook
	 * hasn't moved, and there are no figures between the king and the rook, then the king can castle.
	 */
	@Test
	public void bigCastlingTest()
	{
		Figure f = game.getFigureAt(4, 7);

		// Big castling
		// Move knight and bishop
		game.getFigureAt(1, 7).setY(5);
		game.getFigureAt(2, 7).setY(5);
		game.getFigureAt(3, 7).setY(5);


		assertThat(service.checkKing(game, f, 2, 7)).isTrue();
	}

	/**
	 * The function checks if the knight can move from one position to another
	 */
	@Test
	public void checkKnightTest()
	{
		// can go forward-right
		assertThat(service.checkKnight(1,7, 2, 5)).isTrue();

		// can go forward-left
		assertThat(service.checkKnight(1,7, 0, 5)).isTrue();

		// can go right-top
		assertThat(service.checkKnight(1,4,3,3)).isTrue();

		// can go right-bottom
		assertThat(service.checkKnight(1,4, 3, 5)).isTrue();

		// can go left-top
		assertThat(service.checkKnight(3,4, 1, 3)).isTrue();

		// can go left-
		assertThat(service.checkKnight(3,4, 1, 5)).isTrue();

		// can't go in left
		assertThat(service.checkKnight(4, 4, 2, 4)).isFalse();

		// can't go in right
		assertThat(service.checkKnight(4, 4, 6, 4)).isFalse();

		// can't go in top
		assertThat(service.checkKnight(4, 4, 4, 2)).isFalse();

		// can't go in bottom
		assertThat(service.checkKnight(4, 4, 4, 6)).isFalse();
	}

	/**
	 * A pawn can move forward one or two squares, or kill an opponent in diagonal.
	 */
	@Test
	public void checkPawnTest()
	{
		Figure f = game.getFigureAt(0, 6);

		// starts by advancing 2 squares
		assertThat(service.checkPawn(game, f, 0, 4)).isTrue();

		// starts by advancing 1 squares
		assertThat(service.checkPawn(game, f, 0, 5)).isTrue();

		// cannot start by advancing 1 diagonal squares
		assertThat(service.checkPawn(game, f, 1, 5)).isFalse();

		f.setY(2);
		// kill opponent in diagonal right
		assertThat(service.checkPawn(game, f, 1, 1)).isTrue();

		f.setX(1);
		// kill opponent in diagonal left
		assertThat(service.checkPawn(game, f, 0, 1)).isTrue();

		f.setX(1);
		// cannot go back
		assertThat(service.checkPawn(game, f, 1, 3)).isFalse();
	}

	/**
	 * The function checks if the figure can do an en passant move
	 */
	@Test
	public void checkEnPassantTest()
	{
		// en passant test left position
		Figure f = game.getFigureAt(1, 6);
		assertThat(f).isNotNull();
		f.setY(3);

		game.getFigureAt(2, 1).setY(3);
		game.getFigureAt(2, 3).updateCountPlayed();

		assertThat(service.checkEnPassant(game, f, 2, 2)).isTrue();

		// en passant test right position
		Figure f2 = game.getFigureAt(6, 6);
		assertThat(f2).isNotNull();
		f2.setY(3);

		game.getFigureAt(5, 1).setY(3);
		game.getFigureAt(5, 3).updateCountPlayed();

		assertThat(service.checkEnPassant(game, f2, 5, 2)).isTrue();

		game.getFigureAt(5, 3).setY(4);
		assertThat(service.checkEnPassant(game, f2, 5, 2)).isFalse();

		f2.setY(f2.getY() + 1);
		assertThat(service.checkEnPassant(game, f2, 5, 2)).isFalse();
	}

	/**
	 * It checks if there is any figure between the start and end position of a figure
	 */
	@Test
	public void checkAnyTest()
	{
		//Pawn
		Figure f = game.getFigureAt(0,6);
		assertThat(service.checkAny(game, f, 0, 4)).isTrue();
		assertThat(service.checkAny(game, f, 1, 5)).isFalse();

		//Rook
		f = game.getFigureAt(0,7);
		f.setY(5);
		assertThat(service.checkAny(game, f, 0, 3)).isTrue();
		assertThat(service.checkAny(game, f, 4, 3)).isFalse();

		//Knight
		f = game.getFigureAt(1,7);
		assertThat(service.checkAny(game, f, 2, 5)).isTrue();
		assertThat(service.checkAny(game, f, 1, 4)).isFalse();

		//bishop
		f = game.getFigureAt(2,7);
		f.setY(5);
		assertThat(service.checkAny(game, f, 4, 3)).isTrue();
		assertThat(service.checkAny(game, f, 2, 3)).isFalse();

		//renne
		f = game.getFigureAt(3,7);
		f.setY(5);
		assertThat(service.checkAny(game, f, 7, 5)).isTrue();
		assertThat(service.checkAny(game, f, 1, 5)).isFalse();

		//roi
		f = game.getFigureAt(4,7);
		f.setY(5);
		assertThat(service.checkAny(game, f, 3, 4)).isTrue();
		assertThat(service.checkAny(game, f, 4, 3)).isFalse();
	}

	/**
	 * If the figure is a pawn and it's at the end of the board, then it can be promoted.
	 */
	@Test
	public void enablePromotePawnTest()
	{
		// PAWN
		Figure f = game.getFigureAt(0,6);
		assertThat(FigureName.stringToFigureName(f.getName())).isEqualTo(FigureName.PAWN);

		assertThat(service.enablePromotePawn(f)).isFalse();

		f.setY(0);
		assertThat(service.enablePromotePawn(f)).isTrue();
	}

	/**
	 * It sets the id of every figure on the board to the sum of its x and y coordinates, then calls the findKing function,
	 * and then asserts that the white and black king ids are not null
	 */
	@Test
	public void findKingTest()
	{
		for(long i = 0; i < 8; i++)
		{
			for(long j = 0; j <  8; j++)
			{
				if(game.getFigureAt((int)i, (int)j) != null)
					game.getFigureAt((int)i,(int)j).setId(i+j);
			}
		}
		service.findKing(game);
		assertThat(game.getWhiteKingId()).isNotNull();
		assertThat(game.getBlackKingId()).isNotNull();
	}

	/**
	 * It checks if the king is in check
	 */
	@Test
	public void checkEchecTest()
	{
		for(long i = 0; i < 8; i++)
		{
			for(long j = 0; j <  8; j++)
			{
				if(game.getFigureAt((int)i, (int)j) != null)
					game.getFigureAt((int)i,(int)j).setId(i+j);
			}
		}
		service.findKing(game);

		// White king
		Figure k1 = game.getFigureAt(4,7);
		k1.setY(2);
		assertThat(k1).isNotNull();
		game.setCurrentPlayer(0);

		assertThat(service.checkEchec(game)).isTrue();

		k1.setY(3);
		assertThat(service.checkEchec(game)).isFalse();

		// Black king
		game.setCurrentPlayer(1);
		k1.setY(2);
		assertThat(service.checkEchec(game)).isTrue();

		k1.setY(4);
		assertThat(service.checkEchec(game)).isFalse();
	}

	/**
	 * It checks if the king is in checkmate
	 */
	@Test
	public void checkMateTest()
	{
		for(long i = 0; i < 8; i++)
		{
			for(long j = 0; j <  8; j++)
			{
				if(game.getFigureAt((int)i, (int)j) != null)
					game.getFigureAt((int)i,(int)j).setId(i+j);
			}
		}
		service.findKing(game);

		// White King
		Figure k1 = game.getFigureAt(4,7);
		k1.setY(2);
		assertThat(k1).isNotNull();
		game.setCurrentPlayer(1);
		assertThat(service.checkMate(game)).isTrue();

		k1.setY(3);
		assertThat(service.checkMate(game)).isFalse();

		// Black king
		game.setCurrentPlayer(0);
		k1.setY(2);
		assertThat(service.checkMate(game)).isTrue();

		k1.setY(4);
		assertThat(service.checkMate(game)).isFalse();
	}
}
