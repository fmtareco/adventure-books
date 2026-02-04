package prs.fmtareco.adventure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AdventureApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdventureApplication.class, args);
	}

//    @Bean
//    public CommandLineRunner commandLineRunner() {
//        return args -> {
//            System.out.println(Section.Type.valuesToString());
//            System.out.println(Player.Status.valuesToString());
//            System.out.println(Consequence.Type.valuesToString());
//            System.out.println(Book.Difficulty.valuesToString());
//        };
//    }

}
