package org.ie.tk.application.service;

import org.ie.tk.data.Database;

public abstract class Service {
    protected Database db;

    public Service(Database db) {
        this.db = db;
    }
}
