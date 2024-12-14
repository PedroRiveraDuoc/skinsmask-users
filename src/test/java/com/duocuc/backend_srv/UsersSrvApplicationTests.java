package com.duocuc.backend_srv;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = UsersSrvApplication.class)
public class UsersSrvApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
    void testMainMethodRunsSuccessfully() {
        // Act & Assert
        assertDoesNotThrow(() -> UsersSrvApplication.main(new String[]{}));
    }
}
