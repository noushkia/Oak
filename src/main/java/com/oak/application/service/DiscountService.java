package com.oak.application.service;

import com.oak.data.Database;
import com.oak.domain.Discount;

public class DiscountService extends Service {
    public DiscountService(Database db) {
        super(db);
    }
    public void addDiscount(Discount discount) {
        db.addDiscount(discount);
    }
}
