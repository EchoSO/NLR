import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;


public class ChatClient implements Runnable 
{
	
  private Thread thread;
  private Socket socket;
  private PrintWriter fluxdesortie;
  private BufferedReader fluxdentree;
  private int numeroclient = 0; 
  private int LoginTry=0;
  private String Name="";

  public ChatClient(Socket s, int numclient)
  {
	numeroclient=numclient;
    socket = s;
    
    try
    {
      //initialise les socket d'entrée et de sortie
      fluxdesortie = new PrintWriter(socket.getOutputStream());
      fluxdentree = new BufferedReader(new InputStreamReader(socket.getInputStream()));
     // numeroclient = Main.serveur.ajouterclient(fluxdesortie,Main.Client);
    }
    catch (IOException e){}

    thread = new Thread(this);
    thread.start();
  }

  public void run()
  {

	String ipclient = socket.getInetAddress().toString();
	  
    System.out.println("Connexion chat d'un client en provenance de" + ipclient.replace('/', ' '));
    
    try{
    	
     // char charCur[] = new char[1];
	  String message = "";

      while(Name.equals("") && (message = fluxdentree.readLine()) != null  )
      {
    	  
    	  //message = message + charCur[0];
    	  System.out.println(message);
    	  //System.out.print(Arrays.toString(message.split("\\|")));
    	  	if(message.split("\\|").length == 3 && message.split("\\|")[0].equals("checkpass"))
    	  	{// checkpass|pseudo|pass
    	  		LoginTry++;
    	  		if(LoginTry >3){Main.serveur.envoyer("statmsg:Too many attempts\n",numeroclient);socket.close();}
    	  		
    	  		
    	  		System.out.println("Check password...");
    	  		String login = message.split("\\|")[1].trim();
    	  		String password = Engine64.crypt(Main.md5(message.split("\\|")[2].trim()));
    	  		
    	  		File LoginFile= new File("Players" + File.separator + Main.md5(login.toUpperCase()) + ".connect");
    	  		if(LoginFile.exists() && LoginFile.canRead() && LoginFile.isFile())
    	  		{
    	  			System.out.println(LoginFile.toString() + " pour le joueur " + login + " existe");
    	  			
    	  			
	  				String[] Value = Main.ReadLoginFile(LoginFile.toString());
	  				if(Value[0].equals(password) && Value[1].equals(login.toUpperCase()))
	  				{
	  					Name=login;
    	  				Main.serveur.envoyer("statmsg:Connecté.\n", numeroclient);
	  				}
	  				else
	  				{
	  					Main.serveur.envoyer("statmsg:Bad password: " + Value[1] + ":" + Value[0] + "\n", numeroclient);
	  				}
    	  		}	
	  			
    	  		else
    	  		{
    	  			System.out.println(LoginFile.toString() + " pour le joueur " + login + " n'existe pas");
    	  			Main.serveur.envoyer("statmsg:No player '" + login + "' on server. Create a new account.\n", numeroclient);
    	  		}
	    		//Main.serveur.envoyer(paquet.envoie(), numeroclient);
	    		//message = "";  
    	  	}
    	  	else if(message.split("\\|").length == 3 && message.split("\\|")[0].equals("new"))
    	  	{//  new|pseudo|pass
    	  		System.out.println("Creation compte : " + message);
    	  		String login = message.split("\\|")[1].trim();
    	  		String passNoCrypt = message.split("\\|")[2];
    	  		String password = Engine64.crypt(Main.md5(message.split("\\|")[2].trim()));
    	  		File LoginFile= new File("Players" + File.separator + Main.md5(login.toUpperCase()) + ".connect");
    	  		if(LoginFile.exists() && LoginFile.canRead() && LoginFile.isFile())
    	  		{
    	  			System.out.println(LoginFile.toString() + " déjà existant. Erreur création " + login);
    	  			Main.serveur.envoyer("statmsg:Account already exists. Please choose another name.\n",numeroclient);
    	  		}
    	  		else
    	  		{
    	  			System.out.println(LoginFile.toString() + " pour le joueur " + login + " n'existe pas => OK");
    	  			Main.serveur.envoyer("statmsg:Creating account " + login + "....OK!\n", numeroclient);
    	  			Main.CreateAccount(login.toUpperCase(),password,LoginFile.toString(), LoginFile.toString().replace(".connect",".key"));
    	  			Main.serveur.envoyer("statmsg:Account created\n", numeroclient);
    	  		}
    	  	}
      }
      
      while((message = fluxdentree.readLine()) != null)
      {
    	  	if(message.equals("/exit"))
  	  		{
    	  		socket.close();
  	  		}
    	  	else
    	  	{
    	  		Main.serveur.envoieglobal("chatext:"+Name + ":" + message + "\n");
    	  	}
      }
      
      
      
    }
    catch (Exception e){}
    finally {
      try {
    	  Main.serveur.supprimerclient(numeroclient);
    	  socket.close();
      }
      catch (IOException e){}
    }
  }
  
  
  
  
}