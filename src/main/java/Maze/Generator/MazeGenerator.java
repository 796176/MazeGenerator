package Maze.Generator;
import Maze.*;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.lang.Math;

public class MazeGenerator implements MazeGeneratorInterface{
	private int mazeWidth;
	private int mazeHeight;
	private int mazeDifficulty;
	private int mazeWidthIndexMin = 0;
	private int mazeWidthIndexMax;
	private int mazeHeightIndexMin = 0;
	private int mazeHeightIndexMax;
	private int minBranchLenght = 2;
	private int maxBranchLength = 6;
	private int mazeOffset = 2;
	private int initialBranchLength = 10;
	public MazeGenerator(int width, int height, int difficulty){
		mazeHeight = height;
		mazeWidth = width;
		mazeDifficulty = difficulty;
		mazeWidthIndexMax = mazeWidth - 1;
		mazeHeightIndexMax = mazeHeight - 1;
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
		Crotch root = new Crotch(rootStartPosition, initialBranchLength, Crotch.LEFT_TO_RIGHT);
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

		// if two rectangles overlap their projections onto the abscissa overlap as well
		int []abscissaProjection1 = new int[]{crotch.startPositionX, crotch.endPositionX};
		Arrays.sort(abscissaProjection1);
		int []abscissaProjection2 = new int[]{startX, endX};
		Arrays.sort(abscissaProjection2);
		boolean abscissaOverlap = false;
		if (abscissaProjection1[0] <= abscissaProjection2[0] && abscissaProjection1[1] >= abscissaProjection2[0]) abscissaOverlap = true;
		else if (abscissaProjection2[0] <= abscissaProjection1[0] && abscissaProjection2[1] >= abscissaProjection1[0]) abscissaOverlap = true;

		// if two rectangles overlap their projections onto the ordinate overlap as well
		int []ordinateProjection1 = new int[]{crotch.startPositionY, crotch.endPositionY};
		Arrays.sort(ordinateProjection1);
		int []ordinateProjection2 = new int[]{startY, endY};
		Arrays.sort(ordinateProjection2);
		boolean ordinateOverlap = false;
		if (ordinateProjection1[0] <= ordinateProjection2[0] && ordinateProjection1[1] >= ordinateProjection2[0]) ordinateOverlap = true;
		else if (ordinateProjection2[0] <= ordinateProjection1[0] && ordinateProjection2[1] >= ordinateProjection1[0]) ordinateOverlap = true;

		if (abscissaOverlap && ordinateOverlap) return crotch;


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
		int leftBranchLengthIndex = 0;
		int centralBranchLengthIndex = 1;
		int rightBranchLengthIndex = 2;
		int sideCollisionOffset = 1;
		int frontCollisionOffset = 2;
		switch (c.direction) {
			case Crotch.LEFT_TO_RIGHT:
				while (c.endPositionY - lengths[leftBranchLengthIndex] > mazeOffset &&
					findCrotch(
						root,
						c.endPositionX - sideCollisionOffset,
						c.endPositionY - lengths[leftBranchLengthIndex] - frontCollisionOffset,
						c.endPositionX + sideCollisionOffset,
						c.endPositionY - lengths[leftBranchLengthIndex] - frontCollisionOffset
					) == null
				) lengths[leftBranchLengthIndex]++;
				while (c.endPositionX + lengths[centralBranchLengthIndex] < mazeWidthIndexMax - mazeOffset &&
					findCrotch(root,
						c.endPositionX + lengths[centralBranchLengthIndex] + frontCollisionOffset,
						c.endPositionY - sideCollisionOffset,
						c.endPositionX + lengths[centralBranchLengthIndex] + frontCollisionOffset,
						c.endPositionY + sideCollisionOffset
					) == null
				) lengths[centralBranchLengthIndex]++;
				while (c.endPositionY + lengths[rightBranchLengthIndex] < mazeHeightIndexMax - mazeOffset &&
					findCrotch(
						root,
						c.endPositionX - sideCollisionOffset,
						c.endPositionY + lengths[rightBranchLengthIndex] + frontCollisionOffset,
						c.endPositionX + sideCollisionOffset,
						c.endPositionY + lengths[rightBranchLengthIndex] + frontCollisionOffset
					) == null
				) lengths[rightBranchLengthIndex]++;
				break;
			case Crotch.RIGHT_TO_LEFT:
				while (c.endPositionY + lengths[leftBranchLengthIndex] < mazeHeightIndexMax - mazeOffset &&
					findCrotch(
						root,
						c.endPositionX - sideCollisionOffset,
						c.endPositionY + lengths[leftBranchLengthIndex] + frontCollisionOffset,
						c.endPositionX + sideCollisionOffset,
						c.endPositionY + lengths[leftBranchLengthIndex] + frontCollisionOffset
					) == null
				) lengths[leftBranchLengthIndex]++;
				while (c.endPositionX - lengths[centralBranchLengthIndex] > mazeOffset &&
					findCrotch(
						root,
						c.endPositionX - lengths[centralBranchLengthIndex] - frontCollisionOffset,
						c.endPositionY - sideCollisionOffset,
						c.endPositionX - lengths[centralBranchLengthIndex] - frontCollisionOffset,
						c.endPositionY + sideCollisionOffset
					) == null
				) lengths[centralBranchLengthIndex]++;
				while (c.endPositionY - lengths[rightBranchLengthIndex] > mazeOffset &&
					findCrotch(
						root,
						c.endPositionX - sideCollisionOffset,
						c.endPositionY - lengths[rightBranchLengthIndex] - frontCollisionOffset,
						c.endPositionX + sideCollisionOffset,
						c.endPositionY - lengths[rightBranchLengthIndex] - frontCollisionOffset
					) == null
				) lengths[rightBranchLengthIndex]++;
				break;
			case Crotch.DOWNWARD:
				while (c.endPositionX + lengths[leftBranchLengthIndex] < mazeWidthIndexMax - mazeOffset &&
					findCrotch(
						root,
						c.endPositionX + lengths[leftBranchLengthIndex] + frontCollisionOffset,
						c.endPositionY - sideCollisionOffset,
						c.endPositionX + lengths[leftBranchLengthIndex] + frontCollisionOffset,
						c.endPositionY + sideCollisionOffset
					) == null
				) lengths[leftBranchLengthIndex]++;
				while (c.endPositionY + lengths[centralBranchLengthIndex] < mazeHeightIndexMax - mazeOffset &&
					findCrotch(
						root,
						c.endPositionX - sideCollisionOffset,
						c.endPositionY + lengths[centralBranchLengthIndex] + frontCollisionOffset,
						c.endPositionX + sideCollisionOffset,
						c.endPositionY + lengths[centralBranchLengthIndex] + frontCollisionOffset
					) == null
				) lengths[centralBranchLengthIndex]++;
				while (c.endPositionX - lengths[rightBranchLengthIndex] > mazeOffset &&
					findCrotch(
						root,
						c.endPositionX - lengths[rightBranchLengthIndex] - frontCollisionOffset,
						c.endPositionY - sideCollisionOffset,
						c.endPositionX - lengths[rightBranchLengthIndex] - frontCollisionOffset,
						c.endPositionY - sideCollisionOffset
					) == null
				) lengths[rightBranchLengthIndex]++;
				break;
			case Crotch.UPWARD:
				while (c.endPositionX - lengths[leftBranchLengthIndex] > mazeOffset &&
					findCrotch(
						root,
						c.endPositionX - lengths[leftBranchLengthIndex] - frontCollisionOffset,
						c.endPositionY - sideCollisionOffset,
						c.endPositionX - lengths[leftBranchLengthIndex] - frontCollisionOffset,
						c.endPositionY + sideCollisionOffset
					) == null
				) lengths[leftBranchLengthIndex]++;
				while (c.endPositionY - lengths[centralBranchLengthIndex] > mazeOffset &&
					findCrotch(
						root,
						c.endPositionX - sideCollisionOffset,
						c.endPositionY - lengths[centralBranchLengthIndex] - frontCollisionOffset,
						c.endPositionX + sideCollisionOffset,
						c.endPositionY - lengths[centralBranchLengthIndex] - frontCollisionOffset
					) == null
				) lengths[centralBranchLengthIndex]++;
				while (c.endPositionX + lengths[rightBranchLengthIndex] < mazeWidthIndexMax - mazeOffset &&
					findCrotch(
						root,
						c.endPositionX + lengths[rightBranchLengthIndex] + frontCollisionOffset,
						c.endPositionY - sideCollisionOffset,
						c.endPositionX + lengths[rightBranchLengthIndex] + frontCollisionOffset,
						c.endPositionY + sideCollisionOffset
					) == null
				) lengths[rightBranchLengthIndex]++;
				break;
		}
		return lengths;
	}

