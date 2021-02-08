package Chess;

public class Driver {
	public static void main (String[] args) {
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++) {
			ChessGame g = new ChessGame();
			Move m;
			for (int j = 0; j < 40; j++) {
				m = g.getValidMoves().getFirst();
				g.doMove(m.toString());
			}
			for (int j = 0; j < 30; j++) {
				g.undoMove();
			}
		}
		long endTime = System.currentTimeMillis();
		System.out.println("Took " + (endTime - startTime));
	}
}
