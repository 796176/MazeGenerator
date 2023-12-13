package Maze;

public class Crotch {
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
