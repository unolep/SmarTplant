package com.tflite.Deteksipenyakittanaman

data class Deteksi(
    val name: String,
    val probability: Float
) {
    override fun toString() =
        "$name : ${probability*100}%"
}
