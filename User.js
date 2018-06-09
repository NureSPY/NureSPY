'use strict'

module.exports = class User{
  constructor(mail){
  this.spayed_by = new Map();
  this.mail = mail;
  this.id = -1;
  this.coords = {
    lat:0,
    lng: 0,
    h:0
    }
  }
  Spy(id){
    this.spayed_by.set(id, true)
  }
  StopSpy(index){
    this.spyed_by.delete(index)
  }
  getId(){
    return this.id;
  }
  setId(id){
    this.id = id;
  }
  getMail(){
    return this.mail;
  }
  setMail(mail){
    this.mail = mail;
  }
  getCoords(){
    return this.coords;
  }
 
  setCoords(latitude, longitude, height){
    this.coords.lat = latitude;
    this.coords.lng = longitude;
    this.coords.h = height;
    }
  };