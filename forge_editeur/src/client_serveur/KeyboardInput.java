package client_serveur;

//TODO @ALPHA2 : lire+commenter
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyboardInput implements KeyListener {
	
  private static final int KEY_COUNT = 256;
  
  private boolean inputtext;
  private String Texte;
	
  enum KeyState {
    RELEASED, // Not down
    PRESSED,  // Down, but not the first time
    ONCE      // Down for the first time
  }
	
  // Current state of the keyboard
  private boolean[] currentKeys = null;
	
  // Polled keyboard state
  private KeyState[] keys = null;
	
  public KeyboardInput() {
	Texte="";
    currentKeys = new boolean[ KEY_COUNT ];
    keys = new KeyState[ KEY_COUNT ];
    for( int i = 0; i < KEY_COUNT; ++i ) {
      keys[ i ] = KeyState.RELEASED;
    }
  }
	
  public synchronized KeyState[] poll() {
    for( int i = 0; i < KEY_COUNT; ++i ) {
      // Set the key state 
      if( currentKeys[ i ] ) {
        // If the key is down now, but was not
        // down last frame, set it to ONCE,
        // otherwise, set it to PRESSED
        if( keys[ i ] == KeyState.RELEASED )
          keys[ i ] = KeyState.ONCE;
        else
          keys[ i ] = KeyState.PRESSED;
      } else {
        keys[ i ] = KeyState.RELEASED;
      }
    }
    return keys;
  }
	
  public boolean keyDown( int keyCode ) {
    return keys[ keyCode ] == KeyState.ONCE ||
           keys[ keyCode ] == KeyState.PRESSED;
  }
	
  public boolean keyDownOnce( int keyCode ) {
    return keys[ keyCode ] == KeyState.ONCE;
  }
	
  public synchronized void keyPressed( KeyEvent e ) {
    int keyCode = e.getKeyCode();
    if( keyCode >= 0 && keyCode < KEY_COUNT ) {
      currentKeys[ keyCode ] = true;
    }
  }

  public synchronized void keyReleased( KeyEvent e ) {
    int keyCode = e.getKeyCode();
    if( keyCode >= 0 && keyCode < KEY_COUNT ) {
      currentKeys[ keyCode ] = false;
    }
  }

  public synchronized void setIsInputText(boolean isInputText)
  {
	  inputtext=isInputText;
	  Texte="";
  }
  
  public synchronized String getText()
  {
	  return Texte;
  }

  public void keyTyped( KeyEvent e ) {
    if (inputtext)
    {    	
    	char key=e.getKeyChar();
    	if (key==8)
    	{
    		if (Texte.length()>0)
    			Texte=Texte.substring(0,Texte.length()-1);
    	}
    	else
    	if ((key==127)||(key==10))
    		return;
    	else
    		Texte+=key;
    }
  }
}
