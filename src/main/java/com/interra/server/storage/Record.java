package com.interra.server.storage;

import java.util.function.Predicate;

/**
 * Created by pgordon on 23.06.2017.
 */
public class Record {
    private static int uid = 1;
    private int id;
    private String name;
    private String surname;
    private String patronymic;
    private String position;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public boolean commit() {
        this.id = uid++;
        Storage.put(this);
        return true;
    }

    public static boolean isValidName(String name) {
        return name.length() < 10 && isLettersOnly(name);
    }

    public static boolean isValidSurname(String surname) {
        return surname.length() < 20;
    }

    public static boolean isValidPatronymic(String patronymic) {
        return patronymic.length() < 15;
    }

    public static boolean isValidPosition(String position) {
        return position.length() < 8 && isLettersOnly(position);
    }


    private static boolean isLettersOnly(String name) {
        return name.matches("[a-zA-Zа-яА-Я]+");
    }

    public static Predicate<Record> findByQuery(String value) {
        return record -> String.valueOf(record.getId()).equals(value) ||
            record.getName().contains(value) ||
            record.getSurname().contains(value) ||
            record.getPatronymic().contains(value) ||
            record.getPosition().contains(value);
    }


}
