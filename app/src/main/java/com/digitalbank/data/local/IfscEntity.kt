package com.digitalbank.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.digitalbank.model.BankInfo

@Entity(tableName = "ifsc_cache")
data class IfscEntity(
    @PrimaryKey val ifsc: String,
    val bankName: String,
    val branch: String,
    val city: String,
    val state: String
) {
    fun toDomain(): BankInfo {
        return BankInfo(
            bankName = bankName,
            branch = branch,
            ifsc = ifsc,
            city = city,
            state = state
        )
    }
}
