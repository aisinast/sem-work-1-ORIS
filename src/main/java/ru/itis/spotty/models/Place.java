package ru.itis.spotty.models;

import java.util.UUID;

public class Place {
    private UUID placeId;
    private String placeName;
    private String fullAddress;

    private double latitude;
    private double longitude;

    public Place(UUID placeId, String placeName, String fullAddress ) {
        this.placeId = placeId;
        this.placeName = placeName;
        this.fullAddress = fullAddress;
    }

    public Place(String placeName, String fullAddress) {
        this.placeName = placeName;
        this.fullAddress = fullAddress;
    }

    public UUID getPlaceId() {
        return placeId;
    }

    public void setPlaceId(UUID placeId) {
        this.placeId = placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
