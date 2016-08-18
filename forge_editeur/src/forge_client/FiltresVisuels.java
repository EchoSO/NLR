package forge_client;

import java.awt.image.RGBImageFilter;

//TODO @ALPHA2 lire+commenter

public class FiltresVisuels 
{
	public FilterGrotte Grotte;
	public FilterNuit Nuit;
	public FilterChaleur Chaleur;
	
	public FiltresVisuels()
	{
		Grotte = new FilterGrotte();
		Nuit = new FilterNuit();
		Chaleur = new FilterChaleur();
	}
	
	
	  public class FilterGrotte extends RGBImageFilter 
	  {
	        public FilterGrotte() 
	        {
	            canFilterIndexColorModel = true;
	        } 
	        // This method is called for every pixel in the image
	        public int filterRGB(int x, int y, int rgb) 
	        {
	        	int red = (rgb & 0xFF0000) >> 16;
	            int green = (rgb & 0xFF00) >> 8;
	            int blue = rgb & 0xFF;
         		red-=25; if (red<0) red=0;
	    		green-=20; if (green<0) green=0;
	    		blue-=20; if (blue<0) blue=0;
	            rgb = rgb & 0xFF000000; rgb=rgb | (red << 16); rgb=rgb | (green << 8); rgb=rgb | blue;
	    		return rgb;
	        }
	  }

	  public class FilterNuit extends RGBImageFilter 
	  {
	        public FilterNuit() 
	        {
	            canFilterIndexColorModel = true;
	        }
	        // This method is called for every pixel in the image
	        public int filterRGB(int x, int y, int rgb) 
	        {
	        	int red = (rgb & 0xFF0000) >> 16;
	            int green = (rgb & 0xFF00) >> 8;
	            int blue = rgb & 0xFF;
         		red=0;
	    		if (green>40) green-=40;
	            rgb = rgb & 0xFF000000; rgb=rgb | (red << 16); rgb=rgb | (green << 8); rgb=rgb | blue;
	            return rgb;
	        }
	  }

	  public class FilterChaleur extends RGBImageFilter 
	  {
	        public FilterChaleur() 
	        {
	            canFilterIndexColorModel = true;
	        }
	        // This method is called for every pixel in the image
	        public int filterRGB(int x, int y, int rgb) 
	        {
	        	int red = (rgb & 0xFF0000) >> 16;
	            int green = (rgb & 0xFF00) >> 8;
	            int blue = rgb & 0xFF;
	            if (red<215) red+=40;
	            if (green>80) green-=80;
	            if (blue>80) blue-=80;
	            rgb = rgb & 0xFF000000; rgb=rgb | (red << 16); rgb=rgb | (green << 8); rgb=rgb | blue;
	            return rgb;
	        }
	  }
}
