package com.example.scrumhelp;

import com.example.scrumhelp.scrum.service.ScrumHelpBotService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.meta.api.objects.User;

@SpringBootApplication
public class ScrumHelpApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScrumHelpApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(ScrumHelpBotService scrumHelpBotService) {
		return args -> {
        User user = new User(2L,
                "firsName",
                false,
                "lastName",
                "userName",
                "ru",
                true,
                true,
                true);
			System.out.println(scrumHelpBotService.registerUser(1L, user));
			System.out.println(scrumHelpBotService.registerUser(1L, user));

			User user2 = new User(3L,
					"firsName2",
					false,
					"lastName2",
					"userName2",
					"ru",
					true,
					true,
					true);
			System.out.println(scrumHelpBotService.registerUser(1L, user2));
		};
	}

}
