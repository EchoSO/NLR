import java.util.Arrays;




/**
 * The specification for RC5 came from the <code>RC5 Encryption Algorithm</code>
 * publication in RSA CryptoBytes, Spring of 1995. 
 * <em>http://www.rsasecurity.com/rsalabs/cryptobytes</em>.
 * <p>
 * This implementation has a word size of 32 bits.
 * <p>
 * Implementation courtesy of Tito Pena.
 */
class Engine
    implements BlockCipher
{
    /*
     * the number of rounds to perform
     */
    private int _noRounds;

    /*
     * the expanded key array of size 2*(rounds + 1)
     */
    private int _S[];

    /*
     * our "magic constants" for 32 32
     *
     * Pw = Odd((e-2) * 2^wordsize)
     * Qw = Odd((o-2) * 2^wordsize)
     *
     * where e is the base of natural logarithms (2.718281828...)
     * and o is the golden ratio (1.61803398...)
     */
    private static final int P32 = 0xb7e15163;
    private static final int Q32 = 0x9e3779b9;

    private boolean forEncryption;

    /**
     * Create an instance of the RC5 encryption algorithm
     * and set some defaults
     */
    public Engine()
    {
        _noRounds     = 12;         // the default
        _S            = null;
    }

    public String getAlgorithmName()
    {
        return "RC5-32";
    }
    
    public static String crypt(String pass)
    {
    	String pseudo = "Ê®⇔−⇘⇓÷⇙⇓÷⇗";
    	RC5Parameters param= new RC5Parameters(pass.getBytes(), 5);
    	Engine crypt = new Engine();
    	crypt.init(true, param);
    	byte[] pseud= pseudo.getBytes();
    	byte[] result=new byte[pseudo.length()];
    	crypt.processBlock(pseud, 0, result, 0);
    	/*System.out.println(Arrays.toString(result));
    	String s="";
    	for(int i=0; i< result.length;i++)
    	{
    		s=s + (char) result[i];
    	}
    	System.out.println(s);*/
    	return Base64.encodeToString(result, false);
    }

    public int getBlockSize()
    {
        return 2 * 4;
    }

    /**
     * initialise a RC5-32 cipher.
     *
     * @param forEncryption whether or not we are for encryption.
     * @param params the parameters required to set up the cipher.
     * @exception IllegalArgumentException if the params argument is
     * inappropriate.
     */
    public void init(
        boolean             forEncryption,
        CipherParameters    params)
    {
        if (params instanceof RC5Parameters)
        {
            RC5Parameters       p = (RC5Parameters)params;

            _noRounds     = p.getRounds();

            setKey(p.getKey());
        }
        else if (params instanceof KeyParameter)
        {
            KeyParameter       p = (KeyParameter)params;

            setKey(p.getKey());
        }
        else
        {
            throw new IllegalArgumentException("invalid parameter passed to RC532 init - " + params.getClass().getName());
        }

        this.forEncryption = forEncryption;
    }

    public int processBlock(
        byte[]  in,
        int     inOff,
        byte[]  out,
        int     outOff)
    {
        return (forEncryption) ? encryptBlock(in, inOff, out, outOff) 
                                    : decryptBlock(in, inOff, out, outOff);
    }

    public void reset()
    {
    }

    /**
     * Re-key the cipher.
     * <p>
     * @param  key  the key to be used
     */
    private void setKey(
        byte[]      key)
    {
        //
        // KEY EXPANSION:
        //
        // There are 3 phases to the key expansion.
        //
        // Phase 1:
        //   Copy the secret key K[0...b-1] into an array L[0..c-1] of
        //   c = ceil(b/u), where u = 32/8 in little-endian order.
        //   In other words, we fill up L using u consecutive key bytes
        //   of K. Any unfilled byte positions in L are zeroed. In the
        //   case that b = c = 0, set c = 1 and L[0] = 0.
        //
        int[]   L = new int[(key.length + (4 - 1)) / 4];

        for (int i = 0; i != key.length; i++)
        {
            L[i / 4] += (key[i] & 0xff) << (8 * (i % 4));
        }

        //
        // Phase 2:
        //   Initialize S to a particular fixed pseudo-random bit pattern
        //   using an arithmetic progression modulo 2^wordsize determined
        //   by the magic numbers, Pw & Qw.
        //
        _S            = new int[2*(_noRounds + 1)];

        _S[0] = P32;
        for (int i=1; i < _S.length; i++)
        {
            _S[i] = (_S[i-1] + Q32);
        }

        //
        // Phase 3:
        //   Mix in the user's secret key in 3 passes over the arrays S & L.
        //   The max of the arrays sizes is used as the loop control
        //
        int iter;

        if (L.length > _S.length)
        {
            iter = 3 * L.length;
        }
        else
        {
            iter = 3 * _S.length;
        }

        int A = 0, B = 0;
        int i = 0, j = 0;

        for (int k = 0; k < iter; k++)
        {
            A = _S[i] = rotateLeft(_S[i] + A + B, 3);
            B =  L[j] = rotateLeft(L[j] + A + B, A+B);
            i = (i+1) % _S.length;
            j = (j+1) %  L.length;
        }
    }

    /**
     * Encrypt the given block starting at the given offset and place
     * the result in the provided buffer starting at the given offset.
     * <p>
     * @param  in     in byte buffer containing data to encrypt
     * @param  inOff  offset into src buffer
     * @param  out     out buffer where encrypted data is written
     * @param  outOff  offset into out buffer
     */
    private int encryptBlock(
        byte[]  in,
        int     inOff,
        byte[]  out,
        int     outOff)
    {
        int A = bytesToWord(in, inOff) + _S[0];
        int B = bytesToWord(in, inOff + 4) + _S[1];

        for (int i = 1; i <= _noRounds; i++)
        {
            A = rotateLeft(A ^ B, B) + _S[2*i];
            B = rotateLeft(B ^ A, A) + _S[2*i+1];
        }
        
        wordToBytes(A, out, outOff);
        wordToBytes(B, out, outOff + 4);
        
        return 2 * 4;
    }

    private int decryptBlock(
        byte[]  in,
        int     inOff,
        byte[]  out,
        int     outOff)
    {
        int A = bytesToWord(in, inOff);
        int B = bytesToWord(in, inOff + 4);

        for (int i = _noRounds; i >= 1; i--)
        {
            B = rotateRight(B - _S[2*i+1], A) ^ A;
            A = rotateRight(A - _S[2*i],   B) ^ B;
        }
        
        wordToBytes(A - _S[0], out, outOff);
        wordToBytes(B - _S[1], out, outOff + 4);
        
        return 2 * 4;
    }

    
    //////////////////////////////////////////////////////////////
    //
    // PRIVATE Helper Methods
    //
    //////////////////////////////////////////////////////////////

    /**
     * Perform a left "spin" of the word. The rotation of the given
     * word <em>x</em> is rotated left by <em>y</em> bits.
     * Only the <em>lg(32)</em> low-order bits of <em>y</em>
     * are used to determine the rotation amount. Here it is 
     * assumed that the wordsize used is a power of 2.
     * <p>
     * @param  x  word to rotate
     * @param  y    number of bits to rotate % 32
     */
    private int rotateLeft(int x, int y)
    {
        return ((x << (y & (32-1))) | (x >>> (32 - (y & (32-1)))));
    }

    /**
     * Perform a right "spin" of the word. The rotation of the given
     * word <em>x</em> is rotated left by <em>y</em> bits.
     * Only the <em>lg(32)</em> low-order bits of <em>y</em>
     * are used to determine the rotation amount. Here it is 
     * assumed that the wordsize used is a power of 2.
     * <p>
     * @param  x  word to rotate
     * @param  y    number of bits to rotate % 32
     */
    private int rotateRight(int x, int y)
    {
        return ((x >>> (y & (32-1))) | (x << (32 - (y & (32-1)))));
    }

    private int bytesToWord(
        byte[]  src,
        int     srcOff)
    {
        return (src[srcOff] & 0xff) | ((src[srcOff + 1] & 0xff) << 8)
            | ((src[srcOff + 2] & 0xff) << 16) | ((src[srcOff + 3] & 0xff) << 24);
    }

    private void wordToBytes(
        int    word,
        byte[]  dst,
        int     dstOff)
    {
        dst[dstOff] = (byte)word;
        dst[dstOff + 1] = (byte)(word >> 8);
        dst[dstOff + 2] = (byte)(word >> 16);
        dst[dstOff + 3] = (byte)(word >> 24);
    }
}


