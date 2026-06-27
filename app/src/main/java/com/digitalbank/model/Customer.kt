package com.digitalbank.model

data class Customer(
    val id: Int,
    val name: String,
    val imageUrl: String?,
    val accountType: String,
    val balance: Int,
    val kycStatus: KycStatus,
    val selfiePath: String?,
    val maskedIban: String,
    val birthDate: String,
    val nationality: String,
    val address: String,
    val contact: String,
    val ifsc: String
)
