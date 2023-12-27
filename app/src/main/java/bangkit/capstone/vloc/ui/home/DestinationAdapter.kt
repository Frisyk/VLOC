package bangkit.capstone.vloc.ui.home


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import bangkit.capstone.vloc.R
import bangkit.capstone.vloc.data.model.LocationListItem
import bangkit.capstone.vloc.databinding.DestinationItemBinding
import com.bumptech.glide.Glide


class DestinationAdapter : ListAdapter<LocationListItem, DestinationAdapter.MyViewHolder>(DIFF_CALLBACK) {
    class MyViewHolder(private val binding: DestinationItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(destination: LocationListItem) {
            Glide.with(itemView)
                .load(destination.destinationImgUrl)
                .into(binding.imgItemPhoto)

            itemView.setOnClickListener {
                val bundle = bundleOf("destinationId" to destination.id)
                it.findNavController().navigate(R.id.action_navigation_home_to_navigation_details2, bundle)
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
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<LocationListItem>() {
            override fun areItemsTheSame(oldItem: LocationListItem, newItem: LocationListItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: LocationListItem, newItem: LocationListItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
