package Chess;

import java.util.LinkedList;

public class ChessBoard {
	private LinkedList<Piece> pieces;
	private LinkedList<Move> legalMoves;
	
	private boolean whitesTurn;
	
	protected byte numRows = 8;
	protected byte numCols = 8;
	
	protected PieceID[][] boardPieces;
	protected boolean[][] boardTeams;
	
	protected byte enPassantCol;
	
	public ChessBoard() {
		pieces = new LinkedList<Piece>();
		boardPieces = new PieceID[numRows][numCols];
		boardTeams = new boolean[numRows][numCols];
		enPassantCol = (byte)-1;
		defaultBoard();
		genAllMoves();
	}
	
	public ChessBoard(ChessBoard original) { // copy constructor
		// this can copy each array reference because the PieceID's themselves do not change, just the 2d array itself does
		boardPieces = new PieceID[numRows][numCols];
		for (int row = 0; row < numRows; row++) {
			for (int col = 0; col < numCols; col++) {
				boardPieces[row][col] = original.boardPieces[row][col];
			}
		}
		// this can also copy each array reference
		boardTeams = new boolean[numRows][numCols];
		for (int row = 0; row < numRows; row++) {
			for (int col = 0; col < numCols; col++) {
				boardTeams[row][col] = original.boardTeams[row][col];
			}
		}
		// this also works for reference copy because the moves themselves do not change
		legalMoves = new LinkedList<Move>();
		for (Move m : original.legalMoves) {
			legalMoves.add(m);
		}
		// the en passant col and turn variables do change, but since they are primitive types this works
		enPassantCol = original.enPassantCol;
		whitesTurn = original.whitesTurn;
		// each piece, however, does change. This means that we have to make add a copy of each piece to the new piece list
		pieces = new LinkedList<Piece>();
		for (Piece p : original.pieces) {
			if (p != null) {
				Piece copyPiece = p.copy();
				pieces.add(copyPiece);
			}
		}
		
	}
	
	public void defaultBoard() {
		whitesTurn = true;
		generateSide(true);
		generateSide(false);
	}
	
	public void genAllMoves() {
		legalMoves = new LinkedList<Move>();
		for (Piece p : pieces) {
			if (p != null) {
				p = p.genMoves(this, null);
				
				if (p != null && p.team == whitesTurn) {
					LinkedList<Move> additions = p.possibleMoves;
					if (additions.size() > 0) {
						legalMoves.addAll(additions);
					}
				}
			}
		}
	}
	
	public void printLegalMoves() {
		for (Move m : legalMoves) {
			System.out.print(m + ", ");
		}
		System.out.println();
	}
	
	public Move doMove(String mStr) {
		for (Move m : legalMoves) {
			if (m.toString().equals(mStr)) {
				doMove(m);
				return m;
			}
		}
		System.out.println("Illegal move");
		return null;
	}
	
	public LinkedList<Move> getValidMoves() {
		return legalMoves;
	}
	
	public boolean getTurn() {
		return whitesTurn;
	}
	
	public void doMove(Move m) {
		legalMoves = new LinkedList<Move>();
		byte startRow = m.startRow;
		byte startCol = m.startCol;
		byte endRow = m.endRow;
		byte endCol = m.endCol;
		boolean isCapture = m.isCapture;
		byte captureRow = m.captureRow;
		byte captureCol = m.captureCol;
		PieceID endPiece = m.endPiece;
		
		PieceID movePiece = boardPieces[startRow][startCol];
		boolean movePieceTeam = boardTeams[startRow][startCol];
		
		if (enPassantCol != -1) {
			enPassantCol = (byte)-1;
		}
		
		if (movePiece == PieceID.PAWN) { // updates en passant column information if applicable
			byte startingRow = movePieceTeam ? (byte)1 : (byte) (numRows - 2);
			if (startRow == startingRow && Math.abs(endRow - startRow) == 2) {
				enPassantCol = startCol;
			}
		}
		
		if (isCapture) {
			boardPieces[captureRow][captureCol] = null;
		}
		
		boardPieces[startRow][startCol] = null;
		boardPieces[endRow][endCol] = endPiece;
		boardTeams[endRow][endCol] = movePieceTeam;
		
		
		
		whitesTurn = !whitesTurn;
		
		for (Piece p : pieces) {
			if (p != null) {
				if (p.dependentOnMove(m)) {
					p = p.genMoves(this, m);
				}
				if (p != null && p.team == whitesTurn) {
					LinkedList<Move> additions = p.possibleMoves;
					if (additions.size() > 0) {
						legalMoves.addAll(additions);
					}
				}
			}
		}
	}
	
