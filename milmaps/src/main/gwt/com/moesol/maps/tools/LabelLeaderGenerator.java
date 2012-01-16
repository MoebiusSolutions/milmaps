package com.moesol.maps.tools;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.imageio.ImageIO;

import com.moesol.gwt.maps.client.DeclutterEngine;

/**
 * Generate a png for each declutter slot and color
 * @author <a href="http://www.moesol.com">Moebius Solutions, Inc.</a>
 */
public class LabelLeaderGenerator {
	private static Map<String, Color> COLORS = new TreeMap<String, Color>();
	static {
		COLORS.put("cyan", Color.CYAN);
		COLORS.put("red", Color.RED);
		COLORS.put("green", Color.GREEN.brighter());
		COLORS.put("yellow", Color.YELLOW);
		COLORS.put("magenta", Color.MAGENTA);
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		DeclutterEngine engine = new DeclutterEngine(null);
		
		int minRowOffset = 0;
		int maxRowOffset = 0;
		int minColOffset = 0;
		int maxColOffset = 0;
		for (int rowOffset : engine.searchRowOffsets) {
			for (int colOffset : engine.searchColOffsets) {
				minRowOffset = Math.min(minRowOffset, rowOffset);
				maxRowOffset = Math.max(maxRowOffset, rowOffset);
				minColOffset = Math.min(minColOffset, colOffset);
				maxColOffset = Math.max(maxColOffset, colOffset);
			}
		}

		int paddingWidth = 4;
		int symbolColWidth = (32 + (engine.cellWidth - 1)) / engine.cellWidth;
		int rowHeight = maxRowOffset - minRowOffset + 2;
		int colWidth = maxColOffset - minColOffset + symbolColWidth;
		int oneImageWidth = colWidth * engine.cellWidth + paddingWidth;
		int oneImageHeight = rowHeight * engine.cellHeight;
		
		System.out.printf("oneImageWidth: %d, oneImageHeight: %d%n", oneImageWidth, oneImageHeight);
		
		int nImages = COLORS.size() * engine.searchRowOffsets.length;
		int totalImageWidth = nImages * oneImageWidth;
		BufferedImage img = new BufferedImage(totalImageWidth, oneImageHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = img.createGraphics();

		graphics.setStroke(new BasicStroke(1.0f));
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int centerX = oneImageWidth / 2;
		int centerY = oneImageHeight / 2;

//		int imageNumber = 0;
		for (String colorName : COLORS.keySet()) {
			Color color = COLORS.get(colorName);
			System.out.printf("map.put(\"%s\",%d);%n", colorName, centerX - oneImageWidth / 2);
			
			for (int i = 0; i < engine.searchRowOffsets.length; i++) {
				int rowOffset = engine.searchRowOffsets[i];
				int colOffset = engine.searchColOffsets[i];
				
				Placement pLabel = makeLabelPlacement(engine, rowOffset, colOffset);
	
				int labelHalfHeight = pLabel.height / 2;
	
				pLabel.x += centerX;
				pLabel.y += centerY;
				
				if (colOffset > 0) {
					pLabel.x += (symbolColWidth + 2*engine.cellWidth);
				} else {
					pLabel.x -= (symbolColWidth + 2*engine.cellWidth) + pLabel.width + 1;
				}
//				graphics.setColor(Color.RED);
//				graphics.drawRect(pLabel.x, pLabel.y, pLabel.width, pLabel.height);
				
				graphics.setColor(color);
				if (centerX < pLabel.x) {
					// right
					graphics.drawLine(centerX, centerY, 
							pLabel.x - engine.cellWidth, pLabel.y + labelHalfHeight);
					graphics.drawLine(pLabel.x - engine.cellWidth, pLabel.y + labelHalfHeight, 
							pLabel.x, pLabel.y + labelHalfHeight);
				} else {
					// left
					graphics.drawLine(centerX, centerY, 
							pLabel.x + pLabel.width + engine.cellWidth, pLabel.y + labelHalfHeight);
					graphics.drawLine(pLabel.x + pLabel.width + engine.cellWidth, pLabel.y + labelHalfHeight, 
							pLabel.x + pLabel.width, pLabel.y + labelHalfHeight);
				}
				
				// draw line on left
//				graphics.drawLine(imageNumber * oneImageWidth, 0, imageNumber * oneImageWidth, oneImageHeight);
				
				// clear circle
				Composite composite = graphics.getComposite();
				graphics.setComposite(AlphaComposite.Clear);
				graphics.fillOval(centerX - 16, centerY - 16, 32, 32);
				graphics.setComposite(composite);
				
				centerX += oneImageWidth;
//				imageNumber++;
			}
		}
		File folder = new File("target/leaderImages");
		folder.mkdirs();
		ImageIO.write(img, "png", new File(folder, "label-leaders.png"));
	}

	private static Placement makeLabelPlacement(DeclutterEngine engine, int rowOffset, int colOffset) {
		Placement pImage = new Placement();
		pImage.x = colOffset * engine.cellWidth;
		pImage.y = rowOffset * engine.cellHeight;
		pImage.width = 32;
		pImage.height = 8;
		return pImage;
	}

}
