package com.raywenderlich.emitron.data.download

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.raywenderlich.emitron.data.content.dao.DownloadDao
import com.raywenderlich.emitron.model.ContentType
import com.raywenderlich.emitron.model.DownloadState
import com.raywenderlich.emitron.model.entity.Download
import com.raywenderlich.emitron.model.entity.DownloadWithContent
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import javax.inject.Inject

/**
 * Local data source to fetch contents
 */
class DownloadDataSourceLocal @Inject constructor(
  private val downloadDao: DownloadDao
) {

  /**
   * Update content download url
   *
   * @param contentId Content id
   * @param url Download url
   */
  suspend fun updateDownloadUrl(
    contentId: String,
    url: String
  ) {
    downloadDao.updateUrl(contentId, url, DownloadState.IN_PROGRESS.ordinal)
  }

  /**
   * Update download progress
   *
   * @param contentId Content id
   * @param progress Int
   * @param state Download state [DownloadState]
   */
  suspend fun updateDownloadProgress(
    contentId: String,
    progress: Int,
    state: DownloadState
  ) {
    downloadDao.updateProgress(contentId, progress, state.ordinal)
  }

  /**
   * Update download state
   *
   * @param contentId Content id
   * @param state Download state [DownloadState]
   */
  suspend fun updateDownloadState(
    contentId: String,
    state: DownloadState
  ) {
    downloadDao.updateState(contentId, state.ordinal)
  }

  /**
   * Add new download
   *
   * @param contentId Content id
   * @param state Download state
   * @param createdAt LocalDateTime
   */
  suspend fun insertDownload(
    contentId: String,
    state: DownloadState,
    createdAt: LocalDateTime
  ) {
    val download = Download(
      contentId,
      state = state.ordinal,
      createdAt = createdAt.format(DateTimeFormatter.ISO_DATE_TIME)
    )
    downloadDao.insert(download)
  }

  /**
   * Remove a download
   *
   * @param downloadIds Download id
   */
  suspend fun deleteDownload(
    downloadIds: List<String>
  ) {
    downloadDao.delete(downloadIds)
  }

  /**
   * Delete all downloads
   */
  suspend fun deleteAllDownloads() {
    downloadDao.deleteAll()
  }

  /**
   * Get queued downloads
   *
   * @param limit no. of pending downloads to fetch
   * @param states download states
   * @param contentTypes download contentTypes
   */
  suspend fun getQueuedDownloads(
    limit: Int = 1,
    states: Array<Int>,
    contentTypes: Array<String>
  ): List<DownloadWithContent> =
    downloadDao.getQueuedDownloads(limit, states, contentTypes)

  /**
   * Get queued download by id
   *
   * @param downloadId Download id
   */
  suspend fun getDownload(downloadId: String): DownloadWithContent? =
    downloadDao.getDownload(downloadId)

  /**
   * Add downloads
   */
  suspend fun addDownloads(downloads: List<Download>) {
    downloadDao.insert(downloads)
  }

  /**
   * Get queued downloads factory
   */
  fun getQueuedDownloads(): DataSource.Factory<Int, DownloadWithContent> =
    downloadDao.getQueuedDownloads(ContentType.getAllowedContentTypes())

  /**
   * Get downloads by id
   *
   * @param ids Download ids
   */
  fun getDownloadsById(ids: List<String>): LiveData<List<Download>> =
    downloadDao.getDownloadsById(ids)
}
