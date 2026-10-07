package com.transithub.controller;

import com.transithub.entity.Stop;
import com.transithub.repository.StopRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class StopControllerTest {

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private StopRepository stopRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    private Long stopId(String text) {
        return stopRepository.findByNameContainingIgnoreCase(text).get(0).getId();
    }

    @Test
    void searchFindsStopsByName() throws Exception {
        mockMvc.perform(get("/api/stops").param("search", "market"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Lipa Public Market"));
    }

    @Test
    void nearbyReturnsTheClosestStopFirst() throws Exception {
        // standing exactly at the stop (its coordinates are read from the database)
        Stop market = stopRepository.findByNameContainingIgnoreCase("Lipa Public Market").get(0);
        mockMvc.perform(get("/api/stops/nearby")
                        .param("lat", String.valueOf(market.getLatitude()))
                        .param("lng", String.valueOf(market.getLongitude()))
                        .param("radiusKm", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].stop.name").value("Lipa Public Market"))
                .andExpect(jsonPath("$[0].distanceKm").value(0.0));
    }

    @Test
    void routesThroughAStopAreListed() throws Exception {
        mockMvc.perform(get("/api/stops/" + stopId("Ibaan") + "/routes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].routeCode").exists());
    }

    @Test
    void createAndDeleteAStop() throws Exception {
        String json = "{\"name\":\"Controller Test Stop\",\"description\":\"temp\",\"latitude\":13.95,\"longitude\":121.16}";

        mockMvc.perform(post("/api/stops").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.name").value("Controller Test Stop"));

        mockMvc.perform(delete("/api/stops/" + stopId("Controller Test Stop")))
                .andExpect(status().isNoContent());
        assertTrue(stopRepository.findByNameContainingIgnoreCase("Controller Test Stop").isEmpty());
    }

    @Test
    void invalidStopIsABadRequest() throws Exception {
        // blank name and latitude out of range
        String json = "{\"name\":\" \",\"latitude\":95,\"longitude\":121.16}";
        mockMvc.perform(post("/api/stops").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest());
    }
}
