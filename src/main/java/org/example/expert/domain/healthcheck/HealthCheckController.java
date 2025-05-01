package org.example.expert.domain.healthcheck;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class HealthCheckController {

    public ResponseEntity<String> health() {
        log.info("✅ Health check");
        return ResponseEntity.ok("OK");
    }
}
