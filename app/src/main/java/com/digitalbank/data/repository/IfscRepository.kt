package com.digitalbank.data.repository

import android.content.Context
import com.digitalbank.data.local.AppDatabase
import com.digitalbank.data.remote.RetrofitClient
import com.digitalbank.model.BankInfo

class IfscRepository(private val context: Context) {
    private val api = RetrofitClient.ifscApi
    private val dao = AppDatabase.getInstance(context).ifscDao()

    suspend fun resolve(ifsc: String): BankInfo {
        dao.getIfsc(ifsc)?.let { return it.toDomain() }
        val dto = api.resolveIfsc(ifsc)
        dao.insert(dto.toEntity())
        return dto.toDomain()
    }
}
