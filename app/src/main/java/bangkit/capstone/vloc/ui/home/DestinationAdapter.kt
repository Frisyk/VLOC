package bangkit.capstone.vloc.ui.home

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import bangkit.capstone.vloc.R
import bangkit.capstone.vloc.data.model.ListDestinationItem
import bangkit.capstone.vloc.databinding.DestinationItemBinding
import com.bumptech.glide.Glide


class DestinationAdapter : PagingDataAdapter<ListDestinationItem, DestinationAdapter.MyViewHolder>(DIFF_CALLBACK) {

    class MyViewHolder(private val binding: DestinationItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(story: ListDestinationItem) {
           binding.tvItemName.text = story.name.uppercase()
            binding.tvItemDescription.text = story.description
            Glide.with(itemView.context)
                .load(story.photoUrl)
                .into(binding.imgItemPhoto)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = DestinationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(story)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListDestinationItem>() {
            override fun areItemsTheSame(oldItem: ListDestinationItem, newItem: ListDestinationItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListDestinationItem, newItem: ListDestinationItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
