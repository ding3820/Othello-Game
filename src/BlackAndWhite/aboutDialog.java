package BlackAndWhite;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;

public class aboutDialog extends JDialog implements ActionListener {
	private JPanel buttonP, labelP, mainP;
	private JButton yesB;
	private JLabel l1, l2;

	public aboutDialog(Frame owner) {
		super(owner, "About", true);

		l1 = new JLabel("Othello Game");
		l2 = new JLabel("by Stan");

		labelP = new JPanel(new GridLayout(5, 1));
		labelP.add(l1);
		labelP.add(l2);
		labelP.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

		yesB = new JButton("OK");
		buttonP = new JPanel();
		buttonP.add(yesB);
		buttonP.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		yesB.addActionListener(this);

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
		hide();
	}
}