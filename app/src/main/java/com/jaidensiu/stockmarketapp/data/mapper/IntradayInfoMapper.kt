package com.jaidensiu.stockmarketapp.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.jaidensiu.stockmarketapp.data.remote.dto.CompanyInfoDto
import com.jaidensiu.stockmarketapp.data.remote.dto.IntradayInfoDto
import com.jaidensiu.stockmarketapp.domain.model.CompanyInfo
import com.jaidensiu.stockmarketapp.domain.model.IntradayInfo
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
fun IntradayInfoDto.toIntradayInfo(): IntradayInfo {
    val pattern = "yyyy-MM-dd HH:mm:ss"
    val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
    val localDateTime = LocalDateTime.parse(timestamp, formatter)
    return IntradayInfo(
        date = localDateTime,
        close = close
    )
}

fun CompanyInfoDto.toCompanyInfo(): CompanyInfo {
    return CompanyInfo(
        symbol = symbol ?: "",
        description = description ?: "",
        name = name ?: "",
        country = country ?: "",
        industry = industry ?: ""
    )
}
