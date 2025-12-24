package ru.itis.spotty.services;

import ru.itis.spotty.models.Place;
import ru.itis.spotty.repositories.PlaceRepository;

import java.util.List;
import java.util.UUID;

public class PlaceServiceImpl implements PlaceService {

    private PlaceRepository placeRepository;

    public PlaceServiceImpl(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    @Override
    public UUID addPlace(Place place) {
        return placeRepository.createPlace(place);
    }

    @Override
    public Place getPlaceById(UUID id) {
        return placeRepository.getPlaceById(id);
    }

    @Override
    public List<Place> findEligiblePlaces(String query) {
        return placeRepository.findEligiblePlaces(query);
    }
}
