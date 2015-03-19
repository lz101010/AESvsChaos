package chaos;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.Point;

import javax.crypto.Cipher;
import javax.imageio.ImageIO;

public class Chaosdemo 
{
	//---------------------------------------------------------------------------------------------
	// helpful global variables, width and height should always be equal!
	//---------------------------------------------------------------------------------------------
	public static int width  = 0;
	public static int height = 0;
	
	public static double[] x;
	public static int[] c;
	public static int c0 = 128; // 0 < c0 < 256
	
	/**
	 * 
	 * @param map the chaos map ("LOGISTIC", "CAT", or "BAKER")
	 * @param key concatenation of the security parameter µ, the initial sequence element x0, and
	 * 				the number of rounds 
	 * @param mode Cipher.ENCRYPT_MODE for encryption, Cipher.DECRYPT_MODE for decryption
	 * @param in input file to be encrypted/decrypted
	 * @param out output file for the encrypted/decrypted input file
	 */
	public static void docrypto(String map, String key, int mode, File in, File out)
	{
		if(mode != Cipher.ENCRYPT_MODE && mode != Cipher.DECRYPT_MODE)
			return;
		
		//-----------------------------------------------------------------------------------------
		// write the image into a buffer and get width and height of the image
		//-----------------------------------------------------------------------------------------
		BufferedImage oldImage = null;
		try {
			oldImage = ImageIO.read(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		width  = oldImage.getWidth();
		height = oldImage.getHeight();
		
		//-----------------------------------------------------------------------------------------
		// get information from the key (number of rounds for BAKER/CAT, r and x_0 for LOGISTIC)
		//-----------------------------------------------------------------------------------------
		
		int numRounds = 0;
		int N_0 = 200;
		
		String[] params = key.split(" ");
		double r   = Float.parseFloat(params[0]);
		double x_0 = Float.parseFloat(params[1]);
		double x_N = r*x_0*(1-x_0);
		
		for(int i = 0; i < N_0; i++)
			x_N = r*x_N*(1-x_N);
		
		x = new double[width * height];
		
		for(int i = 0; i < width * height; i++)
			x[i] = x_N = r*x_N*(1-x_N);	
		
		switch(map)
		{
			case "BAKER":
			case "CAT"  :
				numRounds = Integer.parseInt(params[2]);
				break;
				
			case "LOGISTIC":
				numRounds = 1;
				break;
			
			default: numRounds = 0;
				break;
		}		
		long start = System.nanoTime();
		//-----------------------------------------------------------------------------------------
		// do the crypto stuff!
		// first, undo the diffusion if we're decrypting
		//-----------------------------------------------------------------------------------------
		if(mode == Cipher.DECRYPT_MODE)
			Diffusion.diffuse(mode, oldImage);
		
		//-----------------------------------------------------------------------------------------
		// next, permute the  pixel positions
		//-----------------------------------------------------------------------------------------
		
		BufferedImage newImage = oldImage;
		int type = oldImage.getType(); // type = 3TYPE_BGR
		
		for(int i = 0; i < numRounds; i++)
		{
			//-------------------------------------------------------------------------------------
			// if we're not using the LOGISTIC map, we create a new image and fill in the pixels
			//-------------------------------------------------------------------------------------
			if(map.compareTo("LOGISTIC") != 0)
				newImage = new BufferedImage(width, height, type); 
			
			for(int y = 0; y < height; y++)
			{
			  for(int x = 0; x < width; x++) 
			  {
			     int color = 0;
			     
			     //--------------------------------------------------------------------------------
			     // when decrypting with the LOGISTIC map, we need to start at the end of the
			     // image to invert the shuffling - otherwise, we just start at the beginning
			     //--------------------------------------------------------------------------------
			     if(map.compareTo("LOGISTIC") == 0 && mode == Cipher.DECRYPT_MODE)
					 color = oldImage.getRGB(width - x - 1, height - y - 1); 
			     else
			    	 color = oldImage.getRGB(x,y);
			     
			     //--------------------------------------------------------------------------------
			     // find the new pixel position
			     //--------------------------------------------------------------------------------
			     Point p = null;
			     
			     switch(map)
			     {
				     case "CAT"      : p = ChaosMaps.catMap(new Point(x, y), mode);      break;
				     case "BAKER"    : p = ChaosMaps.bakerMap(new Point(x, y), mode);    break;
				     case "LOGISTIC" : p = ChaosMaps.logisticMap(new Point(x, y), mode); break;
				     default:        p = new Point(x, y); break;
			     }
			     
			     int x_new = p.x, y_new = p.y;
			     
			     //--------------------------------------------------------------------------------
			     // once we have the new pixel position, we do one of two things:
			     // 1.) LOGISTIC:			     
		    	 //     this map exchanges the pixel values at the old and new positions, so we
			     //     first set the pixel at the current position to the value of the pixel at
			     //     the new position, and then we set the pixel at the new position to color
			     // 2.) CAT and BAKER:
			     //     these maps just set the pixel at the new position to color
			     //--------------------------------------------------------------------------------
			     if(map.compareTo("LOGISTIC") == 0)
			     {
			    	 if(mode == Cipher.ENCRYPT_MODE)
			    		 newImage.setRGB(x, y, newImage.getRGB(x_new, y_new));
			    	 else // mode == Cipher.DECRYPT_MODE - again, we decrypt starting at the end
			    		 newImage.setRGB(width-x-1, height-y-1, newImage.getRGB(x_new, y_new));
			     }

			     newImage.setRGB(x_new, y_new, color);
			  }
			}
			oldImage = newImage;
		}
		
		//-----------------------------------------------------------------------------------------
		// if we're encrypting, now randomize the pixel values
		//-----------------------------------------------------------------------------------------
		
		if(mode == Cipher.ENCRYPT_MODE)
			Diffusion.diffuse(mode, oldImage);
		
		//-----------------------------------------------------------------------------------------
		// write the BufferedImage back into a file (with the correct type according to extension)
		//-----------------------------------------------------------------------------------------
		String fileName = in.getPath();
		String imgType = "";
		int i = fileName.lastIndexOf('.');
		if(i >= 0)
			imgType = fileName.substring(i + 1);
		
		try {
			ImageIO.write(newImage, imgType, out);
		} catch (IOException e) {
			e.printStackTrace();
		}
		long stop = System.nanoTime();
		System.out.println(1e-9 * (stop - start));
	}
}
