package Chess;
import java.util.Scanner;

public class Driver {
	
	public static void main(String[] args) {
		ChessGame game = new ChessGame();
		Scanner input = new Scanner(System.in);
		String response;
		
		System.out.println("When prompted to enter a move, you can input a move, type bot for the bot to move, type undo to undo the last move, type 'move history' to see the move history, or type -1 to stop");
		
		do {
			System.out.println("-----------------------------");
			System.out.println(game);
			System.out.print("Enter a move: ");
			response = input.nextLine();
			
			if (!response.equals("-1")) {
				game.doMove(response);
			}
			System.out.println("-----------------------------\n\n");
		} while(!response.equals("-1"));
		input.close();
	}
}
