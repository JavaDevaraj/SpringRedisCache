package com.redis.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@Slf4j
public class UserController {

    private final UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Cacheable(value = "users", key = "#userId")
    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public User getUser(@PathVariable String userId) {
        log.info("Getting user with ID {}.", userId);
        return userRepository.findById(Long.valueOf(userId)).get();
    }

    @CachePut(value = "users", key = "#user.id")
    @PutMapping("/update")
    public User updatePersonByID(@RequestBody User user) {
        log.info("Save user.");
        user =userRepository.save(user);
        return user;
    }

    @Cacheable(value = "allUsers", sync=true)
    @GetMapping("/getall")
    public List<User> getAllUser() {
        log.info("Get all user.");
        return userRepository.findAll();
    }

    @CacheEvict(value = "users", allEntries=true)
    @DeleteMapping("/{userId}")
    public void deleteUserByID(@PathVariable Long userId) {
        log.info("deleting person with id {}", userId);
        userRepository.deleteById(userId);
    }

    @CacheEvict(value = "allUsers", allEntries=true)
    @GetMapping("/clearcache")
    public void clearCache() {
        log.info("Get All Cache Cleared");

    }
}

