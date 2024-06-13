package com.smart.parking.service.parking;

import com.smart.parking.dto.car.CarParkingPostRequest;
import com.smart.parking.dto.parking.ParkingBarrierControl;
import com.smart.parking.dto.parking.ParkingCarGetRequest;
import com.smart.parking.dto.parking.ParkingGetRequest;
import com.smart.parking.dto.parking.ParkingPostRequest;
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

import java.util.*;

@Service
@RequiredArgsConstructor
public class ParkingServiceImpl implements ParkingService {

    private final CarRepository carRepository;
    private final ParkingRepository parkingRepository;

    @Override
    public void save(ParkingPostRequest request, User user) {
        Optional<Parking> parkingEntity = parkingRepository.findByUserIdAndParkingName(user.getId(), request.getParkingName(), false);
        Set<Car> cars = new HashSet<>();

        // IF PARKING EXITS
        if (parkingEntity.isPresent()) {
            for (CarParkingPostRequest car : request.getCars()) {
                Optional<Car> byLicense = carRepository.findByLicense(car.getNumberPlate(), false);
                if (byLicense.isEmpty()) {
                    Car carEntity = Car.builder()
                            .carName(car.getCarName())
                            .numberPlate(car.getNumberPlate())
                            .user(user)
                            .isDeleted(false)
                            .parkingPlaces(new HashSet<>(Collections.singleton(parkingEntity.get())))
                            .build();
                    cars.add(carRepository.save(carEntity));
                } else {
                    throw new AlreadyExists("CAR WITH SUCH NUMBER PLATE ALREADY EXISTS");
                }
            }
            Parking parkingPlace = parkingEntity.get();
            parkingPlace.setParkedCars(cars);
            parkingRepository.save(parkingPlace);
        } else {
            throw new NotFoundException("PARKING WITH NAME NOT FOUND");
        }
    }

    @Override
    public List<ParkingGetRequest> getParkingById(Long userId) {
        List<Parking> parkingList = parkingRepository.findParkingByUserId(userId, false);
        List<ParkingGetRequest> parkingGetRequests = new ArrayList<>();
        for (Parking parking : parkingList) {
            Set<ParkingCarGetRequest> listCarResponse = new HashSet<>();
            for (Car car : parking.getParkedCars()) {
                ParkingCarGetRequest carResponse = ParkingCarGetRequest.builder()
                        .id(car.getId())
                        .carName(car.getCarName())
                        .numberPlate(car.getNumberPlate())
                        .build();
                listCarResponse.add(carResponse);
            }
            ParkingGetRequest listParkingResponse = ParkingGetRequest.builder()
                    .id(parking.getId())
                    .parkingName(parking.getParkingName())
                    .cars(listCarResponse)
                    .build();
            parkingGetRequests.add(listParkingResponse);
        }
        return parkingGetRequests;
    }

    @Override
    public ParkingGetRequest getByParkingId(Long parkingId) {
        Optional<Parking> parkingEntity = parkingRepository.findByParkingId(parkingId, false);
        if (parkingEntity.isPresent()) {
            Set<ParkingCarGetRequest> listCarResponse = new HashSet<>();
            for (Car car : parkingEntity.get().getParkedCars()) {
                ParkingCarGetRequest carResponse = ParkingCarGetRequest.builder()
                        .id(car.getId())
                        .carName(car.getCarName())
                        .numberPlate(car.getNumberPlate())
                        .build();
                listCarResponse.add(carResponse);
            }
            return ParkingGetRequest.builder()
                    .id(parkingEntity.get().getId())
                    .parkingName(parkingEntity.get().getParkingName())
                    .cars(listCarResponse)
                    .build();
        } else {
            throw new NotFoundException("PARKING WITH ID NOT FOUND");
        }
    }

    @Override
    public void deleteParkingById(Long parkingId) {
        Optional<Parking> parkingEntity = parkingRepository.findByParkingId(parkingId, false);
        if (parkingEntity.isPresent()) {
            Parking parking = parkingEntity.get();
            parking.setIsDeleted(true);
            parkingRepository.save(parking);
        } else {
            throw new NotFoundException("PARKING WITH ID NOT FOUND");
        }
    }

    @Override
    public void update(Long parkingId, ParkingPostRequest parkingPostRequest) {
        Optional<Parking> parkingEntity = parkingRepository.findByParkingId(parkingId, false);
        if (parkingEntity.isPresent()) {
            Parking parking = parkingEntity.get();
            parking.setParkingName(parkingPostRequest.getParkingName());
            Set<Car> cars = new HashSet<>();
            for (CarParkingPostRequest car : parkingPostRequest.getCars()) {
                Car carEntity = Car.builder()
                        .carName(car.getCarName())
                        .numberPlate(car.getNumberPlate())
                        .build();
                cars.add(carEntity);
            }
            parking.setParkedCars(cars);
            parkingRepository.save(parking);
        } else {
            throw new NotFoundException("PARKING WITH ID NOT FOUND");
        }
    }

    @Override
    public ParkingBarrierControl barrierController(Long parkingId, Long userId, ParkingBarrierControl parkingBarrierControl) {
        Optional<Parking> parkingBarrier = parkingRepository.findParkingByIdAndUserId(parkingId, userId, false);
        if (parkingBarrier.isPresent()) {
            //TODO you have to give command to barrier according to user request
            return parkingBarrierControl;
        } else {
            throw new NotFoundException("PARKING WITH THESE CREDENTIALS NOT FOUND");
        }
    }
}
