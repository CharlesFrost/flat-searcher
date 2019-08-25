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


    @Scheduled(fixedDelay = 300000)
    public void checkIfThereIsNewFlat() throws IOException {
        StringBuilder sb = new StringBuilder("Nowe mieszkania to: ");
        int counter=0;
        for (String link : getAllLinks()) {
            if (flatRepository.findByLinkStartsWith(link)==null) {
                flatRepository.save(new Flat(link, LocalDate.now()));
                sb.append("\n"+link);
                counter++;
            }
        }
       if (counter>0) {
           emailService.sendSimpleMessage("mefedroniarzodmieszkan@gmail.com", "nowe mieszkanie", sb.toString());

       }
    }

    public void saveAllLinks(List<String> links) {
        for (String link : links) {
            flatRepository.save(new Flat(link, LocalDate.now()));
        }
    }

    public List<String> getAllLinks() throws IOException {
        List<String> links = new ArrayList<>();
        List<String> finalList = new ArrayList<>();
        Document doc = Jsoup
            .connect("https://www.olx.pl/nieruchomosci/mieszkania/wynajem/gdynia/?search%5Bfilter_float_price%3Afrom%5D=1000&search%5Bfilter_float_price%3Ato%5D=3400&search%5Bfilter_enum_rooms%5D%5B0%5D=three&search%5Bdist%5D=15")
            .get();
        Elements elements = doc.getElementsByAttributeValueContaining("class","thumb vtop inlblk rel tdnone linkWithHash scale4 detailsLink");
        for (Element element : elements) {
            String s = element.attr("href").split("html")[0];
            s+="html";

            links.add(s);
        }
        links.remove(0);
        links.remove(0);
        links.remove(0);
        links.remove(0);
        links.remove(0);

        //////////////////////////
        for (String link : links) {
            Document docCheck = Jsoup.connect(link).get();
            Elements elementsCheck = docCheck.getElementsByClass("pdingtop10");
            for (Element elementCheck : elementsCheck) {
                String xD = elementCheck.getElementsByTag("strong").toString();
                if (xD.contains("strong")) {
                    //xD = xD.substring(xD.indexOf("<strong>")+1);
                    xD = xD.split("</strong>")[0];
                    xD = xD.replace("<strong>", "");
                    xD = xD.replace(" ", "");
                    xD = xD.replace("   ", "");

                    int i = Integer.parseInt(xD);
                    if (i < 100) {
                        finalList.add(link);
                    }
                }

            }
        }
        return finalList;
    }

    @Bean
    CommandLineRunner runner() {
        return args -> {
            saveAllLinks(getAllLinks());
            };
    }
}
