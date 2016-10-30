package com.fererlab;

import java.util.Date;

public class PersonMetaData {

    private Date date;

    public PersonMetaData() {
        date = new Date();
    }

    @Override
    public String toString() {
        return "PersonMetaData{" +
                "date=" + date +
                '}';
    }
}
