package com.owod.plateforme_api;

import com.owod.plateforme_api.configuration.MongoTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class PlateformeApiApplicationTests {

	@Test
	void contextLoads() {
	}

}
