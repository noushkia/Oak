package com.oak.application.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oak.application.Server;
import com.oak.application.service.AuthService;
import com.oak.domain.User;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/callback")
public class CallBackController {
    @PostMapping("")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        try {
            String code = body.get("code");
            Document doc = Jsoup.connect("https://github.com/login/oauth/access_token")
                    .header("Accept", "application/json")
                    .data("client_id", "31ca176f1af22ef04b8f")
                    .data("client_secret", "af411e36027af2ef7e941a3b9293a7bd019d2893")
                    .data("code", code)
                    .ignoreContentType(true).post();

            String token_field = "\"access_token\":\"";
            int starting_index = doc.wholeText().indexOf(token_field) + token_field.length();
            int ending_index = doc.wholeText().indexOf("\"", starting_index + 1);
            String access_token = doc.wholeText().substring(starting_index, ending_index);
            doc = Jsoup.connect("https://api.github.com/user")
                    .header("Authorization", "token " + access_token)
                    .ignoreContentType(true).get();

            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> map = mapper.readValue(doc.wholeText(), new TypeReference<>() {
            });
            String username = map.get("login").toString();
            String email = map.get("email").toString();
            String created_at = map.get("created_at").toString();
            Date birthDate = null;
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date date = sdf.parse(created_at);

                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.add(Calendar.YEAR, -18);
                birthDate = cal.getTime();
            } catch (ParseException ignored) {
            }

            User user = new User(username, null, email, birthDate, "address", 0);

            Server.getInstance().getServiceLayer().getUserService().addUser(user, false);
            String jwt = AuthService.generateJWT(username);
            return ResponseEntity.ok(jwt);
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}