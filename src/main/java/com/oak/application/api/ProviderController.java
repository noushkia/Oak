package com.oak.application.api;

import com.oak.application.Server;
import com.oak.application.service.ProviderService;
import com.oak.domain.Provider;
import com.oak.exception.Provider.ProviderNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/providers")
public class ProviderController {
    @GetMapping("/{id}")
    public ResponseEntity<Provider> getProviderById(@PathVariable Integer id) {
        ProviderService providerService = Server.getInstance().getServiceLayer().getProviderService();
        try {
            Provider provider = providerService.getProviderById(id);
            return ResponseEntity.ok(provider);
        } catch (ProviderNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
