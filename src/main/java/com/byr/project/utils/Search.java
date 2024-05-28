package com.byr.project.utils;

public class Search {
    /**
     * 判断des是否在origin中出现，若出现则为true否则为false,不能用直接用find这种要用一些算法比如kmp之类的
     * @param des
     * @param origin
     * @return
     */
    public static boolean search(String des, String origin) {//使用kmp算法，查找匹配字符串
        Search search = new Search();
        int[] next = search.buildNext(des);
        int i = 0, j = 0;
        while (i < origin.length() && j < des.length()) {
            if (j == -1 || origin.charAt(i) == des.charAt(j)) {
                i++;
                j++;
            } else {
                j = next[j];
            }
        }
        return j == des.length();
    }

    private int[] buildNext(String pattern) {
        int[] next = new int[pattern.length()];
        next[0] = -1;
        int k = -1;
        int j = 0;
        while (j < pattern.length() - 1) {
            if (k == -1 || pattern.charAt(j) == pattern.charAt(k)) {
                ++k;
                ++j;
                next[j] = k;
            } else {
                k = next[k];
            }
        }
        return next;
    }
}
