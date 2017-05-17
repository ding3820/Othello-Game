package BlackAndWhite;

import javax.imageio.ImageIO;
import javax.swing.*;

import BlackAndWhite.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.lang.*;

public class ClockPanel
		extends JPanel implements Observer {
	private static final int totalTime = 90;
	private boolean turned;
	private boolean wait;
	private BufferedImage digit[] = new BufferedImage[10];
	private GregorianCalendar time;
	private int oldTime, currentTime;
	private int leftTime;
	private Object lock = new Object();
	private int currentP;
	private Chessboard cb;

	public ClockPanel(Chessboard cb) {
		wait = true;
		turned = false;
		time = new GregorianCalendar();
		for (int i = 0; i < 10; i++) {
			try {
				digit[i] = ImageIO.read(getClass().getResource("/CLOCK" + i + ".png"));
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		synchronized (lock) {
			oldTime = currentTime = time.get(Calendar.SECOND);
			if (BlackAndWhite.timeset)
				leftTime = Integer.valueOf(BlackAndWhite.timesetting);
			else if (BlackAndWhite.timeset2)
				leftTime = Integer.valueOf(BlackAndWhite.timesetting2);
			else
				leftTime = totalTime;
		}
		this.cb = cb;
		cb.addObserver(this);
		setPreferredSize(new Dimension(200, 60));
	}

	public synchronized void paint(Graphics g) {
		super.paint(g);
		if (wait) {
			g.setFont(new Font(Font.DIALOG, Font.BOLD, 25));
			g.drawString("Othello Game", 30, 40);
		}
		else if (turned) {
			time = new GregorianCalendar();
			currentTime = time.get(Calendar.SECOND);
			synchronized (lock) {
				if (currentTime != oldTime)
					leftTime--;
				if (leftTime < 0)
					leftTime = 0;
			}
			oldTime = currentTime;
			g.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 20));
			g.drawString("Time left: ", 15, 40);
			synchronized (lock) {
				if (leftTime < 0) {
					g.drawImage(digit[0], 140, 5, 25, 50, this);
					g.drawImage(digit[0], 170, 5, 25, 50, this);
				}
				if (leftTime < 10) {
					g.drawImage(digit[0], 140, 5, 25, 50, this);
					g.drawImage(digit[leftTime], 170, 5, 25, 50, this);
				}
				else {
					g.drawImage(digit[leftTime / 10], 140, 5, 25, 50, this);
					g.drawImage(digit[leftTime % 10], 170, 5, 25, 50, this);
				}
			}
		}
		else {
			time = new GregorianCalendar();
			currentTime = time.get(Calendar.SECOND);
			synchronized (lock) {
				if (currentTime != oldTime)
					leftTime--;
			}
			oldTime = currentTime;
			g.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 20));
			String s = "";
			for (int i = 0; i <= ((totalTime - leftTime) % 6); i++)
				s += ".";
			g.drawString("Waiting..." + s, 30, 40);
		}
	}

	public synchronized void update(Observable o, Object arg) {
		synchronized (lock) {
			if (BlackAndWhite.timeset)
				leftTime = Integer.valueOf(BlackAndWhite.timesetting);
			else if (BlackAndWhite.timeset2)
				leftTime = Integer.valueOf(BlackAndWhite.timesetting2);
			else
				leftTime = totalTime;
		}
		if (!BlackAndWhite.isOffline) {
			if (currentP == cb.currentP)
				turned = true;
			else
				turned = false;
		}
		else {
			turned = true;
		}

		repaint();
	}

	public synchronized void setTurned(boolean t) {
		turned = t;
	}

	public synchronized void setWait(boolean w) {
		wait = w;
	}

	public synchronized void reset() {
		wait = true;
		turned = false;
		synchronized (lock) {
			if (BlackAndWhite.timeset)
				leftTime = Integer.valueOf(BlackAndWhite.timesetting);
			else if (BlackAndWhite.timeset2)
				leftTime = Integer.valueOf(BlackAndWhite.timesetting2);
			else
				leftTime = totalTime;
		}
		repaint();
	}

	public void restart() {
		reset();
	}

	public void start(int p) {
		time = new GregorianCalendar();
		oldTime = currentTime = time.get(Calendar.SECOND);
		wait = false;
		currentP = p;
		synchronized (lock) {
			if (BlackAndWhite.timeset)
				leftTime = Integer.valueOf(BlackAndWhite.timesetting);
			else if (BlackAndWhite.timeset2)
				leftTime = Integer.valueOf(BlackAndWhite.timesetting2);
			else
				leftTime = totalTime;
		}
		if (BlackAndWhite.isOffline) {
			turned = true;
		}
		else {
			if (currentP == cb.currentP)
				turned = true;
			else
				turned = false;
		}
		repaint();

	}

	public int getLT() {
		synchronized (lock) {
			return leftTime;
		}
	}
}