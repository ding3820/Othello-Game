package BlackAndWhite;

import java.util.*;

class step {
	public int oldX, oldY, currentX, currentY;
	public int oldP, currentP;
	public int board[][] = new int[8][8];

	public void save(Chessboard cb) {

		this.oldX = cb.oldX;
		this.oldY = cb.oldY;
		this.currentX = cb.currentX;
		this.currentY = cb.currentY;
		this.currentP = cb.currentP;
		this.oldP = cb.oldP;
		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++)
				this.board[i][j] = cb.board[i][j];
	}

}

public class Chessboard extends Observable {
	public int board[][] = new int[8][8];
	public int checkboard[][] = new int[8][8];
	public int oldX, oldY, currentX, currentY;
	public int countBlack, countWhite;
	public int oldP, currentP;
	private boolean wait = true;
	private Object lock = new Object();
	private Stack s = new Stack();
	public boolean redo;

	public static int flag;

	musicPlay mP = new musicPlay();

	public Chessboard() {
		reset();
	}

	public synchronized void reset() {
		synchronized (lock) {
			wait = true;
		}
		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++)
				board[i][j] = Style.EMPTY;
		board[3][3] = board[4][4] = Style.BLACK;
		board[3][4] = board[4][3] = Style.WHITE;
		oldX = oldY = currentX = currentY = -1;
		countBlack = countWhite = 2;
		oldP = currentP = Style.EMPTY;
		redo = false;
		s.clear();
	}

	public synchronized void restart() {
		reset();
	}

	public synchronized void start(int p) {
		synchronized (lock) {
			wait = false;
		}
		currentP = p;
		checkboard();
	}

	private synchronized boolean check(int xCheck, int yCheck) {
		int direction[] = new int[2];
		int x, y, oneDirection;
		boolean correct, returnC = false;
		synchronized (lock) {
			if (wait)
				return false;
		}
		if (board[yCheck][xCheck] == Style.EMPTY) {
			for (int i = 0; i < 8; i++) {
				oneDirection = 0;
				x = xCheck;
				y = yCheck;
				convertDirection(i, direction);
				do {
					oneDirection++;
					x += direction[0];
					y += direction[1];
					if (x >= 0 && x < 8 && y >= 0 && y < 8 && board[y][x] != Style.EMPTY && board[y][x] != currentP)
						correct = true;
					else
						correct = false;
				} while (correct);
				if (x >= 0 && x < 8 && y >= 0 && y < 8 && board[y][x] == currentP && oneDirection > 1) {
					returnC = true;
				}
			}
		}
		return returnC;
	}

	public synchronized void playChess(int xDone, int yDone) {
		boolean correct;
		int x, y, oneDirection;
		int direction[] = new int[2];
		if (!check(xDone, yDone))
			return;

		mP.clip2.start();

		step st = new step();
		st.save(this);
		s.push(st);

		for (int i = 0; i < 8; i++) {
			x = xDone;
			y = yDone;
			convertDirection(i, direction);
			oneDirection = 0;
			do {
				oneDirection++;
				x += direction[0];
				y += direction[1];
				if (x >= 0 && x < 8 && y >= 0 && y < 8 && board[y][x] != Style.EMPTY && board[y][x] != currentP)
					correct = true;
				else
					correct = false;
			} while (correct);
			if (x >= 0 && x < 8 && y >= 0 && y < 8 && board[y][x] == currentP && oneDirection > 1) {
				x = xDone;
				y = yDone;
				while (board[y][x] != currentP) {
					board[y][x] = currentP;
					x += direction[0];
					y += direction[1];
				}
				board[yDone][xDone] = Style.EMPTY;
			}
		}
		board[yDone][xDone] = currentP;
		oldP = currentP;
		getCount();
		if (gameOver())
			currentP = Style.EMPTY;
		else if (test()) {
			changeStyle();
			checkboard();
		}
		if (flag == 1) {
			if (BlackAndWhite.AIgame) {
				flag = 0;
				for (int i = 0; i < 8; i++) {
					for (int j = 0; j < 8; j++) {
						if (checkboard[i][j] == Style.RED) {
							playChess(j, i);
							i = 8;
							break;
						}
					}
				}
			}
			/*
			if (gameOver())
				currentP = Style.EMPTY;
			else if (test()) {
				changeStyle();
				checkboard();
			}*/

		}

		setValue(xDone, yDone);

	}

	public void checkboard() {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (check(i, j))
					checkboard[j][i] = Style.RED;
			}
		}

		/*
		 * for (int i = 0; i < 8; i++) { for (int j = 0; j < 8; j++) {
		 * System.out.print(checkboard[i][j]); } System.out.println(); }
		 * System.out.println();
		 */

	}

	public void cleancheckboard() {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				checkboard[i][j] = 0;
			}
		}
	}

	private boolean test() {
		int x, y;
		boolean correct = false;
		changeStyle();
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				x = i;
				y = j;
				if (check(x, y)) {
					correct = true;
				}
			}
		}
		changeStyle();
		return correct;
	}

	private boolean gameOver() {
		boolean local = false, remote = false;
		if (!test())
			remote = true;
		changeStyle();
		if (!test())
			local = true;
		changeStyle();
		return local & remote;
	}

	private void changeStyle() {
		if (currentP == Style.BLACK)
			currentP = Style.WHITE;
		else if (currentP == Style.WHITE)
			currentP = Style.BLACK;
	}

	private void convertDirection(int i, int direction[]) {
		switch (i) {
		case 0:
			direction[0] = -1;
			direction[1] = 1;
			break;// southwest
		case 1:
			direction[0] = -1;
			direction[1] = 0;
			break;// west
		case 2:
			direction[0] = -1;
			direction[1] = -1;
			break;// northwest
		case 3:
			direction[0] = 0;
			direction[1] = 1;
			break;// south
		case 4:
			direction[0] = 0;
			direction[1] = -1;
			break;// north
		case 5:
			direction[0] = 1;
			direction[1] = 1;
			break;// southeast
		case 6:
			direction[0] = 1;
			direction[1] = 0;
			break;// east
		case 7:
			direction[0] = 1;
			direction[1] = -1;
			break;// northeast
		}
	}

	public void setValue(int x, int y) {
		oldX = currentX;
		oldY = currentY;
		currentX = x;
		currentY = y;
		redo = false;
		setChanged();
		notifyObservers();
	}

	public void redo() {
		step st;
		if (!s.empty()) {
			st = (step) s.pop();
			this.oldX = st.oldX;
			this.oldY = st.oldY;
			this.currentX = st.currentX;
			this.currentY = st.currentY;
			this.currentP = st.currentP;
			this.oldP = st.oldP;
			for (int i = 0; i < 8; i++)
				for (int j = 0; j < 8; j++)
					this.board[i][j] = st.board[i][j];
			getCount();
			redo = true;
			checkboard();
			setChanged();
			notifyObservers();
		}
	}

	public void getCount() {
		countBlack = 0;
		countWhite = 0;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (board[i][j] == Style.BLACK) {
					countBlack++;
				}
				else if (board[i][j] == Style.WHITE) {
					countWhite++;
				}
			}
		}
	}
}