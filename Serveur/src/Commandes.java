import java.io.BufferedReader;
//import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;

class Commandes implements Runnable {
	
  private Main _main;
  private BufferedReader fluxdentree;
  private String commande = "";
  private Thread thread;

  public Commandes(Main main){
	  
    _main = main; 
    fluxdentree = new BufferedReader(new InputStreamReader(System.in));
    thread = new Thread(this);
    thread.start(); 
  }

  public void run(){
    try{
    	
      while ((commande = fluxdentree.readLine()) != null)
      {
    	  
        if (commande.equals("/fermer"))
          System.exit(0);
        else if(commande.equals("/count"))        	
          System.out.println("Nombre de joueurs connectés : " + _main.getnombreclients());
        else if(commande.equals("/reboot"))
        {
        	//TODO reboot serv (start "reboot.class" (wait 5 s, reboot "java -jar Editeur.jar"))
        }
        /*else if(commande.equals("/menu")){
        	
        	System.out.println("  --Menu du serveur--\n");    
            System.out.println("[1] Discussions");
            System.out.println("[2] Joueurs");
            System.out.println("[3] Panneau de controle");
            System.out.println("[4] Aide");
            System.out.println("[5] Divers\n");
        }*/
        else if(commande.equals("/ipserveur"))
        {
        	 try {
        		   java.net.InetAddress ip = java.net.InetAddress.getLocalHost();
        		   System.out.println(ip.getHostAddress());
        		   }
        	catch(Exception e){e.printStackTrace();}      
        }
    	else if(commande.equals("/ipclients")){
    		for(int i = 0; i<Main.clients2.size(); i++)
    		{
    			ClientThread ct=Main.clients2.get(i);
    			if (ct.socket!=null && ct.socket.isConnected() && !ct.socket.isClosed())
    				System.out.println(ct.Pseudo + " : "+ct.ipclient);
    		}
    	}
        else
        	System.out.println("Cette commande n'existe pas !");
        
        System.out.flush();

      }
    }
    catch (IOException e) {}
  }
}
