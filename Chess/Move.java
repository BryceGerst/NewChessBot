package Chess;

public class Move {
	
	private String name;
	private boolean isCapture;
	
	public Move(String name, boolean isCapture) {
		this.name = name;
		this.isCapture = isCapture;
	}
	
	public String toString() {
		return name;
	}
	
	public boolean isCapture() {
		return isCapture;
	}
}
