package Maze.Generator;

public class MazeGenerator implements MazeGeneratorInterface{
	private int mazeWidth;
	private int mazeHeight;
	private int mazeDifficulty;
	MazeGenerator(int width, int height, int difficulty){
		mazeHeight = height;
		mazeWidth = width;
		mazeDifficulty = difficulty;
	}
}
