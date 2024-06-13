package com.smart.parking.service.car;

import com.smart.parking.dto.car.CarGetRequest;
import com.smart.parking.dto.car.CarPostRequest;
import com.smart.parking.dto.parking.ParkingRequest;
import com.smart.parking.entity.Car;
import com.smart.parking.entity.Parking;
import com.smart.parking.entity.User;
import com.smart.parking.exception.AlreadyExists;
import com.smart.parking.exception.NotFoundException;
import com.smart.parking.repository.CarRepository;
import com.smart.parking.repository.ParkingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private final CarRepository repository;
    private final ParkingRepository parkingPlaceRepository;


    // TODO need to be deleted
    @Override
    public void save(CarGetRequest request, User user) {

        var car = Car.builder()
                .id(request.getId())
                .carName(request.getCarName())
                .numberPlate(request.getNumberPlate())
                .user(user)
                .build();
        repository.save(car);
    }

    @Override
    public CarGetRequest findByNumberPlate(String numberPlate) {
        Optional<Car> car = repository.findByLicense(numberPlate, false);
        if (car.isPresent()) {
            return CarGetRequest.builder()
                    .id(car.get().getId())
                    .carName(car.get().getCarName())
                    .numberPlate(car.get().getNumberPlate())
                    .build();
        } else {
            throw new NotFoundException("CAR NOT FOUND");
        }
    }

    @Override
    public void update(Long carId, CarPostRequest request) {
        Optional<Car> carEntity = repository.findCarById(carId, false);
        if (carEntity.isPresent()) {
            Car car = carEntity.get();
            car.setCarName(request.getCarName());
            car.setNumberPlate(request.getNumberPlate());
            repository.save(car);
        } else {
            throw new NotFoundException("CAR NOT FOUND");
        }
    }

    @Override
    public List<CarGetRequest> userCars(Long userId) {
        List<Car> cars = repository.findByUserId(userId, false);
        List<CarGetRequest> carRequests = new ArrayList<>();

        for (Car car : cars) {
            Set<ParkingRequest> parkingRequests = new HashSet<>();
            for (Parking parkingPlace : car.getParkingPlaces()) {
                parkingRequests.add(new ParkingRequest(parkingPlace.getParkingName()));
            }
            CarGetRequest build = CarGetRequest.builder()
                    .id(car.getId())
                    .carName(car.getCarName())
                    .numberPlate(car.getNumberPlate())
                    .parking(parkingRequests)
                    .build();
            carRequests.add(build);
        }
        return carRequests;
    }

    @Override
    public CarGetRequest findCarById(Long carId) {
        Optional<Car> car = repository.findCarById(carId, false);
        if (car.isPresent()) {
            return CarGetRequest.builder()
                    .id(car.get().getId())
                    .carName(car.get().getCarName())
                    .numberPlate(car.get().getNumberPlate())
                    .build();
        } else {
            throw new NotFoundException("CAR NOT FOUND WITH ID: " + carId);
        }
    }

    @Override
    public List<Car> findAll() {
        return repository.findAll(Boolean.FALSE);
    }

    @Override
    public void saveCar(CarPostRequest carRequest, User user) {
        Optional<Car> byLicense = repository.findByLicense(carRequest.getNumberPlate(), false);
        if (byLicense.isEmpty()) {
            Car car = Car.builder()
                    .carName(carRequest.getCarName())
                    .numberPlate(carRequest.getNumberPlate())
                    .user(user)
                    .isDeleted(false)
                    .build();

            Set<Parking> parkingPlaces = new HashSet<>();
            Set<Car> cars = new HashSet<>();
            for (ParkingRequest parkingEntity : carRequest.getParking()) {
                Parking parkingPlace = Parking.builder()
                        .parkingName(parkingEntity.getParkingName())
                        .user(user)
                        .parkedCars(cars)
                        .isDeleted(false)
                        .build();

                cars.add(car);
                parkingPlaces.add(parkingPlace);
                parkingPlaceRepository.save(parkingPlace);
            }

            car.setParkingPlaces(parkingPlaces);
            repository.save(car);
        } else {
            throw new AlreadyExists("CAR ALREADY EXISTS WITH SUCH NUMBER PLATE");
        }
    }

    @Override
    public void delete(Long carId) {
        Optional<Car> car = repository.findCarById(carId, false);
        if (car.isPresent()) {
            car.get().setIsDeleted(true);
            repository.save(car.get());
        } else {
            throw new NotFoundException("CAR NOT FOUND");
        }
    }
}
