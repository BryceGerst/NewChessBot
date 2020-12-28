package Chess;

import java.security.SecureRandom;

public class Board {
	
	protected Piece[][] board;
	protected boolean whitesTurn;
	protected String latestMove;
	protected int enPassantCol;
	protected boolean canWhiteKingsideCastle;
	protected boolean canWhiteQueensideCastle;
	protected boolean canBlackKingsideCastle;
	protected boolean canBlackQueensideCastle;
	
	private static SecureRandom sr;
	private static long[] zobristKeys;
	static {
		sr = new SecureRandom();

		zobristKeys = new long[781];
		for (int i = 0; i < 781; i++) {
			zobristKeys[i] = sr.nextLong();
			//System.out.println(zobristKeys[i]);
		}
		
		// zobrist[0] is hash for black to move
		// zobrist[1] is white king castle, zobrist[2] is white queen castle
		// zobrist[3] is black king castle, zobrist[4] is black queen castle
		// zobrist[5,6,7,8,9,10,11,12] are files of en passant squares
		// zobrist[13-780 inclusive] are for each piece at each square
	}
	
	public Board() {
		latestMove = "Start of game";
		whitesTurn = true;
		canWhiteKingsideCastle = canWhiteQueensideCastle = canBlackKingsideCastle = canBlackQueensideCastle = true;
		enPassantCol = -100;
		board = new Piece[8][8];
		
		for (int row = 0; row < board.length; row++) {
			if (row == 1 || row == 6) { // pawn rows
				for (int col = 0; col < board[0].length; col++) {
					board[row][col] = new Piece("Pawn", row == 1);
				}
			} else if (row == 0 || row == 7) { // king rows
				board[row][0] = new Piece("Rook", row == 0);
				board[row][1] = new Piece("Knight", row == 0);
				board[row][2] = new Piece("Bishop", row == 0);
				board[row][3] = new Piece("Queen", row == 0);
				board[row][4] = new Piece("King", row == 0);
				board[row][5] = new Piece("Bishop", row == 0);
				board[row][6] = new Piece("Knight", row == 0);
				board[row][7] = new Piece("Rook", row == 0);
			}
		}
	}
	
	public Board(Board b) {
		whitesTurn = b.whitesTurn;
		board = new Piece[8][8];
		latestMove = b.latestMove;
		enPassantCol = b.enPassantCol;
		canWhiteKingsideCastle = b.canWhiteKingsideCastle;
		canWhiteQueensideCastle = b.canWhiteQueensideCastle;
		canBlackKingsideCastle = b.canBlackKingsideCastle;
		canBlackQueensideCastle = b.canBlackQueensideCastle;
		
		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board[0].length; col++) {
				Piece p = b.board[row][col];
				if (p != null) {
					board[row][col] = new Piece(p);
				}
			}
		}
	}
	
	public String toString() {
		String retString = "";
		
		for (int row = board.length - 1; row >= 0; row--) {
			retString += "" + (row + 1) + "\t";
			for (int col = 0; col < board[0].length; col++) {
				Piece printingPiece = board[row][col];
				
				if (printingPiece != null) {
					retString += ("| " + printingPiece + " | ");
				} else {
					retString += ("|    | ");
				}
			}
			retString += "\n";
		}
		
		retString += " \t";
		
		for (int col = 0; col < board[0].length; col++) {
			retString += "  ";
			int start = (int)'a';
			retString += (char)(start + col) + "    ";
		}
		
		return retString;
	}
	
	public long getId() { // used for hash stuff
		Long retHash = null;
		int idModifier = 0;
		for (int r = 0; r < 8; r++) { // hashing for pieces
			for (int c = 0; c < 8; c++) {
				if (board[r][c] != null) {
					int realId = board[r][c].getId() + idModifier;
					if (retHash == null) {
						retHash = zobristKeys[realId];
					}
					else {
						retHash = retHash ^ zobristKeys[realId];
					}
				}
				idModifier++;
			}
		}
		if (!whitesTurn) { // turn hashing
			retHash = retHash ^ zobristKeys[0];
		}
		if (enPassantCol >= 0 && enPassantCol < 8) { // hashing for en passant column
			retHash = retHash ^ zobristKeys[5 + enPassantCol];
		}
		// castling right hashing
		if (canWhiteKingsideCastle) retHash = retHash ^ zobristKeys[1];
		if (canWhiteQueensideCastle) retHash = retHash ^ zobristKeys[2];
		if (canBlackKingsideCastle) retHash = retHash ^ zobristKeys[3];
		if (canBlackQueensideCastle) retHash = retHash ^ zobristKeys[4];
		
		return retHash;
	}
	
	
}
