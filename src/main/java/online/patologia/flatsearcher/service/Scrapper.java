package online.patologia.flatsearcher.service;

import online.patologia.flatsearcher.model.Flat;
import online.patologia.flatsearcher.repositories.FlatRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@EnableScheduling
public class Scrapper {
    @Autowired
    private FlatRepository flatRepository;
    @Autowired
    private EmailService emailService;


//    @Scheduled(fixedDelay = 10000)
//    public void xD() throws IOException{
//        System.out.println(getAllLinks().get(0));
//    }
    @Scheduled(fixedDelay = 200000)
    public void checkIfThereIsNewFlat() throws IOException {
        StringBuilder sb = new StringBuilder("Nowe mieszkaia to: ");
        int counter=0;
        for (String link : getAllLinks()) {
            if (flatRepository.findByLink(link)==null) {
                flatRepository.save(new Flat(link, LocalDate.now()));
                sb.append("\n"+link);
                counter++;
            }
        }
       if (counter>0) {
            emailService.sendSimpleMessage("mefedroniarzodmieszkan@gmail.com", "nowe mieszkanie", sb.toString());
       }
        System.out.println(flatRepository.findByLink("https://www.olx.pl/oferta/wynajem-340-zl-doba-nowy-apartamentowiec-gdansk-przymorze-8min-do-mo").getLink());

    }

    public void saveAllLinks(List<String> links) {
        for (String link : links) {
            flatRepository.save(new Flat(link, LocalDate.now()));
        }
    }

    public List<String> getAllLinks() throws IOException {
        List<String> links = new ArrayList<>();
        Document doc = Jsoup
            .connect("https://www.olx.pl/nieruchomosci/mieszkania/wynajem/gdynia/?search%5Bfilter_enum_rooms%5D%5B0%5D=three&search%5Bdist%5D=10")
            .get();
        Elements elements = doc.getElementsByClass("thumb vtop inlblk rel tdnone linkWithHash scale4 detailsLink ");
        for (Element element : elements) {
        links.add(element.attr("href"));
        }
        return links;
    }

    @Bean
    CommandLineRunner runner() {
        return args -> {
            saveAllLinks(getAllLinks());
            };
    }
}
