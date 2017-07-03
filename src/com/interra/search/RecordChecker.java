package com.interra.search;

import com.interra.Record;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by pgordon on 03.07.2017.
 */
public class RecordChecker implements Checker<Record> {

    public boolean check(Record record, String value) {
        return String.valueOf(record.getId()).equals(value) ||
            record.getName().contains(value) ||
            record.getSurname().contains(value) ||
            record.getPatronymic().contains(value) ||
            record.getPosition().contains(value);
    }

}
