package com.test.cinemometar;

public class Station {
    int _id;
    String _station_id;
    String _airport_name;
    String _data;
    String _decoded;
    public Station(){   }
    public Station(int id, String station_id, String airport_name, String data, String decoded){
        this._id = id;
        this._station_id = station_id;
        this._airport_name = airport_name;
        this._data = data;
        this._decoded = decoded;
    }

    public Station(String station_id, String airport_name, String data, String decoded){
        this._station_id = station_id;
        this._airport_name = airport_name;
        this._data = data;
        this._decoded = decoded;
    }
    public int getID(){
        return this._id;
    }

    public void setID(int id){
        this._id = id;
    }

    public String getStationId(){
        return this._station_id;
    }

    public void setStationId(String station_id){
        this._station_id = station_id;
    }

    public String getAirportName(){
        return this._airport_name;
    }

    public void setAirportName(String airport_name){
        this._airport_name = airport_name;
    }

    public String getData(){
        return this._data;
    }

    public void setData(String data){
        this._data = data;
    }

    public String getDecoded(){
        return this._decoded;
    }

    public void setDecoded(String decoded){
        this._decoded = decoded;
    }
}
