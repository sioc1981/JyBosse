package com.google.battle.project;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
            "a_example.txt"
            ,
            "b_lovely_landscapes.txt",
            "c_memorable_moments.txt",
            "d_pet_pictures.txt",
            "e_shiny_selfies.txt"
    };

    private static int totalScore = 0;

    private static HashMap<String,AtomicInteger> interests = new HashMap<>();
    public static void main( final String[] args )
    {
        for (final String fileIn : App.fileList) {
            final ClassLoader classLoader = App.class.getClassLoader();
            final File file = new File(classLoader.getResource(fileIn).getFile());
            try {
                final List<Photo> photos = loadPhotos(file);

                final List<Slide> slides = createSlides(photos);
                write(slides, fileIn);
            } catch (final IOException e) {
                System.out.println(e);
            }
        }
        System.out.println("Total score: " + App.totalScore);

    }
    private static List<Slide> createSlides(final List<Photo> photos) {
        int score = 0;
        final List<Slide> slides = new ArrayList<>();
        Slide slide = null;
        Slide previousSlide = null;
        do {
            previousSlide = slide;
            slide = findNextSlide(photos);
            if(slide != null) {
                addSlide(slides, slide);
                //				  slides.add(slide);
                if(previousSlide != null) {
                    score += getScore(previousSlide, slide);
                }
            }
        } while (slide != null);
        System.out.println("Score: " + score);
        App.totalScore += score;
        return slides;
    }

    private static void addSlide(final List<Slide> slides, final Slide slide) {
        //		for (Slide s : slides) {
        //			slide.setBestScored(s);
        //		}
        slides.add(slide);
    }

    private static Slide findNextSlide(final List<Photo> photos) {
        Photo photoV = null;
        Slide slide2 = null;
        for (final Photo photo : photos) {
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
            slide2.getTags();
        }
        return slide2;
    }
    private static List<Photo> loadPhotos(final File file) throws IOException {
        App.interests.clear();
        final AtomicInteger index = new AtomicInteger();
        final Scanner scanner = new Scanner(file);
        scanner.nextLine();
        final ArrayList<Photo> result = new ArrayList<>();
        while(scanner.hasNextLine()) {
            result.add( new Photo(scanner.nextLine(),index.getAndIncrement()));
        }
        result.stream().map(p->p.tags).forEach(s -> s.forEach(t -> App.interests.computeIfAbsent(t, a -> new AtomicInteger()).getAndIncrement()));
        System.out.println("interests: " + App.interests);
        //StreamSupport.stream(Spliterators.spliterator(result, Spliterator.SIZED), true).forEach(p -> p.interestScore = p.tags.stream().mapToInt(t -> App.interests.get(t).get()).sum());
        // StreamSupport.stream(Spliterators.spliterator(result, Spliterator.SIZED), true).forEach(p -> p.interestScore
        // = p.tags.size()/2);
        System.out.println("Sorting: ");
        result.sort( (p1,p2) -> p1.interestScore - p2.interestScore);
        //		  System.out.println("Photos: " + result);
        return result;
    }

    public static void write(final List<Slide> slides, final String name) {
        try (
                FileWriter fw = new FileWriter(name+".out");) {
            fw.write("" + slides.size());
            for (final Slide slide : slides) {
                fw.write('\n');
                fw.write("" + slide.photo1.index + (slide.photo2 != null ? " " + slide.photo2.index : "") );
            }
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static int getScore(final Slide s1, final Slide s2 ) {
        final AtomicInteger onlyS1 = new AtomicInteger();
        final AtomicInteger common = new AtomicInteger();
        final AtomicInteger onlyS2 = new AtomicInteger();
        for (final String s : s1.tags) {
            if(s2.tags.contains(s)) { 
                common.getAndIncrement();
            } else {
                onlyS1.getAndIncrement();
            }
        }
        for (final String s : s2.tags) {
            if(!s1.tags.contains(s)) { 
                onlyS2.getAndIncrement();
            } 
        }
        //		System.out.println(s1.tags);
        //		System.out.println(s2.tags);
        //		System.out.println("onlyS1: " + onlyS1.get());
        //		System.out.println("common: " + common.get());
        //		System.out.println("onlyS2: " + onlyS2.get());
        return Math.min(Math.min(onlyS1.get(),common.get() ), onlyS2.get());
    }

}
