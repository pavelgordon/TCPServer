package com.interra.search;

/**
 * Created by pgordon on 03.07.2017.
 */
public interface Checker<T> {
    public boolean check(T obj, String query);
}




