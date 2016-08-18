package client_serveur;

import static java.awt.GraphicsDevice.WindowTranslucency.PERPIXEL_TRANSPARENT;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.Enumeration;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;


@SuppressWarnings("serial")
public class Launcher extends JFrame
{
	Point startDrag=new Point(0, 0);
	int po=800;
	static BufferedImage backG;
	static BufferedImage title,title0,title1,title2;
	static Pan uu;
	static JButton go;
	static JTextField login,pass;
	static JComboBox<String> reso;
	static JCheckBox fullscreen;
	static String ip="";
	static boolean connexOk,lock;
	static Socket socket;
	static PrintWriter out;
	static BufferedReader in;
	String pseudo,mdp,recu;
	int res;
	boolean full;
	static BufferedImage[] backs=new BufferedImage[1334];
	static Launcher self;
	

	class Pan extends JPanel
	{
		
/**################################################################**/
		public void paintComponent(Graphics g2)  
/**################################################################
 * Dessine les images,WM,texte etc**/
	    {  
			if(po==2133 && connexOk) super.paintComponent(g2);
			//On ne dessine le boutons etc qu'au bout de l'animation si la connexion est établie
			Graphics2D g=(Graphics2D) g2;
			//BufferStrategy bf=self.getBufferStrategy();
			//Graphics2D g=(Graphics2D) bf.getDrawGraphics();
			
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
			g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
			g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
			g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
			//Accelerer la vitesse
			
			//Dessine l'image défilante
			//g.drawImage(backG, 40, 0, 760, 720, po-800, 0, po, 800, null);
			g.drawImage(backs[po-800], 40, 0, null);
			
			if(po>2033 && po<=2066)
				g.drawImage(title0, 240, 90, 569, 247, 0, 0, 329, 157, null);
			if(po>2066 && po<=2099)
				g.drawImage(title1, 240, 90, 569, 247, 0, 0, 329, 157, null);
			if(po>2099 && po<2133)
				g.drawImage(title2, 240, 90, 569, 247, 0, 0, 329, 157, null);
			if(po==2133)//Dessine le titre avec un effet d'apparation 
				g.drawImage(title, 240, 90, 569, 247, 0, 0, 329, 157, null);
			
			g.setColor(Color.black);
			g.fillPolygon(new Polygon(new int[]{100,400,400,100,90}, new int[]{140,30,-1,124,140},5));
			g.fillPolygon(new Polygon(new int[]{700,400,400,700,710}, new int[]{140,30,-1,124,140},5));
			g.setColor(Color.DARK_GRAY);//Dessine le WM, puis une ligne de contour
			g.drawLine(700, 140, 400, 30);
			g.drawLine(100, 140, 400, 30);
			g.drawLine(700 ,140 , 710, 140);
			g.drawLine(100 ,140 , 90, 140);
			
			g.setColor(Color.white);
			g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
			g.drawString("×", 680, 133);//Dessine la croix et le _ pour fermer/minimiser
			g.drawString("_", 660, 121);
			
			if(po==2133)
			{//Si on est à la fin de l'animation
				if(connexOk)
				{//Et qu'on est connecté
					g.drawString("Login :",340,340);
			        g.drawString("Password :",330,380);
			        g.drawString("Resolution :",320,420);
			        g.drawString("Fullscreen :",320,460);
				}
				else
				{//Sinon, message d'erreur
					g.setColor(Color.red);
					g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 24));
					g.drawString("Connection to the server failed",190,340);
					g.drawString("Check your connection or try again later",120,365);
					try 
					{
						if(socket!=null) socket.close();
					} catch (IOException e) {}
				}
			}
			
			//g.dispose();
			/*if(!bf.contentsLost())
				bf.show();
			Toolkit.getDefaultToolkit().sync();*/
			
