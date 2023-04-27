package com.oak.application.api;

import com.oak.application.Server;
import com.oak.application.service.CommodityService;
import com.oak.application.service.ProviderService;
import com.oak.data.Pagination;
import com.oak.domain.Commodity;
import com.oak.domain.Provider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/commodities")
public class CommodityController {
    private final Pagination<Commodity> pagination = new Pagination<>();

    private void prepareParams(Map<String, String> params) {
        CommodityService commodityService = Server.getInstance().getServiceLayer().getCommodityService();
        if (params.containsKey("onlyAvailableCommodities")) {
            commodityService.setQuery("onlyAvailableCommodities");
        }
        if (params.containsKey("searchType")) {
            String method = params.get("searchType");
            String input = params.get("searchQuery");
            if (method.contains("provider")) {
                ProviderService providerService = Server.getInstance().getServiceLayer().getProviderService();
                List<Provider> providers = providerService.getProvidersByName(input);
                commodityService.setQuery(providers);
            }
            else {
                commodityService.setQuery(method, input);
            }
        }
        if (params.containsKey("sortBy")) {
            commodityService.setComparator(params.get("sortBy"));
        }
    }

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> getCommodities(@RequestParam Map<String, String> params) {
        prepareParams(params);

        Integer limit = Integer.parseInt(params.get("limit"));
        Integer pageNumber = Integer.parseInt(params.get("page"));
        pagination.setLimit(limit);

        CommodityService commodityService = Server.getInstance().getServiceLayer().getCommodityService();
        List<Commodity> commodities = commodityService.getCommoditiesList();
        commodityService.reset();

        Map<String, Object> response = new HashMap<>();
        response.put("commodities", pagination.getPage(commodities, pageNumber));
        response.put("pages", pagination.getNumberOfPages(commodities));
        return ResponseEntity.ok(response);
    }
}
