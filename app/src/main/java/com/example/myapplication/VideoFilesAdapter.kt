package com.example.myapplication

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.File
import java.util.concurrent.TimeUnit

class VideoFilesAdapter(
    private val videoList: ArrayList<MediaFiles>,
    private val context: Context
) : RecyclerView.Adapter<VideoFilesAdapter.ViewHolder>() {

    private lateinit var bottomSheetDialog: BottomSheetDialog

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.video_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.videoName.text = videoList[position].displayName
        val size = videoList[position].size ?: "0"
        holder.videoSize.text = Formatter.formatFileSize(context, size.toLong())
        val milliSeconds = videoList[position].duration?.toLong() ?: 0L
        holder.videoDuration.text = timeConversion(milliSeconds)
        Glide.with(context).load(File(videoList[position].path ?: "")).into(holder.thumbNail)

        holder.menu_more.setOnClickListener {
            bottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetTheme)
            val bsView = LayoutInflater.from(context).inflate(R.layout.video_bs_layout, it.findViewById(R.id.bottom_sheet))

            bsView.findViewById<View>(R.id.bs_play).setOnClickListener {
                holder.itemView.performClick()
                bottomSheetDialog.dismiss()
            }

            bsView.findViewById<View>(R.id.bs_rename).setOnClickListener {
                val alertDialog = AlertDialog.Builder(context)
                alertDialog.setTitle("Rename to")
                val editText = EditText(context)
                val path = videoList[position].path ?: ""
                val file = File(path)
                var videoName = file.name
                videoName = videoName.substring(0, videoName.lastIndexOf("."))
                editText.setText(videoName)
                alertDialog.setView(editText)
                editText.requestFocus()
                alertDialog.setPositiveButton("OK") { _, _ ->
                    val onlyPath = file.parentFile?.absolutePath ?: ""
                    val ext = file.absolutePath.substring(file.absolutePath.lastIndexOf("."))
                    val newPath = "$onlyPath/${editText.text}$ext"
                    val newFile = File(newPath)
                    val rename = file.renameTo(newFile)
                    if (rename) {
                        val resolver: ContentResolver = context.applicationContext.contentResolver
                        resolver.delete(MediaStore.Files.getContentUri("external"), "${MediaStore.MediaColumns.DATA}?", arrayOf(file.absolutePath))
                        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                        intent.data = Uri.fromFile(newFile)
                        context.applicationContext.sendBroadcast(intent)
                        notifyDataSetChanged()
                        Toast.makeText(context, "Video Renamed", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Process Failed", Toast.LENGTH_SHORT).show()
                    }
                }
                alertDialog.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                alertDialog.create().show()
                bottomSheetDialog.dismiss()
            }

            bsView.findViewById<View>(R.id.bs_share).setOnClickListener {
                val uri = Uri.parse(videoList[position].path)
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "video/*"
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                context.startActivity(Intent.createChooser(shareIntent, "Share Via"))
                bottomSheetDialog.dismiss()
            }

            bsView.findViewById<View>(R.id.bs_delete).setOnClickListener {
                val alertDialog = AlertDialog.Builder(context)
                alertDialog.setTitle("Delete")
                alertDialog.setMessage("Do you really want to delete this video")
                alertDialog.setPositiveButton("Delete") { _, _ ->
                    val uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoList[position].id?.toLong() ?: 0L)
                    val file = File(videoList[position].path ?: "")
                    val delete = file.delete()
                    if (delete) {
                        context.contentResolver.delete(uri, null, null)
                        videoList.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, videoList.size)
                        Toast.makeText(context, "Video Deleted", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Video can't be Deleted", Toast.LENGTH_SHORT).show()
                    }
                }
                alertDialog.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                alertDialog.show()
                bottomSheetDialog.dismiss()
            }

            bsView.findViewById<View>(R.id.bs_properties).setOnClickListener {
                val alertDialog = AlertDialog.Builder(context)
                alertDialog.setTitle("Properties")
                val one = "File: ${videoList[position].displayName}"
                val path = videoList[position].path ?: ""
                val two = "Path: ${path.substring(0, path.lastIndexOf("/"))}"
                val three = "Size: ${Formatter.formatFileSize(context, size.toLong())}"
                val fourth = "Length: ${timeConversion(milliSeconds)}"
                val nameWithFormat = videoList[position].displayName ?: ""
                val format = nameWithFormat.substring(nameWithFormat.lastIndexOf(".") + 1)
                val five = "Format: $format"
                val mediaMetadataRetriever = MediaMetadataRetriever()
                mediaMetadataRetriever.setDataSource(videoList[position].path)
                val height = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
                val width = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
                val six = "Resolution: ${width}x${height}"
                alertDialog.setMessage("$one\n\n$two\n\n$three\n\n$fourth\n\n$five\n\n$six")
                alertDialog.setPositiveButton("OK") { _, _ -> }
                alertDialog.setNegativeButton("Cancel") { _, _ -> }
                alertDialog.show()
                bottomSheetDialog.dismiss()
            }

            bottomSheetDialog.setContentView(bsView)
            bottomSheetDialog.show()
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, VideoPlayerActivity::class.java)
            intent.putExtra("position", position)
            intent.putExtra("title", videoList[position].displayName)
            val bundle = Bundle()
            bundle.putParcelableArrayList("videoArrayList", videoList)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = videoList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val thumbNail: ImageView = itemView.findViewById(R.id.thumbnail)
        val menu_more: ImageView = itemView.findViewById(R.id.video_menu_more)
        val videoName: TextView = itemView.findViewById(R.id.video_name)
        val videoDuration: TextView = itemView.findViewById(R.id.video_duration)
        val videoSize: TextView = itemView.findViewById(R.id.video_size)
    }

    fun timeConversion(time: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(time).toInt()
        val minute = (TimeUnit.MILLISECONDS.toMinutes(time) % 60).toInt()
        val seconds = (TimeUnit.MILLISECONDS.toSeconds(time) % 60).toInt()
        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minute, seconds)
        } else {
            String.format("%02d:%02d", minute, seconds)
        }
    }
}
