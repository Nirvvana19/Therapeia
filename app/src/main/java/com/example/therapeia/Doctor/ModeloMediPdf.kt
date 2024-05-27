package com.example.therapeia.Doctor

class ModeloMediPdf {

    var uid: String = ""
    var id: String = ""
    var nombre: String = ""
    var descripcion: String = ""
    var tipo_medi: String = ""
    var url: String = ""
    var tiempo: Long = 0

    constructor()
    constructor(uid: String, id: String, nombre: String, descripcion: String,
                tipo_enfer: String, url: String, tiempo: Long) {
        this.uid = uid
        this.id = id
        this.nombre = nombre
        this.descripcion = descripcion
        this.tipo_medi = tipo_enfer
        this.url = url
        this.tiempo = tiempo
    }
}