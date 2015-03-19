package chaos;

import java.awt.Point;

import javax.crypto.Cipher;

public class ChaosMaps 
{
	private static int width  = Chaosdemo.width;
	private static int height = Chaosdemo.height;
	
	/**
	 * Using Arnold's Cat Map, we find the new pixel position. 
	 * Depending on the mode (encrypt/decrypt), we use either the transformation given by the
	 * Cat Map or the inverse transformation.
	 * 
	 * CAUTION: if height != width, this won't work!
	 * 
	 * @param p contains the x- and y- coordinates of the old pixel
	 * @param mode either Cipher.ENCRYPT_MODE for encryption or Cipher.DECRYPT_MODE for decryption
	 * @return the x- and y- coordinates of the new pixel in a Point
	 */
	public static Point catMap(Point p, int mode) 
	{
		if(mode != Cipher.ENCRYPT_MODE && mode != Cipher.DECRYPT_MODE)
			return p;
		
		int x = p.x, y = p.y, x_new = x, y_new = y;
		
		if(mode == Cipher.ENCRYPT_MODE)
		{
			x_new = (2*x + y) % width;
			y_new = (x + y)   % height;
		}
		
		else // mode == Cipher.DECRYPT_MODE
		{
			x_new = (width + (x - y))     % width;  // the modulo operator may return a negative
			y_new = (height + (-x + 2*y)) % height; // value, so we add the modulus for positivity
		}		
		
		return new Point(x_new, y_new);
	}	
	
	/**
	 * Using Baker's Map, we find the new pixel position. 
	 * Depending on the mode (encrypt/decrypt), we use either the transformation given by 
	 * Baker's Map or the inverse transformation.
	 * 
	 * @param p contains the x- and y- coordinates of the old pixel
	 * @param mode either Cipher.ENCRYPT_MODE for encryption or Cipher.DECRYPT_MODE for decryption
	 * @return returns the x- and y- coordinates of the new pixel in a Point
	 */
	public static Point bakerMap(Point p, int mode)
	{
		if(mode != Cipher.ENCRYPT_MODE && mode != Cipher.DECRYPT_MODE)
			return p;
		
		int x = p.x, y = p.y, x_new = x, y_new = y;
		
		if(mode == Cipher.ENCRYPT_MODE)
	    {
		    if(x < width/2)
		    {
		    	x_new = (y + height) % 2 == 0 ? x*2 : x*2 + 1;
		    	y_new = (y + height) / 2;
		    }
		    else
		    {
		    	x_new = y % 2 == 0 ? x*2 - width : x*2 - width + 1;
		    	y_new = y / 2;
		    }	
	    }
		
	    else // mode == Cipher.DECRYPT_MODE
	    {
	    	if(y < height/2)
	    	{
	    		x_new = (x + width) / 2;
	    		y_new = (x + width) % 2 == 0 ? 2*y : 2*y + 1;
	    	}
	    	else
	    	{
	    		x_new = x / 2;
	    		y_new = x % 2 == 0 ? 2*y - height : 2*y - height + 1;
	    	}
	    }
		
		return new Point(x_new, y_new);
	}
	
	/**
	 * Uses the Logistic Map to find new pixel positions.
	 * 
	 * @param p contains the x- and y- coordinates of the old pixel
	 * @param mode either Cipher.ENCRYPT_MODE for encryption or Cipher.DECRYPT_MODE for decryption
	 * @return returns the x- and y- coordinates of the new pixel in a Point
	 */
	public static Point logisticMap(Point p, int mode) 
	{
		if(mode != Cipher.ENCRYPT_MODE && mode != Cipher.DECRYPT_MODE)
			return p;
		
		int x = p.x, y = p.y, x_new = x, y_new = y;		
		
		if(mode == Cipher.ENCRYPT_MODE)
		{			
			double t_n = (Math.floor(Chaosdemo.x[y * width + x] * 1e14)) % (height * width);
			
			x_new = (int) (t_n % width);
			y_new = (int) (t_n / width);
		}
		
		else // mode == Cipher.DECRYPT_MODE
		{
			double t_n = (Math.floor(Chaosdemo.x[height*width - (y * width + x)-1] * 1e14)) % (height * width);
			
			x_new = (int) (t_n % width);
			y_new = (int) (t_n / width);
		}
		
		return new Point(x_new, y_new);
	} 

}
