package com.jaidensiu.stockmarketapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jaidensiu.stockmarketapp.data.remote.dto.StockDao

@Database(
    entities = [CompanyListingEntity::class],
    version = 1
)
abstract class StockDatabase: RoomDatabase() {
    abstract val dao: StockDao
}
