package com.digitalbank.data.remote.dto

import com.digitalbank.data.local.IfscEntity
import com.digitalbank.model.BankInfo
import com.google.gson.annotations.SerializedName

data class IfscDto(
    @SerializedName("BANK") val bank: String,
    @SerializedName("BRANCH") val branch: String,
    @SerializedName("IFSC") val ifsc: String,
    @SerializedName("CITY") val city: String,
    @SerializedName("STATE") val state: String
) {
    fun toEntity(): IfscEntity {
        return IfscEntity(
            ifsc = ifsc,
            bankName = bank,
            branch = branch,
            city = city,
            state = state
        )
    }

    fun toDomain(): BankInfo {
        return BankInfo(
            bankName = bank,
            branch = branch,
            ifsc = ifsc,
            city = city,
            state = state
        )
    }
}
