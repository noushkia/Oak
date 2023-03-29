package com.oak.application.service;

import com.oak.data.Database;

public abstract class Service {
    protected Database db;

    public Service(Database db) {
        this.db = db;
    }
}
