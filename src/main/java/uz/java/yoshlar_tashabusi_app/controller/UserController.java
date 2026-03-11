package uz.java.yoshlar_tashabusi_app.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.java.yoshlar_tashabusi_app.service.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping("/read-from-excel")
    public ResponseEntity<?> readFromExcel(@RequestBody MultipartFile file) {
        return ResponseEntity.ok(service.importFromExcel(file));
    }
}