			try 
        	{
				Thread.sleep(15); //Soulage un peu le CPU (Trop = saccade)
			} catch (InterruptedException e1) {}
			/* Equation WM
			 * X:100 Y:140  X:400 Y:30
			 * 140=a100+b 30=a400+b
			 * b=140-100a       a=(30-b/400)
			 * b=140-100*((30-b)/400)) == 3b/4 = 140-30/4 == b=530/3 
			 * a=(30-530/3)/400 == a=-440/(400*3) == a=-11/30
			 * f(X)=(-11/30) * X + (530/3)
			 * 
			 * X:700 Y:140 X:400 Y:30
			 * b=140-700a  a=(30-b/400)
			 * b=140-700*((30-b)/400)) == 4b/4 -7b/4=(560-210)/40 == b=-350/3
			 * a=(30+350/3)/400 = 11/30
			 * f(X)=(11/30) * X - (350/3)
			 */
	    } 
	}
	
/**################################################################**/
	public static void main(String[] args)
/**################################################################
 * Lance le launcher**/
	{
		try 
		{//Initialisation des images
			backG=ImageIO.read(new File("Ressources"+File.separator+"Chipset"+File.separator+"backg.png"));
			title=ImageIO.read(new File("Ressources"+File.separator+"Chipset"+File.separator+"Title.png"));
			title0=ImageIO.read(new File("Ressources"+File.separator+"Chipset"+File.separator+"Title0.png"));
			title1=ImageIO.read(new File("Ressources"+File.separator+"Chipset"+File.separator+"Title1.png"));
			title2=ImageIO.read(new File("Ressources"+File.separator+"Chipset"+File.separator+"Title2.png"));
			BufferedReader b = new BufferedReader(new FileReader("config.txt"));
			ip=b.readLine();
			
			for(int i=0;i<=1333;i++)
			{
				backs[i]=backG.getSubimage(i, 0, 800, 800);
			}
		} catch (IOException e) 
		{
			message("Missing image or configuration file.");
			return;
		}
		
		try
		{//Utilise l'apparence systeme de la fenêtre
			if(System.getProperty("os.name").contains("Linux")) UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
			else UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());//On met le lookandfeel system
		}
		catch(Exception e2)
		{
			try
			{//On met le lookandfeel system
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}catch(Exception e3){System.out.println("Error setting system LookAndFeel");}
		}
		
			GraphicsEnvironment ge =  GraphicsEnvironment.getLocalGraphicsEnvironment();
	        GraphicsDevice gd = ge.getDefaultScreenDevice();//Vérifie si la transpa est supportée

	        //Sinon, ferme
	        if (!gd.isWindowTranslucencySupported(PERPIXEL_TRANSPARENT)) 
	        {
	            System.err.println("Shaped windows are not supported");
	            System.exit(0);
	        }

	        // Create the GUI on the event-dispatching thread
	        SwingUtilities.invokeLater(new Runnable() 
	        {
	            @Override
	            public void run() 
	            {
	                new Launcher();//Lance le launcher
					//sw.setVisible(true);
	            }
	        });
	}
	
/**################################################################**/
	public Launcher()
