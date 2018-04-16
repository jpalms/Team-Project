package com.webcheckers.model;

import java.util.logging.Logger;

public class CheckersGame {
    private static final Logger LOG = Logger.getLogger(CheckersGame.class.getName());

    protected enum State {
        IN_PLAY,
        WON,
        RESIGNED
    }

    private final Player playerRed;
    private final Player playerWhite;
    private Space[][] board;
    private Turn activeTurn;
    private State state;

    /**
     * Parameterized constructor
     * Create a new checkers game between two players
     *
     * @param playerRed   - Player one, red player, starting player
     * @param playerWhite - Player two
     */
    public CheckersGame(Player playerRed, Player playerWhite) {
        LOG.info(String.format("I am a new CheckersGame between [%s] and [%s]",
                playerRed.getName(),
                playerWhite.getName()));

        this.playerRed = playerRed;
        this.playerWhite = playerWhite;
        this.state = State.IN_PLAY;

        initStartingBoard();

        this.activeTurn = new Turn(board, playerRed, Piece.Color.RED);
    }


    // PLAYER INTERFACE

    /**
     * Used to access the red player in the game
     *
     * @return - Red player in the game
     */
    public Player getPlayerRed() {
        return playerRed;
    }

    /**
     * getWhitePlayer method--
     * Used to access the white player in the game
     *
     * @return - White player in the game
     */
    public Player getPlayerWhite() {
        return playerWhite;
    }

    /**
     * Used to access player whose turn it is
     *
     * @return - player whose turn it is
     */
    public Player getPlayerActive() {
        return this.activeTurn.getPlayer();
    }

    /**
     * Changes the player who is active from red to white or vice versa
	 * This is a private method that assumes the Turn is over
     */
    private void changeActivePlayer() {
        Player activePlayer = activeTurn.getPlayer();
        Player nextPlayer;
        Piece.Color nextPlayerColor;

        // determine who the next player is
		if (activePlayer.equals(playerWhite)) {
		    nextPlayer = playerRed;
		    nextPlayerColor = Piece.Color.RED;
        } else {
            nextPlayer = playerWhite;
            nextPlayerColor = Piece.Color.WHITE;
        }

        // make sure the next player has moves available
        if (MoveValidator.areMovesAvailableForPlayer(board, nextPlayer, nextPlayerColor)) {
            // setup their turn
            activeTurn = new Turn(board, nextPlayer, nextPlayerColor);
        } else {
		    // trigger a win for activePlayer
            LOG.info(String.format("%s has no more moves. Sad! %s wins.", nextPlayer.getName(), activePlayer.getName()));
        }

    }

    /**
     * What color is the given player?
     *
     * @param player - Player whose color is being requested
     * @return Piece.Color  - color of the Player
     */
    public Piece.Color getPlayerColor(Player player) {
        if (player.equals(playerRed)) {
            return Piece.Color.RED;
        } else if (player.equals(playerWhite)) {
            return Piece.Color.WHITE;
        } else {
            return null;
        }
    }


    // BOARD INTERFACE

    /**
     * Two-dimensional Space array representing a Checkers board
     *
     * @return - space board
     */
    public Space[][] getBoard() {
        return board;
    }

