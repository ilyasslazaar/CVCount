package io.novelis.filtragecv.service.textprocessing;

import java.util.HashMap;

public abstract class TextProcessor {

    protected String filePath;
    protected HashMap<String, Integer> words;

    public TextProcessor(String path){
        this.filePath = path;
        words = new HashMap<>();
        extractWords(extractText());
    }


    public boolean validWord(String word) {
        if (word.length() <= 1) {
            return false;
        }
        int i;
        for (i = 0; i < word.length() && !Character.isLetter(word.charAt(i)); i++) ;
        if (i == word.length()) {
            return false;
        }
        return true;
    }

    protected abstract String extractText();


    public void extractWords(String text) {
        int textLength = text.length();
        int i;
        int wordBegin = 0;
        //skip spaces in the beginning
        for (i = 0; i < textLength && !Character.isLetterOrDigit(text.charAt(i)); i++) ;
        //start getting words
        while (i < textLength) {
            if (!Character.isLetterOrDigit(text.charAt(i)) || i == textLength - 1) {
                String word;
                if (i == textLength - 1) {
                    word = text.substring(wordBegin, i + 1);
                } else {
                    word = text.substring(wordBegin, i);
                }
                if (words.get(word) != null) {
                    words.put(word, words.get(word) + 1);
                } else {
                    words.put(word, 1);
                }
                while (i < textLength && !Character.isLetterOrDigit(text.charAt(i))) {
                    i++;
                }
                wordBegin = i;
            }
            i++;
        }
    }
    public HashMap<String, Integer> getWords() {
        return words;
    }

}