/**RC5 64**/



/**
 * The specification for RC5 came from the <code>RC5 Encryption Algorithm</code>
 * publication in RSA CryptoBytes, Spring of 1995. 
 * <em>http://www.rsasecurity.com/rsalabs/cryptobytes</em>.
 * <p>
 * This implementation is set to work with a 64 bit word size.
 * <p>
 * Implementation courtesy of Tito Pena.
 */
class Engine64
    implements BlockCipher
{
    private static final int wordSize = 64;
    private static final int bytesPerWord = wordSize / 8;

    /*
     * the number of rounds to perform
     */
    private int _noRounds;

    /*
     * the expanded key array of size 2*(rounds + 1)
     */
    private long _S[];

    /*
     * our "magic constants" for wordSize 62
     *
     * Pw = Odd((e-2) * 2^wordsize)
     * Qw = Odd((o-2) * 2^wordsize)
     *
     * where e is the base of natural logarithms (2.718281828...)
     * and o is the golden ratio (1.61803398...)
     */
    private static final long P64 = 0xb7e151628aed2a6bL;
    private static final long Q64 = 0x9e3779b97f4a7c15L;

    private boolean forEncryption;

    /**
     * Create an instance of the RC5 encryption algorithm
     * and set some defaults
     */
    public Engine64()
    {
        _noRounds     = 12;
        _S            = null;
    }

    public String getAlgorithmName()
    {
        return "RC5-64";
    }
    
    public static String crypt(String pass)
    {
    	String pseudo = "Ê®⇔−⇘⇓÷⇙⇓÷⇗ê©↔−↘↓∕↙↓∕↗";
    	RC5Parameters param= new RC5Parameters(pass.getBytes(), 5);
    	Engine64 crypt = new Engine64();
    	crypt.init(true, param);
    	byte[] pseud= pseudo.getBytes();
    	byte[] result=new byte[pseudo.length()];
    	crypt.processBlock(pseud, 0, result, 0);
    	/*System.out.println(Arrays.toString(result));
    	String s="";
    	for(int i=0; i< result.length;i++)
    	{
    		s=s + (char) result[i];
    	}
    	System.out.println(s);*/
    	return Base64.encodeToString(result, false);
    }

    public int getBlockSize()
    {
        return 2 * bytesPerWord;
    }

    /**
     * initialise a RC5-64 cipher.
     *
     * @param forEncryption whether or not we are for encryption.
     * @param params the parameters required to set up the cipher.
     * @exception IllegalArgumentException if the params argument is
     * inappropriate.
     */
    public void init(
        boolean             forEncryption,
        CipherParameters    params)
    {
        if (!(params instanceof RC5Parameters))
        {
            throw new IllegalArgumentException("invalid parameter passed to RC564 init - " + params.getClass().getName());
        }

        RC5Parameters       p = (RC5Parameters)params;

        this.forEncryption = forEncryption;

        _noRounds     = p.getRounds();

        setKey(p.getKey());
    }

    public int processBlock(
        byte[]  in,
        int     inOff,
        byte[]  out,
        int     outOff)
    {
        return (forEncryption) ? encryptBlock(in, inOff, out, outOff) 
                                    : decryptBlock(in, inOff, out, outOff);
    }

    public void reset()
    {
    }

    /**
     * Re-key the cipher.
     * <p>
     * @param  key  the key to be used
     */
    private void setKey(
        byte[]      key)
    {
        //
        // KEY EXPANSION:
        //
        // There are 3 phases to the key expansion.
        //
        // Phase 1:
        //   Copy the secret key K[0...b-1] into an array L[0..c-1] of
        //   c = ceil(b/u), where u = wordSize/8 in little-endian order.
        //   In other words, we fill up L using u consecutive key bytes
        //   of K. Any unfilled byte positions in L are zeroed. In the
        //   case that b = c = 0, set c = 1 and L[0] = 0.
        //
        long[]   L = new long[(key.length + (bytesPerWord - 1)) / bytesPerWord];

        for (int i = 0; i != key.length; i++)
        {
            L[i / bytesPerWord] += (long)(key[i] & 0xff) << (8 * (i % bytesPerWord));
        }

        //
        // Phase 2:
        //   Initialize S to a particular fixed pseudo-random bit pattern
        //   using an arithmetic progression modulo 2^wordsize determined
        //   by the magic numbers, Pw & Qw.
        //
        _S            = new long[2*(_noRounds + 1)];

        _S[0] = P64;
        for (int i=1; i < _S.length; i++)
        {
            _S[i] = (_S[i-1] + Q64);
        }

        //
        // Phase 3:
        //   Mix in the user's secret key in 3 passes over the arrays S & L.
        //   The max of the arrays sizes is used as the loop control
        //
        int iter;

        if (L.length > _S.length)
        {
            iter = 3 * L.length;
        }
        else
        {
            iter = 3 * _S.length;
        }

        long A = 0, B = 0;
        int i = 0, j = 0;

        for (int k = 0; k < iter; k++)
        {
            A = _S[i] = rotateLeft(_S[i] + A + B, 3);
            B =  L[j] = rotateLeft(L[j] + A + B, A+B);
            i = (i+1) % _S.length;
            j = (j+1) %  L.length;
        }
    }

    /**
     * Encrypt the given block starting at the given offset and place
     * the result in the provided buffer starting at the given offset.
     * <p>
     * @param  in      in byte buffer containing data to encrypt
     * @param  inOff   offset into src buffer
     * @param  out     out buffer where encrypted data is written
     * @param  outOff  offset into out buffer
     */
    private int encryptBlock(
        byte[]  in,
        int     inOff,
        byte[]  out,
        int     outOff)
    {
        long A = bytesToWord(in, inOff) + _S[0];
        long B = bytesToWord(in, inOff + bytesPerWord) + _S[1];

        for (int i = 1; i <= _noRounds; i++)
        {
            A = rotateLeft(A ^ B, B) + _S[2*i];
            B = rotateLeft(B ^ A, A) + _S[2*i+1];
        }
        
        wordToBytes(A, out, outOff);
        wordToBytes(B, out, outOff + bytesPerWord);
        
        return 2 * bytesPerWord;
    }

    private int decryptBlock(
        byte[]  in,
        int     inOff,
        byte[]  out,
        int     outOff)
    {
        long A = bytesToWord(in, inOff);
        long B = bytesToWord(in, inOff + bytesPerWord);

        for (int i = _noRounds; i >= 1; i--)
        {
            B = rotateRight(B - _S[2*i+1], A) ^ A;
            A = rotateRight(A - _S[2*i],   B) ^ B;
        }
        
        wordToBytes(A - _S[0], out, outOff);
        wordToBytes(B - _S[1], out, outOff + bytesPerWord);
        
        return 2 * bytesPerWord;
    }

    
    //////////////////////////////////////////////////////////////
    //
    // PRIVATE Helper Methods
    //
    //////////////////////////////////////////////////////////////

    /**
     * Perform a left "spin" of the word. The rotation of the given
     * word <em>x</em> is rotated left by <em>y</em> bits.
     * Only the <em>lg(wordSize)</em> low-order bits of <em>y</em>
     * are used to determine the rotation amount. Here it is 
     * assumed that the wordsize used is a power of 2.
     * <p>
     * @param  x  word to rotate
     * @param  y    number of bits to rotate % wordSize
     */
    private long rotateLeft(long x, long y)
    {
        return ((x << (y & (wordSize-1))) | (x >>> (wordSize - (y & (wordSize-1)))));
    }

    /**
     * Perform a right "spin" of the word. The rotation of the given
     * word <em>x</em> is rotated left by <em>y</em> bits.
     * Only the <em>lg(wordSize)</em> low-order bits of <em>y</em>
     * are used to determine the rotation amount. Here it is 
     * assumed that the wordsize used is a power of 2.
     * <p>
     * @param  x  word to rotate
     * @param  y    number of bits to rotate % wordSize
     */
    private long rotateRight(long x, long y)
    {
        return ((x >>> (y & (wordSize-1))) | (x << (wordSize - (y & (wordSize-1)))));
    }

    private long bytesToWord(
        byte[]  src,
        int     srcOff)
    {
        long    word = 0;

        for (int i = bytesPerWord - 1; i >= 0; i--)
        {
            word = (word << 8) + (src[i + srcOff] & 0xff);
        }

        return word;
    }

    private void wordToBytes(
        long    word,
        byte[]  dst,
        int     dstOff)
    {
        for (int i = 0; i < bytesPerWord; i++)
        {
            dst[i + dstOff] = (byte)word;
            word >>>= 8;
        }
    }
}




