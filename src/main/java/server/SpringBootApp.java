package server;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;

/**
 * Spring-Boot-Einstiegsklasse. Wird von Main.java in einem Daemon-Thread
 * gestartet, nicht ueber eine eigene main-Methode.
 */
@SpringBootApplication
public class SpringBootApp {

    @Bean
    public GameState gameState() {
        return GameState.getInstance();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        GameState.getInstance().markServerReady();
    }

    public static void startInBackground(String[] args) {
        // headless(false) noetig, da sonst Spring java.awt.headless=true setzt
        // und das Swing-Fenster nicht mehr geoeffnet werden kann
        Thread springThread = new Thread(() -> new SpringApplicationBuilder(SpringBootApp.class)
                .headless(false)
                .run(args));
        springThread.setDaemon(true);
        springThread.setName("spring-boot");
        springThread.start();
    }
}
