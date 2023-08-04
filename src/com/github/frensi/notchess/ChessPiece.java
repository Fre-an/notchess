package com.github.frensi.notchess;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;


/**
 * The abstract class that is used for the chess pieces
 * 
 * @author Frensi Angjo
 * @verion Spring 2023
 */
public abstract class ChessPiece {
	// The color of the piece
	public Color color;	
	// The position of the piece
	public Point position; 
	// The image of the piece
	public ImageIcon image;
	// The matrix of buttons
	public JButton[][] buttonMatrix;
	
	/**
	 * Constructor of the pieces 
	 * @param color    The color of the piece
	 * @param position The current position of the piece
	 * @param image    The image of the piece
	 * @param buttonMatrix The matrix of the buttons
	 */
	public ChessPiece(Color color, Point position, ImageIcon image, JButton[][] buttonMatrix) {
		
		this.color = color;
		this.position = position;
		this.buttonMatrix = buttonMatrix;
		this.image = image;
	}
	
	
	/**
	 * Constructor without icon (OBSOLETE)
	 * @param color The Color of the piece
	 * @param position The Position of the piece
	 * @param buttonMatrix The matrix of the buttons
	 */
	public ChessPiece(Color color, Point position, JButton[][] buttonMatrix) {		
		
		this.color = color;
		this.position = position;
		this.buttonMatrix = buttonMatrix;
	}

	/**
	 * Move the piece
	 * 
	 * @param move The move to make
	 */
	public abstract void move(String move);
	
	/**
	 * Return the move that the piece can make to capture another piece
	 * 
	 * @return The move that can capture
	 */
	public abstract ArrayList<String> canCapture();
	
	/**
	 * Determines if the move is valid
	 * @param move The move to check
	 */
	public abstract Boolean validMove(String move);

	/**
	 * Make a random move
	 */
	public abstract String pcMove();
}