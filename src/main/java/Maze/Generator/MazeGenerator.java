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

	public int getHeight() {
		return mazeHeight;
	}

	public int getWidth() {
		return mazeWidth;
	}

	public int getDifficulty() {
		return mazeDifficulty;
	}

	public void setHeight(int height) {
		mazeHeight = height;
	}

	public void setWidth(int width) {
		mazeWidth = width;
	}

	public void setDifficulty(int difficulty) {
		mazeDifficulty = difficulty;
	}
}
