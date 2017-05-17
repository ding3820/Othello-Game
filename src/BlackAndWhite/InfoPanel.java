package BlackAndWhite;

import javax.swing.*;

import java.awt.*;
import java.util.*;

public class InfoPanel
		extends JPanel implements Observer {
	private String black, white;
	private Chessboard cb;

	public InfoPanel(Chessboard cb) {
		this.cb = cb;
		black = white = "";
		setPreferredSize(new Dimension(200, 200));
		cb.addObserver(this);
	}

	public void setName(String black, String white) {
		this.black = black;
		this.white = white;
	}

	public void paint(Graphics g) {
		super.paint(g);
		g.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 20));
		g.fillOval(10, 40, 40, 40);
		g.setColor(Color.BLACK);
		g.drawString(black, 10, 20);
		g.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 20));
		
		g.setColor(Color.WHITE);
		g.fillOval(10, 140, 40, 40);
		
		g.setColor(Color.BLACK);
		g.drawString(white, 10, 110);
		
		g.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 90));

		if (cb.countBlack < 10)
			g.drawString("0" + cb.countBlack, 70, 90);
		else
			g.drawString("" + cb.countBlack, 70, 90);

		g.setColor(Color.WHITE);
		g.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 90));

		if (cb.countWhite < 10)
			g.drawString("0" + cb.countWhite, 70, 180);
		else
			g.drawString("" + cb.countWhite, 70, 180);
	}

	public void update(Observable o, Object arg) {
		repaint();
	}

	public void restart() {
		setName("", "");
		repaint();
	}

	public void reset() {
		setName("", "");
		repaint();
	}

	public void start(String black, String white) {
		setName(black, white);
		repaint();
	}
}