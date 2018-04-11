package com.webcheckers.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Tag("Model-tier")
public class MoveValidatorTest {

	private static final String RED_PLAYER_NAME = "redPlayer";
	private static final String WHITE_PLAYER_NAME = "whitePlayer";

	private static Player player;

	private static CheckersBoardBuilder boardBuilder;

	private Space[][] board;

	@BeforeAll
	public static void testSetup() {

		// Build dependent objects
		player = new Player(WHITE_PLAYER_NAME);

		boardBuilder = CheckersBoardBuilder.aStartingBoard();
	}

	@Test
	public void test_aDiagonalMove() {
		board = boardBuilder.getBoard();

		Move diagonalMove = new Move(
				new Position(2,1),
				new Position(3,0),
				player,
				Piece.Color.WHITE
		);

		assertTrue(moveValidator.validateMove(board, diagonalMove));
	}

	@Test
	@Disabled
	public void test_aKingMoveBackwards() {
		board = boardBuilder.getBoard();

		// The board is laid out with white pieces starting on top
		// red pieces on bottom
		// so a white KING piece starting down on row 4 should be able to move up to row 3

		Piece king = new Piece(Piece.Type.KING, Piece.Color.WHITE);
		Position kingPosition = new Position(4, 1);

		Space[][] boardWithKing = CheckersBoardBuilder.aBoard().withPieceAt(king, kingPosition).getBoard();

		when(game.getBoard()).thenReturn(boardWithKing);

		Move kingMoveBackwards = new Move(
				kingPosition,
				new Position(3, 2),
				player,
				Piece.Color.WHITE
		);

		assertTrue(moveValidator.validateMove(board, kingMoveBackwards));
	}
}
