package Maze.Generator;
import Maze.*;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.lang.Math;

public class MazeGenerator implements MazeGeneratorInterface{
	private int mazeWidth;
	private int mazeHeight;
	private int mazeDifficulty;
	public MazeGenerator(int width, int height, int difficulty){
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

	public Maze generate(){
		int []rootStartPosition = {mazeWidth/2, mazeHeight/2};
		Crotch root = new Crotch(rootStartPosition, 10, Crotch.LEFT_TO_RIGHT);
		LinkedBlockingQueue queue = new LinkedBlockingQueue();
		queue.add(root);
		int queueIterator = 1;

		while (!queue.isEmpty()){
			if (queueIterator >= mazeDifficulty) break;
			Crotch crotch = (Crotch) queue.poll();
			if (crotch == null) break;
			int []branchLengths = getMaxBranchLengths(root, crotch);
			generateAndAttachBranches(crotch, branchLengths);

			if (crotch.lCrotch != null) {
				queue.add(crotch.lCrotch);
				queueIterator++;
			}

			if (crotch.cCrotch != null) {
				queue.add(crotch.cCrotch);
				queueIterator++;
			}

			if (crotch.rCrotch != null) {
				queue.add(crotch.rCrotch);
				queueIterator++;
			}
		}

		generateExit(root);

		return new Maze(mazeHeight, mazeWidth, root);
	}


	private Crotch isWall(Crotch crotch, int x, int y) {
		if (crotch == null) return null;

		if (
				Math.min(crotch.startPositionX, crotch.endPositionX) <= x &&
				Math.max(crotch.startPositionX, crotch.endPositionX) >= x  &&
				Math.min(crotch.startPositionY, crotch.endPositionY) <= y &&
				Math.max(crotch.startPositionY, crotch.endPositionY) >= y
		) return crotch;

		Crotch lCrotchResult = isWall(crotch.lCrotch, x, y);
		if (lCrotchResult != null) return lCrotchResult;

		Crotch cCrotchResult = isWall(crotch.cCrotch, x, y);
		if (cCrotchResult != null) return cCrotchResult;

		Crotch rCrotchResult = isWall(crotch.rCrotch, x, y);
		if (rCrotchResult != null) return rCrotchResult;

		return null;
	}

	private Crotch findCrotch(Crotch crotch, int startX, int startY, int endX, int endY){
		if (crotch == null) return null;

		if (
			(
				Math.min(crotch.startPositionX, crotch.endPositionX) > Math.max(startX, endX) ||
					Math.min(startX, endX) > Math.max(crotch.startPositionX, crotch.endPositionY)
			) &&
			(
				Math.max(crotch.startPositionY, crotch.endPositionY) > Math.max(startY, endY) ||
					Math.min(startY, endY) > Math.max(crotch.startPositionY, crotch.endPositionY)
			)
		) return crotch;


		if (crotch.startPosition[0] == crotch.endPosition[0]) return crotch;

		Crotch lCrotchResult = findCrotch(crotch.lCrotch, startX, startY, endX, endY);
		if (lCrotchResult != null) return lCrotchResult;

		Crotch cCrotchResult = findCrotch(crotch.cCrotch, startX, startY, endX, endY);
		if (cCrotchResult != null) return cCrotchResult;

		Crotch rCrotchResult = findCrotch(crotch.rCrotch, startX, startY, endX, endY);
		if (rCrotchResult != null) return rCrotchResult;

		return null;
	}

	private int[] getMaxBranchLengths(Crotch root, Crotch c) {
		int[] lengths = new int[3];
		switch (c.direction) {
			case Crotch.LEFT_TO_RIGHT:
				while (c.endPositionY - lengths[0] > 2 &&
					isWall(root, c.endPositionX, c.endPositionY - lengths[0] - 2) == null &&
					isWall(root, c.endPositionX - 1, c.endPositionY - lengths[0] - 2) == null &&
					isWall(root, c.endPositionX + 1, c.endPositionY - lengths[0] - 2) == null) lengths[0]++;
				while (c.endPositionX + lengths[1] < mazeWidth - 3 &&
					isWall(root, c.endPositionX + lengths[1] + 2, c.endPositionY) == null &&
					isWall(root, c.endPositionX + lengths[1] + 2, c.endPositionY - 1) == null &&
					isWall(root, c.endPositionX + lengths[1] + 2, c.endPositionY + 1) == null) lengths[1]++;
				while (c.endPositionY + lengths[2] < mazeHeight - 3 &&
					isWall(root, c.endPositionX, c.endPositionY + lengths[2] + 2) == null &&
					isWall(root, c.endPositionX - 1, c.endPositionY + lengths[2] + 2) == null &&
					isWall(root, c.endPositionX + 1, c.endPositionY + lengths[2] + 2) == null) lengths[2]++;
				break;
			case Crotch.RIGHT_TO_LEFT:
				while (c.endPositionY + lengths[0] < mazeHeight - 3 &&
					isWall(root, c.endPositionX, c.endPositionY + lengths[0] + 2) == null &&
					isWall(root, c.endPositionX - 1, c.endPositionY + lengths[0] + 2) == null &&
					isWall(root, c.endPositionX + 1, c.endPositionY + lengths[0] + 2) == null) lengths[0]++;
				while (c.endPositionX - lengths[1] > 2 &&
					isWall(root, c.endPositionX - lengths[1] - 2, c.endPositionY) == null &&
					isWall(root, c.endPositionX - lengths[1] - 2, c.endPositionY - 1) == null &&
					isWall(root, c.endPositionX - lengths[1] - 2, c.endPositionY + 1) == null) lengths[1]++;
				while (c.endPositionY - lengths[2] > 2 &&
					isWall(root, c.endPositionX, c.endPositionY - lengths[2] - 2) == null &&
					isWall(root, c.endPositionX - 1, c.endPositionY - lengths[2] - 2) == null &&
					isWall(root, c.endPositionX + 1, c.endPositionY - lengths[2] - 2) == null) lengths[2]++;
				break;
			case Crotch.DOWNWARD:
				while (c.endPositionX + lengths[0] < mazeWidth - 3 &&
					isWall(root, c.endPositionX + lengths[0] + 2, c.endPositionY) == null &&
					isWall(root, c.endPositionX + lengths[0] + 2, c.endPositionY - 1) == null &&
					isWall(root, c.endPositionX + lengths[1] + 2, c.endPositionY + 1) == null) lengths[0]++;
				while (c.endPositionY + lengths[1] < mazeHeight - 3 &&
					isWall(root, c.endPositionX, c.endPositionY + lengths[1] + 2) == null &&
					isWall(root, c.endPositionX - 1, c.endPositionY + lengths[1] + 2) == null &&
					isWall(root, c.endPositionX + 1, c.endPositionY + lengths[1] + 2) == null) lengths[1]++;
				while (c.endPositionX - lengths[2] > 2 &&
					isWall(root, c.endPositionX - lengths[2] - 2, c.endPositionY) == null &&
					isWall(root, c.endPositionX - lengths[2] - 2, c.endPositionY - 1) == null &&
					isWall(root, c.endPositionX - lengths[2] - 2, c.endPositionY + 1) == null) lengths[2]++;
				break;
			case Crotch.UPWARD:
				while (c.endPositionX - lengths[0] > 2 &&
					isWall(root, c.endPositionX - lengths[0] - 2, c.endPositionY) == null &&
					isWall(root, c.endPositionX - lengths[0] - 2, c.endPositionY - 1) == null &&
					isWall(root, c.endPositionX - lengths[0] - 1, c.endPositionY + 1) == null) lengths[0]++;
				while (c.endPositionY - lengths[1] > 2 &&
					isWall(root, c.endPositionX, c.endPositionY - lengths[1] - 2) == null &&
					isWall(root, c.endPositionX - 1, c.endPositionY - lengths[1] - 2) == null &&
					isWall(root, c.endPositionX + 1, c.endPositionY - lengths[1] - 2) == null) lengths[1]++;
				while (c.endPositionX + lengths[2] < mazeWidth - 3 &&
					isWall(root, c.endPositionX + lengths[2] + 2, c.endPositionY) == null &&
					isWall(root, c.endPositionX + lengths[2] + 2, c.endPositionY - 1) == null &&
					isWall(root, c.endPositionX + lengths[2] + 2, c.endPositionY + 1) == null) lengths[2]++;
				break;
		}
		return lengths;
	}

	private void generateAndAttachBranches(Crotch c, int[] lengths) {
		if (lengths[0] < 2 || lengths[2] < 2) return;

		int[] adjustedLength = lengths.clone();
		for (int index = 0; index < adjustedLength.length; index++){
			if (adjustedLength[index] > 6) adjustedLength[index] = 6;
		}
		Random random = new Random();
		if (adjustedLength[0] >= 2 && adjustedLength[1] >= 2 && adjustedLength[2] >= 2){
			switch (random.nextInt(4)){
				case 0:
					generateAndAttachLeftBranch(c, random.nextInt(adjustedLength[0] - 1) + 2);
					generateAndAttachCentralBranch(c, random.nextInt(adjustedLength[1] - 1) + 2);
					break;
				case 1:
					generateAndAttachCentralBranch(c, random.nextInt(adjustedLength[1] - 1) + 2);
					generateAndAttachRightBranch(c, random.nextInt(adjustedLength[2] - 1) + 2);
					break;
				case 2:
					generateAndAttachLeftBranch(c, random.nextInt(adjustedLength[0] - 1) + 2);
					break;
				case 3:
					generateAndAttachRightBranch(c, random.nextInt(adjustedLength[2] - 1) + 2);
					break;
			}
		} else if(adjustedLength[0] >= 2 && adjustedLength[1] >= 2){
			switch (random.nextInt(2)){
				case 0:
					generateAndAttachLeftBranch(c, random.nextInt(adjustedLength[0] - 1) + 2);
					generateAndAttachCentralBranch(c, random.nextInt(adjustedLength[1] - 1) + 2);
					break;
				case 1:
					generateAndAttachLeftBranch(c, random.nextInt(adjustedLength[0] - 1) + 2);
					break;
			}
		} else if (adjustedLength[1] >= 2 && adjustedLength[2] >= 2) {
			switch (random.nextInt(2)){
				case 0:
					generateAndAttachCentralBranch(c, random.nextInt(adjustedLength[1] - 1) + 2);
					generateAndAttachRightBranch(c, random.nextInt(adjustedLength[2] - 1) + 2);
					break;
				case 1:
					generateAndAttachRightBranch(c, random.nextInt(adjustedLength[2] - 1) + 2);
					break;
			}
		} else if (adjustedLength[0] >= 2) {
			generateAndAttachLeftBranch(c, random.nextInt(adjustedLength[0] - 1) + 2);
		} else if (adjustedLength[2] >= 2) {
			generateAndAttachRightBranch(c, random.nextInt(adjustedLength[2] - 1) + 2);
		}
	}

	private void generateAndAttachLeftBranch(Crotch c, int length){
		int[] startPosition = new int[2];
		int direction = -1;
		switch (c.direction){
			case Crotch.LEFT_TO_RIGHT:
				startPosition[0] = c.endPositionX;
				startPosition[1] = c.endPositionY - 1;
				direction = Crotch.UPWARD;
				break;
			case Crotch.RIGHT_TO_LEFT:
				startPosition[0] = c.endPositionX;
				startPosition[1] = c.endPositionY + 1;
				direction = Crotch.DOWNWARD;
				break;
			case Crotch.DOWNWARD:
				startPosition[0] = c.endPositionX + 1;
				startPosition[1] = c.endPositionY;
				direction = Crotch.LEFT_TO_RIGHT;
				break;
			case Crotch.UPWARD:
				startPosition[0] = c.endPositionX - 1;
				startPosition[1] = c.endPositionY;
				direction = Crotch.RIGHT_TO_LEFT;
				break;
		}
		c.lCrotch = new Crotch(startPosition, length, direction);
	}
	private void generateAndAttachCentralBranch(Crotch c, int length){
		int[] startPosition = new int[2];
		switch (c.direction){
			case Crotch.LEFT_TO_RIGHT:
				startPosition[0] = c.endPositionX + 1;
				startPosition[1] = c.endPositionY;
				break;
			case Crotch.RIGHT_TO_LEFT:
				startPosition[0] = c.endPositionX - 1;
				startPosition[1] = c.endPositionY;
				break;
			case Crotch.DOWNWARD:
				startPosition[0] = c.endPositionX;
				startPosition[1] = c.endPositionY + 1;
				break;
			case Crotch.UPWARD:
				startPosition[0] = c.endPositionX;
				startPosition[1] = c.endPositionY - 1;
				break;
		}
		c.cCrotch = new Crotch(startPosition, length, c.direction);
	}
	private void generateAndAttachRightBranch(Crotch c, int length){
		int[] startPosition = new int[2];
		int direction = -1;
		switch (c.direction){
			case Crotch.LEFT_TO_RIGHT:
				startPosition[0] = c.endPositionX;
				startPosition[1] = c.endPositionY + 1;
				direction = Crotch.DOWNWARD;
				break;
			case Crotch.RIGHT_TO_LEFT:
				startPosition[0] = c.endPositionX;
				startPosition[1] = c.endPositionY - 1;
				direction = Crotch.UPWARD;
				break;
			case Crotch.DOWNWARD:
				startPosition[0] = c.endPositionX - 1;
				startPosition[1] = c.endPositionY;
				direction = Crotch.RIGHT_TO_LEFT;
				break;
			case Crotch.UPWARD:
				startPosition[0] = c.endPositionX + 1;
				startPosition[1] = c.endPositionY;
				direction = Crotch.LEFT_TO_RIGHT;
				break;
		}
		c.rCrotch = new Crotch(startPosition, length, direction);
	}

	private void generateExit(Crotch root){
		for (int offset = 2; offset < Math.min(mazeHeight, mazeWidth) / 2; offset++){
			Crotch[] possibleExits = new Crotch[4];
			possibleExits[0] = findCrotch(root, 0, offset, mazeWidth - 1, offset);
			possibleExits[1] = findCrotch(root, mazeWidth - 1 - offset, 0, mazeWidth - 1 - offset, mazeHeight - 1);
			possibleExits[2] = findCrotch(root, 0, mazeHeight - 1 - offset, mazeWidth - 1, mazeHeight - 1 - offset);
			possibleExits[3] = findCrotch(root, offset, 0, offset, mazeHeight - 1);

			boolean isPossibleExistsEmpty = true;
			for (Crotch possibleExit : possibleExits) {
				if (possibleExit != null) isPossibleExistsEmpty = false;
			}
			if (isPossibleExistsEmpty) continue;

			Random random = new Random();
			int exitCrotchIndex;
			Crotch exitCrotch;
			do {
				exitCrotchIndex = random.nextInt(4);
				exitCrotch = possibleExits[exitCrotchIndex];
			} while (exitCrotch == null);

			if (exitCrotch.direction == Crotch.UPWARD && exitCrotchIndex == 0 ||
				exitCrotch.direction == Crotch.LEFT_TO_RIGHT && exitCrotchIndex == 1 ||
				exitCrotch.direction == Crotch.DOWNWARD && exitCrotchIndex == 2 ||
				exitCrotch.direction == Crotch.RIGHT_TO_LEFT && exitCrotchIndex == 3) {
				generateAndAttachCentralBranch(exitCrotch, offset);
			} else if  (exitCrotch.direction == Crotch.LEFT_TO_RIGHT && exitCrotchIndex == 0 ||
						exitCrotch.direction == Crotch.DOWNWARD && exitCrotchIndex == 1 ||
						exitCrotch.direction == Crotch.RIGHT_TO_LEFT && exitCrotchIndex == 2 ||
						exitCrotch.direction == Crotch.UPWARD && exitCrotchIndex == 3) {
				generateAndAttachLeftBranch(exitCrotch, offset);
			} else {
				generateAndAttachRightBranch(exitCrotch, offset);
			}
		}
	}
}