	private void generateAndAttachBranches(Crotch c, int[] lengths) {
		int leftBranchLengthIndex = 0;
		int centralBranchLengthIndex = 1;
		int rightBranchLengthIndex = 2;

		if (lengths[leftBranchLengthIndex] < minBranchLenght || lengths[rightBranchLengthIndex] < minBranchLenght)
			return;

		int[] adjustedLength = lengths.clone();
		for (int index = 0; index < adjustedLength.length; index++){
			if (adjustedLength[index] > maxBranchLength) adjustedLength[index] = maxBranchLength;
		}
		Random random = new Random(MazeRandom.getSeed());
		if (
			adjustedLength[leftBranchLengthIndex] >= minBranchLenght &&
			adjustedLength[centralBranchLengthIndex] >= minBranchLenght &&
			adjustedLength[rightBranchLengthIndex] >= minBranchLenght
		){
			switch (random.nextInt(4)){
				case 0:
					generateAndAttachLeftBranch(
						c,
						random.nextInt(adjustedLength[leftBranchLengthIndex] - 1) + minBranchLenght
					);
					generateAndAttachCentralBranch(
						c,
						random.nextInt(adjustedLength[centralBranchLengthIndex] - 1) + minBranchLenght
					);
					break;
				case 1:
					generateAndAttachCentralBranch(
						c,
						random.nextInt(adjustedLength[centralBranchLengthIndex] - 1) + minBranchLenght
					);
					generateAndAttachRightBranch(
						c,
						random.nextInt(adjustedLength[rightBranchLengthIndex] - 1) + minBranchLenght
					);
					break;
				case 2:
					generateAndAttachLeftBranch(
						c,
						random.nextInt(adjustedLength[leftBranchLengthIndex] - 1) + minBranchLenght
					);
					break;
				case 3:
					generateAndAttachRightBranch(
						c,
						random.nextInt(adjustedLength[rightBranchLengthIndex] - 1) + minBranchLenght
					);
					break;
			}
		} else if (
			adjustedLength[leftBranchLengthIndex] >= minBranchLenght &&
			adjustedLength[centralBranchLengthIndex] >= minBranchLenght
		){
			switch (random.nextInt(2)){
				case 0:
					generateAndAttachLeftBranch(
						c,
						random.nextInt(adjustedLength[leftBranchLengthIndex] - 1) + minBranchLenght
					);
					generateAndAttachCentralBranch(
						c, random.nextInt(adjustedLength[centralBranchLengthIndex] - 1) + minBranchLenght
					);
					break;
				case 1:
					generateAndAttachLeftBranch(
						c,
						random.nextInt(adjustedLength[leftBranchLengthIndex] - 1) + minBranchLenght
					);
					break;
			}
		} else if (
			adjustedLength[centralBranchLengthIndex] >= minBranchLenght &&
			adjustedLength[rightBranchLengthIndex] >= minBranchLenght
		) {
			switch (random.nextInt(2)){
				case 0:
					generateAndAttachCentralBranch(
						c,
						random.nextInt(adjustedLength[centralBranchLengthIndex] - 1) + minBranchLenght
					);
					generateAndAttachRightBranch(
						c,
						random.nextInt(adjustedLength[rightBranchLengthIndex] - 1) + minBranchLenght
					);
					break;
				case 1:
					generateAndAttachRightBranch(
						c,
						random.nextInt(adjustedLength[rightBranchLengthIndex] - 1) + minBranchLenght
					);
					break;
			}
		} else if (adjustedLength[leftBranchLengthIndex] >= minBranchLenght) {
			generateAndAttachLeftBranch(
				c,
				random.nextInt(adjustedLength[leftBranchLengthIndex] - 1) + minBranchLenght
			);
		} else if (adjustedLength[rightBranchLengthIndex] >= minBranchLenght) {
			generateAndAttachRightBranch(
				c,
				random.nextInt(adjustedLength[rightBranchLengthIndex] - 1) + minBranchLenght
			);
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
			possibleExits[0] = findCrotch(root, mazeWidthIndexMin, offset, mazeWidthIndexMax, offset);
			possibleExits[1] =
				findCrotch(
					root,
					mazeWidthIndexMax - offset,
					mazeHeightIndexMin,
					mazeWidthIndexMax - offset,
					mazeHeightIndexMax
				);
			possibleExits[2] =
				findCrotch(
					root,
					mazeWidthIndexMin,
					mazeHeightIndexMax- offset,
					mazeWidthIndexMax,
					mazeHeightIndexMax - offset
				);
			possibleExits[3] = findCrotch(root, offset, mazeHeightIndexMin, offset, mazeHeightIndexMax);

			boolean isPossibleExistsEmpty = true;
			for (Crotch possibleExit : possibleExits) {
				if (possibleExit != null) isPossibleExistsEmpty = false;
			}
			if (isPossibleExistsEmpty) continue;

			Random random = new Random(MazeRandom.getSeed());
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
				break;
			} else if  (exitCrotch.direction == Crotch.LEFT_TO_RIGHT && exitCrotchIndex == 0 ||
						exitCrotch.direction == Crotch.DOWNWARD && exitCrotchIndex == 1 ||
						exitCrotch.direction == Crotch.RIGHT_TO_LEFT && exitCrotchIndex == 2 ||
						exitCrotch.direction == Crotch.UPWARD && exitCrotchIndex == 3) {
				generateAndAttachLeftBranch(exitCrotch, offset);
				break;
			} else {
				generateAndAttachRightBranch(exitCrotch, offset);
				break;
			}
		}
	}
}
