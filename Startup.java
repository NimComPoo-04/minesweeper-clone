package Mnsp;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.awt.Frame;
import java.awt.Panel;
import java.awt.Label;
import java.awt.TextField;
import java.awt.GridLayout;
import java.awt.Button;

public class Startup
{
	private int numMines;
	private int width;
	private int height;

	private Frame win;

	Startup()
	{
		win = new Frame();

		win.setFont(Main.fnt);

		this.numMines = 0;
		this.width = 0;
		this.height = 0;
	}

	void execute()
	{
		win.setTitle("MineSweeper");
		win.setResizable(false);

		win.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent eve) {
				win.dispose();
			}

			public void windowClosed(WindowEvent eve) {
				System.exit(0);
			}
		});

		Panel p[] = new Panel[] {new Panel(), new Panel(), new Panel(), new Panel()}; 

		TextField nm, wi, he;

		p[0].add(new Label("Enter Mine Concentration (in %): "));
		p[0].add(nm = new TextField("20", 5));

		p[1].add(new Label("Enter Width of Mine Field: "));
		p[1].add(wi = new TextField("50", 5));

		p[2].add(new Label("Enter Height of Mine Field: "));
		p[2].add(he = new TextField("30", 5));

		Button b = new Button("Submit");
		b.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {

				width =  Integer.parseInt(wi.getText().trim());
				height =  Integer.parseInt(he.getText().trim());
				numMines = Integer.parseInt(nm.getText().trim()) * width * height / 100;

				win.setVisible(false);	

				runGame();
			}
		});

		p[3].add(b);

		for(int i = 0; i < p.length; i++)
			win.add(p[i]);

		win.setLayout(new GridLayout(0, 1));

		win.pack();
		win.setVisible(true);
	}

	void runGame()
	{
		Game g = new Game(width, height, numMines);
		g.genMines();
	//	g.targetFog(0, 0);
		// System.out.println(g);

		new Gfx(g);
	}
}
