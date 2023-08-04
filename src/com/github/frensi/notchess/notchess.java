package com.github.frensi.notchess;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import com.github.frensi.notchess.pieces.Bishop;
import com.github.frensi.notchess.pieces.King;
import com.github.frensi.notchess.pieces.Knight;
import com.github.frensi.notchess.pieces.Pawn;
import com.github.frensi.notchess.pieces.Queen;
import com.github.frensi.notchess.pieces.Rook;

/**
*	notchess game. Similar to chess, but not chess
*
*   @author Frensi Angjo
*	@version Spring 2023  
*/
public class notchess extends ThreadGraphicsController implements ActionListener {

	// Store the buttons
	protected ChessButton[][] buttonMatrix = new ChessButton[8][8];
	// The label that shows the moves
	protected static JLabel moveLabel;
	// TextArea to enter moves by text
	protected JTextArea enterMoveText;
	// Button to submit move entered by text
	protected JButton submitMove;
	// Store the curent move
	protected static String move;
	// The arraylist of white pieces
	protected ArrayList<ChessPiece> whiteChessPieces;
	// The array of black pieces
	protected ArrayList<ChessPiece> blackChessPieces;
	// Store which players has to move 
	protected boolean whiteTurn = true;
	// The number of moves left until the game ends
	protected int movesLeft;
	// Store if player is playing against pc
	protected static boolean pcPlayer;

	// Store connection details for client
	protected DataOutputStream outToServer;
    protected BufferedReader inFromServer;
	// Store if game is client or server
    static boolean client = false;
    static boolean server = false;

	// Store connection details for server
    protected DataOutputStream outToClient;
    protected BufferedReader inFromClient;
	// Store game results for server to send to client
    protected String finishgame = null;
	// Check for server if it si client turn
	protected boolean clientTurn = false;
	// Stores for server client invalid move
	protected String clientInvalidMove = null;
	// Stores the previous move for clinet to show in label if server says move was invalid
	protected String prevMove;
	
    /**
     * Constructor which calls the superclass constructor.
     */
	public notchess() {
		
		super("Not Chess", 700, 700);
	}


	/**
	 * Add the action listeners to the buttons
	 */
	@Override
    protected void addListeners(JButton button) {
		button.addActionListener(this);
    }

	/**
	 * Build the gui of the notchess game
	 */
	@Override
	protected void buildGUI(JFrame frame, JPanel panel) {
		//change the look and feel to the cross platform look and feel
		try{
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		}catch(Exception e){
			e.printStackTrace(); 
		}

        // Panel to show the move
		JPanel topPanel = new JPanel();
		moveLabel = new JLabel("Move: ");
		topPanel.add(moveLabel);
		frame.add(topPanel, BorderLayout.NORTH);

		// Create panel to store buttons
		JPanel mainPanel = new JPanel(new GridLayout(9, 10));

		// Add labels for the top text
		mainPanel.add(new JLabel());
		mainPanel.add(new JLabel("A", SwingConstants.CENTER));
		mainPanel.add(new JLabel("B", SwingConstants.CENTER));
		mainPanel.add(new JLabel("C", SwingConstants.CENTER));
		mainPanel.add(new JLabel("D", SwingConstants.CENTER));
		mainPanel.add(new JLabel("E", SwingConstants.CENTER));
		mainPanel.add(new JLabel("F", SwingConstants.CENTER));
		mainPanel.add(new JLabel("G", SwingConstants.CENTER));
		mainPanel.add(new JLabel("H", SwingConstants.CENTER));
		mainPanel.add(new JLabel());
		
		// Create the labels and buttons for the chess and add to panel and matrix
		for (int i = 0; i < 8; i++){
			String count = Integer.toString(8-i);
			mainPanel.add(new JLabel(count, SwingConstants.CENTER));
			for (int j = 0; j < 8; j++) {
				Color buttonColor;
				if (i%2 == 0) {
					if (j % 2 == 0) {
						buttonColor = new Color(211,211,211);
					} else {
						buttonColor = new Color(110,110,110);
					}
				} else {
					if (j % 2 == 0) {
						buttonColor = new Color(110,110,110);
					} else {
						buttonColor = new Color(211,211,211);
					}
				}
				ChessButton button = new ChessButton(i, j);
				button.setBackground(buttonColor);
				button.setOpaque(true);
				button.setBorderPainted(false);
                addListeners(button);
				buttonMatrix[i][j] = button;
				mainPanel.add(button);
			}
			mainPanel.add(new JLabel());
		}

		// Set up the board
		setUpBoard();

		//Add panel to the frame
		panel.add(mainPanel, BorderLayout.CENTER);

		// Create the label, field, and button to enter text
		JPanel bottomPanel = new JPanel();
		JLabel enterMove = new JLabel("Enter Move: ");
		enterMoveText = new JTextArea(1, 10);
		submitMove = new JButton("Move");
        addListeners(submitMove);

		// Add them to panel and add panel to frame
		bottomPanel.add(enterMove);
		bottomPanel.add(enterMoveText);
		bottomPanel.add(submitMove);
		panel.add(bottomPanel, BorderLayout.SOUTH);
		frame.add(panel);
    }

