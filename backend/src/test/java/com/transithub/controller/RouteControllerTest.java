package com.transithub.controller;

import com.transithub.repository.RouteRepository;
import com.transithub.repository.StopRepository;
import com.transithub.repository.TransportationRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Sends fake HTTP requests to the controllers (no real server needed) and checks the
 * status code and JSON. Needs the database with the sample data. Rolled back after each test.
 */
@SpringBootTest
@Transactional
class RouteControllerTest {

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private RouteRepository routeRepository;
    @Autowired
    private StopRepository stopRepository;
    @Autowired
    private TransportationRepository transportationRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    private Long routeId(String code) {
        return routeRepository.findByRouteCode(code).orElseThrow().getId();
    }

    private String routeJson(String code, String status, int firstStopMinutes) {
        Long transportationId = transportationRepository.findAll().get(0).getId();
        Long stopA = stopRepository.findAll().get(0).getId();
        Long stopB = stopRepository.findAll().get(1).getId();
        return """
                {"routeCode":"%s","routeName":"Controller Test Route","origin":"Alpha","destination":"Beta",
                 "status":"%s","estimatedMinutes":30,"distanceKm":10.0,"transportationId":%d,
                 "fare":{"baseFare":15.00,"perKmRate":1.00},
                 "stops":[{"stopId":%d,"minutesFromStart":%d},{"stopId":%d,"minutesFromStart":30}],
                 "schedules":[{"firstTrip":"06:00","lastTrip":"18:00","frequencyMinutes":15,"daysOperating":"MON-SAT"}]}
                """.formatted(code, status, transportationId, stopA, firstStopMinutes, stopB);
    }

    // ---------------- reading ----------------

    @Test
    void getRoutesReturnsTheDemoRoutes() throws Exception {
        mockMvc.perform(get("/api/routes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].routeCode").exists())
                .andExpect(jsonPath("$[0].transportation.type").exists())
                .andExpect(jsonPath("$[0].path").exists());
    }

    @Test
    void getRoutesCanFilterByType() throws Exception {
        mockMvc.perform(get("/api/routes").param("type", "BUS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].transportation.type").value("Bus"));
    }

    @Test
    void searchReturnsTheFastestRouteFirstWithItsFare() throws Exception {
        mockMvc.perform(get("/api/routes/search").param("origin", "Lipa").param("destination", "Batangas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].routeCode").value("LB-BUS-01"))
                .andExpect(jsonPath("$[0].estimatedFare").value(55.0))
                .andExpect(jsonPath("$[1].routeCode").value("LB-JEEP-01"))
                .andExpect(jsonPath("$[1].estimatedFare").value(35.1));
    }

    @Test
    void getRouteByIdReturnsItsDetails() throws Exception {
        mockMvc.perform(get("/api/routes/" + routeId("LB-JEEP-01")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.routeCode").value("LB-JEEP-01"))
                .andExpect(jsonPath("$.transportation.type").value("Jeepney"))
                .andExpect(jsonPath("$.stops.length()").value(3))
                .andExpect(jsonPath("$.schedules.length()").value(1));
    }

    @Test
    void routeStopsComeInOrder() throws Exception {
        mockMvc.perform(get("/api/routes/" + routeId("LB-JEEP-01") + "/stops"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].stopOrder").value(1))
                .andExpect(jsonPath("$[0].stop.name").value("Lipa City Grand Terminal"))
                .andExpect(jsonPath("$[2].stop.name").value("Batangas Grand Terminal"));
    }

    @Test
    void searchWithoutAParameterIsABadRequest() throws Exception {
        mockMvc.perform(get("/api/routes/search").param("origin", "Lipa"))
                .andExpect(status().isBadRequest());
    }

    // ---------------- writing ----------------

    @Test
    void createRouteReturns201() throws Exception {
        mockMvc.perform(post("/api/routes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(routeJson("CTRL-TEST-01", "ACTIVE", 0)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.routeCode").value("CTRL-TEST-01"))
                .andExpect(jsonPath("$.stops.length()").value(2))
                .andExpect(jsonPath("$.estimatedFare").exists());
    }

    @Test
    void invalidRouteIsABadRequest() throws Exception {
        // blank route code, no stops and no fare
        String invalid = """
                {"routeCode":" ","routeName":"X","origin":"A","destination":"B","status":"ACTIVE",
                 "estimatedMinutes":30,"distanceKm":10.0,"transportationId":1,"stops":[]}
                """;
        mockMvc.perform(post("/api/routes").contentType(MediaType.APPLICATION_JSON).content(invalid))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unknownStatusValueIsABadRequest() throws Exception {
        mockMvc.perform(post("/api/routes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(routeJson("CTRL-TEST-02", "FLYING", 0)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateChangeStatusAndDeleteARoute() throws Exception {
        mockMvc.perform(post("/api/routes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(routeJson("CTRL-TEST-03", "ACTIVE", 0)))
                .andExpect(status().isCreated());
        Long id = routeId("CTRL-TEST-03");

        // update
        mockMvc.perform(put("/api/routes/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(routeJson("CTRL-TEST-03", "INACTIVE", 5)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INACTIVE"))
                .andExpect(jsonPath("$.stops[0].minutesFromStart").value(5));

        // change only the status
        mockMvc.perform(patch("/api/routes/" + id + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"SUSPENDED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUSPENDED"));

        // delete
        mockMvc.perform(delete("/api/routes/" + id))
                .andExpect(status().isNoContent());
        assertTrue(routeRepository.findByRouteCode("CTRL-TEST-03").isEmpty(), "the route should be gone");
    }
}
