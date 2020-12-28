package Chess;

public class Piece {
	
	public static final int PAWN = 1;
	public static final int KNIGHT = 2;
	public static final int BISHOP = 3;
	public static final int ROOK = 4;
	public static final int QUEEN = 5;
	public static final int KING = 6;
	
	private boolean team; // true means white, false means black
	private int pieceType;
	
	private int id;
	
	public Piece(String name, boolean team) {
		this.team = team;
		
		if (name.equals("Pawn")) {
			pieceType = PAWN;
		} else if (name.equals("Knight")) {
			pieceType = KNIGHT;
		} else if (name.equals("Bishop")) {
			pieceType = BISHOP;
		} else if (name.equals("Rook")) {
			pieceType = ROOK;
		} else if (name.equals("Queen")) {
			pieceType = QUEEN;
		} else if (name.equals("King")) {
			pieceType = KING;
		}
		
		setId();
	}
	
	public Piece(Piece p) {
		pieceType = p.pieceType;
		team = p.team;
		
		setId();
	}
	
	private void setId() {
		id = 13; // not an arbitrary number, do not change. Check hashifier.java for reason behind this
		id += (pieceType - 1) * 64; // 8x8 for board size = 64
		if (team) {
			id += 0;
		}
		else {
			id += 384; // 6x64
		}
	}
	
	public int getId() {
		return id;
	}
	
	public String toString() {
		String retString = team ? "w" : "b";
		
		switch(pieceType) {
			case PAWN: retString += "P"; break;
			case KNIGHT: retString += "N"; break;
			case BISHOP: retString += "B"; break;
			case ROOK: retString += "R"; break;
			case QUEEN: retString += "Q"; break;
			case KING: retString += "K"; break;
		}
		return retString;
	}
	
	public boolean getTeam() {
		return team;
	}
	
	public int getType() {
		return pieceType;
	}
	

}
