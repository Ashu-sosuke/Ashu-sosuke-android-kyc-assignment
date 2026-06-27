package com.digitalbank.data.remote.dto

import com.digitalbank.data.local.CustomerEntity
import com.digitalbank.model.KycStatus
import com.google.gson.annotations.SerializedName

data class UsersResponseDto(
    @SerializedName("users") val users: List<UserDto>,
    @SerializedName("total") val total: Int,
    @SerializedName("skip") val skip: Int,
    @SerializedName("limit") val limit: Int
)

data class UserDto(
    @SerializedName("id") val id: Int,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("birthDate") val birthDate: String,
    @SerializedName("image") val image: String?,
    @SerializedName("address") val address: AddressDto?,
    @SerializedName("bank") val bank: BankDto?
) {
    fun toEntity(
        existingSelfiePath: String? = null,
        existingKycStatus: String = KycStatus.PENDING.name
    ): CustomerEntity {
        val addressStr = address?.let { "${it.address}, ${it.city}, ${it.state}" } ?: "123 Main St, Mumbai, MH"
        val nationalityVal = "Indian"
        val ibanVal = bank?.iban ?: "IN0000000000000000000000"
        return CustomerEntity(
            id = id,
            firstName = firstName,
            lastName = lastName,
            email = email,
            phone = phone,
            birthDate = birthDate,
            image = image,
            nationality = nationalityVal,
            address = addressStr,
            iban = ibanVal,
            kycStatus = existingKycStatus,
            selfiePath = existingSelfiePath
        )
    }
}

data class AddressDto(
    @SerializedName("address") val address: String,
    @SerializedName("city") val city: String,
    @SerializedName("state") val state: String
)

data class BankDto(
    @SerializedName("iban") val iban: String
)
