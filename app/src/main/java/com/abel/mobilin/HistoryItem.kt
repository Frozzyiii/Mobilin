package com.abel.mobilin.model

data class HistoryItem(
    val carName: String,
    val carType: String,
    val rentalDate: String,
    val totalAmount: String,
    val transactionId: String,
    val bookingCode: String,
    val carImage: String // bisa URL atau nama file drawable
)
