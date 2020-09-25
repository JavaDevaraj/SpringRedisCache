package com.redis.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@Slf4j
@SpringBootApplication
@EnableCaching
public class RedisCacheDemoProjectApplication implements CommandLineRunner {

	private final UserRepository userRepository;

	@Autowired
	public RedisCacheDemoProjectApplication(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public static void main(String[] args) {
		SpringApplication.run(RedisCacheDemoProjectApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("Saving users. Current user count is {}.", userRepository.count());
		User shubham = new User("Kumar", 2000);
		User pankaj = new User("Monika", 29000);
		User lewis = new User("Samrat", 550);

		userRepository.save(shubham);
		userRepository.save(pankaj);
		userRepository.save(lewis);
		log.info("Done saving users. Data: {}.", userRepository.findAll());
	}
}
