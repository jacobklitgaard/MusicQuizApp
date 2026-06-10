package org.example.musicquizapp;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MusicQuizAppApplicationTests {

	@Test
    @Disabled("Kræver eksterne API-nøgler – køres ikke i CI")
	void contextLoads() {
	}

}
