package Mnsp;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.function.Predicate;
import java.util.Arrays;

class Game
{
	public static final byte MINE = 9;

	// Dimention
	private int width;
	private int height;

	// The minefield
	private byte mineField[][];
	private boolean fogField[][];

	// Number of mines
	private int mineCount;

	private LinkedList<Integer> flags;
	private ArrayList<Integer> mines;

	Game(int w, int h, int mc)
	{
		width = w;
		height = h;

		mineCount = mc;

		mineField = new byte[h][w];
		fogField = new boolean[h][w];

		flags = new LinkedList<Integer>();
		mines = new ArrayList<Integer>();
	}

	boolean toggleFlag(int x, int y)
	{
		Predicate<Integer> test = (Integer a) -> y * width + x == a;

		if(flags.removeIf(test))
			return false;

		if(flags.size() >= mineCount)
			return false;

		flags.addFirst(y * width + x);
		return true;
	}

	boolean targetFog(int x, int y)
	{
		if(y < 0 || y >= height || x < 0 || x >= width)
			return false;

		if(fogField[y][x])
			return false;

		// XXX: Recursion is probably viable for any human sized games but
		// it is prone to failiure in the case of high width x height and low
		// number of mines.
		if(mineField[y][x] == 0)
		{
			ArrayList<Integer> find = new ArrayList<Integer>();

			find.add(x + y * width);

			while(find.size() != 0)
			{
				// we know x < width
				int nx = find.get(0) % width;
				int ny = find.get(0) / width;

				if(!fogField[ny][nx])
				{
					fogField[ny][nx] = true;

					if(mineField[ny][nx] == 0)
					{
						for(int i = -1; i <= 1; i++)
						{
							if(ny + i < 0 || ny + i >= height)
								continue;

							for(int j = -1; j <= 1; j++)
							{
								if(nx + j < 0 || nx + j >= width)
									continue;

								if(!fogField[ny + i][nx + j])
									find.add((ny + i) * width + (nx + j));
							}
						}
					}
				}

				// remove the 0th element
				find.remove(0);
			}
		}
		else if(mineField[y][x] == Game.MINE)
		{
			fogField[y][x] = true;
			return true;		// bomb
		}
		else
		{
			fogField[y][x] = true;
		}

		return false;
	}

	void genMines()
	{
		for(int i = 0; i < mineCount; i++)
		{
			int x, y;

			do
			{
				x = (int)(Math.random() * width);
				y = (int)(Math.random() * height);
			}
			while(mineField[y][x] == Game.MINE);

			mineField[y][x] = Game.MINE;
			mines.add(y * width + x);

			for(int k = -1; k <= 1; k++)
			{
				if(y + k >= height || y + k < 0)
					continue;

				for(int l = -1; l <= 1; l++)
				{
					if(k == 0 && l == 0)
						continue;

					if(l + x >= width || l + x < 0)
						continue;

					if(mineField[y + k][x + l] == Game.MINE)
						continue;

					mineField[y + k][x + l]++;
				}
			}
		}
	}	

	boolean correctlyFlagged()
	{
		if(flags.size() == mineCount)
		{
			for(int a : flags)
			{
				int h = a / width;
				int w = a % width;

				if(mineField[h][w] == Game.MINE)
					return true;
			}
		}

		return false;
	}

	public LinkedList<Integer> getFlags()
	{
		return flags;
	}

	public boolean[][] getFogField()
	{
		return fogField;
	}

	public byte[][] getMineField()
	{
		return mineField;
	}

	public int getMineCount()
	{
		return mineCount;
	}

	public String toString()
	{
		String str = "";

		for(int i = 0; i < height; i++)
		{
			for(int j = 0; j < width; j++)
			{
				if(fogField[i][j])
				{
					switch(mineField[i][j])
					{
						case 0:
							str += ' ';
							break;

						case 9:
							str += 'B';
							break;

						default:
							str += mineField[i][j];
					}
				}
				else
					str += '.';

				str += ' ';
			}

			str += '\n';
		}

		return str;
	}

	int getWidth()
	{
		return width;
	}

	int getHeight()
	{
		return height;
	}

	ArrayList<Integer> getMines()
	{
		return mines;
	}
}
