package com.example.car.services;

import com.example.car.entities.Car;
import com.example.car.entities.Client;
import com.example.car.models.CarResponse;
import com.example.car.repositories.CarRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class WebClientService {
    private final WebClient webClient;
    private final CarRepository carRepository;

    public WebClientService(WebClient webClient, CarRepository carRepository) {
        this.webClient = webClient;
        this.carRepository = carRepository;
    }

    public List<CarResponse> findAll() {
        List<Car> cars = carRepository.findAll();
        List<Client> clients = webClient.get()
                .uri("/api/client")
                .retrieve()
                .bodyToFlux(Client.class)
                .collectList()
                .block();

        return cars.stream()
                .map(car -> mapToCarResponse(car, clients))
                .toList();
    }

    private CarResponse mapToCarResponse(Car car, List<Client> clients) {
        Client client = clients.stream()
                .filter(c -> c.getId().equals(car.getClient_id()))
                .findFirst()
                .orElse(null);

        return CarResponse.builder()
                .id(car.getId())
                .brand(car.getBrand())
                .model(car.getModel())
                .matricue(car.getMatricule())
                .client(client)
                .build();
    }
}