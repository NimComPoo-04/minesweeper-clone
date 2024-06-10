package Mnsp;

import java.awt.Font;
import java.awt.GraphicsEnvironment;

public class Main
{
	static Font fnt;

	public static void main(String args[])
	{
		/*
		try
		{
			GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
			genv.registerFont(Font.createFont(Font.TRUETYPE_FONT, genv.getClass().getResourceAsStream("IBMPlexMono.ttf")));
			fnt = new Font("IBMPlexMono", Font.TRUETYPE_FONT | Font.BOLD, 16);
		}
		catch(Exception e)
		{
			System.err.println(e);
		}
		*/

		fnt = new Font(null, Font.TRUETYPE_FONT | Font.BOLD, 16);

		Startup s = new Startup();
		s.execute();
	}
}
