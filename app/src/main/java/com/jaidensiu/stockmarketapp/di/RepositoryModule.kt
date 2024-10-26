package com.jaidensiu.stockmarketapp.di

import com.jaidensiu.stockmarketapp.data.csv.CSVParser
import com.jaidensiu.stockmarketapp.data.csv.CompanyListingsParser
import com.jaidensiu.stockmarketapp.data.csv.IntradayInfoParser
import com.jaidensiu.stockmarketapp.data.repository.StockRepositoryImplementation
import com.jaidensiu.stockmarketapp.domain.model.CompanyListing
import com.jaidensiu.stockmarketapp.domain.model.IntradayInfo
import com.jaidensiu.stockmarketapp.domain.repository.StockRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindCompanyListingsParser(
        companyListingsParser: CompanyListingsParser
    ): CSVParser<CompanyListing>

    @Binds
    @Singleton
    abstract fun bindStockRepository(
        stockRepositoryImplementation: StockRepositoryImplementation
    ): StockRepository

    @Binds
    @Singleton
    abstract fun bindIntradayInfoParser(
        intradayInfoParser: IntradayInfoParser
    ): CSVParser<IntradayInfo>
}
