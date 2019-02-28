package com.google.battle.project;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Hello world!
 *
 */
public class App 
{
	private static final String[] fileList = new String [] {
			"a_example.txt",
			"b_lovely_landscapes.txt",
			"c_memorable_moments.txt",
			"d_pet_pictures.txt",
			"e_shiny_selfies.txt"
	};
    public static void main( String[] args )
    {
    	for (String fileIn : fileList) {
	       	ClassLoader classLoader = App.class.getClassLoader();
	    	File file = new File(classLoader.getResource(fileIn).getFile());
	        try {
	          List<Photo> photos = loadPhotos(file);
	          
	          List<Slide> slides = createSlides(photos);
	          
	          write(slides, fileIn);
	        } catch (IOException e) {
	          System.out.println(e);
	        }
	    }

    }
	private static List<Slide> createSlides(List<Photo> photos) {
		List<Slide> slides = new ArrayList<Slide>();
		  Photo photoV = null;
		  Slide slide = null;
		  do {
			  slide = findNextSlide(photos, photoV);
			  if(slide != null)
				  slides.add(slide);
		  } while (slide != null);
		  
		  return slides;
	}
	private static Slide findNextSlide(List<Photo> photos, Photo photoV) {
		Slide slide2 = null;
		  for (Photo photo : photos) {
			  if (photo.horizontal) {
				  slide2 = new Slide(photo);
				  break;
			  } else {
				  if (photoV == null) {
					  photoV = photo;
				  } else {
					  slide2 =  new Slide(photoV);
					  slide2.photo2 = photo;
					  photoV = null;
					  break;
				  }
			  }
		  }
		  if (slide2 != null) {
			  photos.remove(slide2.photo1);
			  if (slide2.photo2 != null) {
				  photos.remove(slide2.photo2);
			  }
		  }
		  return slide2;
	}
	private static List<Photo> loadPhotos(File file) throws IOException {
		  final AtomicInteger index = new AtomicInteger();
		  Scanner scanner = new Scanner(file);
		  scanner.nextLine();
		  ArrayList<Photo> result = new ArrayList<>();
		  while(scanner.hasNextLine()) {
			  result.add( new Photo(scanner.nextLine(),index.getAndIncrement()));
		  }
		  
		  return result;
	}
	
	public static void write(List<Slide> slides, String name) {
		try (
			FileWriter fw = new FileWriter(name+".out");) {
			fw.write("" + slides.size());
			for (Slide slide : slides) {
				fw.write('\n');
				fw.write("" + slide.photo1.index + (slide.photo2 != null ? " " + slide.photo2.index : "") );
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
