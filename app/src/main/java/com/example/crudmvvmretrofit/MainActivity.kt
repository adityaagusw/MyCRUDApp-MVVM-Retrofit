package com.example.crudmvvmretrofit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.crudmvvmretrofit.adapters.RecipeAdapter
import com.example.crudmvvmretrofit.utils.Constants
import com.example.crudmvvmretrofit.viewmodels.RecipeState
import com.example.crudmvvmretrofit.viewmodels.RecipeViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var recipeViewModel: RecipeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        isLoggedIn()
        setupRecycler()

        recipeViewModel = ViewModelProviders.of(this).get(RecipeViewModel::class.java)
        recipeViewModel.getRecipes().observe(this, Observer {
            rv_recipe.adapter?.let { adapter ->
                if (adapter is RecipeAdapter){
                    adapter.setRecipes(it)
                }
            }
        })

        recipeViewModel.getState().observer(this, Observer {
            handleUIState(it)
        })

        fab.setOnClickListener { view ->
            startActivity(Intent(this, RecipeActivity::class.java).apply {
                putExtra("is_update", false)
            })
        }

    }

    private fun setupRecycler(){
        rv_recipe.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = RecipeAdapter(mutableListOf(), this@MainActivity)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_signout -> {
                Constants.clearToken(this@MainActivity)
                startActivity(Intent(this@MainActivity, LoginActivity::class.java).apply {
                    flags  = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    flags  = Intent.FLAG_ACTIVITY_CLEAR_TOP
                }).also { finish() }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        recipeViewModel.fetchAllPost(Constants.getToken(this@MainActivity))
    }

    private fun handleUIState(it : RecipeState){
        when(it){
            is RecipeState.IsLoading -> isLoading(it.state)
            is RecipeState.Error -> {
                toast(it.err)
                isLoading(false)
            }
        }
    }

    //for handleUIState
    private fun toast(message : String?) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()

    private fun isLoading(state : Boolean){
        if(state){
            loading.visibility = View.VISIBLE
        }else{
            loading.visibility = View.GONE
        }
    }

    private fun isLoggedIn(){
        if(Constants.getToken(this@MainActivity).equals("undefined")){
            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }).also { finish() }
        }
    }

}
