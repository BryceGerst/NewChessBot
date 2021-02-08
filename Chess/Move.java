package Chess;

public class Move {
	protected byte startRow, startCol, endRow, endCol, captureRow, captureCol; // will only not be the end row and end col if the move is en passant
	protected boolean isCapture, isPromotion;
	protected PieceID endPiece; // keeps track of the ending id of a piece through the move in case this move is a promotion
	protected PieceID capturedPiece; // keeps track of the captured piece (if applicable) for purposes of move undo-ing
	
	public Move(byte startRow, byte startCol, byte endRow, byte endCol, PieceID endPiece, boolean isCapture, byte captureRow, byte captureCol, PieceID capturedPiece, boolean isPromotion) {
		this.startRow = startRow;
		this.startCol = startCol;
		this.endRow = endRow;
		this.endCol = endCol;
		this.endPiece = endPiece;
		this.isCapture = isCapture;
		this.captureRow = captureRow;
		this.captureCol = captureCol;
		this.capturedPiece = capturedPiece;
		this.isPromotion = isPromotion;
	}
	
	public String toString() {
		int aVal = (int)('a');
		char startLetter = (char)(aVal + startCol);
		char endLetter = (char)(aVal + endCol);
		
		String retString = "" + startLetter + (startRow + 1) + endLetter + (endRow + 1);
		
		return retString;
	}
	
}
