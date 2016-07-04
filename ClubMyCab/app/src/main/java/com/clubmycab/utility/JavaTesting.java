package com.clubmycab.utility;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by newpc on 2/5/16.
 */
public class JavaTesting {
    public static void main(String[] args) {
        ArrayList<Double> arrayList = new ArrayList<Double>();
        arrayList.add(6.0);
        arrayList.add(8.0);
        arrayList.add(5.0);
        arrayList.add(9.0);
        arrayList.add(3.0);
        arrayList.add(7.0);
        Collections.sort(arrayList);
        for (int i = 0; i < arrayList.size(); i++) {
            System.out.println(arrayList.get(i));
        }
    }
}
