package com.webcheckers.model;

import com.webcheckers.util.DoublyLinkedQueue;

import java.util.logging.Logger;

/**
 * Turn handles the lifecycle of a player's turn
 * From validating moves
 * to backing up those moves
 * to submitting a list of moves and completing their turn
 * The turn controller is an expert at it all!
 */
public class Turn {
    private static final Logger LOG = Logger.getLogger(Turn.class.getName());

    public enum State {
        EMPTY_TURN,
        STABLE_TURN,
        TURN_SUBMITTED
    }

    private CheckersGame game;
    private Space[][] matrix;
    private Player player;
    private DoublyLinkedQueue<Move> pendingMoves;
    private State state;
    private boolean single;
    private int jumps;

    private MoveValidator moveValidator;

    /**
     * Parameterized constructor
     * A turn is identified by a game and a player
     *
     * @param game   - game the Turn is being made for
     * @param matrix - The checkers board matrix
     * @param player - player the Turn is being made for
     */
    Turn(CheckersGame game, Space[][] matrix, Player player) {
        this.game = game;
        this.matrix = matrix;
        this.player = player;
        this.single = false;
        this.jumps = 0;

        this.pendingMoves = new DoublyLinkedQueue<>();
        this.state = State.EMPTY_TURN;

        this.moveValidator = new MoveValidator(game, player);

        LOG.fine(String.format("Turn initialized for Player [%s] in [%s] state", player.getName(), this.state));
    }

    /**
     * Validate an incoming move and add it to this turn's list of moves if its valid
     *
     * @param move - move being validated
     * @return - true if move was validated, otherwise false
     */
    public boolean validateMove(Move move) {
        LOG.finer(String.format("%s Player [%s] is validing move %s",
                game.getPlayerColor(player),
                player.getName(),
                move.toString()));
        boolean hasJumped = false;
        boolean forcedJump = moveValidator.forcedJump(move);
        if(jumps > 0)
            hasJumped = true;
        if(move.isASingleMoveAttempt() && (hasJumped || forcedJump)){
            return false;
        }
        if (!single && moveValidator.validateMove(move)) {
            LOG.finer("Move has been validated successfully");

            pendingMoves.enqueue(move);

            state = State.STABLE_TURN;
            if(move.isASingleMoveAttempt()) {
                LOG.finer("This is a single move");
                single = true;
            }
            if(move.isAJumpMoveAttempt()){
                LOG.finer("This is a jump move");
                jumps++;
            }
            LOG.finest(String.format("%s Player [%s] has %d queued moves in [%s] state",
                    game.getPlayerColor(player),
                    player.getName(),
                    pendingMoves.size(),
                    state));

            return true;
        }

        return false;
    }

    /**
     * This method implement the submitMove logic
     * making the necessary changes to the board
     *
     * @return - true if the submitMove was successful, false otherwise
     */
    public boolean submitTurn() {
        LOG.finer(String.format("%s Player [%s] is submitting their turn of %d moves.",
                game.getPlayerColor(player),
                player.getName(),
                pendingMoves.size()));

        if (pendingMoves.isEmpty()) {
            return false;
        }

        while (!pendingMoves.isEmpty()) {
            if (!makeMove(matrix, pendingMoves.removeFromFront())) {
                return false;
            }
        }

        this.state = State.TURN_SUBMITTED;

        game.nextTurn();

        return true;
    }

    /**
     * This method make moves pieces on the board
     *
     * @param matrix - represents the checkers board
     * @param move   - move to be made on the checkers board
     * @return - true if the pieces where moved successfully
     */
    public boolean makeMove(Space[][] matrix, Move move) {
        LOG.finer(String.format("%s Player [%s] turn - executing move %s",
                game.getPlayerColor(player),
                player.getName(),
                move.toString()));

        Position start = move.getStart();
        Position end = move.getEnd();

        if (move.isAJumpMoveAttempt()) {
            jumps++;
            Space startSpace = matrix[start.getRow()][start.getCell()];
            Space endSpace = matrix[end.getRow()][end.getCell()];
            Position midPos = move.getEnd().midPosition(move.getEnd(), move.getStart());
            Space midSpace = matrix[midPos.getRow()][midPos.getCell()];

            return endSpace.jumpPieceMove(startSpace, midSpace);

        } else {
            Space startSpace = matrix[start.getRow()][start.getCell()];
            Space endSpace = matrix[end.getRow()][end.getCell()];
            single = true;

            return endSpace.movePieceFrom(startSpace);
        }
    }


    /**
     * When planning a turn a player may wish to backup and try a new strategy
     * We will remove their last valid move from the queued moves
     *
     * @return - true if there are valid moves, false otherwise
     */
    public boolean backupMove() {
        if (!pendingMoves.isEmpty()) {
            Move badMove = pendingMoves.removeFromRear();

            LOG.finest(String.format("Removing move %s from %s's history",
                    badMove.toString(),
                    player.getName()));

            // Return Turn state to EMPTY_TURN if they have no pending moves
            if (pendingMoves.isEmpty()) {
                this.state = State.EMPTY_TURN;
                jumps = 0;
                single = false;
            }
            if(jumps > 0)
                jumps--;
            single = false;
            return true;
        }

        return false;
    }

    /**
     * Is the given player in an active turn?
     *
     * @return - true if player is active player
     */
    public boolean isMyTurn(Player player) {
        return this.player.equals(player);
    }

    /**
     * Returns whether or not the turn has been submitted
     *
     * @return - true if the turn has been submitted, false otherwise
     */
    public boolean isSubmitted() {
        return (this.state == State.TURN_SUBMITTED);
    }

    public boolean canResign(){
        return this.state == State.EMPTY_TURN;
    }


    /**
     * Return the player whose turn it is
     *
     * @return - the player
     */
    public Player getPlayer() {
        return this.player;
    }


    /**
     * Used in testing to inspect component state
     * @return Turn State
     */
    public State getState() {
        return this.state;
    }
}
