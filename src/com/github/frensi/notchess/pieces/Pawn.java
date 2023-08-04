package com.github.frensi.notchess.pieces;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import com.github.frensi.notchess.ChessPiece;

/**
 * A class to create the pawn piece
 * 
 * @author Frensi Angjo
 * @version Spring 2023
 */
public class Pawn extends ChessPiece {

    /**
     * The contructor of the pawn class
     * 
     * @param color The color of the piece
     * @param position The position of the piece
     * @param image The image of the piece
     * @param buttonMatrix The matrix of the buttons 
     */
    public Pawn(Color color, Point position, ImageIcon image, JButton[][] buttonMatrix) {
        super(color, position, image, buttonMatrix);
        buttonMatrix[position.x][position.y].setForeground(color);
        buttonMatrix[position.x][position.y].setName("P");
        buttonMatrix[position.x][position.y].setIcon(image);
    }

    /**
     * Method to check if the move is valid for the piece
     * 
     * @param move The move trying to make
     * @return True if possible, false otherwise
     */
    @Override
    public Boolean validMove(String move) {
        // Check move format
        if (!Pattern.matches("P[A-H][1-8][A-H][1-8]", move)) {
            return false;
        }
        // Get previous and new rows
        int prevRow = Character.getNumericValue(move.charAt(2)) - 1;
        int newRow = Character.getNumericValue(move.charAt(4)) - 1;
        // Check if rows input is valid
        if (prevRow == -1 || newRow == -1) {
            return false;
        }

        // Get previous and new columns
        int prevCol = (int)(move.charAt(1) - 65);
        int newCol = (int)(move.charAt(3) - 65);
        // Check if columns input is valid
        if ((Math.abs(prevCol - 7) > 7) || (Math.abs(newCol - 7) > 7)) {
            return false;
        }

        // Check if the first and second positions are the same
        if (prevCol == newCol && prevRow == newRow) {
            return false;
        }

        // Check if previous button was Pawn
        if (!buttonMatrix[7-prevRow][prevCol].getName().equals("P")) {
            return false;
        }

        // Check if color is the same
        if(!buttonMatrix[7-prevRow][prevCol].getForeground().equals(color)) {
            return false;
        }

        // Check if text value and value in button is the same
        if (!buttonMatrix[7-prevRow][prevCol].getName().equals(move.substring(0,1))) {
            return false;
        }

        // Chech if the pawn is the corrent one
        if (prevRow != 7-position.x || prevCol != position.y) {
            return false;
        }

        // Check if pawn is moving only forward
        if(buttonMatrix[7-prevRow][prevCol].getForeground().equals(Color.white)) {
            if (prevRow >= newRow) {
                return false;
            }
        } else {
            if (prevRow <= newRow) {
                return false;
            }
        }

        // Check if valid move (distance and capturing)
        // If capturing
        if (Math.abs(prevCol - newCol) == 1) {
            // Check if moving one forward
            if (Math.abs(prevRow - newRow) > 1) {
                return false;
            }
            // Check if there is a piece to capture
            if (buttonMatrix[7-newRow][newCol].getName().isEmpty() || buttonMatrix[7-newRow][newCol].getName().equals("")) {
                return false;
            }
            // Check if piece is not yours
            if (buttonMatrix[7-newRow][newCol].getForeground().equals(color)) {
                return false;
            }
        // If moving
        } else if (prevCol == newCol) {
            // Check if the new position is empty
            if (!buttonMatrix[7-newRow][newCol].getName().isEmpty() || !buttonMatrix[7-newRow][newCol].getName().equals("")) {
                return false;
            }
            // Check if you can move two positions at start
            if ((prevRow == 1 && color == Color.white) || (prevRow == 6 && color == Color.black)) {
                if (Math.abs(prevRow - newRow) > 2) {
                    return false;
                }
                // Check if there is a piece in front blocking you
                if (color.equals(Color.black)){
                    if (!buttonMatrix[7-prevRow+1][prevCol].getName().isEmpty() || !buttonMatrix[7-prevRow+1][prevCol].getName().equals("")) {
                        return false;
                    } 
                } else {
                    if (!buttonMatrix[7-prevRow-1][prevCol].getName().isEmpty() || !buttonMatrix[7-prevRow-1][prevCol].getName().equals("")) {
                        return false;
                    }
                }
            // Can move only one position veritcally
            } else {
                if (Math.abs(prevRow - newRow) > 1) {
                    return false;
                }
            }
        // Moving more than one position horizontally
        } else if (Math.abs(prevCol - newCol) > 1) {
            return false;
        }

        return true;
    }

