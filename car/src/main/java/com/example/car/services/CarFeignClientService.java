// Path: car/src/main/java/com/example/car/services/CarFeignClientService.java
package com.example.car.services;

import com.example.car.clients.ClientFeignClient;
import com.example.car.entities.Car;
import com.example.car.entities.Client;
import com.example.car.models.CarResponse;
import com.example.car.repositories.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarFeignClientService {
    @Autowired
    private ClientFeignClient clientFeignClient;

    @Autowired
    private CarRepository carRepository;

    public List<CarResponse> findAll() {
        List<Car> cars = carRepository.findAll();
        List<Client> clients = clientFeignClient.findAll();
        return cars.stream()
                .map(car -> mapToCarResponse(car, clients))
                .toList();
    }

    public CarResponse findById(Long id) throws Exception {
        Car car = carRepository.findById(id).orElseThrow(() -> new Exception("Invalid Car Id"));
        Client client = clientFeignClient.findById(car.getClient_id());
        return mapToCarResponse(car, List.of(client));
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