package BlackAndWhite;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;

public class askRedoDialog extends JDialog implements ActionListener {
	public static final int request = 0;
	public static final int response = 1;
	private JPanel buttonP, labelP, mainP;
	private JButton yesB, noB;
	private JLabel l1, lu, ld;
	public boolean yes = false;

	private String status[] = { "Are you sure to undo?", "The oppoenet requests to undo, do you accept?" };

	public askRedoDialog(Frame owner, int s) {
		super(owner, "undo", true);

		l1 = new JLabel("                       " + status[s] + "                       ");
		lu = new JLabel(" ");
		ld = new JLabel(" ");
		labelP = new JPanel(new GridLayout(3, 1));
		labelP.add(lu);
		labelP.add(l1);
		labelP.add(ld);
		labelP.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

		if (s == request) {
			yesB = new JButton("Yes");
			noB = new JButton("No");
		}
		else {
			yesB = new JButton("Agree");
			noB = new JButton("Disagree");
		}
		buttonP = new JPanel();
		buttonP.add(yesB);
		buttonP.add(noB);

		buttonP.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		yesB.addActionListener(this);
		noB.addActionListener(this);

		mainP = new JPanel(new BorderLayout());
		mainP.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

		mainP.add(labelP, BorderLayout.NORTH);
		mainP.add(buttonP, BorderLayout.SOUTH);
		setResizable(false);

		getContentPane().add(mainP);
		Point p;
		p = owner.getLocation();
		int x = (int) p.getX() + 200;
		int y = (int) p.getY() + 150;
		this.setLocation(x, y);
		pack();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == yesB)
			yes = true;
		hide();
	}
}