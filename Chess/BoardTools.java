package Chess;
import java.util.ArrayList;

public class BoardTools {
	
	public static ArrayList<Move> genValidMoves(Board b) {
		ArrayList<Move> naiveMoves = new ArrayList<Move>();
		
		for (int row = 0; row < b.board.length; row++) {
			for (int col = 0; col < b.board[0].length; col++) {
				Piece p = b.board[row][col];
				
				if (p != null && p.getTeam() == b.whitesTurn) {
					switch(p.getType()) {
						case Piece.PAWN: {
							int[] signs = {-1, 1};
							String[] upgradePieceNames = {"Knight", "Bishop", "Rook", "Queen"};
							
							if (p.getTeam()) { // following code has hard coded numbers for white
								if (col == b.enPassantCol - 1 || col == b.enPassantCol + 1) { // en passant capturing
									if (row == 4) {
										naiveMoves.add(new Move(convertToMove(row, col, row + 1, b.enPassantCol, true, ""), true));
									}
								} if (row == 1 && b.board[row + 1][col] == null && b.board[row + 2][col] == null) { // can move forward twice if on starting row and sqaures ahead are empty
									naiveMoves.add(new Move(convertToMove(row, col, row + 2, col, false, ""), false));
								} if (row == 6) { // checks for upgrading situations
									if (b.board[row + 1][col] == null) {
										for (String upgradeName : upgradePieceNames) {
											naiveMoves.add(new Move(convertToMove(row, col, row + 1, col, false, upgradeName), false));
										}
									}
									for (int direction : signs) {
										int testCol = col + direction;
										if (testCol >= 0 && testCol < b.board[0].length) {
											Piece checkPiece = b.board[row + 1][testCol];
											if (checkPiece != null) {
												if (checkPiece.getTeam() != p.getTeam()) {
													for (String upgradeName : upgradePieceNames) {
														naiveMoves.add(new Move(convertToMove(row, col, row + 1, testCol, false, upgradeName), true));
													}
												}
											}
										}
									}
									
								} else {
									if (b.board[row + 1][col] == null) { // checks if it can move forward normally
										naiveMoves.add(new Move(convertToMove(row, col, row + 1, col, false, ""), false));
									}  
									for (int direction : signs) { // checks for diagonal captures
										int testCol = col + direction;
										if (testCol >= 0 && testCol < b.board[0].length) {
											Piece checkPiece = b.board[row + 1][testCol];
											if (checkPiece != null) {
												if (checkPiece.getTeam() != p.getTeam()) {
													naiveMoves.add(new Move(convertToMove(row, col, row + 1, testCol, false, ""), true));
												}
											}
										}
									}
									
								}
							} else { // following code has hard coded numbers for black
								if (col == b.enPassantCol - 1 || col == b.enPassantCol + 1) { // en passant capturing
									if (row == 3) {
										naiveMoves.add(new Move(convertToMove(row, col, row - 1, b.enPassantCol, true, ""), true));
									}
								} if (row == 6 && b.board[row - 1][col] == null && b.board[row - 2][col] == null) { // can move forward twice if on starting row and sqaures ahead are empty
									naiveMoves.add(new Move(convertToMove(row, col, row - 2, col, false, ""), false));
								} if (row == 1) { // checks for upgrading situations
									if (b.board[row - 1][col] == null) {
										for (String upgradeName : upgradePieceNames) {
											naiveMoves.add(new Move(convertToMove(row, col, row - 1, col, false, upgradeName), false));
										}
									}
									for (int direction : signs) {
										int testCol = col + direction;
										if (testCol >= 0 && testCol < b.board[0].length) {
											Piece checkPiece = b.board[row - 1][testCol];
											if (checkPiece != null) {
												if (checkPiece.getTeam() != p.getTeam()) {
													for (String upgradeName : upgradePieceNames) {
														naiveMoves.add(new Move(convertToMove(row, col, row - 1, testCol, false, upgradeName), true));
													}
												}
											}
										}
									}
									
								} else {
									if (b.board[row - 1][col] == null) { // checks if it can move forward normally
										naiveMoves.add(new Move(convertToMove(row, col, row - 1, col, false, ""), false));
									}
									for (int direction : signs) { // checks for diagonal captures
										int testCol = col + direction;
										if (testCol >= 0 && testCol < b.board[0].length) {
											Piece checkPiece = b.board[row - 1][testCol];
											if (checkPiece != null) {
												if (checkPiece.getTeam() != p.getTeam()) {
													naiveMoves.add(new Move(convertToMove(row, col, row - 1, testCol, false, ""), true));
												}
											}
										}
									}
									
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
											Piece checkPiece = b.board[testRow][testCol];
											if (checkPiece != null) {
												if (checkPiece.getTeam() != p.getTeam()) {
													naiveMoves.add(new Move(convertToMove(row, col, testRow, testCol, false, ""), true));
												}
											} else {
												naiveMoves.add(new Move(convertToMove(row, col, testRow, testCol, false, ""), false));
											}
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
											if (checkPiece != null) {
												if (checkPiece.getTeam() != p.getTeam()) {
													naiveMoves.add(new Move(convertToMove(row, col, testRow, testCol, false, ""), true));
												}
												hasLineOfSight = false;
											} else {
												naiveMoves.add(new Move(convertToMove(row, col, testRow, testCol, false, ""), false));
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
												if (checkPiece != null) {
													if (checkPiece.getTeam() != p.getTeam()) {
														naiveMoves.add(new Move(convertToMove(row, col, testRow, testCol, false, ""), true));
													}
													hasLineOfSight = false;
												} else {
													naiveMoves.add(new Move(convertToMove(row, col, testRow, testCol, false, ""), false));
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
												if (checkPiece != null) {
													if (checkPiece.getTeam() != p.getTeam()) {
														naiveMoves.add(new Move(convertToMove(row, col, testRow, testCol, false, ""), true));
													}
													hasLineOfSight = false;
												} else {
													naiveMoves.add(new Move(convertToMove(row, col, testRow, testCol, false, ""), false));
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
											Piece checkPiece = b.board[testRow][testCol];
											if (checkPiece != null) {
												if (checkPiece.getTeam() != p.getTeam()) {
													naiveMoves.add(new Move(convertToMove(row, col, testRow, testCol, false, ""), true));
												}
											} else {
												naiveMoves.add(new Move(convertToMove(row, col, testRow, testCol, false, ""), false));
											}
										}
									}
								}
							}
							if (p.getTeam()) { // checks castling
								if (b.canWhiteKingsideCastle || b.canWhiteQueensideCastle) {
									boolean[][] enemyPressure = genPressureArray(b, !b.whitesTurn);
									
									if (b.canWhiteKingsideCastle) {
										if (b.board[0][5] == null && b.board[0][6] == null && !enemyPressure[0][4] && !enemyPressure[0][5] && !enemyPressure[0][6]) {
											naiveMoves.add(new Move("e1g1", false));
										}
									}
									if (b.canWhiteQueensideCastle) {
										if (b.board[0][1] == null && b.board[0][2] == null && b.board[0][3] == null  && !enemyPressure[0][4] && !enemyPressure[0][3] && !enemyPressure[0][2]) {
											naiveMoves.add(new Move("e1c1", false));
										}
									}
								}
							} else {
								if (b.canBlackKingsideCastle || b.canBlackQueensideCastle) {
									boolean[][] enemyPressure = genPressureArray(b, !b.whitesTurn);
									
									if (b.canBlackKingsideCastle) {
										if (b.board[7][5] == null && b.board[7][6] == null && !enemyPressure[7][4] && !enemyPressure[7][5] && !enemyPressure[7][6]) { // TODO: check to be sure the pressure on these squares is also 0
											naiveMoves.add(new Move("e8g8", false));
										}
									}
									if (b.canBlackQueensideCastle) {
										if (b.board[7][1] == null && b.board[7][2] == null && b.board[7][3] == null && !enemyPressure[7][4] && !enemyPressure[7][3] && !enemyPressure[7][2]) { // TODO: check to be sure the pressure on 2 and 3 is 0, square 0,1 CAN be attacked
											naiveMoves.add(new Move("e8c8", false));
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
		
		ArrayList<Move> validMoves = new ArrayList<Move>();
		for (Move move : naiveMoves) {
			Board testBoard = forceMove(b, move.toString());
			boolean[][] enemyPressure = genPressureArray(testBoard, !b.whitesTurn);
			
			for (int row = 0; row < testBoard.board.length; row++) {
				for (int col = 0; col < testBoard.board[0].length; col++) {
					Piece p = testBoard.board[row][col];
					
					if (p != null && p.getTeam() == b.whitesTurn && p.getType() == Piece.KING) { // true if it found our team's king
						if (!enemyPressure[row][col]) {
							validMoves.add(move);
							row = testBoard.board.length;
							col = testBoard.board[0].length;
						}
					}
				}
			}
		}
		
		return validMoves;
	}
	
	public static boolean[][] genPressureArray(Board b, boolean team) {
		boolean[][] hasPressureOn = new boolean[8][8];
		
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
									hasPressureOn[testRow][testCol] = true;
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
											hasPressureOn[testRow][testCol] = true;
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
											hasPressureOn[testRow][testCol] = true;
											
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
												hasPressureOn[testRow][testCol] = true;
												
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
												hasPressureOn[testRow][testCol] = true;
												
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
											hasPressureOn[testRow][testCol] = true;
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
		
		return hasPressureOn;
	}
	
	public static boolean kingInCheck(Board b, boolean team) {
		boolean[][] enemyPressure = genPressureArray(b, !team);
		for (int row = 0; row < b.board.length; row++) {
			for (int col = 0; col < b.board[0].length; col++) {
				Piece p = b.board[row][col];
				
				if (p != null && p.getTeam() == team && p.getType() == Piece.KING) { // true if it found our team's king
					if (!enemyPressure[row][col]) {
						return false;
					} else {
						return true;
					}
				}
			}
		}
		return true;
	}
	
	public static Board forceMove(Board originalBoard, String move) { // can and will force even non legal moves (such as ones that put your king in check or have pieces move illegally)
		Board newBoard = new Board(originalBoard);
		newBoard.latestMove = move;
		newBoard.whitesTurn = !newBoard.whitesTurn;
		int aVal = (int)('a');
		
		try {
			int startRow = Integer.parseInt("" + move.charAt(1)) - 1;
			int startCol = ((int)move.charAt(0)) - aVal;
			int endRow = Integer.parseInt("" + move.charAt(3)) - 1;
			int endCol = ((int)move.charAt(2)) - aVal;
			
			if (newBoard.enPassantCol != -100) { // ensures that en passant availability will only last for 1 move
				newBoard.enPassantCol = -100;
			}
			
			if (newBoard.board[startRow][startCol] != null && newBoard.board[startRow][startCol].getType() == Piece.PAWN) { // checks to update en passant row
				if ((startRow == 1 && endRow == 3) || (startRow == 6 && endRow == 4)) {
					newBoard.enPassantCol = startCol;
				}
			}
			
			// the following conditionals update castling rights
			if (startRow == 0 && startCol == 4) {
				newBoard.canWhiteKingsideCastle = newBoard.canWhiteQueensideCastle = false;
			} else if ((startRow == 0 && startCol == 7) || (endRow == 0 && endCol == 7)) {
				newBoard.canWhiteKingsideCastle = false;
			} else if ((startRow == 0 && startCol == 0) || (endRow == 0 && endCol == 0)) {
				newBoard.canWhiteQueensideCastle = false;
			}
			
			if (startRow == 7 && startCol == 4) {
				newBoard.canBlackKingsideCastle = newBoard.canBlackQueensideCastle = false;
			} else if ((startRow == 7 && startCol == 7) || (endRow == 7 && endCol == 7)) {
				newBoard.canBlackKingsideCastle = false;
			} else if ((startRow == 7 && startCol == 0) || (endRow == 7 && endCol == 0)) {
				newBoard.canBlackQueensideCastle = false;
			}
			
			// the rest actually handles the moving of the pieces
			
			if ((move.equals("e1g1") && originalBoard.canWhiteKingsideCastle) || (move.equals("e8g8") && originalBoard.canBlackKingsideCastle)) { // kingside castle
				if (originalBoard.whitesTurn) {
					newBoard.canWhiteKingsideCastle = newBoard.canWhiteQueensideCastle = false;
				} else {
					newBoard.canBlackKingsideCastle = newBoard.canBlackQueensideCastle = false;
				}
				newBoard.board[startRow][startCol + 2] = newBoard.board[startRow][startCol];
				newBoard.board[startRow][startCol] = null;
				newBoard.board[startRow][startCol + 1] = newBoard.board[startRow][7];
				newBoard.board[startRow][7] = null;
			} else if ((move.equals("e1c1") && originalBoard.canWhiteQueensideCastle) || (move.equals("e8c8") && originalBoard.canBlackQueensideCastle)) { // queenside castle
				if (originalBoard.whitesTurn) {
					newBoard.canWhiteKingsideCastle = newBoard.canWhiteQueensideCastle = false;
				} else {
					newBoard.canBlackKingsideCastle = newBoard.canBlackQueensideCastle = false;
				}
				newBoard.board[startRow][startCol - 2] = newBoard.board[startRow][startCol];
				newBoard.board[startRow][startCol] = null;
				newBoard.board[startRow][startCol - 1] = newBoard.board[startRow][0];
				newBoard.board[startRow][0] = null;
			} else if (move.length() == 4) { // if length > 4 then it is either en passant or pawn upgrade
				newBoard.board[endRow][endCol] = newBoard.board[startRow][startCol];
				newBoard.board[startRow][startCol] = null;
			} else if (move.length() == 6) { // if the length is 6 then it is en passant (looks like: d5e4ep)
				newBoard.board[endRow][endCol] = newBoard.board[startRow][startCol];
				newBoard.board[startRow][startCol] = null;
				newBoard.board[startRow][endCol] = null;
			} else if (move.length() > 6) { // if the length is greater than 6 then it is a pawn upgrade (looks like: e7e8Queen)
				if(newBoard.board[startRow][startCol] == null) {
					System.out.println("embrace and become void");
					System.out.println("start row " + startRow + " start col " + startCol);
					if (originalBoard.board[startRow][startCol] == null) {
						System.out.println("hmmmm??");
					} else {
						System.out.println("but you have heard of me");
					}
				}
				newBoard.board[endRow][endCol] = new Piece(move.substring(4), (newBoard.board[startRow][startCol]).getTeam());
				newBoard.board[startRow][startCol] = null;
			} else {
				System.out.println("critical error move " + move + " is not recognized as a doable move");
			}
			
		} catch (Exception e) {
			System.out.println("critical error, move " + move + " doesn't contain valid square names");
			e.printStackTrace();
		}
		
		return newBoard;
	}
	
	private static String convertToMove(int startRow, int startCol, int endRow, int endCol, boolean enPassant, String upgradePieceName) {
		int aVal = (int)('a');
		char startLetter = (char)(aVal + startCol);
		char endLetter = (char)(aVal + endCol);
		
		String retString = "" + startLetter + (startRow + 1) + endLetter + (endRow + 1);
		
		if (enPassant) {
			retString += "ep";
		} else {
			retString += upgradePieceName;
		}
		return retString;
	}
}
