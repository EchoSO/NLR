import java.net.ServerSocket;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;


public class LauncherThread implements Runnable 
{
	  
	public void run() 
	{
		 
		try 
		{
		      Integer port = new Integer(4002);
		      //Crée serv
		      
		      ServerSocket ss = new ServerSocket(port);
		      
		      System.out.println("Ecoute Launcher sur le port " + port+"\n---------------");
		      for(;;)
		      {//Attente en boucle de connexion
		        new Client_Launcher(ss.accept()); //Si un client se connecte, un nouveau thread Client_Launcher est lancé !
		         System.out.print(">>>");
		      }
		}
		catch (Exception e) {System.err.println("Crash serveur(Launcher): "+e.getMessage()); e.printStackTrace();}
		
	}
}