package Chess;

import java.util.LinkedList;

public abstract class Piece {
	protected byte row; // the row and col values refer to the piece's current location
	protected byte col;
	protected boolean team; // white is true, black is false
	protected PieceID id;
	protected boolean[][] dependentOnSquare; // a piece is dependent on a square if any change to that square would mean that this piece's possible moves would change
	protected LinkedList<Move> possibleMoves;
	protected boolean isAlive; // true at the start, false if captured
	
	protected boolean startDependent;
	protected boolean endDependent;
	protected boolean captureDependent;
	
	
	public abstract Piece genMoves(ChessBoard board, Move latestMove); // returns "this" if not a promotion, returns the new promoted piece otherwise
	
	public abstract Piece copy();
	
	protected void copyInfoInto(Piece destination) {
		destination.dependentOnSquare = new boolean[dependentOnSquare.length][dependentOnSquare[0].length];
		for (int row = 0; row < dependentOnSquare.length; row++) {
			for (int col = 0; col < dependentOnSquare[0].length; col++) {
				destination.dependentOnSquare[row][col] = dependentOnSquare[row][col];
			}
		}
		// this works by means of reference copy because the move objects themselves do not change
		destination.possibleMoves = new LinkedList<Move>();
		for (Move m : possibleMoves) {
			(destination.possibleMoves).add(m);
		}
		destination.startDependent = startDependent;
		destination.endDependent = endDependent;
		destination.captureDependent = captureDependent;
		destination.isAlive = isAlive;
	}
	
	protected boolean canMoveTo(ChessBoard board, byte endRow, byte endCol) { // returns the possible move if it can make the move, otherwise returns null
		if (endRow >= 0 && endRow < board.numRows && endCol >= 0 && endCol < board.numCols) { // checks if the row and column numbers are inside the board
			dependentOnSquare[endRow][endCol] = true;
			PieceID checkPiece = board.boardPieces[endRow][endCol];
			
			if (checkPiece == null)  { // true if the ending square is empty
				possibleMoves.add(new Move(row, col, endRow, endCol, id, false, (byte)-1, (byte)-1, null, false));
			} else if (board.boardTeams[endRow][endCol] != team) { // true if the ending square has a piece belonging to the opposite team (meaning this move is a capture)
				possibleMoves.add(new Move(row, col, endRow, endCol, id, true, endRow, endCol, checkPiece, false));
			}

			return true;
		} else {
			return false;
		}
	}
	
	public boolean dependentOnMove(Move m) {
		startDependent = dependentOnSquare[m.startRow][m.startCol];
		endDependent = dependentOnSquare[m.endRow][m.endCol];
		captureDependent = m.isCapture && dependentOnSquare[m.captureRow][m.captureCol];
		return (startDependent || endDependent || captureDependent);
	}
}
