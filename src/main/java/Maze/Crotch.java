package Maze;

public class Crotch {
	public Crotch (int[] startPosition, int[] endPosition){
		this.startPosition = startPosition;
		this.endPosition = endPosition;
		startPositionX = startPosition[0];
		startPositionY = startPosition[1];
		endPositionX = endPosition[0];
		endPositionY = endPosition[1];

		if (startPositionX == endPositionX && startPositionY < endPositionY) {
			length = endPositionY - startPositionY + 1;
			direction = DOWNWARD;
		} else if (startPositionX == endPositionX && startPositionY > endPositionY) {
			length = startPositionY - endPositionY + 1;
			direction = UPWARD;
		} else if (startPositionY == endPositionY && startPositionX < endPositionX) {
			length = endPositionX - startPositionX + 1;
			direction = LEFT_TO_RIGHT;
		} else {
			length = startPositionX - endPositionX + 1;
			direction = RIGHT_TO_LEFT;
		}
	}

	public Crotch(int[] startPosition, int length, int direction){
		this.startPosition = startPosition;
		this.length = length;
		this.direction = direction;
		startPositionX = startPosition[0];
		startPositionY = startPosition[1];

		switch (direction){
			case DOWNWARD:
				endPositionX = startPositionX;
				endPositionY = startPositionY + length - 1;
				break;
			case UPWARD:
				endPositionX = startPositionX;
				endPositionY = startPositionY - length + 1;
				break;
			case LEFT_TO_RIGHT:
				endPositionY = startPositionY;
				endPositionX = startPositionX + length - 1;
				break;
			case RIGHT_TO_LEFT:
				endPositionY = startPositionY;
				endPositionX = startPositionX - length + 1;
				break;
		}
		endPosition = new int[]{endPositionX, endPositionY};
	}
	public static final int LEFT_TO_RIGHT = 0;
	public static final int RIGHT_TO_LEFT = 1;
	public static final int DOWNWARD = 2;
	public static final int UPWARD = 3;
	public Crotch lCrotch;
	public Crotch cCrotch;
	public Crotch rCrotch;

	public int[] startPosition = new int[2];
	public int startPositionX;
	public int startPositionY;
	public int endPositionX;
	public int endPositionY;
	public int[] endPosition = new int[2];

	public int length;
	public int direction;
}
