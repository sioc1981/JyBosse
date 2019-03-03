package com.google.battle.project;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.StreamSupport;

/**
 * Hello world!
 *
 */
public class App 
{
    private static final String[] fileList = new String [] {
            "a_example.txt"
            ,
            "b_lovely_landscapes.txt"
            ,
            "c_memorable_moments.txt"
//            ,
//            "d_pet_pictures.txt"
//            ,
//            "e_shiny_selfies.txt"
    };

    private static int totalScore = 0;
    
    private static TreeSet<SlideTransition> transitions = new TreeSet<SlideTransition>();

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
    private static List<Slide> createSlides(final List<Photo> myPhotos) {
    	transitions.clear();
    	List<Photo> photos = myPhotos;
        List<Slide> initSlides = new ArrayList<>();
        Slide slide = null;
        do {
            slide = findNextSlide(photos);
            if(slide != null) {
                addSlide(initSlides, slide);
                //				  slides.add(slide);
            }
        } while (slide != null);

//        do {
//        	slide = findNextHorizontalSlide(photos);
//        	if(slide != null) {
//        		addSlide(slides, slide);
//        		//				  slides.add(slide);
//        	}
//        } while (slide != null);
//        
//        photos = sortVerticalPhotos(photos);
//        do {
//        	slide = findNextVerticalSlide(photos);
//        	if(slide != null) {
//        		addSlide(slides, slide);
//        	}
//        } while (slide != null);
        
        
        
//        slides.stream().map(p->p.tags).forEach(s -> s.forEach(t -> App.interests.computeIfAbsent(t, a -> new AtomicInteger()).getAndIncrement()));
//        App.interests.keySet().forEach(k -> App.interests.get(k).set(App.interests.get(k).get()/2));
//        System.out.println("interests: " + App.interests);
//        StreamSupport.stream(Spliterators.spliterator(slides, Spliterator.SIZED), true).forEach(p -> p.interestScore = (int) p.tags.stream().mapToInt(t -> App.interests.get(t).get()).average().orElseThrow());
//
//        
//        slides.sort( (p1,p2) -> p1.interestScore - p2.interestScore);
//        
//        slides = parabolaSort(slides);
        List<Slide> slides = new ArrayList<>();
        
        while(!transitions.isEmpty()) {
        	List<Slide> tmpSlides = new ArrayList<>();
	        SlideTransition startingTransition = transitions.pollLast();
			Slide startSlide = startingTransition.slide1;
			Slide endSlide = startingTransition.slide2;
			startingTransition.disable();
			tmpSlides.add(0,startSlide);
	        initSlides.remove(startSlide);
	        tmpSlides.add(endSlide);
	        initSlides.remove(endSlide);
	        iterateOnSlide(startSlide, endSlide, tmpSlides, initSlides);
	        slides.addAll(tmpSlides);
        }
        int score = computeScore(slides);
        System.out.println("Score: " + score);
        App.totalScore += score;
        return slides;
    }

