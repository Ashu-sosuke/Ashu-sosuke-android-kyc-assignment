package com.digitalbank.data.repository

import android.content.Context
import com.digitalbank.data.local.AppDatabase
import com.digitalbank.data.remote.RetrofitClient
import com.digitalbank.model.Customer
import com.digitalbank.model.KycStatus
import com.digitalbank.util.CACHE_TTL_MS
import com.digitalbank.util.isExpired

class CustomerRepository(private val context: Context) {
    private val api = RetrofitClient.dummyJsonApi
    private val dao = AppDatabase.getInstance(context).customerDao()

    suspend fun getCustomers(page: Int): List<Customer> {
        val skip = page * 30
        val cached = dao.getCustomers()
        val isFresh = cached.isNotEmpty() && !cached[0].cachedAt.isExpired(CACHE_TTL_MS)
        
        return if (isFresh && page == 0) {
            cached.map { it.toDomain() }
        } else {
            val response = api.getUsers(limit = 30, skip = skip)
            val entities = response.users.map { user ->
                val existing = dao.getCustomerById(user.id)
                user.toEntity(
                    existingSelfiePath = existing?.selfiePath,
                    existingKycStatus = existing?.kycStatus ?: KycStatus.PENDING.name
                )
            }
            if (page == 0) dao.deleteAll()
            dao.insertAll(entities)
            entities.map { it.toDomain() }
        }
    }

    suspend fun getCustomer(id: Int): Customer {
        val cached = dao.getCustomerById(id)
        return if (cached != null) {
            cached.toDomain()
        } else {
            val userDto = api.getUser(id)
            val entity = userDto.toEntity()
            dao.insert(entity)
            entity.toDomain()
        }
    }

    suspend fun completeKyc(customerId: Int, selfiePath: String) {
        dao.updateKyc(customerId, KycStatus.VERIFIED.name, selfiePath)
    }
}