    /**
     * Uses our static CheckersBoardBuilder to generate the starting Checkers Board
     */
    private void initStartingBoard() {
        CheckersBoardBuilder builder = CheckersBoardBuilder.aBoard();

        switch (playerRed.getName()) {
            case "noMoreMoves":
                builder = CheckersBoardBuilder.aBoard();

                builder.withPieceAt(
                        new Piece(Piece.Type.SINGLE, Piece.Color.WHITE),
                        new Position(1, 0)
                ).withPieceAt(
                        new Piece(Piece.Type.SINGLE, Piece.Color.WHITE),
                        new Position(1, 2)
                ).withPieceAt(
                        new Piece(Piece.Type.SINGLE, Piece.Color.WHITE),
                        new Position(0, 3)
                ).withPieceAt(
                        new Piece(Piece.Type.SINGLE, Piece.Color.RED),
                        new Position(3, 0)
                );
                break;

            case "endgame":
                builder = CheckersBoardBuilder.aBoard();

                builder.withPieceAt(
                        new Piece(Piece.Type.SINGLE, Piece.Color.WHITE),
                        new Position(1, 0)
                ).withPieceAt(
                        new Piece(Piece.Type.SINGLE, Piece.Color.RED),
                        new Position(2, 1)
                );
                break;

            case "kingMe":
                builder = CheckersBoardBuilder.aBoard();

                builder.withPieceAt(
                        new Piece(Piece.Type.SINGLE, Piece.Color.RED),
                        new Position(1, 0)
                ).withPieceAt(
                        new Piece(Piece.Type.SINGLE, Piece.Color.WHITE),
                        new Position(6, 1)
                ).withPieceAt(
                        new Piece(Piece.Type.SINGLE, Piece.Color.WHITE),
                        new Position(1, 2)
                );
                break;

            case "noPieces":
                builder = CheckersBoardBuilder.aBoard();
                builder.withPieceAt(
                        new Piece(Piece.Type.SINGLE, Piece.Color.RED),
                        new Position(1, 0)
                );

            default:
                // I don't actually have a public method to place starting pieces.. otherwise that woudl go here

                // Red pieces (start row 5-7)
//                for(int row = 5; row < 8; row++){
//                    for(int cell = 0; cell < 8; cell+=2){
//                        LOG.info(String.format("row: [%i] cell: [%i]", row, cell));
//                        // Adds one for the offset
//                        if(row %2 == 0)
//                            cell++;
//                        builder.withPieceAt(
//                                new Piece(Piece.Type.SINGLE, Piece.Color.RED),
//                                new Position(row, cell)
//                        );
//                    }
//                }
//
//                // White pieces (start row 0-2)
//                for(int row = 0; row < 8; row++){
//                    for(int cell = 0; cell < 8; cell+=2){
//                        LOG.info(String.format("row: [%i] cell: [%i]", row, cell));
//                        // Adds one for the offset
//                        if(row %2 == 1)
//                            cell++;
//                        builder.withPieceAt(
//                                new Piece(Piece.Type.SINGLE, Piece.Color.WHITE),
//                                new Position(cell, row)
//                        );
//                    }
//                }

                builder = CheckersBoardBuilder.aStartingBoard();

                break;
        }


        LOG.finest("Starting board:");
        LOG.finest(builder.formatBoardString());

        board = builder.getBoard();
    }


    // TURN INTERFACE

    /**
     * Get the active turn from the game
     * @return - the active turn
     */
    public Turn getTurn() {
        return activeTurn;
    }

    /**
	 * Allow the active player to submit their turn
     * @param player
     * @return
     */
    public boolean submitTurn(Player player) {
        if (player.equals(getPlayerActive()) && activeTurn.isStable()) {
            board = activeTurn.getLatestBoard();

            makeKings();

            changeActivePlayer();

            return true;
        }

        return false;
    }

    /**
     * When pieces reach the proper end row, the piece will be kinged
     */
    public void makeKings(){
        // King red pieces
        for(int cell = 0; cell < 8; cell++){
            if(board[0][cell].isOccupied() && board[0][cell].getPiece().getColor() == Piece.Color.RED){
                board[0][cell].getPiece().kingMe();
            }
        }

        // King white pieces
        for(int cell = 0; cell < 8; cell++){
            if(board[7][cell].isOccupied() && board[7][cell].getPiece().getColor() == Piece.Color.WHITE){
                board[7][cell].getPiece().kingMe();
            }
        }
    }

    /**
     * Allow the active player to resign the game
     * @return boolean - if resignation was successful
     */
    public boolean resignGame() {
        if (activeTurn.canResign()) {
            state = State.RESIGNED;

            return true;
        }

        return false;
    }

    /**
     * Indiciates if this game is resigned
     * @return boolean
     */
    public boolean isResigned() {
        return state == State.RESIGNED;
    }

    /**
     * Used for logging
     * @return
     */
    public State getState() {
        return state;
    }

}
