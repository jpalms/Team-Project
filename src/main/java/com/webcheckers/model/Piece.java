package com.webcheckers.model;

/**
 * <p>Title: Piece class</p>
 * <p>Description: This class represents a checkers piece</p>
 */
public class Piece {

    //enums
    public enum Type {
        SINGLE, KING
    }

    public enum Color {RED, WHITE}

    //instance
    private Type ty;
    private Color col;

    /**
     * Parameterized constructor
     * This initializes the type and color
     * to the passed in values
     *
     * @param ty  - type of the piece (SINGLE, KING)
     * @param col - Color of the PIECE (RED, WHITE)
     */
    public Piece(Type ty, Color col) {
        this.ty = ty;
        this.col = col;
    }

    /**
     * getter for type
     *
     * @return - type of the piece
     */
    public Type getType() {
        return ty;
    }

    /**
     * getter for col (color)
     *
     * @return - color of the piece
     */
    public Color getColor() {
        return col;
    }

    /**
     * clone method
     * This method return a cloned copy of the object
     */
    public Piece clone()
    {
        return new Piece(this.ty, this.col);
    }

    @Override
    public boolean equals(Object other)
    {
        if(other == null)
            return false;
        if(!(other instanceof Piece))
            return false;
        if(this.ty != ((Piece)other).ty)
            return false;
        if(this.col != ((Piece)other).col)
            return false;

        return true;
    }

    /**
     * Creates and returns a string detailing the piece's color and type
     *
     * @return - String created
     */
    public String toString() {
        return new String("Piece: "
                + ((col == Color.RED) ? "Red " : "White ")
                + ((ty == Type.SINGLE) ? "Single" : "King"));
    }

}
