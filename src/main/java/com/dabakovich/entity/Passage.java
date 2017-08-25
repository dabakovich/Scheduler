package com.dabakovich.entity;

/**
 * Created by dabak on 14.08.2017, 0:15.
 */
public class Passage {

    private String book;

    private String verses;

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book.substring(0, getLastIndexOfLetter(book) + 1);
    }

    public String getVerses() {
        return verses;
    }

    public void setVerses(String verses) {
        this.verses = verses.substring(getLastIndexOfLetter(verses) + 2);
    }

    private int getLastIndexOfLetter(String string) {
        int lastIndexOfLetter = 0;
        char[] chars = new char[string.length()];
        string.getChars(0, string.length() - 1, chars, 0);
        for (int i = 0; i < chars.length; i++) {
            if (Character.isLetter(chars[i])) {
                lastIndexOfLetter = i;
            }
        }
        return lastIndexOfLetter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Passage passage = (Passage) o;

        if (!book.equals(passage.book)) return false;
        return verses != null ? verses.equals(passage.verses) : passage.verses == null;
    }

    @Override
    public int hashCode() {
        int result = book.hashCode();
        result = 31 * result + (verses != null ? verses.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Passage{" +
                "book='" + book + '\'' +
                ", verses='" + verses + '\'' +
                '}';
    }
}
