package mil.usmc.mapCache;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;


public class Split {
	public class ImgInfo {
		public int level;
		public int x;
		public int y;
		@Override
		public String toString() {
			return "[level = " + level + ", x = " + x + ", y = " + y + "]";
		}
	} 
	
	public ImgInfo createInfObj(){ return new ImgInfo(); }
	
	private String m_csBase = "e:/milmapsCache";
	private String m_data = "bmng";
	private static final Logger s_logger = Logger.getLogger(Split.class.getName());
	private final ImgInfo m_info = new ImgInfo();
	
	String buildDirPath( String data, int size, int level, int xTile ){
		String csDir = m_csBase + "/" + data + "/" + size + "/" + level + "/" + xTile;
		return csDir;
	}
	
	String buildFilePath( String csPath, int yTile, String ext ){
		String csFile = csPath + "/" + yTile + "." + ext;
		return csFile;
	}

	private void writeToFile(String path, BufferedImage bi) throws IOException {
		File file = new File(path);
		try {
			if ( file.exists() == false ){
				file.createNewFile();
				ImageIO.write(bi, "png", file);
			}
		} catch (Throwable t) {
			s_logger.log(Level.WARNING, "Could not process file:" + file.getAbsolutePath(),t);
		} 
	}
	
	public ImgInfo getImageInfo(File file){
		String path = file.toString();
		String[] tokens = path.split(Pattern.quote(File.separator));
		if ( tokens.length == 7 ){
			m_info.level = Integer.parseInt(tokens[4]);
			m_info.x = Integer.parseInt(tokens[5]);
			int endIndex = tokens[6].length() -4;
			String y = tokens[6].substring(0, endIndex);
			m_info.y = Integer.parseInt(y);
			return m_info;
		}
		return null;
	}
	
	private boolean makeDirs( String paths[] ){
		boolean bGoodDir = true;
		File dir = new File(paths[0]);
		if ( dir.exists() == false ){
			bGoodDir = dir.mkdirs();
		}
		if( bGoodDir ){
			dir = new File(paths[1]);
			if ( dir.exists() == false ){
				bGoodDir = dir.mkdirs();
			}
		}
		return bGoodDir;
	}
	
	String[] buildDirPaths(ImgInfo info){
		int x= 2*info.x;
		int y = 2*info.y;
		int level = info.level;
		String dirPaths[] = new String[2];
		dirPaths[0] = buildDirPath(m_data, 256, level, x);
		dirPaths[1] = buildDirPath(m_data, 256, level, x+1);
		return dirPaths;
	}
	
	public void writeSplitImage( ImgInfo info, BufferedImage img[] ) throws IOException{
		
		String[] dirPaths = buildDirPaths(info);
		boolean bGoodDir = makeDirs( dirPaths );
		if ( bGoodDir ){
			int y = 2*info.y;
			int n = 0;
			for( int j = y; j <= y + 1; j++){
				for ( int i = 0; i <= 1; i++ ){
					String filePath = buildFilePath( dirPaths[i], j, "png" );
					writeToFile( filePath, img[n] );
					n++;
				}
			}	
		}
	}
	
	public void splitImage( ImgInfo inf, BufferedImage img ) throws IOException{
		if ( 0 <= inf.y ){
			int numYs = (int)Math.pow(2, inf.level);
			if ( inf.y < numYs ){
				BufferedImage imgs[] = split(2, 2, img);
				writeSplitImage(inf,imgs);
			}
		}		
	}
	
	public void processImage(File file ){
		try{
			String filePath = file.toString();
			if ( filePath.endsWith("png") ){
				//File file = new File(filePath);		
				BufferedImage img = null;
				if ( file.exists() == true ){
					img = (BufferedImage)ImageIO.read(file);
					ImgInfo inf = getImageInfo(file);
					splitImage(inf,img);
				}
			}
		} catch ( IOException e ) {
			e.printStackTrace();
		}
	}
	
	public void scanDirectories(File path) {
	    File files[]; 
	    files = path.listFiles();
	    Arrays.sort(files);
	    for (int i = 0, n = files.length; i < n; i++) {
	      if (files[i].isDirectory()) {
	    	  scanDirectories(files[i]);
	      }
	      else{
	    	  processImage( files[i] );
	      }
	    }
	}
	
    public BufferedImage[] split( int rows, int cols, BufferedImage image )throws IOException {  
  	  
        int chunks = rows * cols;  
  
        int chunkWidth = image.getWidth() / cols; // determines the chunk width and height  
        int chunkHeight = image.getHeight() / rows;  
        int count = 0;  
        BufferedImage imgs[] = new BufferedImage[chunks]; //Image array to hold image chunks  
        for (int x = 0; x < rows; x++) {  
            for (int y = 0; y < cols; y++) {  
                //Initialize the image array with image chunks  
                imgs[count] = new BufferedImage(chunkWidth, chunkHeight, BufferedImage.TYPE_INT_ARGB);  
  
                // draws the image chunk  
                Graphics2D gr = imgs[count++].createGraphics();  
                gr.drawImage(image, 0, 0, chunkWidth, chunkHeight, chunkWidth * y, chunkHeight * x, chunkWidth * y + chunkWidth, chunkHeight * x + chunkHeight, null);  
                gr.dispose();  
            }  
        }  
        return imgs; 
    } 
	

	
	public static void main(String[] args) {
		Split s = new Split();
		s.scanDirectories(new File("e:/milmapsCache"));
	}
}
