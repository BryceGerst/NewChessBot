package Chess;
import java.util.ArrayList;
import java.util.Stack;

public class ChessGame {
	
	private Stack<Board> boards;
	private ArrayList<Move> validMoves;
	private Bot bot;
	
	public ChessGame() {
		Board startBoard = new Board();
		boards = new Stack<Board>();
		
		boards.push(startBoard);
		validMoves = BoardTools.genValidMoves(boards.peek());
		
		bot = new Bot(startBoard);
	}
	
	public boolean doMove(String move) { // returns true if the game is over
		if (move.equals("undo")) {
			undoMove();
			return false;
		} else if (move.equals("move history")) {
			for (Board b : boards) {
				System.out.println(b.latestMove);
			}
			return false;
		} else if (move.equals("bot")) {
			bot.analyzeBoard(boards.peek());
			move = bot.makeMove(10000); // 10000
		}
		
		//System.out.println(validMoves.size() + " valid moves: " + validMoves);
		
		if (isMoveValid(move)) {
			Board newBoard = BoardTools.forceMove(boards.peek(), move);
			
			boards.push(newBoard);
			validMoves = BoardTools.genValidMoves(boards.peek());
			
			if (validMoves.size() == 0) { // indicates one of the two types of mates
				if (BoardTools.kingInCheck(boards.peek(), boards.peek().whitesTurn)) {
					System.out.println("Checkmate, " + (!boards.peek().whitesTurn ? "white" : "black") + " wins");
				} else {
					System.out.println("Stalemate");
				}
			}
			return true;
		} else {
			if (isMoveValid(move + "ep")) {
				move = move + "ep";
				return doMove(move);
			} else {
				System.out.println("invalid move");
				return false;
			}
		}
	}
	
	private boolean isMoveValid(String moveName) {
		for (Move move : validMoves) {
			if (moveName.equals(move.toString())) {
				return true;
			}
		}
		return false;
	}
	
	public void undoMove() {
		if (boards.size() > 1) {
			boards.pop();
			validMoves = BoardTools.genValidMoves(boards.peek());
		} else {
			System.out.println("can't undo past starting board");
		}
	}
	
	public String toString() {
		return boards.peek().toString();
	}
	
	public Piece[][] getBoard() {
		return boards.peek().board;
	}
	
	public boolean getTurn() {
		return boards.peek().whitesTurn;
	}
	
	public ArrayList<Move> getValidMoves() {
		return validMoves;
	}
}