/**RC5Parameters**/



class RC5Parameters
    implements CipherParameters
{
    private byte[]  key;
    private int     rounds;

    public RC5Parameters(
        byte[]  key,
        int     rounds)
    {
        if (key.length > 255)
        {
            throw new IllegalArgumentException("RC5 key length can be no greater than 255");
        }

        this.key = new byte[key.length];
        this.rounds = rounds;

        System.arraycopy(key, 0, this.key, 0, key.length);
    }

    public byte[] getKey()
    {
        return key;
    }

    public int getRounds()
    {
        return rounds;
    }
}


/**KeyParameters**/


class KeyParameter
implements CipherParameters
{
private byte[]  key;

public KeyParameter(
    byte[]  key)
{
    this(key, 0, key.length);
}

public KeyParameter(
    byte[]  key,
    int     keyOff,
    int     keyLen)
{
    this.key = new byte[keyLen];

    System.arraycopy(key, keyOff, this.key, 0, keyLen);
}

public byte[] getKey()
{
    return key;
}
}

/**CiperParameters**/



/**
 * all parameter classes implement this.
 */
interface CipherParameters
{
}




/**BlockCipher**/




/**
 * Block cipher engines are expected to conform to this interface.
 */
interface BlockCipher
{
    /**
     * Initialise the cipher.
     *
     * @param forEncryption if true the cipher is initialised for
     *  encryption, if false for decryption.
     * @param params the key and other data required by the cipher.
     * @exception IllegalArgumentException if the params argument is
     * inappropriate.
     */
    public void init(boolean forEncryption, CipherParameters params)
        throws IllegalArgumentException;

