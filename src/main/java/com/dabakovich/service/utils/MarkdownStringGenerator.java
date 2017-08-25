package com.dabakovich.service.utils;

/**
 * Created by dabak on 20.08.2017, 19:20.
 */
public class MarkdownStringGenerator {

    public String bold(String text) {
        return "*" + text + "*";
    }

    public String italic(String text) {
        return "_" + text + "_";
    }

    public String link(String text, String url) {
        return "[" + text + "](" + url + ")";
    }
}
