package Maze;
public class Maze {
	public Maze(int height, int width, Crotch root){
		this.height = height;
		this.width = width;
		this.root = root;
		mazeArray = new int[height][width];
		arrayStorageTypeTranslation(root);
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

	private void arrayStorageTypeTranslation(Crotch c){
		switch (c.direction) {
			case Crotch.LEFT_TO_RIGHT:
				for (int i = c.startPositionX; i <= c.endPositionX; i++) {
					mazeArray[c.startPositionY][i] = 1;
				}
				break;
			case Crotch.RIGHT_TO_LEFT:
				for (int i = c.endPositionX; i <= c.startPositionX; i++) {
					mazeArray[c.startPositionY][i] = 1;
				}
				break;
			case Crotch.DOWNWARD:
				for (int i = c.startPositionY; i <= c.endPositionY; i++) {
					mazeArray[i][c.startPositionX] = 1;
				}
				break;
			case Crotch.UPWARD:
				for (int i = c.endPositionY; i <= c.startPositionY; i++) {
					mazeArray[i][c.startPositionX] = 1;
				}
				break;
		}

		if (c.lCrotch != null) arrayStorageTypeTranslation(c.lCrotch);
		if (c.cCrotch != null) arrayStorageTypeTranslation(c.cCrotch);
		if (c.rCrotch != null) arrayStorageTypeTranslation(c.rCrotch);
	}
}
