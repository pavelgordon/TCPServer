package com.interra;

import com.interra.search.Checker;
import com.interra.search.RecordChecker;
import com.sun.org.apache.regexp.internal.RE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by pgordon on 23.06.2017.
 */
public class Storage {
    private static LinkedList<Record> list = new LinkedList<>();
    public Record createRecord(){
        return null;
    }
    public static void put(Record record){
        list.add(record);
    }

    public static String prettyPrint(){
        return prettyPrint(list);
    }

    public static String prettyPrint(Record r){
        String str = "";
        str+="|";  str+=padRight(""+r.getId(), 2);
        str+="|";  str+=padRight(r.getName(), 10);
        str+="|";  str+=padRight(r.getSurname(), 20);
        str+="|";  str+=padRight(r.getPatronymic(), 15);
        str+="|";  str+=padRight(r.getPosition(), 8);
        str+="|\n";
        return str;
    }
    public static String prettyPrint(Collection<Record> records){
        StringBuilder table = new StringBuilder();
        for(Record r : records){
            table.append("|--|----------|--------------------|---------------|--------|\n");
            table.append(prettyPrint(r));
        }
        table.append("|--|----------|--------------------|---------------|--------|");
        return table.toString();
    }

    public static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }

    public static String findAll(Checker<Record> chk, String query) {
        LinkedList<Record> l = new LinkedList<Record>();
        for (Record obj : list) {
            if (chk.check(obj, query) )
                l.add(obj);
        }
        return prettyPrint(l);
    }
    public static String findAll(String query) {
        return findAll(new RecordChecker(), query);
    }


}
