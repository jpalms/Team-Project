package com.webcheckers.model;

import java.util.Objects;

/**
 * Model entity of a checkers Player
 */
public class Player {

    // Determines whether a player is in tournament mode or not
    public enum GameType {NORMAL, TOURNAMENT};

    //instance variable
    public final String name;

    public final GameType type;

    public int wins;


    /**
     * Parameterize constructor
     * Intializes the name of the player
     *
     * @param name - Player name String
     * @param type - Player GameType
     */
    public Player(String name, GameType type) {
        this.name = name;
        this.type = type;
        this.wins = 0;
    }

    /**
     * This method is used to get player's name
     *
     * @return - a string containing player's name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Used to get the player's type
     *
     * @return - the player's type
     */
    public Player.GameType getType(){ return this.type; }

    /**
     * Gets the player's number of wins
     *
     * @return - number of wins
     */
    public Integer getWins(){ return this.wins; }

    /**
     * Increment the number of wins if the player won their game
     */
    public void wonAGame(){ this.wins++; }

    /**
     * Used by home.ftl to determine if a player is in tournament mode
     * or not
     *
     * @return - true if in tournament mode, false otherwise
     */
    public boolean isTournament(){ return this.type == GameType.TOURNAMENT; }

    /**
     * This method is used to compare two Player objects
     *
     * @param obj - other Player object to compare to
     * @return - true if both the Player have same name
     */
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof Player)) return false;

        Player otherPlayer = (Player) obj;

        return (this.name).equals(otherPlayer.getName());
    }

    /**
     * This generates Player object's hashcode
     *
     * @return - int value representing object's hashcode
     */
    public int hashCode() {
        return Objects.hash(this.name);
    }


    /**
     * Generates and returns a string of the player's name
     *
     * @return - generated string
     */
    public String toString() {
        return new String("Player Name: " + name);
    }
}
