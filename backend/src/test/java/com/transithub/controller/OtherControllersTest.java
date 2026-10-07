package com.transithub.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Transportation, alert, statistics and admin endpoints. */
@SpringBootTest
@Transactional
class OtherControllersTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    // ---------------- transportations ----------------

    @Test
    void transportationsCanBeFilteredAndShowTheirTypeDetails() throws Exception {
        mockMvc.perform(get("/api/transportations").param("type", "bus"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("Bus"))
                .andExpect(jsonPath("$[0].details.airConditioned").exists());
    }

    @Test
    void aVanCanBeCreated() throws Exception {
        String json = "{\"type\":\"VAN\",\"name\":\"Controller Test Van\",\"code\":\"VAN-CTRL-1\",\"seatingCapacity\":14}";
        mockMvc.perform(post("/api/transportations").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("Van"))
                .andExpect(jsonPath("$.details.seatingCapacity").value(14));
    }

    @Test
    void transportationWithoutATypeIsABadRequest() throws Exception {
        String json = "{\"name\":\"No Type\",\"code\":\"NT-1\"}";
        mockMvc.perform(post("/api/transportations").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest());
    }

    // ---------------- alerts ----------------

    @Test
    void activeAlertsAreListed() throws Exception {
        mockMvc.perform(get("/api/alerts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").exists())
                .andExpect(jsonPath("$[0].severity").exists());
    }

    @Test
    void anAlertCanBeCreated() throws Exception {
        String json = "{\"title\":\"Controller test alert\",\"message\":\"temp\",\"severity\":\"WARNING\"}";
        mockMvc.perform(post("/api/alerts").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.severity").value("WARNING"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void alertWithAnUnknownSeverityIsABadRequest() throws Exception {
        String json = "{\"title\":\"Bad\",\"message\":\"temp\",\"severity\":\"PURPLE\"}";
        mockMvc.perform(post("/api/alerts").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest());
    }

    // ---------------- statistics and admin ----------------

    @Test
    void publicStatsAreReturned() throws Exception {
        mockMvc.perform(get("/api/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activeRoutes").exists())
                .andExpect(jsonPath("$.stops").exists())
                .andExpect(jsonPath("$.availableVehicles").exists())
                .andExpect(jsonPath("$.activeAlerts").exists());
    }

    @Test
    void adminStatsAreReturned() throws Exception {
        mockMvc.perform(get("/api/admin/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRoutes").exists())
                .andExpect(jsonPath("$.totalUsers").exists())
                .andExpect(jsonPath("$.openReports").exists());
    }

    @Test
    void adminRouteTableIsPaged() throws Exception {
        mockMvc.perform(get("/api/admin/routes").param("page", "0").param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(3))
                .andExpect(jsonPath("$.size").value(3))
                .andExpect(jsonPath("$.totalItems").exists())
                .andExpect(jsonPath("$.totalPages").exists());
    }

    @Test
    void adminRouteTableCanBeSearched() throws Exception {
        mockMvc.perform(get("/api/admin/routes").param("search", "tambo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].routeCode").value("LTM-JEEP-01"));
    }

    @Test
    void adminStopTableAndAlertListWork() throws Exception {
        mockMvc.perform(get("/api/admin/stops").param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(5));
        mockMvc.perform(get("/api/admin/alerts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());
    }

    @Test
    void adminReportListWorks() throws Exception {
        mockMvc.perform(get("/api/admin/reports").param("status", "OPEN"))
                .andExpect(status().isOk());
    }
}
