package main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.crypto.Cipher;
import javax.imageio.ImageIO;

import chaos.Chaosdemo;

public class Test {
	
	public static void main(String[] args)
	{
		String s = "dummy.bmp";		
		File file = new File(s);
//		String scheme = "Chaos-LOGISTIC:3.87 0.4";
		String map = "LOGISTIC";
		String key = "3.87 0.4";
		
		long start = System.nanoTime();
		
		
		BufferedImage image = null;
		BufferedImage image_enc = null;
		BufferedImage ciphertext = null;
		try {
			image = ImageIO.read(file);
			image_enc = ImageIO.read(new File("dummy-enc.bmp"));
			ciphertext = ImageIO.read(new File("DECRYPTED.bmp"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int width  = image.getWidth();
		int height = image.getHeight();
		
		int[][] permutation = new int[width][height];
		int[] x_n = new int[width * height];
		
		for(int count = 0; count < width*height; count++)
		{			
			BufferedImage temp = new BufferedImage(width, height, image.getType());
			int n = 0;
			
			for(int y = 0; y < height; y++)
			{
				for(int x = 0; x < width; x++)
				{
					int color = image.getRGB(x, y);
					if(n++ == count)
						temp.setRGB(x, y, 0 << 24 | 255 << 16 | 174 << 8 | 201);
					else
						temp.setRGB(x, y, color);
				}
			}
			
			try {
				ImageIO.write(temp, "bmp", new File("crack/dummy-" + count + ".bmp"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			File in  = new File("crack/dummy-" + count + ".bmp");
			File out = new File("crack/dummy-" + count + "-enc.bmp");
			
			Chaosdemo.docrypto(map, key, Cipher.ENCRYPT_MODE, in, out);
			
			BufferedImage new_temp = null;
			
			try {
				new_temp = ImageIO.read(out);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			outerloop:
			for(int y = 0; y < width; y++)
			{
				for(int x = 0; x < height; x++)
				{
					if(image_enc.getRGB(x, y) != new_temp.getRGB(x, y))
					{
						permutation[count % width][count / width] = y*height + x;
						break outerloop;
					}
				}
			}	
			
			in.delete(); out.delete();
		}
		
		// reverse the diffusion
		// first, find the original k_n
		for(int i = 1; i < width * height; i++)
		{
			int j = 0;
			for(int k = 0; k < width * height; k++)
			{
				if(permutation[k%width][k/height] == i)
					j = k;
			}

			int colorA = image.getRGB(j % width, j / height) >> 16 & 0xff;
			int colorB = image_enc.getRGB(i % width, i / height) >> 16 & 0xff;
			int colorC = image_enc.getRGB((i+width-1) % width, i%width == 0 ? i / height - 1 : i / height) >> 16 & 0xff;
			
			x_n[i] = colorA ^ colorB ^ colorC; 
		}
		
		BufferedImage temp = new BufferedImage(width, height, image.getType());
		
		// then, decrypt the encrypted picture's red channel
		for(int y = 0; y < height; y++)
		{
			for(int x = 0; x < width; x++)
			{
				if(x+y > 0)
				{
					int colorA = ciphertext.getRGB(x,y) >> 16 & 0xff;
					int colorB = ciphertext.getRGB((x - 1 + width) % width, x == 0? y - 1 : y) >> 16 & 0xff;
					
					int rgb = ciphertext.getRGB(x, y);
					int alpha = rgb >> 24 & 0xff;
					int red   = x_n[y*height+x] ^ colorA ^ colorB;
					int green = rgb >> 8  & 0xff;
					int blue  = rgb & 0xff;
					
					int new_color = alpha << 24 | red << 16 | green << 8 | blue;
					
//					System.out.println(x_n[y*height+x] + " " + colorA + " " + colorB + " " + red);
					
					temp.setRGB(x, y, new_color);
				}
			}
		}
		
		// finally, reverse the permutation
		BufferedImage decrypted = new BufferedImage(width, height, image.getType());
		
		for(int y = 0; y < height; y++)
		{
			for(int x = 0; x < width; x++)
			{
				int position = permutation[x][y];
				int x_coord = position % width, y_coord = position / height;
				int color = temp.getRGB(x_coord, y_coord);
				decrypted.setRGB(x, y, color);
			}
		}
		
		try {
			ImageIO.write(decrypted, "bmp", new File("DECRYPTED.bmp"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		long stop = System.nanoTime();
		System.out.println(1e-9 * (stop - start));
	}

}
