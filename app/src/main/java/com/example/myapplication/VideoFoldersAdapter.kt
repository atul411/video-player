package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class VideoFoldersAdapter(
    private val mediaFiles: ArrayList<MediaFiles>,
    private val folderPath: ArrayList<String>,
    private val context: Context
) : RecyclerView.Adapter<VideoFoldersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.folder_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val indexPath = folderPath[position].lastIndexOf("/")
        val nameOfFolder = folderPath[position].substring(indexPath + 1)
        holder.folderName.text = nameOfFolder
        holder.folderPath.text = folderPath[position]
        holder.itemView.setOnClickListener {
            val intent = Intent(context, VideoFilesActivity::class.java)
            intent.putExtra("folderName", nameOfFolder)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = folderPath.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val folderName: TextView = itemView.findViewById(R.id.folderName)
        val folderPath: TextView = itemView.findViewById(R.id.folderPath)
        val noOfFiles: TextView = itemView.findViewById(R.id.noOfFiles)
    }

    private fun noOfFiles(folderName: String): Int {
        var filesNo = 0
        for (mediaFile in mediaFiles) {
            if (mediaFile.path?.substring(0, mediaFile.path!!.lastIndexOf("/"))?.endsWith(folderName) == true) {
                filesNo++
            }
        }
        return filesNo
    }
}
