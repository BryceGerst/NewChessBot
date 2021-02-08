package gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import Chess.ChessGame;
import Chess.Move;
import Chess.PieceID;


public class Board extends JPanel implements MouseListener, KeyListener {

	private static final long serialVersionUID = 1L;
	
	private int width;
	private int height;
	private int w;
	private int h;
	private final int squareSideLength = 100;
	
	private int panelWidth;
	private int panelHeight;
	
	
	private Image lightSquare;
	private Image darkSquare;
	private Image highlightSquare;
	private Image possibleMove;
	
	private Image whitePawn;
	private Image whiteRook;
	private Image whiteKnight;
	private Image whiteBishop;
	private Image whiteQueen;
	private Image whiteKing;
	
	private Image blackPawn;
	private Image blackRook;
	private Image blackKnight;
	private Image blackBishop;
	private Image blackQueen;
	private Image blackKing;
	
	private Image undoButton;
	private Image botButton;
	private Image resetButton;
	private Image blankButton;
	private Image whiteButton;
	private Image blackButton;
	
	private Random r;
	
	private ArrayList<String> destinationSquares;
	
	private ChessGame game;
	
	private int click1x;
	private int click1y;
	
	private int click2x;
	private int click2y;
	
	private String addPieceName;
	private boolean addPieceTeam;
	
	Image[][] optionsMenu;
	

    public Board() {
    	
    	width = squareSideLength * 8;
    	height = squareSideLength * 8;
    	
    	// the following numbers determine the additional width of the screen created to adjust for the options panel
    	panelWidth = squareSideLength * 3;
    	panelHeight = 0;
    	
    	game = new ChessGame();
    	
    	setFocusable(true);
    	
    	addMouseListener(this);
    	addKeyListener(this);
    	
    	click1x = click1y = -100;
    	destinationSquares = new ArrayList<String>();

        initBoard();
    }
    
    private void initBoard() {
        
        loadImages();
        
        r = new Random();
        
        w = width + panelWidth;
        h =  height + panelHeight;
        setPreferredSize(new Dimension(w, h));
        
        optionsMenu = new Image[8][3];
        optionsMenu[0][0] = undoButton;
        optionsMenu[0][1] = botButton;
        optionsMenu[0][2] = resetButton;
        optionsMenu[1][0] = blankButton;
        optionsMenu[1][1] = whiteButton;
        optionsMenu[1][2] = blackButton;
        optionsMenu[2][0] = whitePawn;
        optionsMenu[2][1] = whiteRook;
        optionsMenu[2][2] = whiteKnight;
        optionsMenu[3][0] = whiteBishop;
        optionsMenu[3][1] = whiteQueen;
        optionsMenu[3][2] = whiteKing;
        optionsMenu[4][0] = blackPawn;
        optionsMenu[4][1] = blackRook;
        optionsMenu[4][2] = blackKnight;
        optionsMenu[5][0] = blackBishop;
        optionsMenu[5][1] = blackQueen;
        optionsMenu[5][2] = blackKing;
        
    }
    
    private void loadImages() {
        
        lightSquare = loadPieceImage("lightSquare");
        darkSquare = loadPieceImage("darkSquare");
        highlightSquare = loadPieceImage("highlightSquare");
        possibleMove = loadPieceImage("possibleMove");
        
        whitePawn = loadPieceImage("whitePawn");
        whiteRook = loadPieceImage("whiteRook");
        whiteKnight = loadPieceImage("whiteKnight");
        whiteBishop = loadPieceImage("whiteBishop");
        whiteQueen = loadPieceImage("whiteQueen");
        whiteKing = loadPieceImage("whiteKing");
        
        blackPawn = loadPieceImage("blackPawn");
        blackRook = loadPieceImage("blackRook");
        blackKnight = loadPieceImage("blackKnight");
        blackBishop = loadPieceImage("blackBishop");
        blackQueen = loadPieceImage("blackQueen");
        blackKing = loadPieceImage("blackKing");
        
        undoButton = loadPieceImage("undo");
    	botButton = loadPieceImage("bot");
    	resetButton = loadPieceImage("reset");
    	blankButton = loadPieceImage("blank");
    	
    	whiteButton = loadPieceImage("whitesMove");
    	blackButton = loadPieceImage("blacksMove");
    }
    
    private Image loadPieceImage(String name) {
    	ImageIcon icon = new ImageIcon("src/gui/Images/" + name + ".png");
    	Image image = scaleImage(icon, squareSideLength, squareSideLength);
    	return image;
    }
    
    private Image scaleImage(ImageIcon icon, int newWidth, int newHeight) {
    	return icon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
    }

