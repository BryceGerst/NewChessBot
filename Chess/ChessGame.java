package Chess;

import java.util.LinkedList;
import java.util.Stack;

public class ChessGame {
	private ChessBoard board;
	private Stack<Move> moveList;
	
	public ChessGame() {
		board = new ChessBoard();
		moveList = new Stack<Move>();
	}
	
	public boolean doMove(String moveStr) {
		Move realMove = board.doMove(moveStr);
		if (realMove != null) {
			moveList.add(realMove);
			return true;
		} else {
			return false;
		}
	}
	
	public PieceID[][] getBoard() {
		return board.getBoard();
	}
	
	public boolean[][] getBoardTeams() {
		return board.getBoardTeams();
	}
	
	public boolean getTurn() {
		return board.getTurn();
	}
	
	public LinkedList<Move> getValidMoves() {
		return board.getValidMoves();
	}
	
	public boolean undoMove() {
		Move undoneMove = moveList.pop();
		board.undoMove(undoneMove);
		return true;
	}
}
