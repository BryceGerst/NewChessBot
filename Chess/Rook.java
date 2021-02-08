package Chess;

import java.util.LinkedList;

public class Rook extends Piece {
	
	public Rook(byte row, byte col, boolean team, PieceID id) {
		this.row = row;
		this.col = col;
		this.team = team;
		this.id = id;
		isAlive = true;
	}
	
	@Override
	public Piece copy() {
		Rook copy = new Rook(row, col, team, id);
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
			}
			else if (latestMove != null && (latestMove.isCapture && latestMove.captureRow == row && latestMove.captureCol == col)) {
				isAlive = false;
				return null;
			}
			
			byte testRow, testCol, magnitude;
			boolean hasLineOfSight;
			
			for (byte xSign : signs) {
				for (byte ySign : signs) {
					if (xSign == 0 ^ ySign == 0) { // one of the components must be 0 but not both 
						magnitude = (byte)1;
						hasLineOfSight = true;
						
						while (hasLineOfSight) {
							testRow = (byte) (row + (magnitude * ySign));
							testCol = (byte) (col + (magnitude * xSign));
							
							if (testRow >= 0 && testRow < board.numRows && testCol >= 0 && testCol < board.numCols) {
								canMoveTo(board, testRow, testCol);
								PieceID checkPiece = board.boardPieces[testRow][testCol];
								
								if (checkPiece != null) {
									hasLineOfSight = false;
								}
							} else {
								hasLineOfSight = false;
							}
							magnitude += 1;
						}
					}
				}
			}
			
			return this;
		}
	}

}