    private static void iterateOnSlide(Slide startSlide, Slide endSlide, List<Slide> slides, List<Slide> initSlides) {
    	Slide sladeA = null;
    	if(startSlide != null && ! startSlide.transitions.isEmpty()) {
	    	SlideTransition startingTransition = startSlide.transitions.pollLast();
	    	App.transitions.remove(startingTransition);
			startingTransition.disable();
			TreeSet<SlideTransition> transitions = new TreeSet<SlideTransition>(startSlide.transitions);
			for (SlideTransition transition : transitions) {
	        	transition.disable();
	        	App.transitions.remove(transition);
			}
			sladeA = startingTransition.slide2 == startSlide ? startingTransition.slide1 : startingTransition.slide2;
	        slides.add(0,sladeA);
	        initSlides.remove(sladeA);
    	}
    	Slide sladeB = null;
    	if(endSlide != null && ! endSlide.transitions.isEmpty()) {
	        SlideTransition endTransition = endSlide.transitions.pollLast();
	        App.transitions.remove(endTransition);
			TreeSet<SlideTransition> transitions = new TreeSet<SlideTransition>(endSlide.transitions);
			for (SlideTransition transition : transitions) {
	        	transition.disable();
	        	App.transitions.remove(transition);
			}
	        sladeB = endTransition.slide2 == endSlide ? endTransition.slide1 : endTransition.slide2;
	        endTransition.disable();
	        slides.add(sladeB);
	        initSlides.remove(sladeB);
    	}
    	if(sladeA != null || sladeB != null) {
    		iterateOnSlide(sladeA,sladeB,slides,initSlides);
    	}
	}
	private static List<Photo> sortVerticalPhotos(List<Photo> photos) {
		return photos;
	}
	private static int computeScore(List<Slide> slides) {
    	int score = 0;
    	 Slide previousSlide = null;
    	 for (Slide slide : slides) {
    		 if(previousSlide != null) {
                 score += getScore(previousSlide, slide);
             }
    		 previousSlide = slide;
		}
		return score;
	}
	private static void addSlide(final List<Slide> slides, final Slide newSlide) {
//		System.out.println("add slide " + newSlide);
//		System.out.println("slides " + slides);
		for (Slide slide : slides) {
//			System.out.println("slide " + slide);
			int score =getScore(slide, newSlide);
			if(score > 0 ) {
				SlideTransition slideTransition = new SlideTransition(slide, newSlide, score);
//				System.out.println("slideTransition " + slideTransition);
				slide.transitions.add(slideTransition);
				newSlide.transitions.add(slideTransition);
				transitions.add(slideTransition);
//				System.out.println(transitions);
			}
		}
//		
//		slides.stream().filter(s -> getScore(s, newSlide) > 0).forEach(slide -> {
//			SlideTransition slideTransition = new SlideTransition(slide, newSlide, getScore(slide, newSlide));
//			slide.transitions.add(slideTransition);
//			newSlide.transitions.add(slideTransition);
//			transitions.add(slideTransition);
//
//		});
        slides.add(newSlide);
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
                    slide2 =  new Slide(photoV, photo);
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
    private static Slide findNextHorizontalSlide(final List<Photo> photos) {
        Slide slide2 = null;
        for (final Photo photo : photos) {
            if (photo.horizontal) {
                slide2 = new Slide(photo);
                break;
            }
        }
        if (slide2 != null) {
            photos.remove(slide2.photo1);
        }
    	return slide2;
    }
    private static Slide findNextVerticalSlide(final List<Photo> photos) {
    	Photo photoV = null;
    	Slide slide2 = null;
    	for (final Photo photo : photos) {
			if (photoV == null) {
				photoV = photo;
			} else {
				slide2 =  new Slide(photoV, photo);
				photoV = null;
				break;
			}
    	}
    	if (slide2 != null) {
    		photos.remove(slide2.photo1);
    		if (slide2.photo2 != null) {
    			photos.remove(slide2.photo2);
    		}
    		//slide2.interestScore = slide2.tags.stream().mapToInt(t -> App.interests.get(t).get()).sum();
    	}
    	return slide2;
    }
    
    
    private static List<Photo> loadPhotos(final File file) throws IOException {
        App.interests.clear();
        final AtomicInteger index = new AtomicInteger();
        final Scanner scanner = new Scanner(file);
        scanner.nextLine();
        List<Photo> result = new ArrayList<>();
        while(scanner.hasNextLine()) {
            result.add( new Photo(scanner.nextLine(),index.getAndIncrement()));
        }
//        result.stream().map(p->p.tags).forEach(s -> s.forEach(t -> App.interests.computeIfAbsent(t, a -> new AtomicInteger()).getAndIncrement()));
//        System.out.println("interests: " + App.interests);
//        //StreamSupport.stream(Spliterators.spliterator(result, Spliterator.SIZED), true).forEach(p -> p.interestScore = p.tags.stream().mapToInt(t -> App.interests.get(t).get()).sum());
//        // StreamSupport.stream(Spliterators.spliterator(result, Spliterator.SIZED), true).forEach(p -> p.interestScore
//        // = p.tags.size()/2);
//        System.out.println("Sorting: ");
//        //result.sort( (p1,p2) -> p1.interestScore - p2.interestScore);
//        
//        result = parabolaSort(result);
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
        return Math.min(Math.min(onlyS1.get(),common.get() ), onlyS2.get());
    }

	private static <T> List<T> parabolaSort(List<T> values) {
		List<T> parabolValues = new ArrayList<T>();
		for (int i = 0; i < values.size(); i++) {
			parabolValues.add(i/2, values.get(i));
		}
		return parabolValues;
	}

}
