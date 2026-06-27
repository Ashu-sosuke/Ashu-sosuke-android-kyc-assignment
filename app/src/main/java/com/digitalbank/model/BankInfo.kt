package com.digitalbank.model

data class BankInfo(
    val bankName: String,
    val branch: String,
    val ifsc: String,
    val city: String,
    val state: String
)
