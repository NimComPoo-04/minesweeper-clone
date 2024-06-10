package Mnsp;

import java.awt.Frame;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Button;
import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.GraphicsEnvironment;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.ArrayList;
import java.util.LinkedList;

class Gfx extends Canvas
{
	private Game game;
	private Frame win;

	private Panel score;

	private Label flagged;
	private Button status;
	private Label time;

	private boolean gameOn;
	private int turns;
	private int mineSteppedOn;

	private static final Color textColor[] = new Color[] { Color.RED, Color.BLUE, Color.GREEN,
		Color.MAGENTA, Color.CYAN, Color.YELLOW, Color.PINK, Color.BLACK };

	private static final int CELLWID = 16;

	Gfx(Game game)
	{
		this.game = game;
		this.turns = 0;
		this.mineSteppedOn = 0;

		this.setSize(game.getWidth() * CELLWID + 1, game.getHeight() * CELLWID + 1);

		win = new Frame("Minesweeper");
		win.setResizable(false);

		gameOn = true;

		win.setFont(Main.fnt);

		win.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				win.dispose();
			}

			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
		});

		win.setLayout(new BorderLayout());

		flagged = new Label(String.format("%04d", game.getMineCount() - game.getFlags().size()), Label.CENTER); 

		status  = new Button("..Not Dead Yet..");
		status.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				// System.out.println("Game Reset");
				reset_game();
			}
		});
		time    = new Label("0000", Label.CENTER);

		score = new Panel();
		score.setLayout(new BorderLayout());
		score.add(flagged, BorderLayout.WEST);
		score.add(status, BorderLayout.CENTER);
		score.add(time, BorderLayout.EAST);

		win.add(score, BorderLayout.NORTH);
		win.add(this, BorderLayout.SOUTH);

		win.pack();
		createBufferStrategy(2);
		win.setVisible(true);

		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent eve)
			{
				clicked(eve);
			}
		});
	}

	public void paint(Graphics g)
	{
		Dimension dim = getSize();

		int wid = game.getWidth();
		int hei = game.getHeight();

		int cw = dim.width / wid;
		int ch = dim.height / hei;

		boolean fogField[][] = game.getFogField();
		byte mineField[][] = game.getMineField();

		for(int i = 0; i < hei; i++)
		{
			for(int j = 0; j < wid; j++)
			{
				if(fogField[i][j])
				{
					switch(mineField[i][j])
					{
						case 0:
							g.setColor(new Color(230, 230, 230));
							g.fillRect(cw * j, ch * i, cw, ch);
							break;

						case 9:
							break;

						default:
							g.setColor(new Color(230, 230, 230));
							g.fillRect(cw * j, ch * i, cw, ch);
							g.setColor(Gfx.textColor[mineField[i][j] - 1]);
							g.drawString(mineField[i][j] + "",
									cw * j + 2, ch * (i + 1) - 2);
					}
				}
				else
				{
					g.setColor(new Color(255, 255, 255));
					g.fillRect(cw * j, ch * i, cw, ch);
				}

				g.setColor(new Color(128, 128, 128));
				g.drawRect(cw * j, ch * i, cw, ch);
			}
		}

		LinkedList<Integer> flags = game.getFlags();

		for(Integer i : flags)
		{
			int nx = i % wid;
			int ny = i / wid;

			g.setColor(new Color(0, 0, 245));
			g.fillRect(cw * nx, ch * ny, cw, ch);
			g.setColor(new Color(0, 245, 200));
			g.drawString("F", cw * nx + 2, ch * (ny + 1) - 2);

			g.setColor(new Color(128, 128, 128));
			g.drawRect(cw * nx, ch * ny, cw, ch);
		}

		if(!gameOn)
		{
			ArrayList<Integer> al = game.getMines();

			int x = mineSteppedOn % wid;
			int y = mineSteppedOn / wid;

			for(Integer i : al)
			{
				int nx = i % wid;
				int ny = i / wid;

				if(nx == x && y == ny)
				{
					g.setColor(new Color(245, 0, 0));
					g.fillRect(cw * nx, ch * ny, cw, ch);
					g.setColor(new Color(245, 200, 0));
					g.drawString("B", cw * nx + 2, ch * (ny + 1) - 2);
				}
				else
				{
					g.setColor(new Color(245, 200, 0));
					g.fillRect(cw * nx, ch * ny, cw, ch);
					g.setColor(new Color(245, 0, 0));
					g.drawString("B", cw * nx + 2, ch * (ny + 1) - 2);
				}


				g.setColor(new Color(128, 128, 128));
				g.drawRect(cw * nx, ch * ny, cw, ch);
			}
		}
	}

	void reset_game()
	{
		this.game = new Game(game.getWidth(), game.getHeight(), game.getMineCount());
		this.game.genMines();
		this.turns = 0;
		this.gameOn = true;
		this.repaint();

		time.setText("0000");
		time.repaint();

		status.setLabel("..Not Dead Yet..");
		status.repaint();

		flagged.setText("0000");
		flagged.repaint();
	}

	void clicked(MouseEvent eve)
	{
		if(!gameOn)
			return;

		int x = eve.getX() / CELLWID;
		int y = eve.getY() / CELLWID;

		if(eve.getButton() == MouseEvent.BUTTON1)
		{
			gameOn = !game.targetFog(x, y);

			mineSteppedOn = y * game.getWidth() + x;

			if(turns > 9999)
			{
				turns = 9999;
				gameOn = false;

				status.setLabel("..Out of Turns..");
			}
		}
		else if(eve.getButton() == MouseEvent.BUTTON3)
		{
			game.toggleFlag(x, y);
			flagged.setText(String.format("%04d", game.getMineCount() - game.getFlags().size())); 

			if(game.getFlags().size() == game.getMineCount())
			{
				byte mineField[][] = game.getMineField();
				LinkedList<Integer> li = game.getFlags();

				boolean bt = true;
				for(Integer k : li)
				{
					int kx = k % game.getWidth();
					int ky = k / game.getWidth();

					if(mineField[ky][kx] != 9)
					{
						bt = false;
						break;
					}
				}

				if(bt)
				{
					gameOn = false;
					status.setLabel("..All Mines Flagged You Win..");
				}
			}
		}

		if(!gameOn)
			status.setLabel("..BOOOOOM..");

		turns++;
		time.setText(String.format("%04d", turns));

		time.repaint();
		status.repaint();
		flagged.repaint();
		repaint();
	}
}
