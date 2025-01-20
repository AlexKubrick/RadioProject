package ru.alexkubrick.android.radioproject.dataSource

import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.TransferListener
import java.io.IOException

class CustomDataSource : DataSource {

    private var uri: Uri? = null

    @OptIn(UnstableApi::class)
    override fun open(dataSpec: DataSpec): Long {
        uri = dataSpec.uri
        // Тут логика работы с источником (файл, сеть, API и т.д.)
        return 0 // Вернуть количество доступных байт
    }

    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        // Заполнить буфер данными и вернуть количество прочитанных байт
        return -1 // Вернуть -1 при конце потока
    }

    override fun close() {
        // Очистить ресурсы
    }

    @OptIn(UnstableApi::class)
    override fun addTransferListener(transferListener: TransferListener) {
        // Добавить обработчик для мониторинга загрузки данных
    }

    override fun getUri(): Uri? {
        return uri
    }
}
