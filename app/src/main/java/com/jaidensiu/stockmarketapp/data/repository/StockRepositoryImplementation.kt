package com.jaidensiu.stockmarketapp.data.repository

import coil.network.HttpException
import com.jaidensiu.stockmarketapp.data.local.StockDatabase
import com.jaidensiu.stockmarketapp.data.mapper.toCompanyListing
import com.jaidensiu.stockmarketapp.data.remote.StockApi
import com.jaidensiu.stockmarketapp.domain.model.CompanyListing
import com.jaidensiu.stockmarketapp.domain.repository.StockRepository
import com.jaidensiu.stockmarketapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImplementation @Inject constructor(
    private val api: StockApi,
    private val db: StockDatabase
): StockRepository {
    private val dao = db.dao

    override suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>> {
        return flow {
            emit(Resource.Loading(isLoading = true))
            val localListings = dao.searchCompanyListing(query = query)
            emit(Resource.Success(data = localListings.map { it.toCompanyListing() }))
            val isDbEmpty = localListings.isEmpty() && query.isBlank()
            val shouldLoadFromCache = !isDbEmpty && !fetchFromRemote
            if (shouldLoadFromCache) {
                emit(Resource.Loading(isLoading = false))
                return@flow
            }
            val remoteListings = try {
                val response = api.getListings()
            } catch (e: IOException) {
                e.printStackTrace()
                emit(Resource.Error(message = "IOException caught."))
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error(message = "HttpException caught."))
            }
        }
    }

}