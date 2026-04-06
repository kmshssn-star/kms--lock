package com.kmslock.app

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kmslock.app.databinding.ItemAppBinding

data class AppInfo(
    val packageName: String,
    val name: String,
    val icon: Drawable,
    var isSelected: Boolean = false
)

class AppListAdapter(private var apps: List<AppInfo>) : RecyclerView.Adapter<AppListAdapter.AppViewHolder>() {

    inner class AppViewHolder(private val binding: ItemAppBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(app: AppInfo) {
            binding.appName.text = app.name
            binding.appIcon.setImageDrawable(app.icon)
            binding.checkbox.isChecked = app.isSelected

            binding.root.setOnClickListener {
                app.isSelected = !app.isSelected
                binding.checkbox.isChecked = app.isSelected
                notifyItemChanged(adapterPosition)
            }

            binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                app.isSelected = isChecked
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val binding = ItemAppBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AppViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        holder.bind(apps[position])
    }

    override fun getItemCount(): Int = apps.size

    fun updateApps(newApps: List<AppInfo>) {
        apps = newApps
        notifyDataSetChanged()
    }

    fun getSelectedApps(): List<String> {
        return apps.filter { it.isSelected }.map { it.packageName }
    }
}
