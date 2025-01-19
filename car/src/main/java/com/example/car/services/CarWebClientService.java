// Path: car/src/main/java/com/example/car/services/CarWebClientService.java
package com.example.car.services;

import com.example.car.entities.Car;
import com.example.car.entities.Client;
import com.example.car.models.CarResponse;
import com.example.car.repositories.CarRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;

@Service
public class CarWebClientService {
    private final WebClient webClient;
    private final CarRepository carRepository;

    public CarWebClientService(WebClient webClient, CarRepository carRepository) {
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

    public CarResponse findById(Long id) throws Exception {
        Car car = carRepository.findById(id).orElseThrow(() -> new Exception("Invalid Car Id"));
        Client client = webClient.get()
                .uri("/api/client/" + car.getClient_id())
                .retrieve()
                .bodyToMono(Client.class)
                .block();

        return mapToCarResponse(car, Collections.singletonList(client));
    }

    // Add this missing method
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