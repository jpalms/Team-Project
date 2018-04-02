package com.webcheckers.model;

/**
 * Builder pattern that generates a checkers board
 * We use the static component `BoardBuilder` to generate the default starting board
 * and then use method chaining to mutate the board state to create specific test cases.
 *
 * Not to be confused with `BoardBuilderTest` which is what tests the real component.
 * This Builder is only used in the model test tier to setup a non-starting game state.
 */
public class TestBoardBuilder {

	final static int WHITE_BORDER_INDEX = 2;
	final static int RED_BORDER_INDEX = 5;


	private static int rows = 8;
	private static int cells = 8;

	private Space[][] board;

	/**
	 * Allows for placing a piece on a space in the matrix
	 * @param piece
	 * @param pos
	 * @return
	 */
	public TestBoardBuilder withPieceAt(Piece piece, Position pos) {
		Space target = board[pos.getRow()][pos.getCell()];

		target.removePiece();
		target.addPiece(piece);

		return this;
	}

	/**
	 * buildRow builds a single row of a board
	 * The first row of the board starts with a black square
	 * @param rowId
	 * @return Space[] representing a row of Spaces
	 */
	private Space[] buildRow(int rowId) {

		Space[] row = new Space[cells];

		// Should this row start with a black space?
		boolean startOnBlack = (rowId % 2 == 0);

		boolean cellValid = startOnBlack;

		for (int cellId=0; cellId < cells; cellId++) {
			row[cellId] = buildSpace(rowId, cellId, cellValid);

			cellValid = ! cellValid; // alternate
		}

		return row;
	}

	/**
	 * Build a Space given a row and cell ID context
	 * @param rowId
	 * @param cellId
	 * @param invalidSpace
	 * @return
	 */
	private Space buildSpace(int rowId, int cellId, boolean invalidSpace) {

		if (invalidSpace) {
			return new Space(cellId, Space.State.INVALID);
		} else {

			if (rowId <= WHITE_BORDER_INDEX) {
				return new Space(cellId, new Piece(Piece.Type.SINGLE, Piece.Color.WHITE));

			} else if (rowId >= RED_BORDER_INDEX) {
				return new Space(cellId, new Piece(Piece.Type.SINGLE, Piece.Color.RED));

			} else {
				return new Space(cellId, Space.State.OPEN);

			}
		}
	}

	/**
	 * Build a board according to the parameters we have set in this object
	 *
	 * We build it from the top down. Checkers rules says that there must be a black space
	 * in the corner. So startRowBlackSquare = true;
	 * @return
	 * @param testBoardBuilder
	 */
	public static Space[][] buildBoard(TestBoardBuilder testBoardBuilder) {

		if (testBoardBuilder.board != null) {
			return testBoardBuilder.board;
		}

		Space[][] board = new Space[rows][cells];

		for (int rowId=0; rowId < rows; rowId++) {
			board[rowId] = testBoardBuilder.buildRow(rowId);
		}

		return board;
	}


	public Space[][] build() {

		// This is only used if we don't add things to the default board
		if (this.board == null) {
			this.board = buildBoard(this);
		}
		return this.board;
	}
}
