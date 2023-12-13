package Maze;
public class Maze {
	public Maze(int height, int width, Crotch root){
		this.height = height;
		this.width = width;
		this.root = root;
	}
	private int[][] mazeArray;
	private int height;
	private int width;
	private Crotch root;

	public int getHeight() {
		return height;
	}

	public int getWidth(){
		return width;
	}
}
