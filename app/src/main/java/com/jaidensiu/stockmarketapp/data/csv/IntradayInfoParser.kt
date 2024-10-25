package com.jaidensiu.stockmarketapp.data.csv

import android.os.Build
import androidx.annotation.RequiresApi
import com.jaidensiu.stockmarketapp.data.mapper.toIntradayInfo
import com.jaidensiu.stockmarketapp.data.remote.dto.IntradayInfoDto
import com.jaidensiu.stockmarketapp.domain.model.IntradayInfo
import com.opencsv.CSVReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IntradayInfoParser @Inject constructor(): CSVParser<IntradayInfo> {
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun parse(stream: InputStream): List<IntradayInfo> {
        val csvReader = CSVReader(InputStreamReader(stream))
        return withContext(Dispatchers.IO) {
            csvReader
                .readAll()
                .drop(n = 1)
                .mapNotNull { line ->
                    val timestamp = line.getOrNull(index = 0) ?: return@mapNotNull null
                    val close = line.getOrNull(index = 4) ?: return@mapNotNull null
                    val dto = IntradayInfoDto(
                        timestamp = timestamp,
                        close = close.toDouble()
                    )
                    dto.toIntradayInfo()
                }
                .filter { it.date.dayOfMonth == LocalDateTime.now().minusDays(1).dayOfMonth }
                .sortedBy { it.date.hour }
                .also { csvReader.close() }
        }
    }
}