    /**
     * Moves the piece to the new position
     * 
     * @param move The move to make
     */
    @Override
    public void move(String move) {
        // Get previous and new rows
        int prevRow = Character.getNumericValue(move.charAt(2)) - 1;
        int newRow = Character.getNumericValue(move.charAt(4)) - 1;
        // Get previous and new columns
        int prevCol = (int)(move.charAt(1) - 65);
        int newCol = (int)(move.charAt(3) - 65);

        // Update chess
        buttonMatrix[7-newRow][newCol].setForeground(color);
        buttonMatrix[7-newRow][newCol].setName("P");
        buttonMatrix[7-prevRow][prevCol].setName("");

        // Update the images
        buttonMatrix[7-newRow][newCol].setIcon(image);
        buttonMatrix[7-prevRow][prevCol].setIcon(null);

        // Update the position
        position = new Point(7-newRow, newCol);
    }

    /**
     * Check if the piece can caputre
     * 
     * @return The array of the moves that the piece can make to capture
     */
    @Override
    public ArrayList<String> canCapture() {
        // ArrayList to store possible captures
        ArrayList<String> moves = new ArrayList<>();

        // Check where to check depending on piece color
        if (color.equals(Color.white)) {
            // Check forward for white if he can move
            if (position.x-1 < 8 && position.x-1 >= 0) {
                // Check if can capture right
                if (position.y+1 < 8 && position.y+1 >= 0) {
                    if (!buttonMatrix[position.x-1][position.y+1].getName().isEmpty() || !buttonMatrix[position.x-1][position.y+1].getName().equals("")) {
                        if (!buttonMatrix[position.x-1][position.y+1].getForeground().equals(color)) {
                            moves.add("P" + (char)(65+position.y) + Integer.toString(8-position.x) + (char)(65+position.y+1) + Integer.toString(8-(position.x-1)));
                        }
                    }
                }
                // Check if can capture left
                if (position.y-1 < 8 && position.y-1 >= 0) {
                    if (!buttonMatrix[position.x-1][position.y-1].getName().isEmpty() || !buttonMatrix[position.x-1][position.y-1].getName().equals("")) {
                        if (!buttonMatrix[position.x-1][position.y-1].getForeground().equals(color)) {
                            moves.add("P" + (char)(65+position.y) + Integer.toString(8-position.x) + (char)(65+position.y-1) + Integer.toString(8-(position.x-1)));
                        }
                    }
                }
            }
        } else {
            // Check forward for black if he can move
            if (position.x+1 < 8 && position.x+1 >= 0) {
                // Check if can capture right
                if (position.y+1 < 8 && position.y+1 >= 0) {
                    if (!buttonMatrix[position.x+1][position.y+1].getName().isEmpty() || !buttonMatrix[position.x+1][position.y+1].getName().equals("")) {
                        if (!buttonMatrix[position.x+1][position.y+1].getForeground().equals(color)) {
                            moves.add("P" + (char)(65+position.y) + Integer.toString(8-position.x) + (char)(65+position.y+1) + Integer.toString(8-(position.x+1)));
                        }
                    }
                }
                // Check if can capture left
                if (position.y-1 < 8 && position.y-1 >= 0) {
                    if (!buttonMatrix[position.x+1][position.y-1].getName().isEmpty() || !buttonMatrix[position.x+1][position.y-1].getName().equals("")) {
                        if (!buttonMatrix[position.x+1][position.y-1].getForeground().equals(color)) {
                            moves.add("P" + (char)(65+position.y) + Integer.toString(8-position.x) + (char)(65+position.y-1) + Integer.toString(8-(position.x+1)));
                        }
                    }
                }
            }
        }
        
        return moves;
    }

    /**
     * Make a random move for the pc player (not always valid)
     * 
     * @return The move to make
     */
    @Override
    public String pcMove() {
        ArrayList<String> forcedMoves = this.canCapture();
        // Check if there are forced moves to make
        if (!forcedMoves.isEmpty()) {
            return forcedMoves.get((int)(Math.random() * forcedMoves.size()));
        }

        // Check which direction the piece moved
        if (color.equals(Color.white)) {
            int move;
            // Randomly choose between 1 and 2 if at start, 1 otherwise
            if (position.x == 6) {
                move = (int)(Math.random() * 2) + 1;
            } else {
                move = 1;
            }
            return "P" + (char)(65+position.y) + Integer.toString(8-position.x) + (char)(65+position.y) + Integer.toString(8-(position.x-move));
        } else {
            int move;
            // Randomly choose between 1 and 2 if at start, 1 otherwise
            if (position.x == 1) {
                move = (int)(Math.random() * 2) + 1;
            } else {
                move = 1;
            }
            return "P" + (char)(65+position.y) + Integer.toString(8-position.x) + (char)(65+position.y) + Integer.toString(8-(position.x+move));
        }
    }
    
}
