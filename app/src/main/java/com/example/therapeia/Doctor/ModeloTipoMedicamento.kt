package com.example.therapeia.Doctor

class ModeloTipoMedicamento {
    var id : String = ""
    var tipo_medi : String = ""
    var tiempo : Long = 0
    var uid: String = ""

    constructor()

    constructor(id: String, categoria: String, tiempo: Long, uid: String) {
        this.id = id
        this.tipo_medi = categoria
        this.tiempo = tiempo
        this.uid = uid
    }
}