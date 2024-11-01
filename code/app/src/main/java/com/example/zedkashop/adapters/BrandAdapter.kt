import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.zedkashop.R
import android.content.Context

class BrandAdapter(
    private val context: Context,
    private val brands: List<BrandDB>,
    private val onBrandClick: (BrandDB) -> Unit
) : RecyclerView.Adapter<BrandAdapter.BrandViewHolder>() {

    inner class BrandViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val logo: ImageView = view.findViewById(R.id.brandLogo)


        init {
            view.setOnClickListener {
                onBrandClick(brands[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrandViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_brand, parent, false)
        return BrandViewHolder(view)
    }

    override fun onBindViewHolder(holder: BrandViewHolder, position: Int) {
        val brand = brands[position]
        // Загружаем изображение с использованием Glide
        Glide.with(context)
            .load(brand.imageUrl) // URL изображения
            .into(holder.logo)


    }

    override fun getItemCount(): Int = brands.size
}