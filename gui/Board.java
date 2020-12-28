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
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import Chess.ChessGame;
import Chess.Piece;
import Chess.Move;


public class Board extends JPanel implements MouseListener, KeyListener {

	private static final long serialVersionUID = 1L;
	
	private int width;
	private int height;
	private final int squareSideLength = 100;
	
	
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
	
	private Random r;
	
	private ArrayList<String> destinationSquares;
	
	private ChessGame game;
	
	private int click1x;
	private int click1y;
	
	private int click2x;
	private int click2y;
	

    public Board() {
    	
    	width = squareSideLength * 8;
    	height = squareSideLength * 8;
    	
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
        
        int w = width;
        int h =  height;
        setPreferredSize(new Dimension(w, h));  
        
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
    	
    	for (int y = 0; y < height; y += squareSideLength) {
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
    		isDarkSquare = !isDarkSquare;
    	}
    	
    	Piece[][] board = game.getBoard();
    	for (int row = 0; row < 8; row++) {
    		for (int col = 0; col < 8; col++) {
    			int y = (7 - row) * squareSideLength;
    			int x = col * squareSideLength;
    			Piece pieceOn = board[row][col];
    			
    			if (pieceOn != null) {
    				if (pieceOn.getTeam()) {
	    				switch (pieceOn.getType()) {
	    					case Piece.PAWN: g.drawImage(whitePawn, x, y, this); break;
	    					case Piece.ROOK: g.drawImage(whiteRook, x, y, this); break;
	    					case Piece.KNIGHT: g.drawImage(whiteKnight, x, y, this); break;
	    					case Piece.BISHOP: g.drawImage(whiteBishop, x, y, this); break;
	    					case Piece.QUEEN: g.drawImage(whiteQueen, x, y, this); break;
	    					case Piece.KING: g.drawImage(whiteKing, x, y, this); break;
	    				}
    				} else {
    					switch (pieceOn.getType()) {
	    					case Piece.PAWN: g.drawImage(blackPawn, x, y, this); break;
	    					case Piece.ROOK: g.drawImage(blackRook, x, y, this); break;
	    					case Piece.KNIGHT: g.drawImage(blackKnight, x, y, this); break;
	    					case Piece.BISHOP: g.drawImage(blackBishop, x, y, this); break;
	    					case Piece.QUEEN: g.drawImage(blackQueen, x, y, this); break;
	    					case Piece.KING: g.drawImage(blackKing, x, y, this); break;
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
		destinationSquares = new ArrayList<String>();
		
		int row = (height - y) / squareSideLength;
		int col = x / squareSideLength;
		if (game.getBoard()[row][col] != null && game.getBoard()[row][col].getTeam() == game.getTurn()) {
			click1x = x;
			click1y = y;
			
			int aVal = (int)('a');
			char startLetter = (char)(aVal + col);
			String clickedCell = "" + startLetter + (row + 1);;
			
			ArrayList<Move> validMoves = game.getValidMoves();
			for (Move move : validMoves) {
				if (move.toString().substring(0, 2).equals(clickedCell)) {
					destinationSquares.add(move.toString().substring(2,4));
				}
			}
			repaint();
		} else if (click1x != -100) {
			click2x = x;
			click2y = y;
			updateGame();
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