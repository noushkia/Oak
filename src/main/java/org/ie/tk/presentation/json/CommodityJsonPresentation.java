package org.ie.tk.presentation.json;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.ie.tk.application.service.ServiceLayer;
import org.ie.tk.data.Database;
import org.ie.tk.domain.Commodity;
import org.ie.tk.domain.User;

import java.util.List;
import java.util.stream.Collectors;

public class CommodityJsonPresentation extends JsonPresentation{

    public CommodityJsonPresentation(ServiceLayer serviceLayer) {
        super(serviceLayer);
    }

    public static ObjectNode marshallCommodityObject(Commodity commodity){
        ObjectNode commodityNode = mapper.createObjectNode();
        commodityNode.put("id", commodity.getId());
        commodityNode.put("name", commodity.getName());
        commodityNode.put("providerId", commodity.getProviderId());
        commodityNode.put("price", commodity.getPrice());
        commodityNode.set("categories", mapper.valueToTree(commodity.getCategories()));
        commodityNode.put("rating", commodity.getRating());
        return commodityNode;
    }

    public static List<ObjectNode> marshallCommodityObjects(List<Commodity> commodities){
        return commodities.stream()
                .map(CommodityJsonPresentation::marshallCommodityObject)
                .collect(Collectors.toList())
    }

}
