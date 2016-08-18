package client_serveur;

//TODO @ALPHA2 : lire+commenter
public class calcul
{
	public byte Err,PosErr;
	char[] Op=new char[] {'*','/','-','+'};
	char[] Chiffre=new char[] {'0','1','2','3','4','5','6','7','8','9','.'};
	byte[] Poids=new byte[] {1, 1 , 2 , 2 };
    int I,N,M,tmp;

    public calcul()
	{
		
	}
	public float Calcule(String Buf)
	{
		
	  Buf=Buf.replaceAll(" ", "");
	  Err=0;
	  return Valeur(Buf,0,Buf.length()-1);
	}
	private int InArray(char[] tableau,char tofind)
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
    private float Valeur(String SBuf,int D,int F)
    {
      char[] Buf=SBuf.toCharArray();
      int P=0;
      float R=-1;
      if (D>F) { Err=5; PosErr=(byte) D; return -1; }
      M=0;
      I=F;
      do
      {
       switch(Buf[I])
       {
         case ')': 
               N=1;
               do
               {
                 I--;
                 switch(Buf[I])
                 {
                   case ')': N++;
                   case '(': N--;
                 }
               }
               while (!((N==0) || (I==D)));
               if (N>0) { Err=2; PosErr=(byte) D; return -1; }
               break;
         case '(': Err=1; PosErr=(byte) F; return -1;
         default :
      	   	   if ( (tmp=InArray(Op,Buf[I])) >=0)
      	   		   N=Poids[tmp];
        	   else
        		   N=0;
               if (N>M) { M=N; P=(byte) I; }
       }
       I--;
      }
      while (I>=D);

      if (M>0)
      {                    //On a trouvé un opérateur !
         R=Valeur(SBuf,P+1,F);
         if (Err>0) return -1;
         switch(Buf[P])
         {
           case '*':R=Valeur(SBuf,D,P-1)*R; break;
           case '/': if (R!=0) R=Valeur(SBuf,D,P-1)/R;
                     else { Err=4; PosErr=(byte) P; return -1; }
                     break;
           case '+':R=Valeur(SBuf,D,P-1)+R; break;
           case '-': if (D==P) R=-R; else R=Valeur(SBuf,D,P-1)-R; break;
         }
      }
      else                       //Pas d'opérateur => valeur
      if (Buf[D]=='(')
         if (Buf[F]==')')
           R=Valeur(SBuf, D+1, F-1);
         else
         { Err=3; PosErr=(byte) F; }
       else
       {
           for(P=(byte) D;P<=F;P++)
        	 if (InArray(Chiffre, Buf[P])==-1)
        	 { Err=3; PosErr=(byte) P; return -1; }
           R=Float.parseFloat(SBuf.substring(D,F+1));
           //Val(Copy(Buf,D,F-D+1),R,N)
       }
       if (Err>0) return -1;
       return R;
    }
}
