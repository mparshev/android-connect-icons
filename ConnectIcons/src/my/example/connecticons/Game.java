package my.example.connecticons;

import java.util.Random;

class Game {

	private final static int EMPTY = -1;

	int mRows = 0;
	int mCols = 0;
	int[][] mTiles;

	int selRow = -1;
	int selCol = -1;

	boolean bShowHint = false;
	
	int hintRow0 = -1;
	int hintCol0 = -1;
	int hintRow1 = -1;
	int hintCol1 = -1;
	
	public Game() {
	}

	public void init(int rows, int cols) {
		mRows = rows;
		mCols = cols;
		mTiles = new int[rows][cols];
		fill();
	}
	

	public boolean touch(int row, int col) {
		bShowHint = false;
		int col0 = selCol;
		int row0 = selRow;
		if(!inside(row, col)) {
			selCol = -1;
			selRow = -1;
			return false;
		} else {
			selRow = row;
			selCol = col;
		}

		if (match(row0, col0, selRow, selCol)) {
			mTiles[row0][col0] = EMPTY;
			mTiles[selRow][selCol] = EMPTY;
			if (done()) {
				fill();
			}
			if (!find())
				shuffle();
		}
		return true;
	}

	private void fill() {
		int i = 0;
		for (int row = 0; row < mRows; row++)
			for (int col = 0; col < mCols; col++)
				mTiles[row][col] = (i++) / 4;

		shuffle();
	}

	private boolean match(int row0, int col0, int row1, int col1) {
		if (row0 == row1 && col0 == col1) return false;
		if(!inside(row0, col0)) return false;
		if(!inside(row1, col1)) return false;
		int ico0 = mTiles[row0][col0];
		int ico1 = mTiles[row1][col1];
		if (ico0 != ico1 || ico0 == EMPTY || ico1 == EMPTY)
			return false;
		mTiles[row0][col0] = EMPTY;
		mTiles[row1][col1] = EMPTY;
		boolean match = matchrow(row0, col0, row1, col1)
				|| matchcol(row0, col0, row1, col1);
		mTiles[row0][col0] = ico0;
		mTiles[row1][col1] = ico1;
		return match;
	}

	private boolean matchrow(int row0, int col0, int row1, int col1) {
		int minrow0 = row0;
		while (minrow0 > -1 && empty(minrow0 - 1, col0))
			--minrow0;
		int maxrow0 = row0;
		while (maxrow0 < mRows && empty(maxrow0 + 1, col0))
			++maxrow0;
		int minrow1 = row1;
		while (minrow1 > -1 && empty(minrow1 - 1, col1))
			--minrow1;
		int maxrow1 = row1;
		while (maxrow1 < mRows && empty(maxrow1 + 1, col1))
			++maxrow1;
		int minrow = max(minrow0, minrow1);
		int maxrow = min(maxrow0, maxrow1);
		int mincol = min(col0, col1);
		int maxcol = max(col0, col1);
		for (int row = minrow; row <= maxrow; row++) {
			boolean found = true;
			for (int col = mincol; col <= maxcol; col++) {
				if (!empty(row, col))
					found = false;
			}
			if (found)
				return true;
		}
		return false;
	}

	private boolean matchcol(int row0, int col0, int row1, int col1) {
		int mincol0 = col0;
		while (mincol0 > -1 && empty(row0, mincol0 - 1))
			--mincol0;
		int maxcol0 = col0;
		while (maxcol0 < mCols && empty(row0, maxcol0 + 1))
			++maxcol0;
		int mincol1 = col1;
		while (mincol1 > -1 && empty(row1, mincol1 - 1))
			--mincol1;
		int maxcol1 = col1;
		while (maxcol1 < mCols && empty(row1, maxcol1 + 1))
			++maxcol1;
		int mincol = max(mincol0, mincol1);
		int maxcol = min(maxcol0, maxcol1);
		int minrow = min(row0, row1);
		int maxrow = max(row0, row1);
		for (int col = mincol; col <= maxcol; col++) {
			boolean found = true;
			for (int row = minrow; row <= maxrow; row++) {
				if (!empty(row, col))
					found = false;
			}
			if (found)
				return true;
		}
		return false;
	}

	private void shuffle() {
		selRow = -1;
		selCol = -1;
		Random rand = new Random();
		do {
			for (int row = 0; row < mRows; row++) {
				for (int col = 0; col < mCols; col++) {
					int ico = mTiles[row][col];
					int rnd = rand.nextInt(mRows * mCols);
					int row1 = rnd / mCols;
					int col1 = rnd % mCols;
					mTiles[row][col] = mTiles[row1][col1];
					mTiles[row1][col1] = ico;
				}
			}
		} while (!find());
	}

	private boolean find() {
		for (int row0 = 0; row0 < mRows; row0++) {
			for (int col0 = 0; col0 < mCols; col0++) {
				for (int row1 = 0; row1 < mRows; row1++) {
					for (int col1 = 0; col1 < mCols; col1++) {
						if (match(row0, col0, row1, col1)) {
							hintRow0 = row0;
							hintCol0 = col0;
							hintRow1 = row1;
							hintCol1 = col1;
							//Log.d("Hint", "r" + row0 + "c" + col0 + "-" + "r"+ row1 + "c" + col1);
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	private boolean done() {
		for (int row = 0; row < mRows; row++) {
			for (int col = 0; col < mCols; col++) {
				if (mTiles[row][col] != EMPTY)
					return false;
			}
		}
		return true;
	}

	private int max(int v0, int v1) {
		return (v0 > v1) ? v0 : v1;
	}

	private int min(int v0, int v1) {
		return (v0 < v1) ? v0 : v1;
	}

	private boolean empty(int row, int col) {
		return !inside(row, col) || mTiles[row][col] == EMPTY;
	}
	
	private boolean inside(int row, int col) {
		return row >= 0 && col >= 0 && row < mRows && col < mCols;
	}
}
