package chaos;

import java.awt.image.BufferedImage;

import javax.crypto.Cipher;

public class Diffusion 
{
	
	public static void diffuse(int mode, BufferedImage image)
	{
		int width  = image.getWidth();
		int height = image.getHeight();
		
		if(mode == Cipher.ENCRYPT_MODE)
		{			
			for(int y = 0; y < height; y++)
			{
				for(int x = 0; x < width; x++)
				{					
					int rgb = image.getRGB(x, y);
					int alpha = rgb >> 24 & 0xff;
					int red   = rgb >> 16 & 0xff;
					int green = rgb >> 8  & 0xff;
					int blue  = rgb & 0xff;
					
					int p_n = red;
					int k_n = (int) ((Math.floor(Chaosdemo.x[y * width + x] * 1e14)) % 256);
					int c_old  = Chaosdemo.c0;
					if(x > 0 || y > 0)
						c_old = image.getRGB((x-1+width) % width, x==0 ? y-1 : y) >> 16 & 0xff;
						
					int c_next = p_n ^ k_n ^ c_old;
					
					int new_color = alpha << 24 | c_next << 16 | green << 8 | blue;
					
					image.setRGB(x, y, new_color);
				}
			}
		}
		
		else // mode == Cipher.DECRYPT_MODE
		{
			for(int y = height-1; y >= 0; y--)
			{
				for(int x = width-1; x >= 0; x--)
				{
					int rgb = image.getRGB(x, y);
					int alpha = rgb >> 24 & 0xff;
					int red   = rgb >> 16 & 0xff;
					int green = rgb >> 8  & 0xff;
					int blue  = rgb & 0xff;
					
					int c_n = red;
					int k_n = (int) ((Math.floor(Chaosdemo.x[y * width + x] * 1e14)) % 256);
					int c_old = Chaosdemo.c0;
					if(x > 0 || y > 0)
						c_old = image.getRGB((x-1+width) % width, x==0 ? y-1 : y) >> 16 & 0xff;
						
					int p_n = c_n ^ k_n ^ c_old;
					
					int new_color = alpha << 24 | p_n << 16 | green << 8 | blue;
					
					image.setRGB(x, y, new_color);
				}
			}
		}
	}

}