    @Override
    public void paintComponent(Graphics g) {
    	
    	//super.paintComponent(g);
    	
    	//g.fillRect(0, 0, width, height);
    	
    	boolean isDarkSquare = true;
    	
    	int optionsRow = 0, optionsCol;
    	for (int y = 0; y < height; y += squareSideLength) {
    		optionsCol = 0;
    		for (int x = 0; x < width; x += squareSideLength) {
    			if (click1x != -100 && click1x >= x && click1x < x + squareSideLength && click1y >= y && click1y < y + squareSideLength) {
    				g.drawImage(highlightSquare, x, y, this);
    			} else {
    				if (isDarkSquare) {
        				g.drawImage(lightSquare, x, y, this);
        			} else {
        				g.drawImage(darkSquare, x, y, this);
        			}
    			}
    			isDarkSquare = !isDarkSquare;
    		}
    		for (int x = width; x < w; x += squareSideLength) {
    			Image option = optionsMenu[optionsRow][optionsCol];
    			if (option != null) {
    				g.drawImage(option, x, y, this);
    			}
    			optionsCol++;
    		}
    		optionsRow++;
    		isDarkSquare = !isDarkSquare;
    	}
    	
    	PieceID[][] board = game.getBoard();
    	boolean[][] boardTeams = game.getBoardTeams();
    	for (int row = 0; row < 8; row++) {
    		for (int col = 0; col < 8; col++) {
    			int y = (7 - row) * squareSideLength;
    			int x = col * squareSideLength;
    			PieceID pieceOn = board[row][col];
    			
    			if (pieceOn != null) {
    				if (boardTeams[row][col]) {
	    				switch (pieceOn) {
	    					case PAWN: g.drawImage(whitePawn, x, y, this); break;
	    					case ROOK: g.drawImage(whiteRook, x, y, this); break;
	    					case KNIGHT: g.drawImage(whiteKnight, x, y, this); break;
	    					case BISHOP: g.drawImage(whiteBishop, x, y, this); break;
	    					case QUEEN: g.drawImage(whiteQueen, x, y, this); break;
	    					case KING: g.drawImage(whiteKing, x, y, this); break;
	    				}
    				} else {
    					switch (pieceOn) {
	    					case PAWN: g.drawImage(blackPawn, x, y, this); break;
	    					case ROOK: g.drawImage(blackRook, x, y, this); break;
	    					case KNIGHT: g.drawImage(blackKnight, x, y, this); break;
	    					case BISHOP: g.drawImage(blackBishop, x, y, this); break;
	    					case QUEEN: g.drawImage(blackQueen, x, y, this); break;
	    					case KING: g.drawImage(blackKing, x, y, this); break;
    					}
    				}
    			}
    		}
    	}
    	
    	for (String square : destinationSquares) {
    		int aVal = (int)('a');
    		int row = 7 - (Integer.parseInt("" + square.charAt(1)) - 1);
			int col = ((int)square.charAt(0)) - aVal;
			g.drawImage(possibleMove, col * squareSideLength, row * squareSideLength, this);
    	}
    }
    
    public void updateGame() {
    	int startCol = click1x / squareSideLength;
    	int startRow = (height - click1y) / squareSideLength;
    	int endCol = click2x / squareSideLength;
    	int endRow = (height - click2y) / squareSideLength;
    	int aVal = (int)('a');
		char startLetter = (char)(aVal + startCol);
		char endLetter = (char)(aVal + endCol);
		
		String move = "" + startLetter + (startRow + 1) + endLetter + (endRow + 1);
		
		/*if (enPassant) {
			move += "ep";
		} else {
			move += upgradePieceName;
		}*/  // TODO: implement good ways to do en passant and upgrading
		
		System.out.println("this your move? " + move);
		
		if (game.doMove(move)) { // only plays sound on valid moves
			playMoveSound();
		}
		
		repaint();
		
    	click1x = click1y = -100;
    }
    
    private void playMoveSound() {
    	int result = r.nextInt(7); // the number at the end should be the number of move noises
    	File f = new File("src/gui/Images/pieceMove" + result + ".wav");
    	try {
	        AudioInputStream audioIn = AudioSystem.getAudioInputStream(f.toURI().toURL());  
	        Clip clip = AudioSystem.getClip();
	        clip.open(audioIn);
	        clip.start();
    	} catch (Exception e) {}
    }
    