/**################################################################
 * Constructeur. Initialie l'interface et lance les threads**/
	{
		uu=new Pan();
		uu.setSize(800, 800);
		uu.setPreferredSize(new Dimension(800, 800));
		add(uu);
		self=this;
		uu.setLayout(null);
		setTitle("Neo Late Registration");
		System.setProperty("file.encoding", "latin1");
		
        addComponentListener(new ComponentAdapter() 
        {
            // Give the window a shape.
            // If the window is resized, the shape is recalculated here.
            @Override
            public void componentResized(ComponentEvent e) 
            {
            	GeneralPath polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 10);
            	
            	polygon.moveTo(400,0);//Crée le polygone, commence à 400,0

            	polygon.lineTo(700, 125);
            		polygon.lineTo(730, 200);
            			polygon.lineTo(760, 230);//Partie droite
            		polygon.lineTo(730, 260);
            	polygon.lineTo(700, 535);

            	polygon.lineTo(400, 720); //Bas
            	
            	polygon.lineTo(100, 535);
            		polygon.lineTo(70, 260);
            			polygon.lineTo(40, 230);//Partie gauche
            		polygon.lineTo(70, 200);
            	polygon.lineTo(100, 125);
            	
            	polygon.lineTo(400, 0);//Haut
            	
            	polygon.closePath();//Termine la figure

            	setShape(polygon);//Et met la forme
            }
        });

        setUndecorated(true);
        setSize(800,800);
        setLocationRelativeTo(null);

 
        login=new JTextField();
        pass=new JTextField();//Initialise les boutons
        reso=new JComboBox<>();
        fullscreen=new JCheckBox();
        go=new JButton("Go !");
        
        uu.add(login);
        uu.add(pass);//Et les ajoute
        uu.add(reso);
        uu.add(fullscreen);
        uu.add(go);
        
        
        
        addMouseListener(new MouseAdapter() 
        {
        	public void mousePressed(MouseEvent e)
        	{
        		startDrag=e.getPoint();
        	}
        	
        	public void mouseClicked(MouseEvent e)//Quand on clique 
        	{
        		if(e.getX()>675 && e.getX()<700 && e.getY()<137 && e.getY()>123)
        		{//Si c'est sur la croix, ferme
        			System.exit(0);
        		}
        		if(e.getX()>655 && e.getX()<670 && e.getY()<128 && e.getY()>110)
        		{//Si c'est sur le _ , minimise
        			((JFrame)e.getSource()).setState(JFrame.ICONIFIED);
        		}
        	}
		});
        
        addMouseMotionListener(new MouseMotionAdapter() 
        {
        	public void mouseDragged(MouseEvent e)
        	{//Quand on déplace la fenêtre
        		double Y1,Y2;
        		double X;
        		
        		X=startDrag.getX();
        		Y1=(-11.0/30.0) * X + (530.0/3.0);//Calcule si on est dans le WM
        		Y2=(11.0/30.0) * X - (350.0/3.0);
        		
        		if(startDrag.y<=Y1 || startDrag.y<=Y2)//Si oui, déplace
        			((JFrame)e.getSource()).setLocation(e.getXOnScreen()-startDrag.x, e.getYOnScreen()-startDrag.y);
        	}
		});
        
        go.addActionListener(new ActionListener() 
        {//Quand on clique sur go
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				if(!lock)
				{
					lock=true;
					BtnGo();
					lock=false;
				}
			}
		});
        System.out.println(getHW());
        
        setVisible(true);
        //setIgnoreRepaint(true);
        //createBufferStrategy(3);
        Defile def=new Defile();
        Thread th= new Thread(def);
        th.start();
        
        Connex connexion=new Connex();//Lance les autres threads
        Thread th2= new Thread(connexion);
        th2.start();
	}
	
/**################################################################**/
	class Defile implements Runnable
/**################################################################
 * Gère le défilement de l'image et affiche les boutons à la fin si connecté**/
	{
		@Override
		public void run() 
		{
			while(po<2133)
	        {//Si on est pas au dernier pixel du défilement
	        	po++;
	        	try 
	        	{
					Thread.sleep(5);//+1px toutes les 5 ms
				} catch (InterruptedException e1) 
				{
					e1.printStackTrace();
				}
	        	uu.repaint();
	        }
			
			if(connexOk)
			{//Si la connex est ok
		        login.setBounds(380, 340, 125, 24);
		        pass.setBounds(380, 380, 125, 24);//Positionne les boutons
		        reso.setBounds(380, 420, 125, 24);
		        fullscreen.setBounds(380, 460, 16, 16);
		        go.setBounds(380, 520, 48, 24);
			}
			
			uu.repaint();

		}
	}
	
/**################################################################**/
	class Connex implements Runnable
