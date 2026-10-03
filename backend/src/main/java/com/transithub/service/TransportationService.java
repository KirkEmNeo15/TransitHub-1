package com.transithub.service;

import com.transithub.dto.request.TransportationRequest;
import com.transithub.dto.response.TransportationResponse;
import com.transithub.entity.Bus;
import com.transithub.entity.Jeepney;
import com.transithub.entity.Shuttle;
import com.transithub.entity.Train;
import com.transithub.entity.Transportation;
import com.transithub.entity.Van;
import com.transithub.exception.DuplicateResourceException;
import com.transithub.exception.InvalidRequestException;
import com.transithub.exception.ResourceInUseException;
import com.transithub.exception.ResourceNotFoundException;
import com.transithub.mapper.TransportationMapper;
import com.transithub.repository.RouteRepository;
import com.transithub.repository.TransportationRepository;
import com.transithub.repository.VehicleRepository;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransportationService {

    private final TransportationRepository transportationRepository;
    private final RouteRepository routeRepository;
    private final VehicleRepository vehicleRepository;
    private final TransportationMapper transportationMapper;

    public TransportationService(TransportationRepository transportationRepository,
                                 RouteRepository routeRepository,
                                 VehicleRepository vehicleRepository,
                                 TransportationMapper transportationMapper) {
        this.transportationRepository = transportationRepository;
        this.routeRepository = routeRepository;
        this.vehicleRepository = vehicleRepository;
        this.transportationMapper = transportationMapper;
    }

    /** All transportation services; optional filter by type name such as "BUS". */
    @Transactional(readOnly = true)
    public List<TransportationResponse> getAll(String type) {
        return transportationRepository.findAllByOrderByNameAsc().stream()
                .filter(t -> type == null || type.isBlank() || t.getTransportationType().equalsIgnoreCase(type.trim()))
                .map(transportationMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TransportationResponse getById(Long id) {
        return transportationMapper.toResponse(find(id));
    }

    @Transactional
    public TransportationResponse create(TransportationRequest request) {
        String code = request.code().trim();
        if (transportationRepository.existsByCode(code)) {
            throw new DuplicateResourceException("A transportation with code " + code + " already exists");
        }

        // One subclass per type. The rest of the app only sees "Transportation".
        Transportation transportation = switch (request.type()) {
            case BUS -> new Bus(request.name(), code, request.description(),
                    Boolean.TRUE.equals(request.airConditioned()));
            case JEEPNEY -> new Jeepney(request.name(), code, request.description(),
                    Boolean.TRUE.equals(request.modernized()));
            case VAN -> new Van(request.name(), code, request.description(),
                    required(request.seatingCapacity(), "Seating capacity"));
            case SHUTTLE -> new Shuttle(request.name(), code, request.description(),
                    requiredText(request.serviceArea(), "Service area"));
            case TRAIN -> new Train(request.name(), code, request.description(),
                    required(request.numberOfCars(), "Number of cars"));
        };
        return transportationMapper.toResponse(transportationRepository.save(transportation));
    }

    @Transactional
    public TransportationResponse update(Long id, TransportationRequest request) {
        // unproxy gives us the real Bus/Jeepney/... object, so the casts below are safe
        Object real = Hibernate.unproxy(find(id));
        Transportation transportation = (Transportation) real;

        if (!transportation.getTransportationType().equalsIgnoreCase(request.type().name())) {
            throw new InvalidRequestException("The type of an existing transportation cannot be changed");
        }
        String code = request.code().trim();
        if (!transportation.getCode().equals(code) && transportationRepository.existsByCode(code)) {
            throw new DuplicateResourceException("A transportation with code " + code + " already exists");
        }

        transportation.setName(request.name());
        transportation.setCode(code);
        transportation.setDescription(request.description());
        switch (request.type()) {
            case BUS -> ((Bus) real).setAirConditioned(Boolean.TRUE.equals(request.airConditioned()));
            case JEEPNEY -> ((Jeepney) real).setModernized(Boolean.TRUE.equals(request.modernized()));
            case VAN -> ((Van) real).setSeatingCapacity(required(request.seatingCapacity(), "Seating capacity"));
            case SHUTTLE -> ((Shuttle) real).setServiceArea(requiredText(request.serviceArea(), "Service area"));
            case TRAIN -> ((Train) real).setNumberOfCars(required(request.numberOfCars(), "Number of cars"));
        }
        return transportationMapper.toResponse(transportationRepository.save(transportation));
    }

    @Transactional
    public void delete(Long id) {
        Transportation transportation = find(id);
        if (routeRepository.existsByTransportationId(id)) {
            throw new ResourceInUseException("This transportation is used by one or more routes");
        }
        if (!vehicleRepository.findByTransportationId(id).isEmpty()) {
            throw new ResourceInUseException("This transportation still has vehicles assigned to it");
        }
        transportationRepository.delete(transportation);
    }

    private Transportation find(Long id) {
        return transportationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transportation", id));
    }

    private static int required(Integer value, String fieldName) {
        if (value == null) {
            throw new InvalidRequestException(fieldName + " is required for this transportation type");
        }
        return value;
    }

    private static String requiredText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new InvalidRequestException(fieldName + " is required for this transportation type");
        }
        return value;
    }
}
