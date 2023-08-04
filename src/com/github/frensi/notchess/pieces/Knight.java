package com.github.frensi.notchess.pieces;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import com.github.frensi.notchess.ChessPiece;

/**
 * A class to create the knight piece
 * 
 * @author Frensi Angjo
 * @version Spring 2023
 */
public class Knight extends ChessPiece {

    /**
     * The contructor of the knight class
     * 
     * @param color The color of the piece
     * @param position The position of the piece
     * @param image The image of the piece
     * @param buttonMatrix The matrix of the buttons 
     */
    public Knight(Color color, Point position, ImageIcon image, JButton[][] buttonMatrix) {
        super(color, position, image, buttonMatrix);
        buttonMatrix[position.x][position.y].setForeground(color);
        buttonMatrix[position.x][position.y].setName("N");
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
        if (!Pattern.matches("N[A-H][1-8][A-H][1-8]", move)) {
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

        // Check if previous button was Knight
        if (!buttonMatrix[7-prevRow][prevCol].getName().equals("N")) {
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

        // Check moving
        if (Math.abs(newRow - prevRow) == 2) {
            if (Math.abs(newCol - prevCol) != 1) {
                return false;
            }
        } else if (Math.abs(newRow - prevRow) == 1) {
            if (Math.abs(newCol - prevCol) != 2) {
                return false;
            }
        } else {
            return false;
        }

        // Check if you can capture
        if (!buttonMatrix[7-newRow][newCol].getName().isEmpty() || !buttonMatrix[7-newRow][newCol].getName().equals("")) {
            if (buttonMatrix[7-newRow][newCol].getForeground().equals(color)) {
                return false;
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
        buttonMatrix[7-newRow][newCol].setName("N");
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

        // Check up right
        if (position.x+2 < 8 && position.x+2 >= 0 && position.y+1 < 8 && position.y+1 >= 0) {
            // Check if piece exists
            if (!buttonMatrix[position.x+2][position.y+1].getName().isEmpty() || !buttonMatrix[position.x+2][position.y+1].getName().equals("")) {
                // Check if piece color
                if (!buttonMatrix[position.x+2][position.y+1].getForeground().equals(color)) {
                    moves.add("N" + (char)(65+position.y) + Integer.toString(8-position.x) + (char)(65+position.y+1) + Integer.toString(8-(position.x+2)));
                }
            }
        }
        // Check up left
        if (position.x+2 < 8 && position.x+2 >= 0 && position.y-1 < 8 && position.y-1 >= 0) {
            // Check if piece exists
            if (!buttonMatrix[position.x+2][position.y-1].getName().isEmpty() || !buttonMatrix[position.x+2][position.y-1].getName().equals("")) {
                // Check if piece color
                if (!buttonMatrix[position.x+2][position.y-1].getForeground().equals(color)) {
                    moves.add("N" + (char)(65+position.y) + Integer.toString(8-position.x) + (char)(65+position.y-1) + Integer.toString(8-(position.x+2)));
                }
            }
        }
        // Check down right
        if (position.x-2 < 8 && position.x-2 >= 0 && position.y+1 < 8 && position.y+1 >= 0) {
            // Check if piece exists
            if (!buttonMatrix[position.x-2][position.y+1].getName().isEmpty() || !buttonMatrix[position.x-2][position.y+1].getName().equals("")) {
                // Check if piece color
                if (!buttonMatrix[position.x-2][position.y+1].getForeground().equals(color)) {
                    moves.add("N" + (char)(65+position.y) + Integer.toString(8-position.x) + (char)(65+position.y+1) + Integer.toString(8-(position.x-2)));
                }
            }
        }
        // Check down left
        if (position.x-2 < 8 && position.x-2 >= 0 && position.y-1 < 8 && position.y-1 >= 0) {
            // Check if piece exists
            if (!buttonMatrix[position.x-2][position.y-1].getName().isEmpty() || !buttonMatrix[position.x-2][position.y-1].getName().equals("")) {
                // Check if piece color
                if (!buttonMatrix[position.x-2][position.y-1].getForeground().equals(color)) {
                    moves.add("N" + (char)(65+position.y) + Integer.toString(8-position.x) + (char)(65+position.y-1) + Integer.toString(8-(position.x-2)));
                }
            }
        }
        // Check right up
        if (position.x+1 < 8 && position.x+1 >= 0 && position.y+2 < 8 && position.y+2 >= 0) {
            // Check if piece exists
            if (!buttonMatrix[position.x+1][position.y+2].getName().isEmpty() || !buttonMatrix[position.x+1][position.y+2].getName().equals("")) {
                // Check if piece color
                if (!buttonMatrix[position.x+1][position.y+2].getForeground().equals(color)) {
                    moves.add("N" + (char)(65+position.y) + Integer.toString(8-position.x) + (char)(65+position.y+2) + Integer.toString(8-(position.x+1)));
                }
            }
        }
        // Check right down
        if (position.x-1 < 8 && position.x-1 >= 0 && position.y+2 < 8 && position.y+2 >= 0) {
            // Check if piece exists
            if (!buttonMatrix[position.x-1][position.y+2].getName().isEmpty() || !buttonMatrix[position.x-1][position.y+2].getName().equals("")) {
                // Check if piece color
                if (!buttonMatrix[position.x-1][position.y+2].getForeground().equals(color)) {
                    moves.add("N" + (char)(65+position.y) + Integer.toString(8-position.x) + (char)(65+position.y+2) + Integer.toString(8-(position.x-1)));
                }
            }
        }
        // Check left up
        if (position.x+1 < 8 && position.x+1 >= 0 && position.y-2 < 8 && position.y-2 >= 0) {
            // Check if piece exists
            if (!buttonMatrix[position.x+1][position.y-2].getName().isEmpty() || !buttonMatrix[position.x+1][position.y-2].getName().equals("")) {
                // Check if piece color
                if (!buttonMatrix[position.x+1][position.y-2].getForeground().equals(color)) {
                    moves.add("N" + (char)(65+position.y) + Integer.toString(8-position.x) + (char)(65+position.y-2) + Integer.toString(8-(position.x+1)));
                }
            }
        }
        // Check left down
        if (position.x-1 < 8 && position.x-1 >= 0 && position.y-2 < 8 && position.y-2 >= 0) {
            // Check if piece exists
            if (!buttonMatrix[position.x-1][position.y-2].getName().isEmpty() || !buttonMatrix[position.x-1][position.y-2].getName().equals("")) {
                // Check if piece color
                if (!buttonMatrix[position.x-1][position.y-2].getForeground().equals(color)) {
                    moves.add("N" + (char)(65+position.y) + Integer.toString(8-position.x) + (char)(65+position.y-2) + Integer.toString(8-(position.x-1)));
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
        int firstMove = new Random().nextBoolean() ? -2 : 2;
        int secondMove = new Random().nextBoolean() ? -1 : 1;
        // Horizontally or vertically
        int direction = (int)(Math.random() * 2);
        if (direction == 0) {
            return "N" + (char)(65+position.y) + Integer.toString(8-position.x) + (char)(65+position.y+firstMove) + Integer.toString(8-(position.x+secondMove));
        } else {
            return "N" + (char)(65+position.y) + Integer.toString(8-position.x) + (char)(65+position.y+firstMove) + Integer.toString(8-(position.x+secondMove));
        }
    }
}
