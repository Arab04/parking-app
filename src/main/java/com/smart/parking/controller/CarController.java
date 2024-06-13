package com.smart.parking.controller;

import com.smart.parking.dto.car.CarGetRequest;
import com.smart.parking.dto.car.CarPostRequest;
import com.smart.parking.entity.Car;
import com.smart.parking.entity.User;
import com.smart.parking.service.car.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/car")
@RequiredArgsConstructor
public class CarController {

    private final CarService service;

    @GetMapping("/findBy/{numberPlate}")
    public CarGetRequest getByNumberPlate(@PathVariable String numberPlate) {
        return service.findByNumberPlate(numberPlate);
    }

    @GetMapping("/all/system")
    public ResponseEntity<List<Car>> findAllCars() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/user/all")
    public List<CarGetRequest> userCars(@AuthenticationPrincipal User user) {
        return service.userCars(user.getId());
    }

    @GetMapping("/findBy/{carId}")
    public CarGetRequest getByCarId(@PathVariable("carId") Long carId) {
        return service.findCarById(carId);
    }

    //TODO need to be deleted
//    @PostMapping("/without/parking")
//    public ResponseEntity<?> save(@RequestBody CarRequest request, @AuthenticationPrincipal User user) {
//        service.save(request, user);
//        return ResponseEntity.accepted().build();
//    }

    @PutMapping("/update/{car_id}")
    public ResponseEntity<?> update(@PathVariable("car_id") Long id, @RequestBody CarPostRequest request) {
        service.update(id, request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveCar(@RequestBody CarPostRequest carRequest, @AuthenticationPrincipal User user) {
        service.saveCar(carRequest, user);
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("/{carId}")
    public ResponseEntity<?> delete(@PathVariable Long carId) {
        service.delete(carId);
        return ResponseEntity.accepted().build();
    }
}
