package hw1;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;

import hw1.DigitalImageIO.ImageType;

public class Tester {

	public static boolean areEqual( BufferedImage im1, DigitalImage im2 ) {
		boolean dims = 
				im1.getWidth() == im2.getWidth() &&
				im1.getHeight() == im2.getHeight();
		if( ! dims ) return false;
		
		for( int row = 0 ; row < im1.getHeight(); row ++ ) {
			for( int col = 0; col < im1.getWidth(); col ++ ) {
				for( int band = 0; band < 3; band ++ ) {
					if( im1.getRaster().getSample( col, row, band ) != im2.getSample( col, row, band ) ) return false;
				}
			}
		}
		
		return true;				
	}
	
	public static boolean areEqual( BufferedImage im1, BufferedImage im2 ) {
		boolean dims = 
				im1.getWidth() == im2.getWidth() &&
				im1.getHeight() == im2.getHeight();
		if( ! dims ) return false;
		
		for( int row = 0 ; row < im1.getHeight(); row ++ ) {
			for( int col = 0; col < im1.getWidth(); col ++ ) {
				for( int band = 0; band < 3; band ++ ) {
					if( im1.getRaster().getSample( col, row, band ) != im2.getRaster().getSample( col, row, band ) ) return false;
				}
			}
		}
		
		return true;				
	}
		
	public static boolean areEqual( DigitalImage im1, DigitalImage im2 ) {
		boolean dims = 
				im1.getWidth() == im2.getWidth() &&
				im1.getHeight() == im2.getHeight();
		if( ! dims ) return false;
		
		for( int row = 0 ; row < im1.getHeight(); row ++ ) {
			for( int col = 0; col < im1.getWidth(); col ++ ) {
				for( int band = 0; band < 3; band ++ ) {
					if( im1.getSample( col, row, band ) != im2.getSample( col, row, band ) ) return false;
				}
			}
		}
		
		return true;				
	}
	
	
	private static Class getImplementationClass( ImageType type ) {
		switch( type ) {
		case INDEXED : return IndexedDigitalImage.class;
		case PACKED : return PackedPixelImage.class;
		case LINEAR_ARRAY : return LinearArrayDigitalImage.class;
		case MULTIDIM_ARRAY : return ArrayDigitalImage.class;
		default : return null;
		}
	}
	
	// args[0] == PNG or JPEG image
	// args[1] == name of PPM to create
	// args[2] == one of :  PACKED, INDEXED, LINEAR_ARRAY, MULTIDIM_ARRAY
	// args[3] == PNG image filename of processed throughput
	public static void main(String[] args) throws IOException, IllegalFileFormatException {

		BufferedImage oracle = ImageIO.read( new File( args[ 0 ] ) ) ;
		DigitalImage image = ImageConverter.toDigitalImage( oracle );		
		
		if( !areEqual( oracle, image ) ) {
			System.out.println("ImageConverter.toDigitalImage error.");
			System.exit(1);
		}
		
		DigitalImageIO.write( new File( args[ 1 ] ), image );
		ImageType type = ImageType.valueOf( args[2] );		
		DigitalImage image2 = DigitalImageIO.read( new File( args[1] ), type );
		
		if( !getImplementationClass( type ).isInstance(image2) ) {
			System.out.println("wrong type of DigitalImage produced by DigitalImageIO.read");
		}
		
//		if( !areEqual( image, image2 ) ) {
//			System.out.println("Error with either DigitalImage read or write");
//			System.exit(1);
//		}
		
		BufferedImage copy = ImageConverter.toBufferedImage( image2 );
		ImageIO.write( copy,  "PNG", new File( args[3] ) );				
		
		if( !areEqual( oracle, copy ) ) {
			System.out.println("Error with toBufferedImage");
			System.exit(1);
		}
	}
	
}
