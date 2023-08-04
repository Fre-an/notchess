package com.github.frensi.notchess.pieces;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import com.github.frensi.notchess.ChessPiece;

/**
 * A class to create the rook piece
 * 
 * @author Frensi Angjo
 * @version Spring 2023
 */
public class Rook extends ChessPiece {

    /**
     * The contructor of the rook class
     * 
     * @param color The color of the piece
     * @param position The position of the piece
     * @param image The image of the piece
     * @param buttonMatrix The matrix of the buttons 
     */
    public Rook(Color color, Point position, ImageIcon image, JButton[][] buttonMatrix) {
        super(color, position, image, buttonMatrix);
        buttonMatrix[position.x][position.y].setForeground(color);
        buttonMatrix[position.x][position.y].setName("R");
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
        if (!Pattern.matches("R[A-H][1-8][A-H][1-8]", move)) {
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

        // Check if previous button was Rook
        if (!buttonMatrix[7-prevRow][prevCol].getName().equals("R")) {
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
        
        // Check if moving horizontally or vertically
        if (prevRow == newRow) {
            // Check if there is any piece that blocks from moving
            if (newCol > prevCol) {
                for (int i = prevCol+1; i < newCol; i++) {
                    if (!buttonMatrix[7-prevRow][i].getName().isEmpty() || !buttonMatrix[7-prevRow][i].getName().equals("")) {
                        return false;
                    }
                }
            } else {
                for (int i = newCol+1; i < prevCol; i++) {
                    if (!buttonMatrix[7-prevRow][i].getName().isEmpty() || !buttonMatrix[7-prevRow][i].getName().equals("")) {
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
        } else if (prevCol == newCol) {
            // Check if there is any piece that blocks from moving
            if (newRow > prevRow) {
                for (int i = prevRow+1; i < newRow; i++) {
                    if (!buttonMatrix[7-i][prevCol].getName().isEmpty() || !buttonMatrix[7-i][prevCol].getName().equals("")) {
                        return false;
                    }
                }
            } else {
                for (int i = newRow+1; i < prevRow; i++) {
                    if (!buttonMatrix[7-i][prevCol].getName().isEmpty() || !buttonMatrix[7-i][prevCol].getName().equals("")) {
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
        } else {
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
        buttonMatrix[7-newRow][newCol].setName("R");
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
        // Checking down
        for (int i = 1; i < 8; i++) {
            // Checking bounds
            if (position.x+i >=0 && position.x+i < 8) {
                // Check piece
                if (!buttonMatrix[position.x+i][position.y].getName().isEmpty() || !buttonMatrix[position.x+i][position.y].getName().equals("")) {
                    if (!buttonMatrix[position.x+i][position.y].getForeground().equals(color)) {
                        moves.add("R" + (char)(65+position.y) + Integer.toString(8-position.x) + (char)(65+position.y) + Integer.toString(8-(position.x+i)));
                        break;
                    } else {
                        break;
                    }
                } 
            }
        }
        // Checking up
        for (int i = 1; i < 8; i++) {
            // Checking bounds
            if (position.x-i >=0 && position.x-i < 8) {
                // Check piece
                if (!buttonMatrix[position.x-i][position.y].getName().isEmpty() || !buttonMatrix[position.x-i][position.y].getName().equals("")) {
                    if (!buttonMatrix[position.x-i][position.y].getForeground().equals(color)) {
                        moves.add("R" + (char)(65+position.y) + Integer.toString(8-position.x) + (char)(65+position.y) + Integer.toString(8-(position.x-i)));
                        break;
                    } else {
                        break;
                    }
                } 
            }
        }
        // Checking right
        for (int i = 1; i < 8; i++) {
            // Checking bounds
            if (position.y+i >=0 && position.y+i < 8) {
                // Check piece
                if (!buttonMatrix[position.x][position.y+i].getName().isEmpty() || !buttonMatrix[position.x][position.y+i].getName().equals("")) {
                    if (!buttonMatrix[position.x][position.y+i].getForeground().equals(color)) {
                        moves.add("R" + (char)(65+position.y) + Integer.toString(8-position.x) + (char)(65+position.y+i) + Integer.toString(8-position.x));
                        break;
                    } else {
                        break;
                    }
                } 
            }
        }
        // Checking left
        for (int i = 1; i < 8; i++) {
            // Checking bounds
            if (position.y-i >=0 && position.y-i < 8) {
                // Check piece
                if (!buttonMatrix[position.x][position.y-i].getName().isEmpty() || !buttonMatrix[position.x][position.y-i].getName().equals("")) {
                    if (!buttonMatrix[position.x][position.y-i].getForeground().equals(color)) {
                        moves.add("R" + (char)(65+position.y) + Integer.toString(8-position.x) + (char)(65+position.y-i) + Integer.toString(8-position.x));
                        break;
                    } else {
                        break;
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

        // Randomly choose the amount to move
        int randomMove = (int)(Math.random() * 16 - 8);
        // Randomly choose the direction
        int direction = (int)(Math.random() * 2);
        if (direction == 0) {
            return "R" + (char)(65+position.y) + Integer.toString(8-position.x) + (char)(65+position.y) + Integer.toString(8-(position.x+randomMove));
        } else {
            return "R" + (char)(65+position.y) + Integer.toString(8-position.x) + (char)(65+position.y+randomMove) + Integer.toString(8-(position.x));
        }
    }
    
}
