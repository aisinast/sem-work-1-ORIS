package ru.itis.spotty.repositories;

import ru.itis.spotty.models.Place;

import java.util.List;
import java.util.UUID;

public interface PlaceRepository {
    UUID createPlace(Place place);
    Place getPlaceById(UUID placeId);
    List<Place> findEligiblePlaces(String query);
}
