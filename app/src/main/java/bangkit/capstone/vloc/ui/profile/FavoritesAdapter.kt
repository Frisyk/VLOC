package bangkit.capstone.vloc.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import bangkit.capstone.vloc.R
import bangkit.capstone.vloc.data.local.database.Favorites
import bangkit.capstone.vloc.databinding.DestinationItemBinding
import com.bumptech.glide.Glide

class FavoritesAdapter : ListAdapter<Favorites, FavoritesAdapter.MyViewHolder>(DIFF_CALLBACK) {

    class MyViewHolder(private val binding: DestinationItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(favorites: Favorites) {
            Glide.with(itemView.context)
                .load(favorites.destinationUrl)
                .into(binding.imgItemPhoto)

            itemView.setOnClickListener {
                val bundle = bundleOf("destinationId" to favorites.id)
                it.findNavController().navigate(R.id.action_navigation_home_to_navigation_details2, bundle)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = DestinationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val favorites = getItem(position)
        if (favorites != null) {
            holder.bind(favorites)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Favorites>() {
            override fun areItemsTheSame(oldItem: Favorites, newItem: Favorites): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Favorites, newItem: Favorites): Boolean {
                return oldItem == newItem
            }
        }
    }
}
