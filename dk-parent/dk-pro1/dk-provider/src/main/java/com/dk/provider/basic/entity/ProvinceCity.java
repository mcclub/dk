package com.dk.provider.basic.entity;

import java.io.Serializable;
import java.util.List;

public class ProvinceCity extends Area implements Serializable {
    List<Area> city;

    public List<Area> getCity() {
        return city;
    }

    public void setCity(List<Area> city) {
        this.city = city;
    }
}
