package ru.alexkubrick.android.radioproject.dataSource

import android.net.Uri
import androidx.annotation.Nullable
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.TransferListener
import java.io.IOException

interface DataSource {

    @OptIn(UnstableApi::class)
    fun open(dataSpec: DataSpec): Long

    @Throws(IOException::class)
    fun read(buffer: ByteArray, offset: Int, length: Int): Int

    @Throws(IOException::class)
    fun close()

    @OptIn(UnstableApi::class)
    fun addTransferListener(transferListener: TransferListener)

    @Nullable
    fun getUri(): Uri?
}
