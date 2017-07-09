package com.interra.server.storage;


import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by pgordon on 23.06.2017.
 */
public class Storage {
    private static LinkedList<Record> list = new LinkedList<>();

    public static void put(Record record) {
        list.add(record);
    }


    public static String prettyPrint() {
        return prettyPrint(list);
    }

    public static String prettyPrint(Record r) {
        String str = "";
        str += "|";
        str += padRight("" + r.getId(), 2);
        str += "|";
        str += padRight(r.getName(), 10);
        str += "|";
        str += padRight(r.getSurname(), 20);
        str += "|";
        str += padRight(r.getPatronymic(), 15);
        str += "|";
        str += padRight(r.getPosition(), 8);
        str += "|\r\n";
        return str;
    }

    public static String prettyPrint(Collection<Record> records) {
        StringBuilder table = new StringBuilder();
        for (Record r : records) {
            table.append("|--|----------|--------------------|---------------|--------|\r\n");
            table.append(prettyPrint(r));
        }
        table.append("|--|----------|--------------------|---------------|--------|");
        return table.toString();
    }

    public static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }

    public static String findAll(String query) {
        List<Record> result = list.stream()
            .filter(Record.findByQuery(query))
            .collect(Collectors.toList());
        return prettyPrint(result);
    }



}
