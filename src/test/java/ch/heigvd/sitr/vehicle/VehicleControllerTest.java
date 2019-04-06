/*
 * Filename: VehicleControllerTest.java
 * Creation date: 28.03.2019
 */

package ch.heigvd.sitr.vehicle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test for Vehicle controller.
 * @author Simon Walther
 */
public class VehicleControllerTest {
    VehicleController vehicleController = null;
    Vehicle vehicle = null;
    Vehicle frontVehicle = null;

    @BeforeEach
    public void createDummyVehicleController() {
        vehicleController = new VehicleController();
        vehicleController.setDesiredVelocity(33.33);
        vehicleController.setMinimumSpacing(2);
        vehicleController.setDesiredTimeHeadway(1.5);
        vehicleController.setMaxAcceleration(0.3);
        vehicleController.setComfortableBrakingDeceleration(3);
    }

    @BeforeEach
    public void createDummyVehicle() {
        frontVehicle = new Vehicle(vehicleController, 1.6, 33.33);
        vehicle = new Vehicle(vehicleController, 1.6, 33.33);
        vehicle.setFrontVehicle(frontVehicle);
    }

    @Test
    public void desiredVelocity() {
        VehicleController vehicleController = new VehicleController();
        vehicleController.setDesiredVelocity(22.22);
        assertEquals(22.22, vehicleController.getDesiredVelocity());
    }

    @Test
    public void minimumSpacing() {
        VehicleController vehicleController = new VehicleController();
        vehicleController.setMinimumSpacing(2.5);
        assertEquals(2.5, vehicleController.getMinimumSpacing());
    }

    @Test
    public void desiredTimeHeadway() {
        VehicleController vehicleController = new VehicleController();
        vehicleController.setDesiredTimeHeadway(1.5);
        assertEquals(1.5, vehicleController.getDesiredTimeHeadway());
    }

    @Test
    public void maxAcceleration() {
        VehicleController vehicleController = new VehicleController();
        vehicleController.setMaxAcceleration(0.73);
        assertEquals(0.73, vehicleController.getMaxAcceleration());
    }

    @Test
    public void comfortableBrakingDeceleration() {
        VehicleController vehicleController = new VehicleController();
        vehicleController.setComfortableBrakingDeceleration(1.67);
        assertEquals(1.67, vehicleController.getComfortableBrakingDeceleration());
    }

    /**
     * s*(v, deltaV) = s0 + max(0, (v*T + (v * deltaV)/(2*sqrt(a * b)))
     *
     * Variables :
     * s0 (minimumSpacing)                   : 2 [m]
     * v (speed)                             : 22.22 [m/s]
     * T (desired time headway)              : 1.5 [s]
     * deltaV (relative speed)               : -5.55 [m/s]
     * a (max acceleration)                  : 0.3 [m/s^2]
     * b (comfortable braking deceleration)  : 3 [m/s^2]
     *
     * => s*(22.22, -5.55) = 2 + max(0, 22.22 * 1.5 + (22.22 * -5.55)/(2*sqrt(0.3 * 3)))
     *                     = 2 [m] = s0
     */
    @Test
    public void desiredDynamicalDistanceWithFasterFrontVehicle() {
        vehicle.setSpeed(22.22);
        frontVehicle.setSpeed(27.77);
        assertEquals(2.0, vehicleController.desiredDynamicalDistance(vehicle));
    }

    /**
     * s*(v, deltaV) = s0 + max(0, (v*T + (v * deltaV)/(2*sqrt(a * b)))
     *
     * Variables :
     * s0 (minimumSpacing)                   : 2 [m]
     * v (speed)                             : 22.22 [m/s]
     * T (desired time headway)              : 1.5 [s]
     * deltaV (relative speed)               : 2.78 [m/s]
     * a (max acceleration)                  : 0.3 [m/s^2]
     * b (comfortable braking deceleration)  : 3 [m/s^2]
     *
     * => s*(22.22, 2.78) = 2 + max(0, 22.22 * 1.5 + (22.22 * 2.78)/(2*sqrt(0.3 * 3)))
     *                    = 67.88 [m]
     */
    @Test
    public void desiredDynamicalDistanceWithSlowerFrontVehicle() {
        vehicle.setSpeed(22.22);
        frontVehicle.setSpeed(19.44);
        assertEquals(67.88649178547615, vehicleController.desiredDynamicalDistance(vehicle));
    }