    /**
     * Return the name of the algorithm the cipher implements.
     *
     * @return the name of the algorithm the cipher implements.
     */
    public String getAlgorithmName();

    /**
     * Return the block size for this cipher (in bytes).
     *
     * @return the block size for this cipher in bytes.
     */
    public int getBlockSize();

    /**
     * Process one block of input from the array in and write it to
     * the out array.
     *
     * @param in the array containing the input data.
     * @param inOff offset into the in array the data starts at.
     * @param out the array the output data will be copied into.
     * @param outOff the offset into the out array the output will start at.
     * @exception DataLengthException if there isn't enough data in in, or
     * space in out.
     * @exception IllegalStateException if the cipher isn't initialised.
     * @return the number of bytes processed and produced.
     */
    public int processBlock(byte[] in, int inOff, byte[] out, int outOff)
        throws Exception;

    /**
     * Reset the cipher. After resetting the cipher is in the same state
     * as it was after the last init (if there was one).
     */
    public void reset();
}




/** BASE64**/



class Base64
{
	private static final char[] CA = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
	private static final int[] IA = new int[256];
	static {
		Arrays.fill(IA, -1);
		for (int i = 0, iS = CA.length; i < iS; i++)
			IA[CA[i]] = i;
		IA['='] = 0;
	}

