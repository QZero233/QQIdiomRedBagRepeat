package com.qzero.repeat;

public class Idiom {

    private String startPronunciation;
    private String endPronunciation;
    private String idiom;

    public Idiom() {
    }

    public Idiom(String startPronunciation, String endPronunciation, String idiom) {
        this.startPronunciation = startPronunciation;
        this.endPronunciation = endPronunciation;
        this.idiom = idiom;
    }

    public String getStartPronunciation() {
        return startPronunciation;
    }

    public void setStartPronunciation(String startPronunciation) {
        this.startPronunciation = startPronunciation;
    }

    public String getEndPronunciation() {
        return endPronunciation;
    }

    public void setEndPronunciation(String endPronunciation) {
        this.endPronunciation = endPronunciation;
    }

    public String getIdiom() {
        return idiom;
    }

    public void setIdiom(String idiom) {
        this.idiom = idiom;
    }

    @Override
    public String toString() {
        return "Idiom{" +
                "startPronunciation='" + startPronunciation + '\'' +
                ", endPronunciation='" + endPronunciation + '\'' +
                ", idiom='" + idiom + '\'' +
                '}';
    }
}
