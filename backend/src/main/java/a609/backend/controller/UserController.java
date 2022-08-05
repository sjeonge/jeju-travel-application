package a609.backend.controller;

import a609.backend.db.entity.User;
import a609.backend.service.UserService;
import a609.backend.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1")
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    JwtUtil jwtUtil;

    @PatchMapping("/users")
    public ResponseEntity updateUser(@RequestBody User user, @RequestHeader Map<String,Object> token) {
        log.info("----------------------------------------------------");
        log.info(token.toString());
        log.info("----------------------------------------------------");
        log.info(user.getNickname());
        String saveNickname = userService.updateUser(user, (String) token.get("authorization"));
        HashMap<String, String> map = new HashMap<>();
        map.put("nickname", saveNickname);
        return new ResponseEntity(map, HttpStatus.OK);
    }




    @GetMapping("/users/me")
    public ResponseEntity<Map<String, Object>> myProfile(@RequestBody Map<String, String> tokenMap) {
        Map<String, Object> resultMap = new HashMap<>();
        Claims claims = userService.verifyToken(tokenMap.get("token"));
        resultMap.put("id", claims.get("id"));
        resultMap.put("authority", claims.get("authority"));
        resultMap.put("nickname", claims.get("nickname"));
        return new ResponseEntity<Map<String, Object>>(resultMap, HttpStatus.OK);
    }

    @GetMapping("/auth/verify")
    public ResponseEntity<Map<String, Object>> verifyToken(@RequestBody Map<String, String> tokenMap) {
        Map<String, Object> resultMap = new HashMap<>();
        Claims claims = userService.verifyToken(tokenMap.get("token"));
        HttpStatus status = null;
        if (claims == null) {
            resultMap.put("message", "토큰 에러");
            status = HttpStatus.BAD_REQUEST;
        } else {
            resultMap = claims;
            status = HttpStatus.OK;
        }
        return new ResponseEntity<Map<String, Object>>(resultMap, status);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader Map<String,Object> token) {
        userService.logout((String) token.get("authorization"));
        return new ResponseEntity<String>("success", HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestHeader Map<String,Object> token) {

        userService.deleteUser((String) token.get("authorization"));

        return new ResponseEntity<String>("성공", HttpStatus.OK);
    }
}
