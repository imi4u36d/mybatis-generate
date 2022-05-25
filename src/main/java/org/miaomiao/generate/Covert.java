package com.miaowu.generate;

import java.util.ArrayList;
import java.util.List;

public class Covert {

    public static Long[] toLongArray(List<Long> list){
        Long[] array = new Long[list.size()];
        return list.toArray(array);
    }

    public static void main(String[] args) {
        List<Long> list = new ArrayList<>();
        list.add(1L);
        list.add(2L);
        list.add(3L);
        Long[] longs = list.toArray(new Long[0]);
        for (int i = 0; i < longs.length; i++) {
            System.out.println(longs[i]);
        }
    }
}