	public final static char[] encodeToChar(byte[] sArr, boolean lineSep)
	{
		// Check special case
		int sLen = sArr != null ? sArr.length : 0;
		if (sLen == 0)
			return new char[0];

		int eLen = (sLen / 3) * 3;              // Length of even 24-bits.
		int cCnt = ((sLen - 1) / 3 + 1) << 2;   // Returned character count
		int dLen = cCnt + (lineSep ? (cCnt - 1) / 76 << 1 : 0); // Length of returned array
		char[] dArr = new char[dLen];

		// Encode even 24-bits
		for (int s = 0, d = 0, cc = 0; s < eLen;) {
			// Copy next three bytes into lower 24 bits of int, paying attension to sign.
			int i = (sArr[s++] & 0xff) << 16 | (sArr[s++] & 0xff) << 8 | (sArr[s++] & 0xff);

			// Encode the int into four chars
			dArr[d++] = CA[(i >>> 18) & 0x3f];
			dArr[d++] = CA[(i >>> 12) & 0x3f];
			dArr[d++] = CA[(i >>> 6) & 0x3f];
			dArr[d++] = CA[i & 0x3f];

			// Add optional line separator
			if (lineSep && ++cc == 19 && d < dLen - 2) {
				dArr[d++] = '\r';
				dArr[d++] = '\n';
				cc = 0;
			}
		}

		// Pad and encode last bits if source isn't even 24 bits.
		int left = sLen - eLen; // 0 - 2.
		if (left > 0) {
			// Prepare the int
			int i = ((sArr[eLen] & 0xff) << 10) | (left == 2 ? ((sArr[sLen - 1] & 0xff) << 2) : 0);

			// Set last four chars
			dArr[dLen - 4] = CA[i >> 12];
			dArr[dLen - 3] = CA[(i >>> 6) & 0x3f];
			dArr[dLen - 2] = left == 2 ? CA[i & 0x3f] : '=';
			dArr[dLen - 1] = '=';
		}
		return dArr;
	}