    /**
     * s*(v, deltaV) = s0 + max(0, (v*T + (v * deltaV)/(2*sqrt(a * b)))
     *
     * Variables :
     * s0 (minimumSpacing)                   : 2 [m]
     * v (speed)                             : 22.22 [m/s]
     * T (desired time headway)              : 1.5 [s]
     * deltaV (relative speed)               : 0 [m/s]
     * a (max acceleration)                  : 0.3 [m/s^2]
     * b (comfortable braking deceleration)  : 3 [m/s^2]
     *
     * => s*(22.22, 0) = 2 + max(0, 22.22 * 1.5 + (22.22 * 0)/(2 * sqrt(0.3 * 3)))
     *                 = 35.33 [m]
     */
    @Test
    public void desiredDynamicalDistanceWithSameSpeedFrontVehicle() {
        frontVehicle.setSpeed(22.22);
        vehicle.setSpeed(22.22);
        vehicle.setFrontVehicle(frontVehicle);
        assertEquals(35.33, vehicleController.desiredDynamicalDistance(vehicle));
    }

    /**
     * safe distance is : s0 + v*T
     *
     * Variables :
     * s0 (minimum spacing)      : 2 [m]
     * v (speed)                 : 22.22 [m/s]
     * T (desired time headway)  : 1.5 [s]
     *
     * => 2 + 22.22 * 1.5 = 36.33 [m]
     */
    @Test
    public void safeDistance() {
        vehicle.setSpeed(22.22);
        assertEquals(35.33, vehicleController.safeDistance(vehicle));
    }

    /**
     * desired acceleration is : a * [1 - (v / v0) ^ delta]
     *
     * Variables :
     * a (max acceleration)   : 0.3 [m/s^2]
     * v (speed)              : 22.22 [m/s]
     * v0 (desired velocity)  : 33.33 [m/s]
     * delta                  : 4
     *
     * => 0.3 * [1 - (22.22 / 33.33) ^ 4] = 0.2407407 [m/s^2]
     */
    @Test
    public void desiredAcceleration() {
        vehicle.setSpeed(22.22);
        assertEquals(0.241, vehicleController.desiredAcceleration(vehicle), 0.001);
    }

    /**
     * acceleration is : a * [1 - (v / v0)^delta - (s*(v, deltaV) / s)^2]
     *
     * where : s*(v, deltaV) is the desired dynamical distance
     *
     * Variables :
     * a (max acceleration)    : 0.3 [m/s^2]
     * v (speed)               : 22.22 [m/s]
     * v0 (desired velocity)   : 33.33 [m/s]
     * deltaV (relative speed) : 2.78 [m/s]
     * s (front distance)      : 20 [m]
     * delta
     *
     * => 0.3 * [1 - (22.22 / 33.33)^4 - (s*(22.22, 2.78) / 20)^2] = -3.842945253121075 [m/s^2]
     */
    @Test
    public void accelerationSlowerFrontVehicle() {
        vehicle.setSpeed(22.22);
        vehicle.setPosition(80);
        frontVehicle.setSpeed(19.44);
        frontVehicle.setPosition(100);
        assertEquals(-3.843, vehicleController.acceleration(vehicle), 0.001);
    }

    /**
     * acceleration is : a * [1 - (v / v0)^delta - (s*(v, deltaV) / s)^2]
     *
     * where : s*(v, deltaV) is the desired dynamical distance
     *
     * Variables :
     * a (max acceleration)    : 0.3 [m/s^2]
     * v (speed)               : 22.22 [m/s]
     * v0 (desired velocity)   : 33.33 [m/s]
     * deltaV (relative speed) : -5.55 [m/s]
     * s (front distance)      : 20 [m]
     * delta
     *
     * => 0.3 * [1 - (22.22 / 33.33)^4 - (s*(22.22, -5.55) / 20)^2] = 0.23719631730028704 [m/s^2]
     */
    @Test
    public void accelerationFasterFrontVehicle() {
        vehicle.setSpeed(22.22);
        vehicle.setPosition(80);
        vehicle.setFrontVehicle(frontVehicle);
        frontVehicle.setSpeed(27.77);
        frontVehicle.setPosition(100);
        assertEquals(0.237, vehicleController.acceleration(vehicle), 0.001);
    }

    /**
     * acceleration is : a * [1 - (v / v0)^delta - (s*(v, deltaV) / s)^2]
     *
     * where : s*(v, deltaV) is the desired dynamical distance
     *
     * Variables :
     * a (max acceleration)    : 0.3 [m/s^2]
     * v (speed)               : 22.22 [m/s]
     * v0 (desired velocity)   : 33.33 [m/s]
     * deltaV (relative speed) : 0 [m/s]
     * s (front distance)      : infinity [m]
     * delta
     *
     * => 0.3 * [1 - (22.22 / 33.33)^4 - (s*(22.22, 0) / infinity)^2] = 0.24074074074074073 [m/s^2]
     */
    @Test
    public void accelerationWithoutFrontVehicle() {
        frontVehicle.setSpeed(22.22);
        frontVehicle.setPosition(80);
        assertEquals(0.241, vehicleController.acceleration(frontVehicle), 0.001);
    }
}