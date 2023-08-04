package com.github.frensi.notchess.pieces;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import com.github.frensi.notchess.ChessPiece;

/**
 * A class to create the bishop piece
 * 
 * @author Frensi Angjo
 * @version Spring 2023
 */
public class Bishop extends ChessPiece {

    /**
     * The contructor of the bishop class
     * 
     * @param color The color of the piece
     * @param position The position of the piece
     * @param image The image of the piece
     * @param buttonMatrix The matrix of the buttons 
     */
    public Bishop(Color color, Point position, ImageIcon image, JButton[][] buttonMatrix) {
        super(color, position, image, buttonMatrix);
        buttonMatrix[position.x][position.y].setForeground(color);
        buttonMatrix[position.x][position.y].setName("B");
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
        if (!Pattern.matches("B[A-H][1-8][A-H][1-8]", move)) {
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

        // Check if previous button was Bishop
        if (!buttonMatrix[7-prevRow][prevCol].getName().equals("B")) {
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

        // Chech if moving vertically
        if (Math.abs(newRow-prevRow) != Math.abs(newCol-prevCol)) {
            return false;
        }

        int steps = Math.abs(newRow - prevRow);
        // Check if there is any piece blocking
        if (newRow > prevRow && newCol > prevCol) {
            for (int i = 1; i < steps; i++) {
                if (!buttonMatrix[7-prevRow-i][prevCol+i].getName().isEmpty() || !buttonMatrix[7-prevRow-i][prevCol+i].getName().equals("")) {
                    return false;
                }
            }
        } else if (newRow > prevRow && newCol < prevCol) {
            for (int i = 1; i < steps; i++) {
                if (!buttonMatrix[7-prevRow-i][prevCol-i].getName().isEmpty() || !buttonMatrix[7-prevRow-i][prevCol-i].getName().equals("")) {
                    return false;
                }
            }
        } else if (newRow < prevRow && newCol > prevCol) {
            for (int i = 1; i < steps; i++) {
                if (!buttonMatrix[7-prevRow+i][prevCol+i].getName().isEmpty() || !buttonMatrix[7-prevRow+i][prevCol+i].getName().equals("")) {
                    return false;
                }
            }
        } else {
            for (int i = 1; i < steps; i++) {
                if (!buttonMatrix[7-prevRow+i][prevCol-i].getName().isEmpty() || !buttonMatrix[7-prevRow+i][prevCol-i].getName().equals("")) {
                    return false;
                }
            }
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
        buttonMatrix[7-newRow][newCol].setName("B");
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
        
        // Variable to move diagonally
        int j = position.y;
        
        // Check for for forced moved right and up
        for (int i = position.x+1; i < 8; i++) {
            j++;
            // Check if out of bounds
            if (j > 7) break;

            // Check if there is a piece to capture
            if (!buttonMatrix[i][j].getName().isEmpty() || !buttonMatrix[i][j].getName().equals("")) {
                // If its opponent's piece, add to array list. Otherwise it is your piece blocking from moving
                if (!buttonMatrix[i][j].getForeground().equals(color)) {
                    moves.add("B" + (char)(65+position.y) + Integer.toString(8-position.x) + (char)(65+j) + Integer.toString(8-i));
                    break;
                } else {
                    break;
                }
            }
        }
        j = position.y;

        // Check for for forced moved left down
        for (int i = position.x+1; i < 8; i++) {
            j--;
            // Check if out of bounds
            if (j < 0) break;

            // Check if there is a piece to capture
            if (!buttonMatrix[i][j].getName().isEmpty() || !buttonMatrix[i][j].getName().equals("")) {
                // If its opponent's piece, add to array list. Otherwise it is your piece blocking from moving
                if (!buttonMatrix[i][j].getForeground().equals(color)) {
                    moves.add("B" + (char)(65+position.y) + Integer.toString(8-position.x) + (char)(65+j) + Integer.toString(8-i));
                    break;
                } else {
                    break;
                }
            }
        }
        j = position.y;

        // Check for for forced moved left and up
        for (int i = position.x-1; i >= 0; i--) {
            j++;
            // Check if out of bounds
            if (j > 7) break;

            // Check if there is a piece to capture
            if (!buttonMatrix[i][j].getName().isEmpty() || !buttonMatrix[i][j].getName().equals("")) {
                // If its opponent's piece, add to array list. Otherwise it is your piece blocking from moving
                if (!buttonMatrix[i][j].getForeground().equals(color)) {
                    moves.add("B" + (char)(65+position.y) + Integer.toString(8-position.x) + (char)(65+j) + Integer.toString(8-i));
                    break;
                } else {
                    break;
                }
            }
        }
        j = position.y;

        // Check for for forced moved right and down
        for (int i = position.x-1; i >= 0; i--) {
            j--;
            // Check if out of bounds
            if (j < 0) break;

            // Check if there is a piece to capture
            if (!buttonMatrix[i][j].getName().isEmpty() || !buttonMatrix[i][j].getName().equals("")) {
                // If its opponent's piece, add to array list. Otherwise it is your piece blocking from moving
                if (!buttonMatrix[i][j].getForeground().equals(color)) {
                    moves.add("B" + (char)(65+position.y) + Integer.toString(8-position.x) + (char)(65+j) + Integer.toString(8-i));
                    break;
                } else {
                    break;
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

        // Chose a random position
        int randomMove = (int)(Math.random() * 16 - 8);
        int direction = (int)(Math.random() * 2);
        if (direction == 0) {
            return "B" + (char)(65+position.y) + Integer.toString(8-position.x) + (char)(65+position.y+randomMove) + Integer.toString(8-(position.x+randomMove));
        } else {
            return "B" + (char)(65+position.y) + Integer.toString(8-position.x) + (char)(65+position.y+(-1*randomMove)) + Integer.toString(8-(position.x+randomMove));
        }
    }
}
