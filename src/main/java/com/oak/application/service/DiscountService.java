package com.oak.application.service;

import com.oak.data.Database;
import com.oak.data.dao.DAOLayer;
import com.oak.data.dao.DiscountDAO;
import com.oak.domain.Discount;

public class DiscountService extends Service {
    public DiscountService(Database db, DAOLayer daoLayer) {
        super(db, daoLayer);
    }
    public void addDiscount(Discount discount) {
        DiscountDAO discountDAO = daoLayer.getDiscountDAO();
        discountDAO.addDiscount(discount);
    }
}
