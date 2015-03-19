package main;

import java.io.File;

import javax.crypto.Cipher;

import chaos.Chaosdemo;
import aes.AESdemo;

public class CryptoUtils 
{
	private static File plaintext;
	private static File ciphertext;
	
	public static void docrypto(String fileName, String scheme, int mode)
	{
		//-----------------------------------------------------------------------------------------
		// check the mode
		//-----------------------------------------------------------------------------------------
		if(mode != Cipher.ENCRYPT_MODE && mode != Cipher.DECRYPT_MODE)
		{
			System.err.println("Incorrect mode! Set to Cipher.ENCRYPT_MODE or Cipher.DECRYPT_MODE.");
			return;
		}
			
		//-----------------------------------------------------------------------------------------
		// check the file
		//-----------------------------------------------------------------------------------------
		File file = new File(fileName);
		if(!file.exists())
		{
			System.err.println("No file " + fileName);
			return;
		}
		
		if(mode == Cipher.ENCRYPT_MODE)
			plaintext = file;
			
		else
			ciphertext = file;
		
		//-----------------------------------------------------------------------------------------
		// get the encryption type and mode
		//-----------------------------------------------------------------------------------------
		String type = "", variant = "", key = "";
		int i = scheme.lastIndexOf('-');
		int j = scheme.lastIndexOf(':');
		
		type    = scheme.substring(0, i);
		variant = scheme.substring(i+1, j);
		key     = scheme.substring(j+1);
		
		//-----------------------------------------------------------------------------------------
		// do the actual crypto stuff
		//-----------------------------------------------------------------------------------------
		switch(type)
		{
		case "AES" :
			
			if(mode == Cipher.ENCRYPT_MODE)
			{
				ciphertext = new File(fileName + ".enc");
				AESdemo.docrypto(variant, key, mode, plaintext, ciphertext);
			}
			else
			{
				String[] parts = fileName.split("\\.");
				if(parts.length < 3 || parts[2].compareTo("enc")!=0)
				{
					System.out.println(parts[2]);
					System.out.println("Cannot decode this file!");
					return;
				}
				plaintext = new File(parts[0] + "-dec." + parts[1]);
				AESdemo.docrypto(variant, key, mode, ciphertext, plaintext);
			}			
			
			break;
		case "Chaos" :
			
			if(mode == Cipher.ENCRYPT_MODE)
			{
				String[] parts = fileName.split("\\.");
				ciphertext = new File(parts[0] + "-enc." + parts[1]);
				
				Chaosdemo.docrypto(variant, key, mode, plaintext, ciphertext);
			}
			
			else
			{
				String[] parts = fileName.split("\\.");
				plaintext = new File(parts[0].split("-")[0] + "-dec." + parts[1]);
				
				Chaosdemo.docrypto(variant, key, mode, ciphertext, plaintext);
			}			
			
			break;
		default: System.err.println("Incorrect scheme type! Choose either AES or Chaos.");
			break;
		}
	}
}
