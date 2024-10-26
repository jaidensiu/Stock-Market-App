package com.jaidensiu.stockmarketapp.domain.repository

import com.jaidensiu.stockmarketapp.domain.model.CompanyListing
import com.jaidensiu.stockmarketapp.util.Resource
import kotlinx.coroutines.flow.Flow

interface StockRepository {
    suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>>
}
