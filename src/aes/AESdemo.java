package aes;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class AESdemo 
{
	private static final int IV_LENGTH   = 16;
	
	/**
	 * Encrypts an input file with the specified mode of AES and password with
	 * the Java-intern implementation of AES.
	 * 
	 * @param variant "AES/CFB8/NoPadding" or "AES/CBC/PKCS5Padding"
	 * @param key should have 16 characters (e.g. "password12345678")
	 * @param mode
	 * @param in
	 * @param out
	 */
	public static void docrypto(String variant, String key, int mode, File in, File out)
	{
		if(mode != Cipher.ENCRYPT_MODE && mode != Cipher.DECRYPT_MODE)
			return;		
		
		try
		{
			InputStream is = new BufferedInputStream (new FileInputStream(in));
			OutputStream os = new BufferedOutputStream(new FileOutputStream(out));			
			
			
			byte[] iv = new byte[IV_LENGTH];
			Cipher cipher = Cipher.getInstance(variant);
			long start = System.nanoTime();
			SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
			IvParameterSpec ivSpec = new IvParameterSpec(iv);
			cipher.init(mode, keySpec, ivSpec); 
			
			if(mode == Cipher.ENCRYPT_MODE)
			{
				SecureRandom r = new SecureRandom();
				r.nextBytes(iv);
				os.write(iv);
				os.flush();	
				os = new CipherOutputStream(os, cipher);
			}
			else // mode == Cipher.DECRYPT_MODE
			{			
				is.read(iv);
				is = new CipherInputStream(is, cipher);
			}
			
			byte[] buf = new byte[1024];
			int numRead = 0;
			
			while ((numRead = is.read(buf)) >= 0)
				os.write(buf, 0, numRead);
			
			long stop = System.nanoTime();
			System.out.println(1e-9 * (stop - start));
			
			is.close();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
