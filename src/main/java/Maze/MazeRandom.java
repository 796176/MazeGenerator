package Maze;
import MazeBuild.BuildParameters;

public class MazeRandom {
	static public long getSeed(){
		if (BuildParameters.DEBUG) return 0;
		else return System.currentTimeMillis();
	}
}