package com.oak.application.service;

import com.oak.data.Database;
import com.oak.data.dao.DAOLayer;

public abstract class Service {
    protected Database db;
    protected DAOLayer daoLayer;

    public Service(Database db, DAOLayer daoLayer) {

        this.db = db;
        this.daoLayer = daoLayer;
    }
}
