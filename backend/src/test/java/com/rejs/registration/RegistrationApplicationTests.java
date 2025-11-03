package com.rejs.registration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class RegistrationApplicationTests {

	@Test
	void contextLoads() {
	}

}
