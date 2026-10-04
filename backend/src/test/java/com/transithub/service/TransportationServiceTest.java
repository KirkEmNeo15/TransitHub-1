package com.transithub.service;

import com.transithub.dto.request.TransportationRequest;
import com.transithub.dto.response.TransportationResponse;
import com.transithub.entity.enums.TransportType;
import com.transithub.exception.DuplicateResourceException;
import com.transithub.exception.InvalidRequestException;
import com.transithub.exception.ResourceInUseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class TransportationServiceTest {

    @Autowired
    private TransportationService transportationService;

    private TransportationRequest van(String code, Integer seats) {
        return new TransportationRequest(TransportType.VAN, "Service Test Van", code, null, null, null, seats);
    }

    @Test
    void eachTypeIsCreatedAsItsOwnSubclass() {
        TransportationResponse createdVan = transportationService.create(van("VAN-SVC-1", 14));
        assertEquals("Van", createdVan.type());
        assertEquals(Map.of("seatingCapacity", 14), createdVan.details());

        TransportationResponse bus = transportationService.create(new TransportationRequest(
                TransportType.BUS, "Service Test Bus", "BUS-SVC-1", null, true, null, null));
        assertEquals("Bus", bus.type());
        assertEquals(Map.of("airConditioned", true), bus.details());

        TransportationResponse jeepney = transportationService.create(new TransportationRequest(
                TransportType.JEEPNEY, "Service Test Jeepney", "JEEP-SVC-1", null, null, true, null));
        assertEquals("Jeepney", jeepney.type());
        assertEquals(Map.of("modernized", true), jeepney.details());
    }

    @Test
    void typeSpecificDetailsAreRequired() {
        assertThrows(InvalidRequestException.class, () -> transportationService.create(van("VAN-SVC-2", null)));
    }

    @Test
    void duplicateCodeIsRejected() {
        transportationService.create(van("VAN-SVC-3", 10));
        assertThrows(DuplicateResourceException.class, () -> transportationService.create(van("VAN-SVC-3", 12)));
    }

    @Test
    void updateChangesTheDetailsButNotTheType() {
        TransportationResponse created = transportationService.create(van("VAN-SVC-4", 10));

        TransportationResponse updated = transportationService.update(created.id(), van("VAN-SVC-4", 18));
        assertEquals(Map.of("seatingCapacity", 18), updated.details());

        TransportationRequest changeType = new TransportationRequest(
                TransportType.BUS, "Service Test Van", "VAN-SVC-4", null, true, null, null);
        assertThrows(InvalidRequestException.class, () -> transportationService.update(created.id(), changeType));
    }

    @Test
    void getAllCanFilterByType() {
        List<TransportationResponse> buses = transportationService.getAll("bus");
        assertTrue(buses.size() >= 1);
        assertTrue(buses.stream().allMatch(t -> t.type().equals("Bus")));
    }

    @Test
    void aTransportationUsedByRoutesCannotBeDeleted() {
        Long busId = transportationService.getAll("BUS").get(0).id();
        assertThrows(ResourceInUseException.class, () -> transportationService.delete(busId));
    }

    @Test
    void anUnusedTransportationCanBeDeleted() {
        TransportationResponse created = transportationService.create(van("VAN-SVC-5", 10));
        transportationService.delete(created.id());
        assertTrue(transportationService.getAll("VAN").stream().noneMatch(t -> t.code().equals("VAN-SVC-5")));
    }
}
