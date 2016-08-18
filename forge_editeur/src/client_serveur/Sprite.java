package client_serveur;

//TODO @ALPHA2 : lire+commenter

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;

import javax.swing.JFrame;

class ComparateurSprite implements Comparator<Sprite> {
	public int compare(Sprite s1, Sprite s2){
                //tri desc
		if (s1.compareTo(s2) == 1) {
			return -1;
		} else if (s1.compareTo(s2) == -1) {
			return 1;        	
		} else {
			return 0;
		}
	}      
}

public class Sprite
{
    idSprite ID; /* we can easily determine the sprite's type */
    ArrayList<Sprite> ParentList ;
 //   Flags : cardinal; { for SDL_BlitSurface }
    boolean isDead; /* need to destroy ? */
   int AnimPhase; /* which image we draw */
    int x, y, z; /* x, y coords for screen, z for sorting */
    int w, h; /* Width & Height of sprite */
    Image Surface, Image; /* Screen and sprite images */
    int NumberOfFrames; /* count of frames [by brombs] */
    float Transparency;
    JFrame Source;
    public Sprite(String _Image,int Width,int Height,JFrame _Source)
    {
    	ID = idSprite.idDefault;
    	if (new File(_Image).exists())
    	{
/*    	    try {
				Image = ImageIO.read(new File(_Image));
			} catch (IOException e) {
				e.printStackTrace();
			}*/
    		ImageLoader im=new ImageLoader(null);
    		Image=im.loadImage(_Image);
    	    NumberOfFrames = Image.getWidth(null) / Width; 
    	}
    	else
    	  NumberOfFrames = 0;
    	AnimPhase = 0;
    	isDead = false;
    	x = 0;
    	y = 0;
    	z = 0;
    	w = Width;
    	h = Height;
    	Transparency=1;
    	Source=_Source;
    }
    //Static ?
	public static enum idSprite {
		  idDefault,
		  idPLAYER,
		  idMChar,
		  idCurseur,
		  idFChar,
		  idEvenement,
		  idMonstre,
		  idAnimation,
		  idDegat
	}
    public int compareTo(Sprite Item2)
    {
 	    if (this.z < Item2.z)
 	      return 1;
 	    else if (this.z > Item2.z)
 	      return -1;
 	    else
 	      return 0;
    }
    public void GetCollisionRect(Rectangle Rect)
    {
    	  Rect.x = x;
    	  Rect.y = y;
    	  Rect.width = w;
    	  Rect.height = h;
    }
    public void Draw(Graphics2D g) // draw sprite on screen 
    {
/*    	Rectangle DestRect=new Rectangle();
    	SrcRect.x = AnimPhase * w; // which animation phase need to draw?
    	DestRect.x = x; // set screen positions/
    	DestRect.y = y;
  	    g.drawImage(Image,DestRect.x,DestRect.y,SrcRect.width,SrcRect.height,Source);
    	PrevRect = DestRect;*/
    }
    public void Move(KeyboardInput.KeyState[] keys) /* move a sprite */
    {	
    }
    public void Kill() /* we will need to destroy this sprite */
    {
      isDead = true;    	
    }
	public void Draw() {
		
	}
  }
