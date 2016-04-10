package com.bupt.xkc.historytoday.models;

import java.io.Serializable;

/**
 * 历史事件列表
 *
 "day":"4\/8",
 "date":"前565年04月08日",
 "title":"释迦牟尼诞辰",
 "e_id":"4324"
 * Created by xkc on 4/7/16.
 */
public class ListModel{
    private String e_id;
    private String day;
    private String date;
    private String title;

    public ListModel(String e_id, String day, String date, String title) {
        this.e_id = e_id;
        this.day = day;
        this.date = date;
        this.title = title;
    }

    public String getE_id() {
        return e_id;
    }

    public void setE_id(String e_id) {
        this.e_id = e_id;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
