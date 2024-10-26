package com.jaidensiu.stockmarketapp.data.repository

import com.jaidensiu.stockmarketapp.data.csv.CSVParser
import com.jaidensiu.stockmarketapp.data.csv.IntradayInfoParser
import com.jaidensiu.stockmarketapp.data.local.StockDatabase
import com.jaidensiu.stockmarketapp.data.mapper.toCompanyInfo
import com.jaidensiu.stockmarketapp.data.mapper.toCompanyListing
import com.jaidensiu.stockmarketapp.data.mapper.toCompanyListingEntity
import com.jaidensiu.stockmarketapp.data.remote.StockApi
import com.jaidensiu.stockmarketapp.domain.model.CompanyInfo
import com.jaidensiu.stockmarketapp.domain.model.CompanyListing
import com.jaidensiu.stockmarketapp.domain.model.IntradayInfo
import com.jaidensiu.stockmarketapp.domain.repository.StockRepository
import com.jaidensiu.stockmarketapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImplementation @Inject constructor(
    private val api: StockApi,
    db: StockDatabase,
    private val companyListingsParser: CSVParser<CompanyListing>,
    private val intradayInfoParser: CSVParser<IntradayInfo>
): StockRepository {
    private val dao = db.dao

    override suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>> {
        return flow {
            emit(Resource.Loading(isLoading = true))
            val localListings = dao.searchCompanyListing(query = query)
            emit(Resource.Success(
                data = localListings.map { it.toCompanyListing() }
            ))
            val isDbEmpty = localListings.isEmpty() && query.isBlank()
            val shouldLoadFromCache = !isDbEmpty && !fetchFromRemote
            if (shouldLoadFromCache) {
                emit(Resource.Loading(isLoading = false))
                return@flow
            }
            val remoteListings = try {
                val response = api.getListings()
                companyListingsParser.parse(response.byteStream())
            } catch (e: IOException) {
                e.printStackTrace()
                emit(Resource.Error(message = "IOException caught."))
                null
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error(message = "HttpException caught."))
                null
            }
            remoteListings?.let { listings ->
                dao.clearCompanyListings()
                dao.insertCompanyListings(
                    companyListingEntities = listings.map { it.toCompanyListingEntity() }
                )
                emit(Resource.Success(
                    data = dao.searchCompanyListing(query = "").map { it.toCompanyListing() }
                ))
                emit(Resource.Loading(isLoading = false))
            }
        }
    }

    override suspend fun getIntradayInfo(symbol: String): Resource<List<IntradayInfo>> {
        return try {
            val response = api.getIntradayInfo(symbol = symbol)
            val results = intradayInfoParser.parse(response.byteStream())
            Resource.Success(results)
        } catch (e: IOException) {
            e.printStackTrace()
            Resource.Error(message = "IOException caught.")
        } catch (e: HttpException) {
            e.printStackTrace()
            Resource.Error(message = "HttpException caught.")
        }
    }

    override suspend fun getCompanyInfo(symbol: String): Resource<CompanyInfo> {
        return try {
            val result = api.getCompanyInfo(symbol = symbol)
            Resource.Success(result.toCompanyInfo())
        } catch (e: IOException) {
            e.printStackTrace()
            Resource.Error(message = "IOException caught.")
        } catch (e: HttpException) {
            e.printStackTrace()
            Resource.Error(message = "HttpException caught.")
        }
    }
}
