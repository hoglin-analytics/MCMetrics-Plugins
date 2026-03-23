package net.mcmetrics.datagenerator;

import gg.hoglin.sdk.Hoglin;
import gg.hoglin.sdk.models.analytic.NamedAnalytic;
import gg.hoglin.sdk.models.analytic.RecordedAnalytic;
import kong.unirest.core.HttpResponse;

import java.util.Collections;
import java.util.List;

public class Main {


    public static void main(String[] args) {
        System.out.println("API URL: " + System.getProperty("hoglin.base.url"));
        System.out.println("API Key: " + System.getProperty("hoglin.api.key"));

        Hoglin hoglin = Hoglin.builder(System.getProperty("hoglin.api.key"))
                .baseUrl(System.getProperty("hoglin.base.url"))
                .build();
        DataGenerator dataGenerator = new DataGenerator();

        System.out.println("Starting simulation...");
        List<RecordedAnalytic<? extends NamedAnalytic>> events = dataGenerator.runSimulation();
        System.out.println("Total events generated: " + events.size());

        hoglin.trackMany(Collections.unmodifiableCollection(events));
        HttpResponse<String> response = hoglin.flush();
        assert response != null;
        System.out.println("Flushed events:\n" +  response.getBody());
    }
}