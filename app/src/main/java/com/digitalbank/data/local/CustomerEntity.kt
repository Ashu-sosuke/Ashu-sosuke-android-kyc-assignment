package com.digitalbank.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.digitalbank.model.Customer
import com.digitalbank.model.KycStatus
import com.digitalbank.util.IFSC_LIST

@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val birthDate: String,
    val image: String?,
    val nationality: String,
    val address: String,
    val iban: String,
    val kycStatus: String,
    val selfiePath: String?,
    val cachedAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): Customer {
        val name = "$firstName $lastName"
        val status = try {
            KycStatus.valueOf(kycStatus)
        } catch (e: Exception) {
            KycStatus.PENDING
        }
        
        val ifscCode = IFSC_LIST[id % IFSC_LIST.size]
        
        val type = when (id % 10) {
            0, 1 -> "NRI"
            2, 3 -> "Current"
            else -> "Savings"
        }
        
        val bal = (id * 7919 + 1000) % 200000 + 1000
        
        val maskedIban = "**** " + iban.takeLast(4)
        val contact = "$email / $phone"
        
        return Customer(
            id = id,
            name = name,
            imageUrl = image,
            accountType = type,
            balance = bal,
            kycStatus = status,
            selfiePath = selfiePath,
            maskedIban = maskedIban,
            birthDate = birthDate,
            nationality = nationality,
            address = address,
            contact = contact,
            ifsc = ifscCode
        )
    }
}
