package forge_client;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class ImageLoader {
   final GraphicsConfiguration gc;
   public ImageLoader(GraphicsConfiguration gc) {
       if(gc==null) {
           gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice ().getDefaultConfiguration();
       }
       this.gc = gc;
   }
 
   public BufferedImage loadImage(String resource) {
       try  {
    	   BufferedImage src=null;
   	       src = ImageIO.read(new File(resource));
//           BufferedImage src = javax.imageio.ImageIO.read(getClass().getResource(resource));
           //In Java 1.4 and earlier Images returned from ImageIO are NOT managed images
           //Therefore, we copy it into a ManagedImage
           BufferedImage dst = gc.createCompatibleImage(src.getWidth(),src.getHeight(),src.getTransparency());
           // Setting transparency
           Graphics2D g2d = dst.createGraphics();
           g2d.setComposite(AlphaComposite.Src);
           // Copy image
           g2d.drawImage(src,0,0,null);
           g2d.dispose();
           return dst;
       } catch(java.io.IOException ioe) {
    	   System.out.println(resource+" not found in project");
           ioe.printStackTrace(); 
           return null;
       }
   }
} 
