package com.sx.quality.bean;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Create dell By 2018/6/6 15:32
 */

public class SearchRecordBean extends DataSupport implements Serializable {
    private String searchTitle;

    public String getSearchTitle() {
        return searchTitle;
    }

    public void setSearchTitle(String searchTitle) {
        this.searchTitle = searchTitle;
    }
}
