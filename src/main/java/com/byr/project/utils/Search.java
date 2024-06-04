package com.byr.project.utils;

public class Search {
    /**
     * 判断des是否在origin中出现，若出现则为true否则为false,使用BM算法
     * @param des
     * @param origin
     * @return
     */
    public static boolean search(String des, String origin) {
        int[] right = new int[256];
        for (int c = 0; c < 256; c++)
            right[c] = -1;
        for (int j = 0; j < des.length(); j++)
            right[des.charAt(j)] = j;

        int M = des.length();
        int N = origin.length();
        int skip;
        for (int i = 0; i <= N - M; i += skip) {
            skip = 0;
            for (int j = M - 1; j >= 0; j--) {
                if (des.charAt(j) != origin.charAt(i + j)) {
                    skip = Math.max(1, j - right[origin.charAt(i + j)]);
                    break;
                }
            }
            if (skip == 0) return true;    // 找到
        }
        return false;                       // 没找到
    }
}
