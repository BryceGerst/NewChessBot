package Chess;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Stack;

public class Bot {
	
	private Stack<Board> boards;
	private long startTime;
	private long timeLimit;
	
	private HashMap<Long, MemoryNode> memory;
	private HashMap<Long, MemoryNode> quiesceMemory;
	
	private int rememberedMoves;
	private int improvedOnRememberedMoves;
	private int totalMovesAnalyzed;
	
	private final int OUT_OF_TIME = -10000001;
	
	private final int maxDepth = 20;
	
	// the following are board rating variables
	private final int pieceValueMultiplier = 20; // the multiplier for the standard piece values
	private final int pieceValueMultiplierIncrease = 1; // for every piece down, the multiplier will increase by this amount. This is meant to incentivize trading when up material.
	private final int bishopPairValue = 5; // points gained from keeping both bishops
	private final int upwardPawns = 2; // points per row from starting row
	private final int kingCanCastle = 6; // gets this many points for being able to castle
	private final int kingSafety = 4; // points for pawns surrounding the king as well as having an escape square in a different row, col, or both, and also being away from the center (I think that is a good thing?)
	private final int doubledPawns = -5; // points for having multiple pawns in the same row
	// idea: private final int pressureValue = 1; // points earned for having pressure on any square
	// maybe add more like backwards pawns
	// end board rating variables
	
	public Bot(Board b) {
		memory = new HashMap<Long, MemoryNode>(100000);
		quiesceMemory = new HashMap<Long, MemoryNode>(100000);
		analyzeBoard(b);
	}
	
	public Bot(Bot copyBot) {
		boards = new Stack<Board>();
		for (Board b : copyBot.boards) {
			boards.push(b);
		}
		startTime = copyBot.startTime;
		timeLimit = copyBot.timeLimit;
		memory = copyBot.memory;
		quiesceMemory = copyBot.quiesceMemory;
		
	}
	
	public void analyzeBoard(Board b) {
		boards = new Stack<Board>();
		boards.push(b);
	}
	
