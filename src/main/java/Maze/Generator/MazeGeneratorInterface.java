package Maze.Generator;
import Maze.*;

public interface MazeGeneratorInterface {
	public Maze generate();
	public int getHeight();
	public int getWidth();
	public int getDifficulty();
	public void setHeight(int height);
	public void setWidth(int width);
	public void setDifficulty(int difficulty);
}
