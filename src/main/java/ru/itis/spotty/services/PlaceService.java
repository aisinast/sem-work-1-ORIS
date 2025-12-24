package ru.itis.spotty.services;

import ru.itis.spotty.models.Place;

import java.util.List;
import java.util.UUID;

public interface PlaceService {
    UUID addPlace(Place place);
    Place getPlaceById(UUID id);

    List<Place> findEligiblePlaces(String query);
}
