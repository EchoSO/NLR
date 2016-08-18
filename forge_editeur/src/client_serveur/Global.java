package client_serveur;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import client_serveur.Game.PosXY;

public class Global 
{
	public static String pseudo,mdp,ip,key;
	public static int res;
	public static boolean full;
	public static Socket socket;
	public static PrintWriter out;
	public static BufferedReader in;
	public static String recu;
	public static Game game;
	public static ObjectInputStream ois;
	public static ObjectOutputStream oos;
	public static boolean lock=true;
	public static boolean lockR=true;
	public static boolean connected=false;
	public static boolean typing=false;
	public static String tMsg="";
	public static char keyT='a';
	public static String tMsgFinal="";
	public static long timerMsg=0;
	public static boolean dummy=false;
	public static FontMetrics fm=null;
	public static FontMetrics fmP=null;
	public static String[] talk=new String[4];
	public static int strWCentre=0;
	public static int pseudoCentre=0;
	public static int xPs,yPs;
	public static int xMs,yMs,sizMs;
	public static LinkedList<String> Msgs= new LinkedList<>();
	public static boolean affPse=true;
	
/**################################################################**/
	public static void connect()
/**################################################################
 * Etablit la connexion au serveur**/
	{
		try 
		{
			socket = new Socket(ip, 4001);
			socket.setSoTimeout(3000);
			out = new PrintWriter(socket.getOutputStream(),true);//Définit les I/O
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			ois = new ObjectInputStream(socket.getInputStream());  
			//oos= new ObjectOutputStream(socket.getOutputStream());
			recu="";
		} catch (IOException e) 
		{
			message("Fatal error 1D. "+e.getMessage());
			System.exit(1);
		}
	}
	
	
/**################################################################**/
	public static void message(String msg)
/**################################################################
 * Affiche le msg dans une fenêtre**/
	{
		msg=msg.replace("[~]", "\n");
		JOptionPane.showMessageDialog(null, msg);
	}
	
/**################################################################**/
	public static void envoi(String msg)
/**################################################################
 * Envoie msg au serveur**/
	{
		out.println(msg);
	}
	
/**################################################################**/
	public static String rec() 
/**################################################################
 * Renvoie la réponse du serveur**/
	{
		String tmp="";
		try 
		{
			tmp=in.readLine();
			if(tmp==null) throw new IOException("Connection closed.\nCheck your connection or try again later");
			if(!Charset.defaultCharset().displayName().toLowerCase().contains("utf"))
				return new String(tmp.getBytes(Charset.defaultCharset()),"UTF-8");
			return tmp;
		} catch (IOException e) 
		{
			if(Global.connected)
				message("Fatal error 1C. "+e.getMessage());
			System.exit(1);
			return null;
		}
	}
	
/**################################################################**/
	public static void rec0() 
/**################################################################
 * Met la réponse dans recu**/
	{
		String tmp="";
		try 
		{
			tmp=in.readLine();
			if(tmp==null) throw new IOException("Connection closed.\nCheck your connection or try again later");
			if(!Charset.defaultCharset().displayName().toLowerCase().contains("utf"))
				recu=new String(tmp.getBytes(Charset.defaultCharset()),"UTF-8");
			else recu=tmp;
		} catch (IOException e) 
		{
			message("Fatal error 1B. "+e.getMessage());
			System.exit(1);
		}
	}
	
/**################################################################**/
	public static Object recOb() 
/**################################################################
 * Met la réponse dans recu**/
	{
		Object tmp="";
		try 
		{
			tmp=ois.readObject();
			if(tmp==null) throw new IOException("Connection closed.\nCheck your connection or try again later");
			return tmp;
		} catch (IOException | ClassNotFoundException e) 
		{
			message("Fatal error 1A. "+e.getLocalizedMessage());
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}
	
	public static void startTyping()
	{
		typing = true;
		tMsg="";
		Global.fm=null;
	}
	
	public static void stopTyping()
	{
		typing=false;
		tMsgFinal=tMsg;
		tMsg="";
		timerMsg=System.currentTimeMillis() + 5000;
		Global.talk[0]=null;
		/*if(tMsgFinal.length()>0)
		{
			Msgs.add("t|"+tMsgFinal+"\n");
		}*/
	}
	
	public static void lock()
	{
		Global.lock=true;
		Global.lockR=true;
		Global.envoi("lock");
		if(dummy) Global.envoi("dummy");
	}
	public static void unlock()
	{
		Global.envoi("unlock");
		Global.lock=false;
		Global.lockR=false;	
	}
	
	public static String toucher()
	{
		Global.envoi("a:");
		String rep=Global.rec();
		return rep;
	}
	
}
