package Chess;

import java.util.LinkedList;

public class King extends Piece {
	private boolean canCastleKingSide;
	private boolean canCastleQueenSide;
	
	public King(byte row, byte col, boolean team, PieceID id) {
		this.row = row;
		this.col = col;
		this.team = team;
		this.id = id;
		canCastleKingSide = true;
		canCastleQueenSide = true;
		isAlive = true;
	}
	
	@Override
	public Piece copy() {
		King copy = new King(row, col, team, id);
		copyInfoInto(copy);
		copy.canCastleKingSide = canCastleKingSide;
		copy.canCastleQueenSide = canCastleQueenSide;
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
			
			byte testRow, testCol;
			
			for (int xSign : signs) { // normal king movement
				for (int ySign : signs) {
					if (!(xSign == 0 && ySign == 0)) {
						testRow = (byte) (row + ySign);
						testCol = (byte) (col + xSign);
						
						canMoveTo(board, testRow, testCol);
					}
				}
			}
			
			return this;
		}
	}

}