	public void undoMove(Move m) {
		legalMoves = new LinkedList<Move>();
		byte startRow = m.startRow;
		byte startCol = m.startCol;
		byte endRow = m.endRow;
		byte endCol = m.endCol;
		boolean isCapture = m.isCapture;
		byte addRow = m.captureRow;
		byte addCol = m.captureCol;
		
		PieceID movePiece = m.isPromotion ? PieceID.PAWN : m.endPiece;
		boolean movePieceTeam = boardTeams[endRow][endCol];
		
		if (movePiece == PieceID.PAWN) {
			if (Math.abs(startRow - endRow) == 2) {
				enPassantCol = (byte)startCol;
			}
		} else {
			enPassantCol = (byte)-1;
		}
		
		if (isCapture) {
			boardPieces[addRow][addCol] = m.capturedPiece;
			boardTeams[addRow][addCol] = !movePieceTeam;
		}
		
		boardPieces[endRow][endCol] = null;
		boardPieces[startRow][startCol] = movePiece;
		boardTeams[startRow][startCol] = movePieceTeam;
		
		whitesTurn = !whitesTurn;
		
		for (Piece p : pieces) {
			if (p != null) {
				if (p.dependentOnMove(m)) {
					p = p.genMoves(this, m);
				}
				if (p != null && p.team == whitesTurn) {
					LinkedList<Move> additions = p.possibleMoves;
					if (additions.size() > 0) {
						legalMoves.addAll(additions);
					}
				}
			}
		}
	}
	
	public PieceID[][] getBoard() {
		return boardPieces;
	}
	
	public boolean[][] getBoardTeams() {
		return boardTeams;
	}
	
	private void generateSide(boolean team) {
		int direction = team ? 1 : -1;
		int startRow = team ? 0 : numRows - 1;
		
		for (byte col = 0; col < numCols; col++) { // creates the pawn row
			Piece newPiece = new Pawn((byte) (startRow + direction), col, team, PieceID.PAWN);
			pieces.add(newPiece);
			boardPieces[startRow + direction][col] = PieceID.PAWN;
			boardTeams[startRow + direction][col] = team;
		}
		
		for (byte col = 0; col < numCols; col++) { // creates the back row
			if (col == 0 || col == 7) { // rooks
				Piece newPiece = new Rook((byte) (startRow), col, team, PieceID.ROOK);
				pieces.add(newPiece);
				boardPieces[startRow][col] = PieceID.ROOK;
				boardTeams[startRow][col] = team;
			} else if (col == 1 || col == 6) { // knights
				Piece newPiece = new Knight((byte) (startRow), col, team, PieceID.KNIGHT);
				pieces.add(newPiece);
				boardPieces[startRow][col] = PieceID.KNIGHT;
				boardTeams[startRow][col] = team;
			} else if (col == 2 || col == 5) { // bishops
				Piece newPiece = new Bishop((byte) (startRow), col, team, PieceID.BISHOP);
				pieces.add(newPiece);
				boardPieces[startRow][col] = PieceID.BISHOP;
				boardTeams[startRow][col] = team;
			} else if (col == 3) { // queen
				Piece newPiece = new Queen((byte) (startRow), col, team, PieceID.QUEEN);
				pieces.add(newPiece);
				boardPieces[startRow][col] = PieceID.QUEEN;
				boardTeams[startRow][col] = team;
			} else { // col == 4, king
				Piece newPiece = new King((byte) (startRow), col, team, PieceID.KING);
				pieces.add(newPiece);
				boardPieces[startRow][col] = PieceID.KING;
				boardTeams[startRow][col] = team;
			}
		}
	}
}