	/**
	 * Method to set up the board every time that a game ends
	 */
	public void setUpBoard() {
		// Make all button names empty and remove the iconds
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				buttonMatrix[i][j].setName("");
				buttonMatrix[i][j].setIcon(null);
			}
		}
		// Initiate new arraylists
		whiteChessPieces = new ArrayList<ChessPiece>();
		blackChessPieces = new ArrayList<ChessPiece>();

		// Create the white pieces
		whiteChessPieces.add(new King(Color.white, new Point(7, 4), new ImageIcon("images/white_king.png"), buttonMatrix));
		whiteChessPieces.add(new Pawn(Color.white, new Point(6, 0), new ImageIcon("images/white_pawn.png"), buttonMatrix));
		whiteChessPieces.add(new Pawn(Color.white, new Point(6,1), new ImageIcon("images/white_pawn.png"), buttonMatrix));
		whiteChessPieces.add(new Pawn(Color.white, new Point(6,2), new ImageIcon("images/white_pawn.png"), buttonMatrix));
		whiteChessPieces.add(new Pawn(Color.white, new Point(6, 3), new ImageIcon("images/white_pawn.png"), buttonMatrix));
		whiteChessPieces.add(new Pawn(Color.white, new Point(6, 4), new ImageIcon("images/white_pawn.png"), buttonMatrix));
		whiteChessPieces.add(new Pawn(Color.white, new Point(6, 5), new ImageIcon("images/white_pawn.png"), buttonMatrix));
		whiteChessPieces.add(new Pawn(Color.white, new Point(6, 6), new ImageIcon("images/white_pawn.png"), buttonMatrix));
		whiteChessPieces.add(new Pawn(Color.white, new Point(6, 7), new ImageIcon("images/white_pawn.png"), buttonMatrix));
		whiteChessPieces.add(new Rook(Color.white, new Point(7, 0), new ImageIcon("images/white_rook.png"), buttonMatrix));
		whiteChessPieces.add(new Rook(Color.white, new Point(7, 7), new ImageIcon("images/white_rook.png"), buttonMatrix));
		whiteChessPieces.add(new Bishop(Color.white, new Point(7, 2), new ImageIcon("images/white_bishop.png"), buttonMatrix));
		whiteChessPieces.add(new Bishop(Color.white, new Point(7, 5), new ImageIcon("images/white_bishop.png"), buttonMatrix));
		whiteChessPieces.add(new Knight(Color.white, new Point(7, 1), new ImageIcon("images/white_knight.png"), buttonMatrix));
		whiteChessPieces.add(new Knight(Color.white, new Point(7, 6), new ImageIcon("images/white_knight.png"), buttonMatrix));
		whiteChessPieces.add(new Queen(Color.white, new Point(7, 3), new ImageIcon("images/white_queen.png"), buttonMatrix));

		// Create the black pieces
		blackChessPieces.add(new King(Color.black, new Point(0, 4), new ImageIcon("images/black_king.png"), buttonMatrix));
		blackChessPieces.add(new Pawn(Color.black, new Point(1, 0), new ImageIcon("images/black_pawn.png"), buttonMatrix));
		blackChessPieces.add(new Pawn(Color.black, new Point(1, 1), new ImageIcon("images/black_pawn.png"), buttonMatrix));
		blackChessPieces.add(new Pawn(Color.black, new Point(1, 2), new ImageIcon("images/black_pawn.png"), buttonMatrix));
		blackChessPieces.add(new Pawn(Color.black, new Point(1, 3), new ImageIcon("images/black_pawn.png"), buttonMatrix));
		blackChessPieces.add(new Pawn(Color.black, new Point(1, 4), new ImageIcon("images/black_pawn.png"), buttonMatrix));
		blackChessPieces.add(new Pawn(Color.black, new Point(1, 5), new ImageIcon("images/black_pawn.png"), buttonMatrix));
		blackChessPieces.add(new Pawn(Color.black, new Point(1, 6), new ImageIcon("images/black_pawn.png"), buttonMatrix));
		blackChessPieces.add(new Pawn(Color.black, new Point(1, 7), new ImageIcon("images/black_pawn.png"), buttonMatrix));
		blackChessPieces.add(new Rook(Color.black, new Point(0, 0), new ImageIcon("images/black_rook.png"),buttonMatrix));
		blackChessPieces.add(new Rook(Color.black, new Point(0, 7), new ImageIcon("images/black_rook.png"),buttonMatrix));
		blackChessPieces.add(new Bishop(Color.black, new Point(0, 2), new ImageIcon("images/black_bishop.png"), buttonMatrix));
		blackChessPieces.add(new Bishop(Color.black, new Point(0, 5),new ImageIcon("images/black_bishop.png"),  buttonMatrix));
		blackChessPieces.add(new Knight(Color.black, new Point(0, 1), new ImageIcon("images/black_knight.png"), buttonMatrix));
		blackChessPieces.add(new Knight(Color.black, new Point(0, 6), new ImageIcon("images/black_knight.png"), buttonMatrix));
		blackChessPieces.add(new Queen(Color.black, new Point(0, 3), new ImageIcon("images/black_queen.png"), buttonMatrix));
		// White player always starts
		whiteTurn = true;
		// 50 moves until game ends
		movesLeft = 50;
	}

	/**
	 * Method to handel actions performed on buttons
	 * 
	 * @param e The action event performed
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// Check if submit button was pressed
		if (e.getSource() == submitMove) {
			move = enterMoveText.getText();
		} else {
			// Get button pressed
			ChessButton aButton = (ChessButton)e.getSource();
			// Check if did not clicked on a piece
			if (move == null && (aButton.getName().isEmpty() || aButton.getName().equals(""))) {
				moveLabel.setText("Move: Invalid Move");
				return;
			// Check if trying to move
			} else if (move != null) {
				move += (char)(65+aButton.getCol()) + Integer.toString(8-aButton.getRow());
			// Choosing a piece
			} else {
				// Check if the button has a piece
				if (!aButton.getName().isEmpty() && !aButton.getName().equals("")) {
					move = aButton.getName() + (char)(65+aButton.getCol()) + Integer.toString(8-aButton.getRow());
				}
			} 
		}

		// First button pressed
		if (move.length() == 3) return;		
		// if (pcPlayer && !whiteTurn) return;
		doAndValidateMove();
	}

	/**
	 * Checks if the move is valid and makes the move
	 */
	public void doAndValidateMove(){
		// Stores if the move to make was found
		boolean found = false;
		// Stores if the move made is forced
		boolean isForcedMove = false;
		// Stores if there is any forced move to make
		boolean foundForcedMove = false;
		// Stores the possible forced moves
		ArrayList<String> possibleMoves = new ArrayList<String>();
		if (whiteTurn) {
			// Check if there is a piece that you have to capture
			for (ChessPiece c : whiteChessPieces) {
				possibleMoves = c.canCapture();
				// Check if there are forced moves to make
				if (!possibleMoves.isEmpty()) {
					foundForcedMove = true;
					// Check each forced move
					for (String s : possibleMoves) {
						// Check if forced move is the move made
						if (s.equals(move)) {
							isForcedMove = true;
							break;
						} 
					}
				}
			}
		} else {
			// Check if there is a piece that you have to capture
			for (ChessPiece c : blackChessPieces) {
				possibleMoves = c.canCapture();
				// Check if there are forced moves to make
				if (!possibleMoves.isEmpty()) {
					foundForcedMove = true;
					// Check each forced move
					for (String s : possibleMoves) {
						// Check if forced move is the move made
						if (s.equals(move)) {
							isForcedMove = true;
							break;
						} 
					}
				}
			}

		}
		// If the move is not one of the forced moves
		if (foundForcedMove && !isForcedMove) {
			moveLabel.setText("Move: NEED TO CAPTURE");
			if (clientTurn) {
				clientInvalidMove = "NEED TO CAPTURE";
				moveLabel.setText(move + " NEED TO CAPTURE");
			}
			move = null;
			return;
		}

		if (whiteTurn) {
			for (ChessPiece c : whiteChessPieces) {
				// Check if the piece was moved
				if (c.validMove(move)){
					// Move the piece and show in the label
					c.move(move);
					moveLabel.setText("Move: " + move);

					int newRow = Character.getNumericValue(move.charAt(4)) - 1;
					int newCol = (int)(move.charAt(3) - 65);
					Point position = new Point(7-newRow, newCol);

					// Check if any piece was captured
					for (int count = 0; count < blackChessPieces.size(); count++) {
						if (blackChessPieces.get(count).position.equals(position)) {
							blackChessPieces.remove(count);
						}
					}

					// Promote pawn if necessary
					if (move.charAt(0) == 'P' && move.charAt(4) == '8') {
						whiteChessPieces.remove(c);
						whiteChessPieces.add(new Queen(Color.white, position, new ImageIcon("images/white_queen.png"), buttonMatrix));
					}

					found = true;
					whiteTurn = false;
					break;
				}
			}
		} else {
			for (ChessPiece c : blackChessPieces) {
				// Check if the piece was moved
				if (c.validMove(move)){
					// Move the piece and show in the label
					c.move(move);
					moveLabel.setText("Move: " + move);

					int newRow = Character.getNumericValue(move.charAt(4)) - 1;
					int newCol = (int)(move.charAt(3) - 65);
					Point position = new Point(7-newRow, newCol);

					// Check if any piece was captured
					for (int count = 0; count < whiteChessPieces.size(); count++) {
						if (whiteChessPieces.get(count).position.equals(position)) {
							whiteChessPieces.remove(count);
						}
					}

					// Promote pawn if necessary
					if (move.charAt(0) == 'P' && move.charAt(4) == '1') {
						blackChessPieces.remove(c);
						blackChessPieces.add(new Queen(Color.black, position, new ImageIcon("images/black_queen.png"), buttonMatrix));
					}

					found = true;
					whiteTurn = true;
					movesLeft--;
					// Check if the players made 50 moves
					if (movesLeft == 0) {
						if (whiteChessPieces.size() > blackChessPieces.size()) {
							moveLabel.setText("BLACK WINS");
							finishgame = "BLACK WINS";
							setUpBoard();
						} else if (blackChessPieces.size() < blackChessPieces.size()) {
							moveLabel.setText("WHITE WINS");
							finishgame = "WHITE WINS";
							setUpBoard();
						} else {
							moveLabel.setText("TIE GAME");
							finishgame = "TIE GAME";
							setUpBoard();
						}
					}
					break;
				}
			}
		}
		// If move was not made, show invalid move in label
		if (!found) {
			moveLabel.setText("Move: Invalid Move");
			if (clientTurn) {
				clientInvalidMove = "INVALID MOVE";
				moveLabel.setText(move + " INVALID MOVE");
			}
		}
		
		// Check if anyone won
		if (whiteChessPieces.size() == 0) {
			moveLabel.setText("BLACK WINS");
			finishgame = "BLACK WINS";
			setUpBoard();
		} else if (blackChessPieces.size() == 0) {
			moveLabel.setText("WHITE WINS");
			finishgame = "WHITE WINS";
			setUpBoard();
		}
		// Check if there is any possible move for the player (blocked pawn)
		if(!canMove()) {
			moveLabel.setText("TIE GAME");
			finishgame = "TIE GAME";
			setUpBoard();
		}
		prevMove = move;
		move = null;
		// Call pc to make move if playing against pc
		if (!client && !server) {
			if (pcPlayer && !whiteTurn) pcMove();
		}
	}

	/**
	 * Check if the player has possible moves to make
	 * 
	 * @return True if the player can make a move, false otherwise
	 */
	public boolean canMove(){
		boolean result = false;
		// Check which players move is
		if (whiteTurn) {
			// Check for each piece if there are more possible moves
			for (ChessPiece c : whiteChessPieces) {
				// If the piece is not pawn, then there are possible moves
				if (!(c instanceof Pawn)) {
					return true;
				}
				// The positin that blocks the pawn
				Point positionBlocking = new Point(c.position.x-1, c.position.y);
				// The positions where the pawn can capture
				Point positionCaptureLeft = new Point(c.position.x-1, c.position.y-1);
				Point positionCaptureRight = new Point(c.position.x-1, c.position.y+1);
				boolean foundPieceBlocking = false;
				// Check each opponent pieces if they block the pawn
				for (ChessPiece cb : blackChessPieces) {
					// Check if the pawn can capture
					if (cb.position.equals(positionCaptureLeft) || cb.position.equals(positionCaptureRight)) {
						foundPieceBlocking = false;
						break;
					}
					// Check if the piece block the pawn 
					if (cb.position.equals(positionBlocking)) {
						foundPieceBlocking = true;
					}
				}
				// Change the return result to true if the pawn can move
				if (!foundPieceBlocking) {
					result = true;
					break;
				}
			}
		} else {
			// Check for each piece if there are more possible moves
			for (ChessPiece c : blackChessPieces) {
				// If the piece is not pawn, then there are possible moves
				if (!(c instanceof Pawn)) {
					return true;
				}
				// The positin that blocks the pawn
				Point positionBlocking = new Point(c.position.x+1, c.position.y);
				// The positions where the pawn can capture
				Point positionCaptureLeft = new Point(c.position.x+1, c.position.y-1);
				Point positionCaptureRight = new Point(c.position.x+1, c.position.y+1);
				// Check each opponent pieces if they block the pawn
				boolean foundPieceBlocking = false;
				for (ChessPiece cb : whiteChessPieces) {
					// Check if the pawn can capture
					if (cb.position.equals(positionCaptureLeft) || cb.position.equals(positionCaptureRight)) {
						foundPieceBlocking = false;
						break;
					}
					// Check if the piece block the pawn 
					if (cb.position.equals(positionBlocking)) {
						foundPieceBlocking = true;
					}
				}
				// Change the return result to true if the pawn can move
				if (!foundPieceBlocking) {
					result = true;
					break;
				}
			}
		}
		return result;
	}

	/**
	 * The method for the pc to make the move
	 */
	public void pcMove(){
		// Check if game finished 
		if (server && finishgame != null) {
			try {
				// Send game results to client
				outToClient.writeBytes(finishgame + "\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}

		// Check if client move was invalid
		if (clientTurn && clientInvalidMove != null) {
			try {
				// Send the message that was invalid or needs to capture
				outToClient.writeBytes(clientInvalidMove + '\n');
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			// Make the message null for future moves
			clientInvalidMove = null;
			return;
		}

		boolean foundMove = false;
		// Loop until the pc found a move
		while (!foundMove) {
			ChessPiece c;
			if (!client) {
                // Iterate each piece to check for forced moves
                for (ChessPiece forced : blackChessPieces) {
                    ArrayList<String> forcedMoves = forced.canCapture();
                    // If there are forced moves, choose one randomly
                    if (!forcedMoves.isEmpty()) {
                        move = forcedMoves.get((int)(Math.random() * forcedMoves.size()));
						foundMove = true;
                    }
                }
                // Choose a piece randomly
                c = blackChessPieces.get((int)(Math.random() * blackChessPieces.size()));
            } else {
                // Iterate each piece to check for forced moves
                for (ChessPiece forced : whiteChessPieces) {
                    ArrayList<String> forcedMoves = forced.canCapture();
                    // If there are forced moves, choose one randomly
                    if (!forcedMoves.isEmpty()) {
                        move = forcedMoves.get((int)(Math.random() * forcedMoves.size()));
                        foundMove = true;
                    }
                }
                // Choose a piece randomly
                c = whiteChessPieces.get((int)(Math.random() * whiteChessPieces.size()));
            }
			if (foundMove) break;

			// Get the move
			String pcMove = c.pcMove();
			// If the move is not valid, try again
			if (!c.validMove(pcMove)) continue;
			move = pcMove;
			foundMove = true;
		}
		// Check if the cient is sending a move
		if (client) {
			try {
				// Send move to server
				outToServer.writeBytes(move + '\n');
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		// Check if the server is sending a move
		if (server) {
			try {
				// Send move to client
				outToClient.writeBytes(move + '\n');
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		// Make your move
		doAndValidateMove();
	}

	/**
	 * The main method that runs the program
	 * 
	 * @param args H to play against human, C to play against pc, S to be server, C to be client + IP to connect
	 */
	public static void main(String args[]) throws Exception{
		
		// // Need to take command line arguments
		if (args.length == 0) {
			System.err.println("Usage: java ... ");
			System.exit(1);
		}
		else {
			// Check what type of game the user wants to play
			if (args[0].equals("H")) {
				pcPlayer = false;
				// construct our object and have its run method invoked to
				// set up the GUI once its thread is ready
				javax.swing.SwingUtilities.invokeLater(new notchess());
			}
			if (args[0].equals("C")) {
				pcPlayer = true;
				// construct our object and have its run method invoked to
		        // set up the GUI once its thread is ready
				javax.swing.SwingUtilities.invokeLater(new notchess());
			}
			if (args[0].equals("S")) {
                server = true;
                ServerChess server = new ServerChess();
                server.start();
            }
            if (args[0].equals("T")) {
                client = true;
                ClientChess client = new ClientChess();
                client.start(args[1]);
            }
		}

	}
}

/**
 * Override the JButton class to add position
 */
class ChessButton extends JButton{

	// The position of the button
	protected int row;
	protected int col;
	
	/**
	 * Constructor of the class
	 * 
	 * @param row The row where the button is 
	 * @param col The column where the button is
	 */
	public ChessButton(int row, int col) {
		this.row = row;
		this.col = col;
	}
	
	/**
	 * Returns the row where the button is 
	 * 
	 * @return The row of the position
	 */
	public int getRow(){
		return row;
	}
	
	/**
	 * Returns the column where the button is 
	 * 
	 * @return The column of the position
	 */
	public int getCol(){
		return col;
	}
}

/**
 * Class extending notchess to work as a server
 */
class ServerChess extends notchess {
    static ServerSocket welcomeSocket;
    static Socket clientSocket;

	/**
	 * Method to start the server
	 */
    public void start() throws Exception {
		// Start the graphics of the game
        thisTGC.run();
		// Make the connection
        welcomeSocket = new ServerSocket(3074);
        clientSocket = welcomeSocket.accept();
        inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        outToClient = new DataOutputStream(clientSocket.getOutputStream());
		// Send initial message
		outToClient.writeBytes("Please Move\n");
        while(true)  {
			// Receive move from client
            move = inFromClient.readLine();
			clientTurn = true;
			// Check in message was null
            if (move == null) break;
			// Make move
            doAndValidateMove();
			// Check if game finished 
			if (server && finishgame != null) {
				try {
					// Send game results to client
					outToClient.writeBytes(finishgame + "\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
				return;
			}

			// Wait two seconds for the server move
			TimeUnit.SECONDS.sleep(2);
			// Make servere move 
            pcMove();
        }
    }
}

/**
 * Class to make notchess work as a client
 */
class ClientChess extends notchess {

	/**
	 * Method to start the client
	 * 
	 * @param ip The ip to connect
	 */
    public void start(String ip) throws Exception {
		// Start the graphics of the game
        thisTGC.run();
		// Make the connection
        Socket clientSocket = new Socket(ip, 3074);
        outToServer = new DataOutputStream(clientSocket.getOutputStream());
        inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		String start = inFromServer.readLine();

		// Wait for the server to send the initial message
		while (!start.equals("Please Move")) {
			start = inFromServer.readLine();
		}
		// Store if the server said that the move is invalid
		boolean invalidMove = false;
		
        while (true) {
			// Make the move
            pcMove();
			// Receive input from server
            String serverInput = inFromServer.readLine();
			// Check if input is invalid
			if (serverInput.equals("INVALID MOVE") || serverInput.equals("NEED TO CAPTURE")) {
				moveLabel.setText(prevMove + " Need human judge");
				invalidMove = true;
			}

			// Stop the program if the input was invalid
			if (invalidMove) {
				while (prevMove.equals(move)) {
					continue;
				}
			}

			// Check if game finished
			if (serverInput.equals("WHITE WINS") || serverInput.equals("BLACK WINS") || serverInput.equals("TIE GAME")) {
				moveLabel.setText(serverInput);
				break;
			}

			// If move was valid, input was the next server move
			move = serverInput;
			// Make the server move
            doAndValidateMove();
        }
		clientSocket.close();
    }
}