package ru.alexkubrick.android.radioproject.dataSource

import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.TransferListener
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.InputStream
import androidx.media3.datasource.DataSource

@UnstableApi
class CustomDataSource : DataSource {

    private var uri: Uri? = null
    private var inputStream: InputStream? = null
    private val client = OkHttpClient()

    @OptIn(UnstableApi::class)
    override fun open(dataSpec: DataSpec): Long {
        uri = dataSpec.uri
        val request = Request.Builder()
            .url(uri.toString())
            .build()

        val response: Response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            throw Exception("HTTP request failed with code: ${response.code}")
        }

        inputStream = response.body?.byteStream()
        return response.body?.contentLength() ?: 0L
    }

    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        return inputStream?.read(buffer, offset, length) ?: -1
    }

    override fun close() {
        inputStream?.close()
        inputStream = null
    }


    @OptIn(UnstableApi::class)
    override fun addTransferListener(transferListener: TransferListener) {
        // Добавить обработчик для мониторинга загрузки данных
    }

    override fun getUri(): Uri? {
        return uri
    }
}

@UnstableApi
class CustomDataSourceFactory : DataSource.Factory {
    override fun createDataSource(): DataSource {
        return CustomDataSource()
    }
}