	public String makeMove(long timeLimit) {
		
		System.out.println("Initial board rated " + rateBoard(true));
		
		rememberedMoves = 0;
		improvedOnRememberedMoves = 0;
		totalMovesAnalyzed = 0;
		
		Random r = new Random();
		
		this.timeLimit = timeLimit;
		startTime = System.currentTimeMillis();
		
		String bestMove = "";
		int bestScore = -10000000;
		String prevBestMove = "";
		int prevBestScore = -10000000;
		
		ArrayList<Move> validMoves = BoardTools.genValidMoves(boards.peek());
		
		if (validMoves.size() == 0) {
			return "";
		} else {
			bestMove = validMoves.get(0).toString();
		}
		
		
		int[] resultsArray = new int[validMoves.size()];
		InitialMoveThread[] threads = new InitialMoveThread[validMoves.size()];
		
		for (int depth = 1; depth < maxDepth; depth++) { // concept of iterative deepening
			System.out.println("made it to depth " + depth);
			prevBestMove = bestMove;
			prevBestScore = bestScore;
			bestMove = "";
			bestScore = -10000000;
			
			for (int i = 0; i < validMoves.size(); i++) {
				Move move = validMoves.get(i);
				boards.push(BoardTools.forceMove(boards.peek(), move.toString())); // does the move
				threads[i] = new InitialMoveThread(this, depth, i, resultsArray); // starts the multithreaded processing
				threads[i].start();
				boards.pop(); // undoes the move
			}
			for (InitialMoveThread thread : threads) {
				try {
					thread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (System.currentTimeMillis() - startTime < timeLimit) {
				for (int i = 0; i < resultsArray.length; i++) {
					int result = resultsArray[i];
					int score = -1 * result;
					
					//System.out.println("Expected score " + score + " for move " + validMoves.get(i).toString() + " with current best score of " + bestScore);
					if (score > bestScore) {
						bestScore = score;
						bestMove = validMoves.get(i).toString();
					} else if (score == bestScore) { // picks randomly if moves are rated the same
						if (r.nextInt(2) == 0) {
							bestScore = score;
							bestMove = validMoves.get(i).toString();
						}
					}
				}
			} else {
				if (depth == 1 && bestMove != "") {
					prevBestMove = bestMove;
					prevBestScore = bestScore;
				}
				System.out.println("ran out of time");
				System.out.println("Remembered: " + rememberedMoves + " Improved on: " + improvedOnRememberedMoves + " Total moves analyzed: " + totalMovesAnalyzed);
				System.out.println("Bot decided on " + prevBestMove + " with hopes of " + prevBestScore);
				printExpectedFollowup(prevBestMove);
				return prevBestMove;
			}
			
		}
		
		System.out.println("Remembered: " + rememberedMoves + " Improved on: " + improvedOnRememberedMoves + " Total moves analyzed: " + totalMovesAnalyzed);
		System.out.println("Bot decided on " + bestMove + " with hopes of " + bestScore);
		printExpectedFollowup(bestMove);
		
		return bestMove;
	}
	
	private void printExpectedFollowup(String move) {
		Board test = BoardTools.forceMove(boards.peek(), move);
		long hashValue = test.getId();
		System.out.println("Has " + memory.size() + " boards memorized (normal)");
		System.out.println("Has " + quiesceMemory.size() + " boards memorized (quiesce)");
		
		if (memory.containsKey(hashValue)) {
			MemoryNode mn = memory.get(hashValue);
			System.out.println("Expecting followup move of " + mn.bestMoveName + " from memory");
		} else {
			System.out.println("Has no memory of follow up board (weird and hopefully wrong)");
		}
	}
	
	private int alphaBeta(int alpha, int beta, int depthLeft) {
		if (System.currentTimeMillis() - startTime > timeLimit) {
			return OUT_OF_TIME;
		}
		
		if (depthLeft == 0) {
			int result = quiesce(alpha, beta);
			if (result == -100000) {
				result = -99999; // this means unavoidable checkmate is further out and therefore more desirable, but still bad
			}
			return result;
		}
		
		totalMovesAnalyzed++;
		
		ArrayList<Move> validMoves = BoardTools.genValidMoves(boards.peek());
		String bestMove = "";
		boolean remembersBoard = false;
		int rememberDepth = 0;
		MemoryNode mn = null;
		
		long boardHash = boards.peek().getId();
		synchronized(memory) {
			if (memory.containsKey(boardHash)) {
				mn = memory.get(boardHash);
			}
		}
		if (mn != null) {
			bestMove = mn.bestMoveName;
			rememberDepth = mn.depthSearched;
			
			if (moveIsValid(bestMove, validMoves)) {
				remembersBoard = true;
				//System.out.println("remembered a move ab");
				rememberedMoves++;
				if (rememberDepth >= depthLeft) { // true is memory is just as if not more relevant than this result will be
					return mn.nodeValue;
				}
				boards.push(BoardTools.forceMove(boards.peek(), bestMove));
				int result = alphaBeta(-beta, -alpha, depthLeft - 1);
				boards.pop();
				if (result == OUT_OF_TIME) {
					return alpha;
				}
				int score = -1 * result;
				
				if (score >= beta) {
					return beta;
				}
				if (score > alpha) {
					alpha = score;
				}
			} else {
				System.out.println("misremembered a move ab");
				System.out.println("Was looking for move " + bestMove);
				System.out.println("With board\n" + boards.peek().toString());
				System.out.println(1/0);
			}
		}
		
		for (Move move: validMoves) {
			if (!move.toString().equals(bestMove)) {
				boards.push(BoardTools.forceMove(boards.peek(), move.toString())); // does the move
				int result = alphaBeta(-beta, -alpha, depthLeft - 1);
				boards.pop(); // undoes the move
				if (result == OUT_OF_TIME) {
					return alpha;
				}
				int score = -1 * result;
				
				if (score >= beta) {
					return beta;
				}
				if (score > alpha) {
					alpha = score;
					bestMove = move.toString();
					if (remembersBoard) {
						improvedOnRememberedMoves++;
					}
				}
			}
		}
		
		if (validMoves.size() == 0) {
			int result = rateBoard(false);
			if (result == -100000) {
				result -= (maxDepth - depthLeft); // this way the bot should favor faster checkmates in the endgame
			}
			return result;
		}
		
		if (!bestMove.equals("") && (!remembersBoard || depthLeft > rememberDepth)) { // updates memory with the new best move
			//System.out.println("put " + bestMove + " into hashtable");
			synchronized(memory) {
				memory.put(boardHash, new MemoryNode(bestMove, depthLeft, alpha));
			}
		}
		
		return alpha;
	}
	
	private int quiesce(int alpha, int beta) {
		if (System.currentTimeMillis() - startTime > timeLimit) {
			return alpha;
		}
		
		totalMovesAnalyzed++;
		
		ArrayList<Move> validMoves = BoardTools.genValidMoves(boards.peek());
		String bestMove = "";
		boolean remembersBoard = false;
		long boardHash = boards.peek().getId();
		MemoryNode mn = null;
		
		synchronized(quiesceMemory) {
			if (quiesceMemory.containsKey(boardHash)) {
				mn = quiesceMemory.get(boardHash);
			}
		}
		if (mn != null) {
			bestMove = mn.bestMoveName;
			
			if (moveIsValid(bestMove, validMoves)) {
				//System.out.println("remembered a move quiesce");
				rememberedMoves++;
				remembersBoard = true;
				return mn.nodeValue; // all quiesce searches of the same board should yield the same result, so if the move is valid we assume it was calculated properly
				
			} else {
				System.out.println("misremembered a move quiesce");
				System.out.println("Was looking for move " + bestMove);
				System.out.println("With board\n" + boards.peek().toString());
				System.out.println(1/0);
			}
		}
		
		int currentScore = rateBoard(!validMoves.isEmpty());
		
		if (currentScore >= beta) {
			return beta;
		}
		if (alpha < currentScore) {
			alpha = currentScore;
		}
		
		for (Move move : validMoves) {
			if (move.isCapture()) {
				boards.push(BoardTools.forceMove(boards.peek(), move.toString())); // does the capturing move
				int result = quiesce(-beta, -alpha);
				boards.pop(); // undoes the capturing move
				if (result == OUT_OF_TIME) {
					return alpha;
				}
				int score = -1 * result;
				
				if (score >= beta) {
					return beta;
				}
				if (score > alpha) {
					bestMove = move.toString();
					alpha = score;
					if (remembersBoard) {
						improvedOnRememberedMoves++;
					}
				}
				
			}
		}
		
		if (!bestMove.equals("") && !remembersBoard) { // updates memory if the quiesce search was successfully completed
			synchronized(quiesceMemory) {
				quiesceMemory.put(boardHash, new MemoryNode(bestMove, 0, alpha)); // integer parameter (the 0) means nothing because there is no "depth" in quiesce search
			}
		}
		
		return alpha;
	}
	
	
	public static int[] convertToRowCol(String name) {
		int[] retArr = new int[2]; // index 0 is row, index 1 is col
		int aVal = (int)('a');
		int tRow = Integer.parseInt("" + name.charAt(1)) - 1;
		int tCol = ((int)name.charAt(0)) - aVal;
		retArr[0] = tRow;
		retArr[1] = tCol;
		return retArr;
	}
	
	private boolean moveIsValid(String moveName, ArrayList<Move> validMoves) {
		for (Move move : validMoves) {
			if (moveName.equals(move.toString())) {
				return true;
			}
		}
		return false;
	}
	
	
	private int rateBoard(boolean canMove) { // assume this rating is for the player making their move
		Board b = boards.peek();
		
		if (!canMove) {
			if (BoardTools.kingInCheck(b, b.whitesTurn)) {
				return -100000; // checkmate
			} else {
				return 0; // stalemate
			}
		}
		
		// the rating is first made under the assumption that it is white's move
		
		int rating = 0;
		
		int numWhiteBishops = 0;
		int numBlackBishops = 0;
		int[] numWhitePawnsInRow = new int[8];
		int[] numBlackPawnsInRow = new int[8];
		
		
		// handles castling points
		if (b.canWhiteKingsideCastle || b.canWhiteQueensideCastle) {
			rating += kingCanCastle;
		}
		if (b.canBlackKingsideCastle || b.canBlackQueensideCastle) {
			rating -= kingCanCastle;
		}
		
		
		int startingNumPieces = 16;
		int numPieces = 0;
		int pieceVals = 0;
		
		for (int row = 0; row < b.board.length; row++) {
			for (int col = 0; col < b.board[0].length; col++) {
				Piece p = b.board[row][col];
				
				if (p != null) {
					numPieces++;
					
					if (p.getTeam()) {
						if (p.getType() == Piece.BISHOP) {
							numWhiteBishops++;
						} else if (p.getType() == Piece.PAWN) {
							rating += ((row - 1) * upwardPawns);
							numWhitePawnsInRow[row] = numWhitePawnsInRow[row] + 1;
						} else if (p.getType() == Piece.KING) { // should probably change this to be better, but the idea is to incentivize castling
							if (col >= 6 || col <= 2) {
								rating += kingSafety;
							}
						}
						pieceVals += getValFor(p.getType());
					} else {
						if (p.getType() == Piece.BISHOP) {
							numBlackBishops++;
						} else if (p.getType() == Piece.PAWN) {
							rating -= ((6 - row) * upwardPawns);
							numBlackPawnsInRow[row] = numBlackPawnsInRow[row] + 1;
						} else if (p.getType() == Piece.KING) { // should probably change this to be better, but the idea is to incentivize castling
							if (col >= 6 || col <= 2) {
								rating -= kingSafety;
							}
						}
						pieceVals -= getValFor(p.getType());
					}
				}
			}
			if (numWhitePawnsInRow[row] > 1) {
				rating += doubledPawns;
			}
			if (numBlackPawnsInRow[row] > 1) {
				rating -= doubledPawns;
			}
		}
		
		int diffPieces = startingNumPieces - numPieces;
		int truePieceValMult = pieceValueMultiplier + (diffPieces * pieceValueMultiplierIncrease);
		rating += (pieceVals * truePieceValMult);
		
		// handles bishop pairs
		if (numWhiteBishops == 2) {
			rating += bishopPairValue;
		}
		if (numBlackBishops == 2) {
			rating -= bishopPairValue;
		}
		
		if (!b.whitesTurn) { // this adjusts the rating if black is the player to move
			rating *= -1;
		}
		
		return rating;
	}
	
	private class InitialMoveThread extends Thread {
		
		private Bot bot;
		private int arrayId;
		private int depth;
		private int[] resultsArray;
		
		public InitialMoveThread(Bot current, int depth, int arrayId, int[] resultsArray) {
			bot = new Bot(current);
			this.arrayId = arrayId;
			this.resultsArray = resultsArray;
			this.depth = depth;
		}
		
		public void run() {
			//long start = System.currentTimeMillis();
			int result = bot.alphaBeta(-10000000, 10000000, depth);
			//long end = System.currentTimeMillis();
			//System.out.println("thread id " + arrayId + " finished in " + (end - start) + " milliseconds at depth " + depth);
			
			synchronized(resultsArray) {
				resultsArray[arrayId] = result;
			}
		}
	}
	
	private static int getValFor(int pieceType) { // uses fairly standard values of pieces with no other knowledge about the piece
		switch(pieceType) {
			case Piece.PAWN: return 1;
			case Piece.KNIGHT: return 3;
			case Piece.BISHOP: return 3;
			case Piece.ROOK: return 5;
			case Piece.QUEEN: return 9;
			case Piece.KING: return 100;
		}
		return -1;
	}
	
	private class MemoryNode {
		public String bestMoveName;
		public int depthSearched;
		public int nodeValue;
		
		public MemoryNode(String bestMoveName, int depthSearched, int nodeValue) {
			this.bestMoveName = bestMoveName;
			this.depthSearched = depthSearched;
			this.nodeValue = nodeValue;
		}
	}
	
	// here is some idea I had
	/*@SuppressWarnings("unchecked")
	ArrayList<BotSquare>[][] reachableBy = new ArrayList[8][8]; // stores information about which pieces can reach which squares and what move allows them to do so
	for (int r = 0; r < 8; r++) {
		for (int c = 0; c < 8; c++) {
			reachableBy[r][c] = new ArrayList<BotSquare>();
		}
	}
	
	for (String move : validMoves) {
		String startName = move.substring(0, 2);
		String desinationName = move.substring(0, 4);
		
		int[] startResult = convertToRowCol(startName);
		int row = startResult[0];
		int col = startResult[1];
		Piece pieceOn = board.board[row][col];
		
		int[] endResult = convertToRowCol(desinationName);
		int endRow = endResult[0];
		int endCol = endResult[1];
		
		if (move.length() <= 6) {
			reachableBy[endRow][endCol].add(new BotSquare(pieceOn.getType(), move));
		} else { // means the move is an upgrade
			Piece newPieceOn = new Piece(move.substring(4), myTeam);
			reachableBy[endRow][endCol].add(new BotSquare(newPieceOn.getType(), move));
		}
		
		if (pieceOn.getType() == Piece.KING && Math.abs(endCol - col) == 2) { // means move is a castle
			int diff = endCol - col;
			if (diff == 2) {
				reachableBy[endRow][endCol - 1].add(new BotSquare(Piece.ROOK, move));
			} else {
				reachableBy[endRow][endCol + 1].add(new BotSquare(Piece.ROOK, move));
			}
		}
		
	}*/
	
	/*private static int[][] genMinValuePressure(Board b, boolean team) {
	int[][] minValPressure = new int[8][8];
	
	for (int r = 0; r < 8; r++) {
		for (int c = 0; c < 8; c++) {
			minValPressure[r][c] = Integer.MAX_VALUE;
		}
	}
	
	for (int row = 0; row < b.board.length; row++) {
		for (int col = 0; col < b.board[0].length; col++) {
			Piece p = b.board[row][col];
			
			if (p != null && p.getTeam() == team) {
				switch(p.getType()) {
					case Piece.PAWN: {
						int[] signs = {-1, 1};
						for (int direction : signs) {
							int testRow = row + (team ? 1 : -1);
							int testCol = col + direction;
							if (testRow >= 0 && testRow < b.board.length && testCol >= 0 && testCol < b.board[0].length) {
								minValPressure[testRow][testCol] = Math.min(getValFor(p.getType()), minValPressure[testRow][testCol]);
							}
						}
						
						break;
					}
					case Piece.KNIGHT: {
						int[] signs = {-2, -1, 1, 2};
						int magnitude, testRow, testCol;
						
						for (int xSign : signs) {
							for (int ySign : signs) {
								if (xSign * ySign == 2 || xSign * ySign == -2) {
									magnitude = 1;
									
									testRow = row + (magnitude * ySign);
									testCol = col + (magnitude * xSign);
									if (testRow >= 0 && testRow < b.board.length && testCol >= 0 && testCol < b.board[0].length) {
										minValPressure[testRow][testCol] = Math.min(getValFor(p.getType()), minValPressure[testRow][testCol]);
									}
								}
							}
								
						}
						break;
					}
					case Piece.BISHOP: {
						int[] signs = {-1, 1};
						int magnitude, testRow, testCol;
						boolean hasLineOfSight;
						
						for (int xSign : signs) {
							for (int ySign : signs) {
								magnitude = 1;
								hasLineOfSight = true;
								
								while (hasLineOfSight) {
									testRow = row + (magnitude * ySign);
									testCol = col + (magnitude * xSign);
									if (testRow >= 0 && testRow < b.board.length && testCol >= 0 && testCol < b.board[0].length) {
										Piece checkPiece = b.board[testRow][testCol];
										minValPressure[testRow][testCol] = Math.min(getValFor(p.getType()), minValPressure[testRow][testCol]);
										
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
						break;
					}
					case Piece.ROOK: {
						int[] signs = {-1, 0, 1};
						int magnitude, testRow, testCol;
						boolean hasLineOfSight;
						
						for (int xSign : signs) {
							for (int ySign : signs) {
								if (xSign * ySign == 0) {
									magnitude = 1;
									hasLineOfSight = true;
									
									while (hasLineOfSight) {
										testRow = row + (magnitude * ySign);
										testCol = col + (magnitude * xSign);
										if (testRow >= 0 && testRow < b.board.length && testCol >= 0 && testCol < b.board[0].length) {
											Piece checkPiece = b.board[testRow][testCol];
											minValPressure[testRow][testCol] = Math.min(getValFor(p.getType()), minValPressure[testRow][testCol]);
											
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
						break;
					}
					case Piece.QUEEN: {
						int[] signs = {-1, 0, 1};
						int magnitude, testRow, testCol;
						boolean hasLineOfSight;
						
						for (int xSign : signs) {
							for (int ySign : signs) {
								if (!(xSign == 0 && ySign == 0)) {
									magnitude = 1;
									hasLineOfSight = true;
									
									while (hasLineOfSight) {
										testRow = row + (magnitude * ySign);
										testCol = col + (magnitude * xSign);
										if (testRow >= 0 && testRow < b.board.length && testCol >= 0 && testCol < b.board[0].length) {
											Piece checkPiece = b.board[testRow][testCol];
											minValPressure[testRow][testCol] = Math.min(getValFor(p.getType()), minValPressure[testRow][testCol]);
											
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
						break;
					}
					case Piece.KING: {
						int[] signs = {-1, 0, 1};
						int magnitude, testRow, testCol;
						
						for (int xSign : signs) { // normal king movement
							for (int ySign : signs) {
								if (!(xSign == 0 && ySign == 0)) {
									magnitude = 1;
									
									testRow = row + (magnitude * ySign);
									testCol = col + (magnitude * xSign);
									if (testRow >= 0 && testRow < b.board.length && testCol >= 0 && testCol < b.board[0].length) {
										minValPressure[testRow][testCol] = Math.min(getValFor(p.getType()), minValPressure[testRow][testCol]);
									}
								}
							}
						}
						break;
					}
				}
			}
		}
	}
	
	for (int r = 0; r < 8; r++) {
		for (int c = 0; c < 8; c++) {
			if (minValPressure[r][c] == Integer.MAX_VALUE) {
				minValPressure[r][c] = 0;
			}
		}
	}
	
	return minValPressure;
} */
	
}
