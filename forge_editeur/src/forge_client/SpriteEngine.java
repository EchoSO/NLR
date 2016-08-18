package forge_client;

import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;

  public class SpriteEngine
  {
    private boolean NeedSort; /* do we need to resort sprites by Z? */
    private Image FSurface;
    //private Image FBackground; /* screen and background surface */
    public void SetSurface(Image _Surface)
    {
    	/* set all sprites' Surface to _Surface */
    	int i;
    	Sprite TempSpr;
	    FSurface = _Surface;
        if (Sprites.size() > 0)
    	    for (i = 0;i<Sprites.size();i++)
    	    {
    	      TempSpr=Sprites.get(i);
    	      TempSpr.Surface = _Surface;
    	    }
    }
/*    public void SetBackground(Image _Surface)
    {
    	//set all sprites' Background surface to _Surface
    	int i;
    	Sprite TempSpr;
    	FBackground = _Surface;
        if (Sprites.size() > 0)
    	    for (i= 0;i<Sprites.size();i++)
    	    {
    	    	TempSpr = Sprites.get(i);
    		    TempSpr.Background = _Surface;
    	    }
    }*/
    public ArrayList<Sprite> Sprites; /* all sprites */
    public boolean NeedRedrawBackground; /* background surface is lost? */
    public void clear() /* destroy all sprites from list */
    {
   	  Sprite TempSpr;
   	  while (Sprites.size() > 0) 
   	  {/* destroy all sprites */
    	    TempSpr = Sprites.get(0);
    	    RemoveSprite( TempSpr );
   	  }
   	  Sprites.clear();
    }
    public void SortSprites() /* that is */
    {
    	Collections.sort(Sprites, new ComparateurSprite());
    }
/*    private static Object resizeArray (Object oldArray, int newSize) {
    	   int oldSize = java.lang.reflect.Array.getLength(oldArray);
    	   Class elementType = oldArray.getClass().getComponentType();
    	   Object newArray = java.lang.reflect.Array.newInstance(
    	         elementType,newSize);
    	   int preserveLength = Math.min(oldSize,newSize);
    	   if (preserveLength > 0)
    	      System.arraycopy (oldArray,0,newArray,0,preserveLength);
    	   return newArray; }*/
    public void AddSprite(Sprite Item ) /* add a sprite to list */
    {
	  Item.Surface = FSurface; /* setting new sprite's surfaces */
	  //Item.Background = FBackground;
	  Item.ParentList = Sprites;
	  Sprites.add( Item );
	  NeedSort = true;
    }
    public void RemoveSprite(Sprite Item) /* remove a sprite from list and from memory */
    {
   	  Sprites.remove( Item );
    }
    public void Move(KeyboardInput.KeyState[] keys) /* move all sprites in the list */
    {
    	int i;
    	int max;
    	Sprite TempSpr;
   	    if (Sprites.size() > 0)
   	    {
   	      NeedRedrawBackground = false;
    	  i = 0; max = Sprites.size();
    	  do
    	  {
    	      TempSpr = Sprites.get(i);
/*    		  if (TempSpr.Remove() == -2)
    	        NeedRedrawBackground = true;*/
    	      if (TempSpr.isDead)
    	      {
    	        RemoveSprite( TempSpr );
    	        max--;
    	      }
    	      else
    	      {
    	        TempSpr.Move(keys);
    	        i++;
    	      }
    	  }
    	  while (i < max);
    	  if (NeedSort == true)
    	  {
    	    SortSprites();
    	    NeedSort = false;
    	  }
   	    }
    }
    public void Draw(Graphics2D g) /* draw all sprites in the list */
    {
   	  int i;
   	  int num;
   	  Sprite TempSpr;
      num = Sprites.size();
      if (num > 0)
      {
    	for(i= 0;i<num;i++)
    	{
          TempSpr=Sprites.get(i);
    	  TempSpr.Draw(g);
    	}
      }
    }
    public SpriteEngine(Image _Surface)
    {
    	  NeedSort = false;
    	  Sprites = new ArrayList<Sprite>(0);
    	  FSurface = _Surface;
    	  NeedRedrawBackground = false;
    }
  }