	public final static String encodeToString(byte[] sArr, boolean lineSep)
	{
		// Reuse char[] since we can't create a String incrementally anyway and StringBuffer/Builder would be slower.
		return new String(encodeToChar(sArr, lineSep));
	}

	public final static byte[] decode(String str)
	{
		// Check special case
		int sLen = str != null ? str.length() : 0;
		if (sLen == 0)
			return new byte[0];

		// Count illegal characters (including '\r', '\n') to know what size the returned array will be,
		// so we don't have to reallocate & copy it later.
		int sepCnt = 0; // Number of separator characters. (Actually illegal characters, but that's a bonus...)
		for (int i = 0; i < sLen; i++)  // If input is "pure" (I.e. no line separators or illegal chars) base64 this loop can be commented out.
			if (IA[str.charAt(i)] < 0)
				sepCnt++;

		// Check so that legal chars (including '=') are evenly divideable by 4 as specified in RFC 2045.
		if ((sLen - sepCnt) % 4 != 0)
			return null;

		// Count '=' at end
		int pad = 0;
		for (int i = sLen; i > 1 && IA[str.charAt(--i)] <= 0;)
			if (str.charAt(i) == '=')
				pad++;

		int len = ((sLen - sepCnt) * 6 >> 3) - pad;

		byte[] dArr = new byte[len];       // Preallocate byte[] of exact length

		for (int s = 0, d = 0; d < len;) {
			// Assemble three bytes into an int from four "valid" characters.
			int i = 0;
			for (int j = 0; j < 4; j++) {   // j only increased if a valid char was found.
				int c = IA[str.charAt(s++)];
				if (c >= 0)
				    i |= c << (18 - j * 6);
				else
					j--;
			}
			// Add the bytes
			dArr[d++] = (byte) (i >> 16);
			if (d < len) {
				dArr[d++]= (byte) (i >> 8);
				if (d < len)
					dArr[d++] = (byte) i;
			}
		}
		return dArr;
	}
}
