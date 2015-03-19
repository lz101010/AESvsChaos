package main;


import javax.crypto.Cipher;

public class Main
{
	
	public static void main(String[] args)
	{
		// enter the file name
		String fileName = "help4x4-enc-enc.bmp";
		
		// specify the scheme
		// format: [AES/Chaos] - [AES/CBC/PKCS5Padding or AES/CFB8/NoPadding / LOGISTIC or CAT or BAKER] : [key]
		// key should be 16 characters long for AES and µ, x0, n for Chaos where µ is the security parameter,
		// x0 is the first element in the sequence for the logistic map (needed for diffusion), and n is the
		// number of rounds. examples are commented below:
		String scheme = "";
		
		scheme = "AES-AES/CBC/PKCS5Padding:password12345678";
//		scheme = "AES-AES/CFB8/NoPadding:password12345678";
//		scheme = "Chaos-LOGISTIC:3.87 0.4";
//		scheme = "Chaos-CAT:3.91 0.1 4";
//		scheme = "Chaos-BAKER:3.754 0.7 2";
		
		// uncomment the appropriate line for encryption or decrpytion
		// int mode = Cipher.DECRYPT_MODE;
		int mode = Cipher.ENCRYPT_MODE;
		
		long start = System.nanoTime();
		CryptoUtils.docrypto(fileName, scheme, mode);
		System.out.println("done! " + 1e-9*(System.nanoTime()-start));		
	}

}
