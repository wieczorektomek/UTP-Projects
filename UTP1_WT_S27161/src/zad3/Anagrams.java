package zad3;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Anagrams {
    List<String> anagrams;
    Map<String, List<String>> anagramGroups;

    public Anagrams(String path) {
        anagrams = new ArrayList<>();
        anagramGroups = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line;

            while ((line = br.readLine()) != null) {
                String[] words = line.split("\\s+");
                for (String word : words) {
                    String normalizedWord = word.trim().toLowerCase();
                    anagrams.add(normalizedWord);

                    char[] chars = normalizedWord.toCharArray();
                    Arrays.sort(chars);
                    String sortedWord = new String(chars);

                    anagramGroups.computeIfAbsent(sortedWord, k -> new ArrayList<>()).add(normalizedWord);
                }
            }
            br.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<List<String>> getSortedByAnQty() {
        List<List<String>> sortedAnagrams = new ArrayList<>(anagramGroups.values());

        sortedAnagrams.sort((a, b) -> Integer.compare(b.size(), a.size()));

        return sortedAnagrams;
    }


    public String getAnagramsFor(String nextWordToFind) {
        String normalizedWord = nextWordToFind.trim().toLowerCase();
        char[] chars = normalizedWord.toCharArray();
        Arrays.sort(chars);
        String sortedWord = new String(chars);
        List<String> anagrams = anagramGroups.getOrDefault(sortedWord, new ArrayList<>());
        anagrams.remove(normalizedWord);

        return nextWordToFind + ": " + anagrams.toString();
    }

}
