import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.Vector;


public class Chat implements Runnable {
	private Vector<PrintWriter> clients = new Vector<PrintWriter>();
	
	public Chat()
	{
	
	  }     
	public void run() 
	{
		
		try {
		      Integer port;
		      
		    	  port = new Integer(4002);
		
		
		      ServerSocket ss = new ServerSocket(port.intValue());
		      
		      System.out.println("Ecoute Chat sur le port " + port);
		      System.out.println("Serveur operationnel !\n---------------");
		      for(int i=10000;true;i++)
		      {//Attente en boucle de connexion
		        new ChatClient(ss.accept(), i); //Si un client se connecte, un nouveau thread ClientThread est lanc� !
		         System.out.print(">>>");
		      }
		    }
		    catch (Exception e) {}
		
	}
}