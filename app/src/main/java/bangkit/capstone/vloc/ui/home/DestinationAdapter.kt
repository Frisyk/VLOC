package bangkit.capstone.vloc.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.os.bundleOf
import androidx.core.util.Pair
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import bangkit.capstone.vloc.R
import bangkit.capstone.vloc.data.model.ListDestinationItem
import bangkit.capstone.vloc.databinding.DestinationItemBinding
import bangkit.capstone.vloc.ui.details.DetailsFragment
//import bangkit.capstone.vloc.data.model.ListDestinationItem
//import bangkit.capstone.vloc.databinding.DestinationItemBinding
import com.bumptech.glide.Glide


class DestinationAdapter : PagingDataAdapter<ListDestinationItem, DestinationAdapter.MyViewHolder>(DIFF_CALLBACK) {
    class MyViewHolder(private val binding: DestinationItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(destination: ListDestinationItem) {
           binding.tvItemName.text = destination.name.uppercase()
            binding.tvItemDescription.text = destination.description
            Glide.with(itemView.context)
                .load(destination.photoUrl)
                .into(binding.imgItemPhoto)

            itemView.setOnClickListener {
                val bundle = bundleOf("destinationId" to destination.id)
                it.findNavController().navigate(R.id.navigation_details, bundle)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = DestinationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val destination = getItem(position)
        if (destination != null) {
            holder.bind(destination)
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
