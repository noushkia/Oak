package com.oak.application.api;

import com.oak.application.Server;
import com.oak.application.service.ProviderService;
import com.oak.application.service.UserService;
import com.oak.domain.Provider;
import com.oak.domain.User;
import com.oak.exception.Provider.ProviderNotFound;
import com.oak.exception.User.UserNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
        allowedHeaders = "*")
@RequestMapping("/api/providers")
public class ProviderController {
    @GetMapping("/{id}")
    public ResponseEntity<Provider> getUserById(@PathVariable Integer id) {
        ProviderService providerService = Server.getInstance().getServiceLayer().getProviderService();
        try {
            Provider provider = providerService.getProviderById(id);
            return ResponseEntity.ok(provider);
        } catch (ProviderNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
