package com.example.sqlprototype.p4;

import android.util.Pair;

import com.example.sqlprototype.R;

import java.util.HashMap;

public class InstructionsImgMap {
    static HashMap<Pair<Integer, Integer>, Integer> m = new HashMap<>();
    static {
        m.put(new Pair<>(1, -1), R.drawable.imgnodo_1_m1);
        m.put(new Pair<>(1, 2), R.drawable.imgnodo_1_2);
        m.put(new Pair<>(2, 3), R.drawable.imgnodo_2_3);
        m.put(new Pair<>(2, 10), R.drawable.imgnodo_2_10);
        m.put(new Pair<>(3, 10), R.drawable.imgnodo_3_10);
        m.put(new Pair<>(3, 5), R.drawable.imgnodo_3_5);
        m.put(new Pair<>(3, 4), R.drawable.imgnodo_3_4);
        m.put(new Pair<>(4, -2), R.drawable.imgnodo_4_m2);
        m.put(new Pair<>(5, 6), R.drawable.imgnodo_5_6);
        m.put(new Pair<>(6, 7), R.drawable.imgnodo_6_7);
        m.put(new Pair<>(7, 8), R.drawable.imgnodo_7_8);
        m.put(new Pair<>(8, 9), R.drawable.imgnodo_8_9);
        m.put(new Pair<>(9, -3), R.drawable.imgnodo_9_m3);
        m.put(new Pair<>(9, 11), R.drawable.imgnodo_9_11);
        m.put(new Pair<>(10, -4), R.drawable.imgnodo_10_m4);
        m.put(new Pair<>(11, -3), R.drawable.imgnodo_11_m3);
        m.put(new Pair<>(11, -5), R.drawable.imgnodo_11_m5);
        m.put(new Pair<>(-1, 1), R.drawable.imgnodo_m1_1);
        m.put(new Pair<>(2, 1), R.drawable.imgnodo_2_1);
        m.put(new Pair<>(3, 2), R.drawable.imgnodo_3_2);
        m.put(new Pair<>(10, 2), R.drawable.imgnodo_10_2);
        m.put(new Pair<>(10, 3), R.drawable.imgnodo_10_3);
        m.put(new Pair<>(5, 3), R.drawable.imgnodo_5_3);
        m.put(new Pair<>(4, 3), R.drawable.imgnodo_4_3);
        m.put(new Pair<>(-2, 4), R.drawable.imgnodo_m2_4);
        m.put(new Pair<>(6, 5), R.drawable.imgnodo_6_5);
        m.put(new Pair<>(7, 6), R.drawable.imgnodo_7_6);
        m.put(new Pair<>(8, 7), R.drawable.imgnodo_8_7);
        m.put(new Pair<>(9, 8), R.drawable.imgnodo_9_8);
        m.put(new Pair<>(-3, 9), R.drawable.imgnodo_m3_9);
        m.put(new Pair<>(11, 9), R.drawable.imgnodo_11_9);
        m.put(new Pair<>(-4, 10), R.drawable.imgnodo_m4_10);
        m.put(new Pair<>(-3, 11), R.drawable.imgnodo_m3_11);
        m.put(new Pair<>(-5, 11), R.drawable.imgnodo_m5_11);
    }

    static public int getImg(int node1, int node2) {
        Integer result = m.get(new Pair<>(node1, node2));
        if (result == null)
            throw new RuntimeException("getImg with nodes with no img available");
        return result;
    }

    // Prevent instantiation
    private InstructionsImgMap(){}
}
