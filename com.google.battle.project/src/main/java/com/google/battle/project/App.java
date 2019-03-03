package com.google.battle.project;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Hello world!
 *
 */
public class App {
	private final List<H> hList;
	private final List<Slide> slideListResult;
	private final List<V> vList;

	public App() {
		hList = new ArrayList<App.H>();
		vList = new ArrayList<App.V>();
		slideListResult = new ArrayList<App.Slide>();
	}

	private static final String[] fileList = new String[] { "a_example.txt", "b_lovely_landscapes.txt",
			"c_memorable_moments.txt", "d_pet_pictures.txt", "e_shiny_selfies.txt" };

	private class Slide {
		final String lineNr;
		final String[] tags;

		public Slide(String lineNr, String[] tags) {
			this.lineNr = lineNr;
			this.tags = tags;
		}

		public String getLineNr() {
			return lineNr;
		}

		public String[] getTags() {
			return tags;
		}

	}

	private class H {
		final int lineNr;
		final String[] tags;

		public H(int lineNr, String[] tags) {
			this.lineNr = lineNr;
			this.tags = tags;
		}

		public int getLineNr() {
			return lineNr;
		}

		public String[] getTags() {
			return tags;
		}

	}

	private class V {
		final int lineNr;
		final String[] tags;

		public V(int lineNr, String[] tags) {
			this.lineNr = lineNr;
			this.tags = tags;
		}

		public int getLineNr() {
			return lineNr;
		}

		public String[] getTags() {
			return tags;
		}

	}

	public static void main(String[] args) {
		App app = new App();
		app.prepare(args);
	}

	private void prepare(String[] args) {
		long total = 0;
		for (String fileIn : fileList) {
			ClassLoader classLoader = App.class.getClassLoader();
			File file = new File(classLoader.getResource(fileIn).getFile());
			Charset charset = Charset.forName("ISO-8859-1");
			try {
				List<String> lines = Files.readAllLines(file.toPath(), charset);
				int lineNr = 0;
				boolean skipfirst = true;
				for (String line : lines) {
					if (skipfirst) {
						skipfirst = false;
						continue;
					}
					if (line.startsWith("H")) {
						H h = new H(lineNr, computeTags(line));
						hList.add(h);
					} else { // V
						V v = new V(lineNr, computeTags(line));
						vList.add(v);
					}
					++lineNr;
				}
				System.out.println(fileIn + " H(" + hList.size() + ")" + " V(" + vList.size() + ")");
				total += process(fileIn);

			} catch (IOException e) {
				System.out.println(e);
			}
			hList.clear();
			vList.clear();
			slideListResult.clear();
		}
		System.out.println(total);

	}

	private long process(String file) {
		long score = 0;
		try {

			List<Slide> hSlideList = new ArrayList<App.Slide>();
			if (!hList.isEmpty()) {
				for (H h : hList) {
					hSlideList.add(new Slide(String.valueOf(h.getLineNr()), h.getTags()));
				}

				Slide currH = hSlideList.get(0);
				slideListResult.add(currH);
				hSlideList.remove(0);

				score = computeResult(score, hSlideList, currH);
			}
			
			V vBuffer = null;
			List<Slide> vSlideList = new ArrayList<App.Slide>();
			for (V v : vList) {
				if (vBuffer == null) {
					vBuffer = v;
				} else {
					StringBuilder sbt = new StringBuilder();
					sbt.append(vBuffer.getLineNr());
					sbt.append(" ");
					sbt.append(v.getLineNr());
					Slide vSlide = new Slide(sbt.toString(), computeTags(v.getTags(), vBuffer.getTags()));
					vSlideList.add(vSlide);
					vBuffer= null;
				}

			}
			if (!vSlideList.isEmpty()) {
				if (slideListResult.isEmpty()) {
					Slide currV = vSlideList.get(0);
					slideListResult.add(currV);
					vSlideList.remove(0);
					score = computeResult(score, vSlideList, currV);
				} else {

					score = computeResult(score, vSlideList, slideListResult.get(slideListResult.size() - 1));
				}
			}
			int count = slideListResult.size();
			StringBuilder sb = new StringBuilder();
			for (Slide s : slideListResult) {
				sb.append(s.getLineNr());
				sb.append("\n");
			}
			Files.writeString(Paths.get(file + ".out"), count + "\n" + sb.toString());
			System.out.println(file + ".out (" + score + ")");

		} catch (IOException e) {
			System.out.println(e);
		}
		return score;

	}

	private long computeResult(long score, List<Slide> slideList, Slide currSlide) {
		int i = 0;
		int scoreMax = -1;
		long newScore = score;
		Slide bestSlide = null;
		while (!slideList.isEmpty()) {
			i = 0;
			scoreMax = -1;
			for (Slide h : slideList) {
				if (i >= 400) {
					break;
				}
				int scoreTmp = getPairScore(currSlide.getTags(), h.getTags());
				if (scoreTmp > scoreMax) {
					bestSlide = h;
					scoreMax = scoreTmp;
				}
				++i;
			}
			currSlide = bestSlide;
			slideListResult.add(new Slide(String.valueOf(currSlide.getLineNr()), currSlide.getTags()));
			slideList.remove(currSlide);
			newScore += scoreMax;
		}
		return newScore;
	}

	private static String[] computeTags(String[] vTags, String[] vTagsBuffer) {
		var set1 = Set.of(vTags);
		var set2 = Set.of(vTagsBuffer);
		Set<String> set3 = new HashSet<String>();
		set3.addAll(set1);
		set3.addAll(set2);

		return set3.toArray(new String[set3.size()]);
	}

	private static int getPairScore(String[] currentTags, String[] previousTags) {
		int common = findMatchCount(currentTags, previousTags);
		int onlyCurrent = currentTags.length - common;
		int onlyPrevious = previousTags.length - common;

		return getMin(onlyCurrent, common, onlyPrevious);
	}

	public static int findMatchCount(final String[] a, final String[] b) {
		List<String> commonElements = new ArrayList<String>();

		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < b.length; j++) {
				if (a[i].contentEquals(b[j])) {
					// Check if the list already contains the common element
					if (!commonElements.contains(a[i])) {
						// add the common element into the list
						commonElements.add(a[i]);
					}
				}
			}
		}
		return commonElements.size();
	}

	private static int getMin(int onlyCurrent, int common, int onlyPrevious) {
		return onlyCurrent < common ? onlyCurrent < onlyPrevious ? onlyCurrent : onlyPrevious
				: common < onlyPrevious ? common : onlyPrevious;
	}

	private static String[] computeTags(final String line) {
		String[] tokens = line.split(" ");
		return Arrays.copyOfRange(tokens, 2, tokens.length);
	}

}
