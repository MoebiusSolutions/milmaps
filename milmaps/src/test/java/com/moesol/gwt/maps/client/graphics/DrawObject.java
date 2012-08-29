/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class DrawObject {

	static protected List<Point> m_list = new ArrayList<Point>();

	public List<Point> getList() {
		return m_list;
	}

	public void clearList() {
		m_list.clear();
	}

	private static void readFile(String name) {
		m_list.clear();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(name));
			String line = br.readLine();
			while (line != null) {
				int n = line.indexOf(',');
				String s = line.substring(0, n);
				int x = (int) Double.parseDouble(s);
				s = line.substring(n + 1);
				int y = (int) Double.parseDouble(s);
				m_list.add(new Point(x, y));
				line = br.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void renderToPng(String name, int count, boolean bClose)
			throws IOException {
		String parent = "src/test/resources/expectedObjects/";
		String tName = (count > 0 ? name + count : name);
		String fileName = parent + tName + ".png";
		File imageTarget = new File(fileName);
		//imageTarget.createNewFile();
		//imageTarget.mkdirs();

		BufferedImage img = new BufferedImage(512, 512,
											  BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 512, 512);

		g.setColor(Color.WHITE);

		String textFileName = parent  + name + ".txt";
		readFile(textFileName);
		int n = m_list.size();
		Point p, q;
		if (n > 0) {
			for (int i = 0; i < n - 1; i++) {
				p = m_list.get(i);
				q = m_list.get(i + 1);
				g.drawLine(p.x, p.y, q.x, q.y);
			}
			if (bClose) {
				p = m_list.get(n - 1);
				q = m_list.get(0);
				g.drawLine(p.x, p.y, q.x, q.y);
			}
			ImageIO.write(img, "png", imageTarget);
			m_list.clear();
		}
	}

	public static void draw(String name, int count, boolean bClose) {
		try {
			renderToPng(name, count, bClose);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
