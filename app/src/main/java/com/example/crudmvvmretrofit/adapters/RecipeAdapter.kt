package com.example.crudmvvmretrofit.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.crudmvvmretrofit.R
import com.example.crudmvvmretrofit.RecipeActivity
import com.example.crudmvvmretrofit.models.Recipe
import kotlinx.android.synthetic.main.list_item_recipe.view.*

class RecipeAdapter(private var recipes : MutableList<Recipe>, private var context: Context) : RecyclerView.Adapter<RecipeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_recipe, parent, false))
    }

    fun setRecipes(r : List<Recipe>){
        recipes.clear()
        recipes.addAll(r)
        notifyDataSetChanged()
    }

    override fun getItemCount() = recipes.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(recipes[position], context)

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        fun bind(recipe : Recipe, context: Context){
            itemView.tv_title.text = recipe.title
            itemView.tv_content.text = recipe.content
            itemView.setOnClickListener {
                context.startActivity(Intent( context, RecipeActivity::class.java).apply {
                    putExtra("is_update", true)
                    putExtra("id", recipe.id)
                })
            }
        }
    }
}