package Chess;

import java.util.LinkedList;

public class Pawn extends Piece {
	private byte startRow;
	private byte direction;
	
	public Pawn(byte row, byte col, boolean team, PieceID id) {
		this.row = row;
		this.col = col;
		this.team = team;
		this.id = id;
		
		if (team) { // true if on white
			startRow = 1;
			direction = 1;
		} else {
			startRow = 6;
			direction = -1;
		}
		
		isAlive = true;
	}
	
	@Override
	public Piece copy() {
		Pawn copy = new Pawn(row, col, team, id);
		copyInfoInto(copy);
		return copy;
	}

	@Override
	public Piece genMoves(ChessBoard board, Move latestMove) {
		dependentOnSquare = new boolean[board.numRows][board.numCols];
		possibleMoves = new LinkedList<Move>();
		
		byte[] signs = new byte[] {(byte)-1, (byte)0, (byte)1};
		
		if (!isAlive) { // in theory this should never evaluate to true
			System.out.println("called gen moves on a captured piece");
			return null;
		} else { // TODO: right now this rechecks all possible moves for a piece if a dependency move occured. In the future, this could be made more efficient by only checking some moves that need to be updated.
			if (latestMove != null && (latestMove.startRow == row && latestMove.startCol == col)) { // true if this is the piece that moved
				row = latestMove.endRow;
				col = latestMove.endCol;
				
				dependentOnSquare[row][col] = true;
				
				if (latestMove.endPiece != id) { // handles promotion of the pawn to another piece
					Piece newPiece;
					
					switch (latestMove.endPiece) {
						case QUEEN: newPiece = new Queen(row, col, team, PieceID.QUEEN); break;
						case KNIGHT: newPiece = new Knight(row, col, team, PieceID.KNIGHT); break;
						case ROOK: newPiece = new Rook(row, col, team, PieceID.ROOK); break;
						case BISHOP: newPiece = new Bishop(row, col, team, PieceID.BISHOP); break;
						default: newPiece = new Queen(row, col, team, PieceID.QUEEN); break; // defaults to queen promotion
					}
					
					return newPiece.genMoves(board, null);
				}
			}
			else if (latestMove != null && (latestMove.isCapture && latestMove.captureRow == row && latestMove.captureCol == col)) {
				System.out.println("kill");
				isAlive = false;
				return null;
			}
			
			dependentOnSquare[row][col] = true;
			
			byte endRow, endCol;
			endRow = (byte) (row + direction);
			
			for (byte sign : signs) { // checks if the pawn can move forward, forward left, and forward right
				endCol = (byte) (col + sign);
				canMoveTo(board, endRow, endCol);
			}
			
			if (row == startRow) { // checks if the pawn can move forward twice
				canMoveTo(board, (byte) (row + (2 * direction)), col);
			}
			
			if (row == (startRow + (3 * direction))) { // if it is on a row where is could potential capture en passant, it is dependent on the squares to its left and right
				if (col - 1 >= 0) dependentOnSquare[row][col - 1] = true;
				if (col + 1 < board.numCols) dependentOnSquare[row][col + 1] = true;
			}
			
			return this;
		}
	}
	
	protected boolean canMoveTo(ChessBoard board, byte endRow, byte endCol) { // pawns have fairly unique movement, so this method had to modified specifically for them
		if (endRow >= 0 && endRow < board.numRows && endCol >= 0 && endCol < board.numCols) { // checks if the row and column numbers are inside the board
			dependentOnSquare[endRow][endCol] = true;
			PieceID checkPiece = board.boardPieces[endRow][endCol];
			PieceID[] promotionPieces = new PieceID[] {PieceID.QUEEN, PieceID.KNIGHT, PieceID.ROOK, PieceID.BISHOP};
			
			
			if (col == endCol && checkPiece == null) { // checks if the pawn can move forward to an empty space
				if (endRow == startRow + (6 * direction)) { // checks if the pawn is moving to the final row (meaning it gets promoted)
					for (PieceID promotionPiece : promotionPieces) {
						possibleMoves.add(new Move(row, col, endRow, endCol, promotionPiece, false, (byte)-1, (byte)-1, null, true));
					}
					return true;
				} else if (row == startRow && endRow == startRow + (2 * direction)) { // checks if the pawn is on the starting rank and is trying to move forward 2 spaces
					if (board.boardPieces[startRow + direction][endCol] == null) {
						possibleMoves.add(new Move(row, col, endRow, endCol, id, false, (byte)-1, (byte)-1, null, false));
						return true;
					}
				} else { // otherwise adds the ability to simply move forward one space under normal circumstances
					possibleMoves.add(new Move(row, col, endRow, endCol, id, false, (byte)-1, (byte)-1, null, false));
					return true;
				}
			} else {
				if (checkPiece != null && endCol != col && board.boardTeams[endRow][endCol] != team) { // checks if the pawn can do a normal diagonal capture
					if (endRow == startRow + (6 * direction)) { // checks if the pawn is moving to the final row by means of diagonal capture (meaning it gets promoted)
						for (PieceID promotionPiece : promotionPieces) {
							possibleMoves.add(new Move(row, col, endRow, endCol, promotionPiece, true, endRow, endCol, checkPiece, true));
						}
						return true;
					} else {
						possibleMoves.add(new Move(row, col, endRow, endCol, id, true, endRow, endCol, checkPiece, false));
						return true;
					}
				} else if (endCol == board.enPassantCol && row == (startRow + (3 * direction))) { // checks for en passant capture |||| NOTE: I could see this being quite buggy
					possibleMoves.add(new Move(row, col, endRow, endCol, id, true, row, endCol, checkPiece, false));
					return true;
				}
			}
		}
		return false;
	}

}
