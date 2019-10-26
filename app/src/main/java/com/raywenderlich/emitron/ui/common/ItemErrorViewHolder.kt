package com.raywenderlich.emitron.ui.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.raywenderlich.emitron.R
import com.raywenderlich.emitron.databinding.ItemErrorBinding
import com.raywenderlich.emitron.ui.content.ContentAdapter
import com.raywenderlich.emitron.utils.UiStateManager
import com.raywenderlich.emitron.utils.extensions.isNetNotConnected
import com.raywenderlich.emitron.utils.extensions.toVisibility

/**
 * View holder for error
 */
class ItemErrorViewHolder(
  private val binding: ItemErrorBinding,
  private val retryCallback: () -> Unit,
  private val emptyCallback: (() -> Unit)?
) : RecyclerView.ViewHolder(binding.root) {

  init {
    binding.textViewProgress.visibility = View.GONE
    binding.buttonRetry.visibility = View.GONE
  }

  /**
   * @param uiState for this item layout
   * @param adapterContentType Type for current adapter.
   */
  fun bindTo(
    uiState: UiStateManager.UiState?,
    adapterContentType: ContentAdapter.AdapterContentType
  ) {

    with(binding) {
      progressBar.toVisibility(uiState == UiStateManager.UiState.LOADING)
      textViewProgress.toVisibility(uiState == UiStateManager.UiState.LOADING)
      textViewError.toVisibility(uiState?.hasError() == true)
      buttonRetry.toVisibility(uiState?.hasError() == true)
      buttonRetry.setOnClickListener {
        if (uiState?.isEmpty() == true) {
          emptyCallback?.invoke()
        } else {
          retryCallback()
        }
      }

      buttonRetry.setIconResource(
        getRetryActionIconResource(
          adapterContentType
        )
      )

      val resources = root.resources

      when (uiState) {
        UiStateManager.UiState.ERROR_CONNECTION -> {
          textViewError.text = resources.getString(R.string.error_no_internet)
          imageError.setImageResource(R.drawable.ic_emoji_crying)
        }
        UiStateManager.UiState.ERROR_EMPTY -> {
          textViewError.text = getEmptyErrorForAdapterType(adapterContentType)
          textViewErrorBody.text =
            resources.getString(R.string.error_library_no_content_body)
          textViewErrorBody.toVisibility(adapterContentType.isContentWithFilters())
          buttonRetry.toVisibility(!adapterContentType.isContentWithFilters())
          buttonRetry.text =
            getRetryButtonLabelForAdapterType(adapterContentType)
          val emptyDrawable = getEmptyDrawable(adapterContentType)
          emptyDrawable?.let { drawable ->
            imageError.setImageResource(drawable)
          }
        }
        else -> if (root.context.isNetNotConnected()) {
          textViewError.text =
            resources.getString(R.string.error_no_internet)
        } else {
          textViewError.text =
            resources.getString(R.string.error_generic)
        }
      }
    }
  }

  private fun getEmptyErrorForAdapterType(adapterContentType: ContentAdapter.AdapterContentType) =
    when (adapterContentType) {
      ContentAdapter.AdapterContentType.Content,
      ContentAdapter.AdapterContentType.ContentWithFilters,
      ContentAdapter.AdapterContentType.ContentWithSearch ->
        binding.root.resources.getString(R.string.error_library_no_content)
      ContentAdapter.AdapterContentType.ContentBookmarked ->
        binding.root.resources.getString(R.string.body_bookmarks_empty)
      ContentAdapter.AdapterContentType.ContentInProgress ->
        binding.root.resources.getString(R.string.body_progressions_empty)
      ContentAdapter.AdapterContentType.ContentCompleted ->
        binding.root.resources.getString(R.string.body_progressions_completed_empty)
      ContentAdapter.AdapterContentType.ContentDownloaded ->
        binding.root.resources.getString(R.string.body_downloads_empty)
    }

  private fun getRetryButtonLabelForAdapterType(
    adapterContentType:
    ContentAdapter.AdapterContentType
  ) =
    when (adapterContentType) {
      ContentAdapter.AdapterContentType.ContentWithSearch,
      ContentAdapter.AdapterContentType.Content,
      ContentAdapter.AdapterContentType.ContentWithFilters ->
        binding.root.resources.getString(R.string.button_retry)
      ContentAdapter.AdapterContentType.ContentBookmarked,
      ContentAdapter.AdapterContentType.ContentCompleted,
      ContentAdapter.AdapterContentType.ContentInProgress,
      ContentAdapter.AdapterContentType.ContentDownloaded ->
        binding.root.resources.getString(R.string.button_explore_tutorials)
    }

  private fun getRetryActionIconResource(
    adapterContentType:
    ContentAdapter.AdapterContentType
  ) =
    when (adapterContentType) {
      ContentAdapter.AdapterContentType.ContentWithSearch,
      ContentAdapter.AdapterContentType.Content,
      ContentAdapter.AdapterContentType.ContentWithFilters -> 0
      ContentAdapter.AdapterContentType.ContentBookmarked,
      ContentAdapter.AdapterContentType.ContentCompleted,
      ContentAdapter.AdapterContentType.ContentInProgress,
      ContentAdapter.AdapterContentType.ContentDownloaded ->
        R.drawable.ic_material_button_icon_arrow_right_green_contained
    }

  private fun getEmptyDrawable(
    adapterContentType:
    ContentAdapter.AdapterContentType
  ): Int? =
    when (adapterContentType) {
      ContentAdapter.AdapterContentType.ContentBookmarked ->
        R.drawable.ic_bookmarks
      ContentAdapter.AdapterContentType.ContentCompleted ->
        R.drawable.ic_completed
      ContentAdapter.AdapterContentType.ContentInProgress ->
        R.drawable.ic_in_progress
      ContentAdapter.AdapterContentType.ContentDownloaded ->
        R.drawable.ic_suitcaseempty
      else -> null
    }

  companion object {
    /**
     * Factory function to create [ItemErrorViewHolder]
     */
    fun create(
      parent: ViewGroup, layoutId: Int,
      retryCallback: () -> Unit,
      emptyCallback: (() -> Unit)?
    ): ItemErrorViewHolder =
      ItemErrorViewHolder(
        DataBindingUtil.inflate(
          LayoutInflater.from(parent.context),
          layoutId, parent, false
        ), retryCallback, emptyCallback
      )
  }
}
