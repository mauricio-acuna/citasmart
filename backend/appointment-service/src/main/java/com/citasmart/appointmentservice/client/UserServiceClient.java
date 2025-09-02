package com.citasmart.appointmentservice.client;

import com.citasmart.appointmentservice.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${app.services.user-service.url:http://localhost:8081}")
public interface UserServiceClient {
    
    @GetMapping("/api/v1/users/{id}")
    UserResponse getUserById(@PathVariable("id") Long id);
    
    @GetMapping("/api/v1/users/email/{email}")
    UserResponse getUserByEmail(@PathVariable("email") String email);
}
