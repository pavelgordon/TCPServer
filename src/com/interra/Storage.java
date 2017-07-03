package com.interra;

import com.sun.org.apache.regexp.internal.RE;

import java.util.ArrayList;

/**
 * Created by pgordon on 23.06.2017.
 */
public class Storage {
    private static ArrayList<Record> list = new ArrayList<>();
    public Record createRecord(){
        return null;
    }
    public static void put(Record record){
        list.add(record);
    }
    public static String prettyPrint(){
        StringBuilder table = new StringBuilder();
        table.append("|--|----------|--------------------|---------------|--------|\n");
        for(Record r : list){
            table.append("|");  table.append(padRight(""+r.getId(), 2));
            table.append("|");  table.append(padRight(r.getName(), 10));
            table.append("|");  table.append(padRight(r.getSurname(), 20));
            table.append("|");  table.append(padRight(r.getPatronymic(), 15));
            table.append("|");  table.append(padRight(r.getPosition(), 8));
            table.append("|\n");
            table.append("|--|----------|--------------------|---------------|--------|");

        };
        return table.toString();
    }
    public static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }


}
