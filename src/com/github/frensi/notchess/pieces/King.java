package com.github.frensi.notchess.pieces;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import com.github.frensi.notchess.ChessPiece;

/**
 * A class to create the king piece
 * 
 * @author Frensi Angjo
 * @version Spring 2023
 */
public class King extends ChessPiece {

    /**
     * The contructor of the king class
     * 
     * @param color The color of the piece
     * @param position The position of the piece
     * @param image The image of the piece
     * @param buttonMatrix The matrix of the buttons 
     */
    public King(Color color, Point position, ImageIcon image, JButton[][] buttonMatrix) {
        super(color, position, image, buttonMatrix);
        buttonMatrix[position.x][position.y].setForeground(color);
        buttonMatrix[position.x][position.y].setName("K");
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
        if (!Pattern.matches("K[A-H][1-8][A-H][1-8]", move)) {
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

        // Check if previous button was King
        if (!buttonMatrix[7-prevRow][prevCol].getName().equals("K")) {
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

        // Check if valid move (distance and color of capturing)
        if ((Math.abs(newRow - prevRow) > 1) || (Math.abs(newCol - prevCol) > 1)) {
            return false;
        } else {
            // Check for piece and its color if it exists
            if (!buttonMatrix[7-newRow][newCol].getName().isEmpty() || !buttonMatrix[7-newRow][newCol].getName().equals("")) {
                if (buttonMatrix[7-newRow][newCol].getForeground().equals(color)) {
                    return false;
                }
            }
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
        buttonMatrix[7-newRow][newCol].setName("K");
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

        // Check around the king for forced moves
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                // Continue if checking your position
                if (i == 0 && j == 0) continue;
                // Check for out of bounds
                if (position.x+i >= 0 && position.x+i < 8 && position.y+j >= 0 && position.y+j < 8) {
                    // Check if button empty
                    if (buttonMatrix[position.x+i][position.y+j].getName().equals("")) {
                        continue;
                    }
                    // Check for color of piece
                    if (!buttonMatrix[position.x+i][position.y+j].getForeground().equals(color)) {
                        moves.add("K" + (char)(65+position.y) + Integer.toString(8-position.x) + (char)(65+position.y+j) + Integer.toString(8-(position.x+i)));
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

        // Get a random position
        int randomX = (int)(Math.random() * 3) - 1;
        int randomY = (int)(Math.random() * 3) - 1;
        return "K" + (char)(65+position.y) + Integer.toString(8-position.x) + (char)(65+position.y+randomY) + Integer.toString(8-(position.x+randomX));
    }
    
}