    @Override
	public void mouseClicked(MouseEvent e) {
		//System.out.println("Mouse Clicked at X: " + x + " - Y: " + y);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		//int x = e.getX();
		//int y = e.getY();
		//System.out.println("Mouse Entered frame at X: " + x + " - Y: " + y);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		//int x = e.getX();
		//int y = e.getY();
		//System.out.println("Mouse Exited frame at X: " + x + " - Y: " + y);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		//int x = e.getX();
		//int y = e.getY();
		int x = e.getX();
		int y = e.getY();
		
		if (x < width && y < height) {
			destinationSquares = new ArrayList<String>();
			
			int row = (height - y) / squareSideLength;
			int col = x / squareSideLength;
			if (game.getBoard()[row][col] != null && game.getBoardTeams()[row][col] == game.getTurn()) {
				click1x = x;
				click1y = y;
				
				int aVal = (int)('a');
				char startLetter = (char)(aVal + col);
				String clickedCell = "" + startLetter + (row + 1);;
				
				LinkedList<Move> validMoves = game.getValidMoves();
				for (Move move : validMoves) {
					if (move.toString().substring(0, 2).equals(clickedCell)) {
						destinationSquares.add(move.toString().substring(2,4));
					}
				}
				repaint();
			} else if (click1x != -100 && click1x != -101) {
				click2x = x;
				click2y = y;
				updateGame();
			} else if (click1x == -101) {
				//game.addPiece(row, col, addPieceName, addPieceTeam);
				click1x = -100;
				repaint();
			}
		} else { // means they are using the options panel
			int rowChoice = 7 - ((height - y) / squareSideLength);
			int colChoice = (x - width) / squareSideLength;
			if (rowChoice == 0) {
				if (colChoice == 0) {
					destinationSquares = new ArrayList<String>();
					click1x = -100;
					//game.undoMove();
					repaint();
				} else if (colChoice == 1) {
					destinationSquares = new ArrayList<String>();
					click1x = -100;
					if (game.doMove("bot")) {
						playMoveSound();
					}
					repaint();
				} else if (colChoice == 2) {
					destinationSquares = new ArrayList<String>();
					click1x = -100;
					//game.reset();
					repaint();
				}
			} else if (rowChoice == 1) {
				if (colChoice == 0) {
					destinationSquares = new ArrayList<String>();
					click1x = -100;
					//game.blank();
					repaint();
				} else if (colChoice == 1) {
					destinationSquares = new ArrayList<String>();
					click1x = -100;
					//game.setTurn(true);
					repaint();
				} else if (colChoice == 2) {
					destinationSquares = new ArrayList<String>();
					click1x = -100;
					//game.setTurn(false);
					repaint();
				}
			} else if (rowChoice == 2) {
				if (colChoice == 0) { // white pawn
					destinationSquares = new ArrayList<String>();
					click1x = -101;
					addPieceName = "Pawn";
					addPieceTeam = true;
				} else if (colChoice == 1) { // white rook
					destinationSquares = new ArrayList<String>();
					click1x = -101;
					addPieceName = "Rook";
					addPieceTeam = true;
				} else if (colChoice == 2) { // white knight
					destinationSquares = new ArrayList<String>();
					click1x = -101;
					addPieceName = "Knight";
					addPieceTeam = true;
				}
			} else if (rowChoice == 3) {
				if (colChoice == 0) { // white bishop
					destinationSquares = new ArrayList<String>();
					click1x = -101;
					addPieceName = "Bishop";
					addPieceTeam = true;
				} else if (colChoice == 1) { // white queen
					destinationSquares = new ArrayList<String>();
					click1x = -101;
					addPieceName = "Queen";
					addPieceTeam = true;
				} else if (colChoice == 2) { // white king
					destinationSquares = new ArrayList<String>();
					click1x = -101;
					addPieceName = "King";
					addPieceTeam = true;
				}
			} else if (rowChoice == 4) {
				if (colChoice == 0) { // black pawn
					destinationSquares = new ArrayList<String>();
					click1x = -101;
					addPieceName = "Pawn";
					addPieceTeam = false;
				} else if (colChoice == 1) { // black rook
					destinationSquares = new ArrayList<String>();
					click1x = -101;
					addPieceName = "Rook";
					addPieceTeam = false;
				} else if (colChoice == 2) { // black knight
					destinationSquares = new ArrayList<String>();
					click1x = -101;
					addPieceName = "Knight";
					addPieceTeam = false;
				}
			} else if (rowChoice == 5) {
				if (colChoice == 0) { // black bishop
					destinationSquares = new ArrayList<String>();
					click1x = -101;
					addPieceName = "Bishop";
					addPieceTeam = false;
				} else if (colChoice == 1) { // black queen
					destinationSquares = new ArrayList<String>();
					click1x = -101;
					addPieceName = "Queen";
					addPieceTeam = false;
				} else if (colChoice == 2) { // black king
					destinationSquares = new ArrayList<String>();
					click1x = -101;
					addPieceName = "King";
					addPieceTeam = false;
				}
			}
		}
		
		//System.out.println("Mouse Pressed at X: " + x + " - Y: " + y);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		//int x = e.getX();
		//int y = e.getY();
		//System.out.println("Mouse Released at X: " + x + " - Y: " + y);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		
		if (key == KeyEvent.VK_U) {
			destinationSquares = new ArrayList<String>();
			click1x = -100;
			game.undoMove();
			repaint();
		} else if (key == KeyEvent.VK_B) {
			destinationSquares = new ArrayList<String>();
			click1x = -100;
			if (game.doMove("bot")) {
				playMoveSound();
			}
			repaint();
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}