package client_serveur;

public class Util 
{

/**################################################################**/
	public static String EscapeChar(String Input)
/**################################################################
 * Enlève les carac spéciaux de "Input". Garde A-Z,0-9**/
	{
  	      char[] NoSpecialChar = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'
	              ,'0','1','2','3','4','5','6','7','8','9'};
		  if (Input!=null)
		  {
			  char[] nom=Input.toUpperCase().toCharArray();
			  String Result="";
			  for(int i=0;i<nom.length;i++)
			  {//On vérif si chaque carac est dans l'alphabet/numéro
				  if (InArray(NoSpecialChar,nom[i])>=0)
					  Result+=nom[i];
			  }
			  return Result;//Renvoie en majuscules
		  }
		  else
			  return "";
	}
	
/**################################################################**/ 
	public static int InArray(char[] tableau,char tofind)
/**################################################################
 * Cherche le caractère tofind dans le tableau et renvoie sa position, sinon -1**/
	{
		int i=0;
		while (i<tableau.length)
		{
			if (tableau[i]==tofind)
				return i;
			i++;
		}
		return -1;
	}
	
	
/**################################################################**/
	public static int random(int max)
/**################################################################
 * Renvoie un nombre aléatoire entre 0 et max**/
	{
		return (int) (Math.random() * max);
	}
	
/**################################################################**/
	public static int PosExterne(char Char,char Char2,char[] Chaine)
/**################################################################
 * Renvoie la position du caractère de fermeture "]"(char2) des Variable[] ou autres 
 * ex: Char='[', Char2=']', Chaine="varTest]".toCharArray()**/
	{
	  int i,indice;
	  indice=1; i=0;
	  while ((indice>0) && (i<Chaine.length))
	  {//On parcoure à la recherche de la fermeture principale
	    if (Chaine[i]==Char)
	      indice++;//Si ouverture, incrémente
	    else
	    if (Chaine[i]==Char2)
	      indice--;//Si fermeture, décrémente
	    i++;
	  }
	  if (indice==0)
	    return i;//Si y'a une fermeture, renvoie sa position
	  else
	    return 0;//Sinon, erreur, 0
	}
	
	
	
}
