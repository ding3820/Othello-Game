package BlackAndWhite;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ChessBoardPanel
		extends JPanel implements Observer, MouseListener {
	private BufferedImage bk, black, white, mark, red;
	private Chessboard cb;
	private boolean wait = true;
	private int currentP = Style.EMPTY;

	
	public ChessBoardPanel(Chessboard cb) {
		try {
			bk = ImageIO.read(getClass().getResource("/boardBk.gif"));
			black = ImageIO.read(getClass().getResource("/chessBlack.gif"));
			white = ImageIO.read(getClass().getResource("/chessWhite.gif"));
			mark = ImageIO.read(getClass().getResource("/mark.gif"));
			red = ImageIO.read(getClass().getResource("/chessRed.png"));
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		cb.addObserver(this);
		this.cb = cb;
		setPreferredSize(new Dimension(420, 420));

		addMouseListener(this);
	}

	public void paint(Graphics g) {
		super.paint(g);
		for (int x = 0; x < 8; x++)
			for (int y = 0; y < 8; y++) {
				g.drawImage(bk, 10 + 50 * x, 10 + 50 * y, this);
				if (cb.board[y][x] == Style.BLACK)
					g.drawImage(black, 10 + 50 * x, 10 + 50 * y, this);
				else if (cb.board[y][x] == Style.WHITE)
					g.drawImage(white, 10 + 50 * x, 10 + 50 * y, this);
				else if (cb.checkboard[y][x] == Style.RED) {
					g.drawImage(red, 10 + 50 * x, 10 + 50 * y, this);
				}
			}
		cb.cleancheckboard();
		if (cb.currentX >= 0 && cb.currentY >= 0)
			g.drawImage(mark, 10 + 50 * cb.currentX, 10 + 50 * cb.currentY, this);

	}

	public void update(Observable o, Object arg) {
		repaint();
	}

	public void restart() {
		wait = true;
		repaint();
	}

	public void reset() {
		restart();
	}

	public void start(int p) {
		wait = false;
		currentP = p;
		repaint();
	}

	public void disable(boolean b) {
		wait = !b;
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
		if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {
			if (!BlackAndWhite.isOffline)
				if (currentP != cb.currentP)
					return;
			if (wait)
				return;
			int x = e.getX();
			int y = e.getY();
			if (x < 10 || y < 10 || x >= 410 || y >= 410)
				return;
			x = (x - 10) / 50;
			y = (y - 10) / 50;
			cb.playChess(x, y);
			Chessboard.flag = 1;
		}
		
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}
}