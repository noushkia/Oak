package com.oak.application.api;

import com.oak.application.Server;
import com.oak.application.service.UserService;
import com.oak.domain.User;
import com.oak.exception.User.InvalidCredentials;
import com.oak.exception.User.UserNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/users")
public class UserController {
    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        UserService userService = Server.getInstance().getServiceLayer().getUserService();
        try {
            User user = userService.login(username, password);
            return ResponseEntity.ok(user);
        } catch (UserNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (InvalidCredentials e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
