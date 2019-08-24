package online.patologia.flatsearcher;

import online.patologia.flatsearcher.service.Scrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class FlatSearcherApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(FlatSearcherApplication.class, args);
	}

}
