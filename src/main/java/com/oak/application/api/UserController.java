package com.oak.application.api;

import com.oak.application.Server;
import com.oak.application.service.UserService;
import com.oak.domain.User;
import com.oak.exception.Commodity.CommodityInBuyList;
import com.oak.exception.Commodity.CommodityNotFound;
import com.oak.exception.Commodity.CommodityOutOfStock;
import com.oak.exception.User.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000",
            methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
            allowedHeaders = "*")
@RequestMapping("/api/users")
public class UserController {
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        UserService userService = Server.getInstance().getServiceLayer().getUserService();
        try {
            userService.login(username, password);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (UserNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (InvalidCredentials e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String email = body.get("email");
        String address = body.get("address");
        String dateString = body.get("birthDate");
        Date birthDate = null;
        try {
            birthDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
        } catch (ParseException ignored) {}

        UserService userService = Server.getInstance().getServiceLayer().getUserService();
        User user = new User(username, password, email, birthDate, address, 0);
        try {
            userService.addUser(user);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (InvalidUsername e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> getUserById(@PathVariable String username) {
        UserService userService = Server.getInstance().getServiceLayer().getUserService();
        try {
            User user = userService.getUserById(username);
            return ResponseEntity.ok(user);
        } catch (UserNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/{username}/buyList")
    public ResponseEntity<User> addToBuyList(@PathVariable String username, @RequestBody Map<String, Integer> body) {
        Integer commodityId = body.get("commodityId");

        UserService userService = Server.getInstance().getServiceLayer().getUserService();
        try {
            userService.addToBuyList(username, commodityId);
            User user = userService.getUserById(username);
            return ResponseEntity.ok(user);
        } catch (UserNotFound | CommodityNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (CommodityOutOfStock | CommodityInBuyList e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{username}/buyList")
    public ResponseEntity<User> updateBuyList(@PathVariable String username, @RequestBody Map<String, Integer> body) {
        Integer commodityId = body.get("commodityId");
        Integer quantity = body.get("quantity");

        UserService userService = Server.getInstance().getServiceLayer().getUserService();
        try {
            userService.updateBuyListCommodityCount(username, commodityId, quantity);
            User user = userService.getUserById(username);
            return ResponseEntity.ok(user);
        } catch (UserNotFound | CommodityNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (CommodityOutOfStock e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/{username}/buyList/finalize")
    public ResponseEntity<User> finalizeBuyList(@PathVariable String username) {
        UserService userService = Server.getInstance().getServiceLayer().getUserService();
        try {
            userService.finalizeBuyList(username);
            User user = userService.getUserById(username);
            return ResponseEntity.ok(user);
        } catch (UserNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (CommodityOutOfStock | InsufficientCredit e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }


    @PutMapping("/{username}/credit")
    public ResponseEntity<User> addCredit(@PathVariable String username, @RequestBody Map<String, Integer> body) {
        Integer amount = body.get("credit");

        UserService userService = Server.getInstance().getServiceLayer().getUserService();
        try {
            userService.addCredit(username, amount);
            User user = userService.getUserById(username);
            return ResponseEntity.ok(user);
        } catch (UserNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (NegativeCredit e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
