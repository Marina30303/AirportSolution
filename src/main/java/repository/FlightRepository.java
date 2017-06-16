package repository;

import entities.FlightEntity;

import java.sql.*;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class FlightRepository implements RepositoryInterface<FlightEntity> {
    private Connection connection;

    public FlightRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void add(FlightEntity flightEntity) throws SQLException {

        PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO flight (flight_number, direction_type, leaving_from, arrival_to, leaving_time, arrival_time) VALUES(?, ?, ?, ?, ?, ?);");

        preparedStatement.setString(1, flightEntity.getFlightNumber());
        preparedStatement.setBoolean(2, flightEntity.getDirectionType());

        preparedStatement.setString(3, flightEntity.getLeavingFrom());
        preparedStatement.setString(4, flightEntity.getArrivalTo());

        preparedStatement.setTime(5, Time.valueOf(flightEntity.getLeavingTime().truncatedTo(ChronoUnit.MINUTES)));
        preparedStatement.setTime(6, Time.valueOf(flightEntity.getArrivalTime().truncatedTo(ChronoUnit.MINUTES)));

        preparedStatement.execute();
    }

    @Override
    public void remove(FlightEntity entity) throws SQLException {

        PreparedStatement preparedStatement = connection.prepareStatement(
                "DELETE FROM flight WHERE flight_number = ?;");
        preparedStatement.setString(1, entity.getFlightNumber());

        preparedStatement.execute();
    }

    @Override
    public void update(FlightEntity flightEntity) throws SQLException {

        PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE flight SET direction_type = ?, leaving_from = ?, arrival_to = ?, leaving_time = ?, arrival_time = ? WHERE flight_number = ?;");
        preparedStatement.setBoolean(1, flightEntity.getDirectionType());

        preparedStatement.setString(2, flightEntity.getLeavingFrom());
        preparedStatement.setString(3, flightEntity.getArrivalTo());

        preparedStatement.setTime(4, Time.valueOf(flightEntity.getLeavingTime().truncatedTo(ChronoUnit.MINUTES)));
        preparedStatement.setTime(5, Time.valueOf(flightEntity.getArrivalTime().truncatedTo(ChronoUnit.MINUTES)));

        preparedStatement.setString(6, flightEntity.getFlightNumber());

        preparedStatement.execute();
    }

    @Override
    public List<FlightEntity> getAll() {
        try (Statement statement = this.connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM flight;")) {
            List<FlightEntity> entitiesList = new ArrayList<>();

            while (resultSet.next()) {

                String flightNumber = resultSet.getString("flight_number");
                boolean directionType = resultSet.getBoolean("direction_type");
                String leavingFrom = resultSet.getString("leaving_from");
                String arrivalTo = resultSet.getString("arrival_to");
                LocalTime leavingTime = resultSet.getTime("leaving_time").toLocalTime().truncatedTo(ChronoUnit.MINUTES);
                LocalTime arrivalTime = resultSet.getTime("arrival_time").toLocalTime().truncatedTo(ChronoUnit.MINUTES);

                FlightEntity flightEntity = new FlightEntity(flightNumber, directionType, leavingFrom, arrivalTo, leavingTime, arrivalTime);
                entitiesList.add(flightEntity);
            }
            return entitiesList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}