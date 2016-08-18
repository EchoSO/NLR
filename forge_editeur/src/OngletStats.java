import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Collections;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

//TODO @ALPHA2 : lire+commenter
class OngletStats extends JPanel
{
	private static final long serialVersionUID = 1L;
	FenetreSimple parent;
	DefaultListModel Statmodel,Classemodel,XPmodel;
	JScrollPane scrollpanexp;
	public JList StatBase,ClasseList,XPList;
	public OngletStats(FenetreSimple p)
	{
	    setLayout(new BorderLayout());
		parent=p;
		TitledBorder title = BorderFactory.createTitledBorder("Statistiques de base");
	    JPanel panebase=new JPanel();
		panebase.setBorder(title);
		panebase.setLayout(null);
		Statmodel= new DefaultListModel();
		StatBase=new JList(Statmodel);
		JScrollPane scrollpanestatbase = new JScrollPane(StatBase);
		scrollpanestatbase.setBounds(new Rectangle(10,20,190,220));
		panebase.add(scrollpanestatbase);
		JButton Bt_AjouteStatBase=new JButton("Ajouter une statistique");
		Bt_AjouteStatBase.setBounds(new Rectangle(205,20,240,20));
	    Bt_AjouteStatBase.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				String NomStat = JOptionPane.showInputDialog(null, "Entrez le nom de la statistique", 
						"", 1);
				if (NomStat!=null)
				{
					if (NomStat.compareTo("")!=0)
					{
						Statmodel.add(StatBase.getModel().getSize(),NomStat);
						parent.general.getStatsBase().add(NomStat);
						for(int i=0;i<parent.general.getClassesJoueur().size();i++)
						{
							parent.general.getClassesJoueur().get(i).StatsMin.add(0);
							parent.general.getClassesJoueur().get(i).StatsMax.add(0);
						}
						for (int i=0;i<parent.general.getObjets().size();i++)
						{
							parent.general.getObjets().get(i).Stats.add(0);
							parent.general.getObjets().get(i).StatsMin.add(0);
						}
					}
				}
			}
		});
		panebase.add(Bt_AjouteStatBase);
		JButton Bt_RetireStatBase=new JButton("Retirer une statistique");
		Bt_RetireStatBase.setBounds(new Rectangle(205,45,240,20));
	    Bt_RetireStatBase.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				if (StatBase.getSelectedIndex()>=0)
				{
			    	if (JOptionPane.showConfirmDialog(null,
			                "Etes vous sûr de vouloir effacer cette statistique?",
			                "Effacer",
			                JOptionPane.YES_NO_OPTION,
			                JOptionPane.WARNING_MESSAGE)==JOptionPane.YES_OPTION)
			    	{
						parent.general.getStatsBase().remove(StatBase.getSelectedIndex());
						for(int i=0;i<parent.general.getClassesJoueur().size();i++)
						{
							parent.general.getClassesJoueur().get(i).StatsMin.remove(StatBase.getSelectedIndex());
							parent.general.getClassesJoueur().get(i).StatsMax.remove(StatBase.getSelectedIndex());
						}
						for (int i=0;i<parent.general.getObjets().size();i++)
						{
							parent.general.getObjets().get(i).Stats.remove(StatBase.getSelectedIndex());
							parent.general.getObjets().get(i).StatsMin.remove(StatBase.getSelectedIndex());
						}
			    		Statmodel.remove(StatBase.getSelectedIndex());
			    	}
				}
			}
		});
		panebase.add(Bt_RetireStatBase);
		JButton Bt_RenommeStatBase=new JButton("Renommer une statistique");
		Bt_RenommeStatBase.setBounds(new Rectangle(205,70,240,20));
	    Bt_RenommeStatBase.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				if (StatBase.getSelectedIndex()>=0)
				{
					String NomStat = (String) JOptionPane.showInputDialog(null, "Entrez le nom de la statistique", 
							"", 1,null,null,Statmodel.get(StatBase.getSelectedIndex()));
					if (NomStat!=null)
					{
						if (NomStat.compareTo("")!=0)
						{
							parent.general.getStatsBase().set(StatBase.getSelectedIndex(),NomStat);
				    		Statmodel.set(StatBase.getSelectedIndex(), NomStat);
						}
					}
				}
			}
		});
		panebase.add(Bt_RenommeStatBase);
		panebase.setPreferredSize(new Dimension(800,250));
		add(panebase,BorderLayout.PAGE_START);
	    JPanel panecourbexp=new JPanel();
	    title = BorderFactory.createTitledBorder("Courbe XP");
	    panecourbexp.setBorder(title);
	    panecourbexp.setLayout(null);
	    panecourbexp.setPreferredSize(new Dimension(800,250));
	    add(panecourbexp);
		XPmodel= new DefaultListModel();
		XPList=new JList(XPmodel);
		scrollpanexp = new JScrollPane(XPList);
		scrollpanexp.setBounds(new Rectangle(10,20,190,220));
		panecourbexp.add(scrollpanexp);
		JButton Bt_AjouteXP=new JButton("Ajouter une valeur");
		Bt_AjouteXP.setBounds(new Rectangle(205,20,240,20));
		Bt_AjouteXP.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				String Valeur = JOptionPane.showInputDialog(null, "Entrez la valeur pour lvl up", 
						"", 1);
				if (Valeur!=null)
				{
					if (Valeur.compareTo("")!=0)
					{
						parent.general.getCourbeXP().add(Integer.parseInt(Valeur));
						Collections.sort(parent.general.getCourbeXP());
						MetAjourListeCourbeXP();
					}
				}
			}
		});
		panecourbexp.add(Bt_AjouteXP);
		JButton Bt_RetireXP=new JButton("Retirer une valeur");
		Bt_RetireXP.setBounds(new Rectangle(205,45,240,20));
		Bt_RetireXP.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				if (XPList.getSelectedIndex()>=0)
				{
			    	if (JOptionPane.showConfirmDialog(null,
			                "Etes vous sûr de vouloir effacer cette valeur de lvl up?",
			                "Effacer",
			                JOptionPane.YES_NO_OPTION,
			                JOptionPane.WARNING_MESSAGE)==JOptionPane.YES_OPTION)
			    	{
						parent.general.getCourbeXP().remove(XPList.getSelectedIndex());
			    		XPmodel.remove(XPList.getSelectedIndex());
			    	}
				}
			}
		});
		panecourbexp.add(Bt_RetireXP);
		ComponentAdapter listener = new ComponentAdapter() {
	  		public void componentResized(ComponentEvent evt) {
	  	        Component c = (Component) evt.getSource();	
	  	        Dimension newSize = c.getSize();
	  	        scrollpanexp.setBounds(new Rectangle(10,20,190,newSize.height-365));
	          }
	  	};
    	parent.addComponentListener(listener);	  
	}
	public void MetAjourListeCourbeXP()
	{
		XPmodel.clear();
		for(int i=0;i<parent.general.getCourbeXP().size();i++)
		{
			XPmodel.add(i,Integer.toString(parent.general.getCourbeXP().get(i)));
		}
	}
}
