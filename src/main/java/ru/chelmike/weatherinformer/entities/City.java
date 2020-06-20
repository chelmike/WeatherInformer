package ru.chelmike.weatherinformer.entities;

/**
 * City - for now this entity includes city's name (in english, russian - in 2 cases - nominative and prepositional)
 * and its geolocation - latitude and longitude
 * <p>
 * TODO: realize storing of cities in DB
 *
 * @author Michael Ostrovsky
 */
public class City {
    private String nameEn;
    private String nameRu;
    private String nameRuIn;
    private double latitude;
    private double longitude;

    public City(String nameEn, String nameRu, String nameRuIn, double lat, double lon) {
        this.nameEn = nameEn;
        this.nameRu = nameRu;
        this.nameRuIn = nameRuIn;
        latitude = lat;
        longitude = lon;
    }

    @Override
    public int hashCode() {
        return this.nameEn.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != getClass())
            return false;
        City that = (City) obj;
        return this.nameEn.equals(that.nameEn);
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getNameRu() {
        return nameRu;
    }

    public void setNameRu(String nameRU) {
        this.nameRu = nameRU;
    }

    public String getNameRuIn() {
        return nameRuIn;
    }

    public void setNameInRu(String nameRuIn) {
        this.nameRuIn = nameRuIn;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
