package BlackAndWhite;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;

public class statusDialog extends JDialog implements ActionListener {
	public static final int WIN = 0;
	public static final int LOSE = 1;
	public static final int DRAW = 2;
	public static final int OPSUR = 3;
	public static final int SURRENDER = 4;
	public static final int TIMEOUT = 5;
	public static final int OPTIMEOUT = 6;
	public static final int BLACKWIN = 7;
	public static final int WHITEWIN = 8;
	public static final int SURRENDER2 = 9;

	private JPanel buttonP, labelP, mainP;
	private JButton yesB, noB;
	private JLabel l1, lu, ld;
	public boolean yes = false;

	private String status[] = { "Win", "Lose", "Draw", "The opponent admit defeat", "admit defeat", "Timeout", "The oppoenet timeout", "Black Win", "White Win", "Are you sure to surrender?" };

	public statusDialog(Frame owner, int s) {
		super(owner, "Message", true);

		l1 = new JLabel("                       " + status[s] + "                       ");
		lu = new JLabel(" ");
		ld = new JLabel(" ");
		labelP = new JPanel(new GridLayout(3, 1));
		labelP.add(lu);
		labelP.add(l1);
		labelP.add(ld);
		labelP.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

		if (s == SURRENDER2) {
			yesB = new JButton("Yes");
			noB = new JButton("No");
		}
		else {
			yesB = new JButton("Ok");
			noB = new JButton("No");
		}
		buttonP = new JPanel();
		buttonP.add(yesB);
		buttonP.add(noB);
		if (s != SURRENDER2)
			noB.hide();

		buttonP.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		yesB.addActionListener(this);
		noB.addActionListener(this);

		mainP = new JPanel(new BorderLayout());
		mainP.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

		mainP.add(labelP, BorderLayout.NORTH);
		mainP.add(buttonP, BorderLayout.SOUTH);
		Point p;
		p = owner.getLocation();
		int x = (int) p.getX() + 200;
		int y = (int) p.getY() + 150;
		this.setLocation(x, y);
		setResizable(false);

		getContentPane().add(mainP);
		pack();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == yesB)
			yes = true;

		hide();
	}
}