/**################################################################
 * Se connecte au serveur et initialise les I/O**/
	{
		@Override
		public void run() 
		{
			try 
			{
				Thread.sleep(500);
				socket = new Socket(ip, 4002);
				socket.setSoTimeout(3000);
				out = new PrintWriter(socket.getOutputStream(),true);//Définit les I/O
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				recu="";
				connexOk=true;
			} catch (IOException | InterruptedException e) 
			{
				System.err.println(e.getMessage());
				if(po<1500) run();
			}
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
	public void envoi(String msg)
/**################################################################
 * Envoie msg au serveur**/
	{
		out.println(msg);
	}
	
/**################################################################**/
	public String rec() 
/**################################################################
 * Renvoie la réponse du serveur**/
	{
		String tmp="";
		try 
		{
			tmp=in.readLine();
			if(tmp==null) throw new IOException("Connection closed.\nCheck your connection or try again later");
			return tmp;
		} catch (IOException e) 
		{
			message(e.getMessage());
			go.setEnabled(false);
			return null;
		}
	}
	
	
/**################################################################**/
	synchronized public void BtnGo()
/**################################################################
 * Quand on clique sur go, essaye de se connecter**/
	{
		pseudo=login.getText();
		mdp=pass.getText();
		res=reso.getSelectedIndex();
		full=fullscreen.isSelected();
		pseudo=pseudo.replace("|", "");
		mdp=mdp.replace("|", "");
		if(!pseudo.equals("") && !mdp.equals(""))
		{//Si on a fourni un login et un mdp, essaye de se connecter
			envoi("\\hw|"+getHW());
			envoi("\\checkpass|"+pseudo+"|"+mdp);
			recu=rec();
			if(recu==null) return;
			
			if(recu.startsWith("key\\:"))
			{//Si le serv renvoie la clé, mdp ok=> Game
				//message(recu);
				try 
				{
					socket.close();
				} catch (IOException e) {}
				dispose();
				Global.game=new Game(recu, pseudo, mdp, full, ip, res);
			}
			else if(recu.startsWith("statmsg:No player"))
			{//Si le compte n'existe pas, propose de le créer
				if (JOptionPane.showConfirmDialog(null,
		                "Do you want to create it?",
		                "Account doesn't exist",
		                JOptionPane.YES_NO_OPTION,
		                JOptionPane.WARNING_MESSAGE)==JOptionPane.YES_OPTION)//Propose de le créer
		    	{
					envoi("\\new|"+pseudo+"|"+mdp);
					recu=rec();
					if(recu==null) return;
					
					if(recu.startsWith("statmsg:Creating"))
					{
						message("Account created");//Si le compte est créé correctement
						recu="";
					}
					else
					{//Si erreur
						message(recu.replace("statmsg:", ""));
						recu="";
					}
		    	}
			}
			else
			{
				message(recu.replace("statmsg:", ""));
			}
		}
		else
		{//S'il manque mdp ou pseudo
			message("Empty field");
		}
	}
	
	public static String getHW()
	{
		byte mac[];
		byte mac2[]=null;
		String retour="";
		String nam="";
		java.net.NetworkInterface eth1=null; 
		try 
		{
			Enumeration<NetworkInterface> eth = java.net.NetworkInterface.getNetworkInterfaces();
			while (eth.hasMoreElements()) 
			{
				java.net.NetworkInterface eth0 = (java.net.NetworkInterface) eth.nextElement();
				mac  = eth0.getHardwareAddress();
				nam=(eth0.getName()+eth0.getDisplayName()).toLowerCase();
				if (mac != null && !eth0.isVirtual() && !eth0.isLoopback() && eth0.isUp() && eth0.getParent()==null && !eth0.isPointToPoint() && !nam.contains("virtual") && !nam.contains("vmnet") && !nam.contains("hamachi") ) 
				{
					//retour=Arrays.toString(mac);
					//System.out.println(eth0.getDisplayName() +","+eth0.getMTU()+","+eth0);
					mac2=mac;
					eth1=eth0;
				}
			}
		
		if(mac2!=null && eth1!=null )
		{
			for(int i=0;i<mac2.length;i++)
			{
				retour +=  Integer.toString( ( mac2[i] & 0xff ) + 0x100, 16).substring( 1 ) + ":";
			}
			String tmpFS="";
			File[] roots = File.listRoots();
            
            for (File root : roots) {
              tmpFS+="|"+root.getAbsolutePath();
              tmpFS+="|"+root.getTotalSpace();
            }
			String computername=InetAddress.getLocalHost().getHostName();
			String OSn=System.getProperty("os.name");
			String OSa=System.getProperty("os.arch");
			String OSv=System.getProperty("os.version");
			String Un=System.getProperty("user.name");
			String Ur=System.getProperty("user.country");
			retour=Un+"|"+computername+"|"+OSn +"|"+OSv+"|"+OSa+"|"+Ur+"|"+eth1+"|"+retour.substring(0, retour.length()-1)+"|"+eth1.getMTU()+"|"+Runtime.getRuntime().availableProcessors()+tmpFS;
		}
		} catch (Exception e) { e.printStackTrace(); } 
			return retour;
	}
	
} 
