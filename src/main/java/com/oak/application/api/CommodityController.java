package com.oak.application.api;

import com.oak.application.Server;
import com.oak.application.service.CommentService;
import com.oak.application.service.CommodityService;
import com.oak.application.service.ProviderService;
import com.oak.application.service.UserService;
import com.oak.domain.Comment;
import com.oak.domain.Commodity;
import com.oak.domain.Provider;
import com.oak.exception.Comment.CommentNotFound;
import com.oak.exception.Commodity.CommodityNotFound;
import com.oak.exception.Commodity.InvalidRating;
import com.oak.exception.Provider.ProviderNotFound;
import com.oak.exception.User.UserNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/commodities")
public class CommodityController {
    private void prepareParams(Map<String, String> params) {
        CommodityService commodityService = Server.getInstance().getServiceLayer().getCommodityService();
        if (params.containsKey("onlyAvailableCommodities")) {
            commodityService.setQuery("onlyAvailableCommodities");
        }
        if (params.containsKey("searchType")) {
            String method = params.get("searchType");
            String input = params.get("searchQuery");
            commodityService.setQuery(method, input);
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

        CommodityService commodityService = Server.getInstance().getServiceLayer().getCommodityService();
        commodityService.setPagination(limit, pageNumber);
        List<Commodity> commodities = commodityService.getCommoditiesList();

        Map<String, Object> response = new HashMap<>();
        response.put("commodities", commodities);
        response.put("pages", commodityService.getNumberOfPages(limit));
        commodityService.reset();
        return ResponseEntity.ok(response);
    }

    public Map<String, Object> prepareCommodity(Integer commodityId) throws CommodityNotFound {
        CommodityService commodityService = Server.getInstance().getServiceLayer().getCommodityService();
        ProviderService providerService = Server.getInstance().getServiceLayer().getProviderService();

        Commodity commodity = commodityService.getCommodityById(commodityId);
        List<Commodity> suggestions = commodityService.getSuggestedCommodities(commodityId);
        Provider provider = null;
        try {
            provider = providerService.getProvider(commodity.getProviderId());
        } catch (ProviderNotFound ignored) {
        }

        Map<String, Object> response = new HashMap<>();
        response.put("commodity", commodity);
        response.put("suggestions", suggestions);
        response.put("providerName", provider != null ? provider.getName() : null);
        return response;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCommodityById(@PathVariable Integer id) {
        try {
            Map<String, Object> response = prepareCommodity(id);
            return ResponseEntity.ok(response);
        } catch (CommodityNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/{id}")
    public ResponseEntity<Map<String, Object>> rateCommodity(@PathVariable Integer id, @RequestBody Map<String, String> body,
                                                             @RequestAttribute("username") String username) {
        String rating = body.get("rating");

        CommodityService commodityService = Server.getInstance().getServiceLayer().getCommodityService();
        try {
            commodityService.rateCommodity(username, id, rating);

            Map<String, Object> response = prepareCommodity(id);
            return ResponseEntity.ok(response);
        } catch (InvalidRating e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (UserNotFound | CommodityNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<Map<String, Object>> addComment(@PathVariable Integer id, @RequestBody Map<String, String> body,
                                                          @RequestAttribute("username") String username) {
        String text = body.get("text");
        String dateString = body.get("date");
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
        } catch (ParseException ignored) {
        }

        UserService userService = Server.getInstance().getServiceLayer().getUserService();
        try {
            String userEmail = userService.getUser(username).getEmail();
            CommentService commentService = Server.getInstance().getServiceLayer().getCommentService();
            Comment comment = new Comment(userEmail, id, text, date);
            commentService.addComment(comment);
            Map<String, Object> response = prepareCommodity(id);
            return ResponseEntity.ok(response);
        } catch (CommodityNotFound | UserNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/{id}/comments/{commentId}/vote")
    public ResponseEntity<Map<String, Object>> voteComment(@PathVariable Integer id, @PathVariable Integer commentId,
                                                           @RequestBody Map<String, String> body,
                                                           @RequestAttribute("username") String username) {
        Integer vote = Integer.parseInt(body.get("vote"));

        CommentService commentService = Server.getInstance().getServiceLayer().getCommentService();
        try {
            commentService.voteComment(username, commentId, vote);
            Map<String, Object> response = prepareCommodity(id);
            return ResponseEntity.ok(response);
        } catch (UserNotFound | CommentNotFound | CommodityNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
