package com.example.crudmvvmretrofit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.crudmvvmretrofit.models.Recipe
import com.example.crudmvvmretrofit.utils.Constants
import com.example.crudmvvmretrofit.viewmodels.RecipeState
import com.example.crudmvvmretrofit.viewmodels.RecipeViewModel
import kotlinx.android.synthetic.main.activity_recipe.*
import kotlinx.android.synthetic.main.content_recipe.*

class RecipeActivity : AppCompatActivity() {

    private lateinit var recipeViewModel: RecipeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener { finish() }
        recipeViewModel = ViewModelProviders.of(this).get(RecipeViewModel::class.java)
        if (isUpdate()) {
            doUpdate()
            recipeViewModel.fetchOnePost(
                Constants.getToken(this@RecipeActivity),
                getId().toString()
            )
            recipeViewModel.getOneRecipe().observe(this, Observer {
                fill(it)
            })
        } else {
            doCreate()
        }

        recipeViewModel.getState().observe(this, Observer {
            handleUIState(it)
        })
    }

    private fun isUpdate() = intent.getBooleanExtra("is_update", false)
    private fun getId() = intent.getIntExtra("id", 0)
    private fun doUpdate() {
        btn_submit.setOnClickListener {
            val title = et_title.text.toString().trim()
            val content = et_content.text.toString().trim()
            if (recipeViewModel.validate(title, content)) {
                recipeViewModel.update(
                    Constants.getToken(this),
                    getId().toString(),
                    title,
                    content
                )
            }
        }
    }

    private fun doCreate() {
        btn_submit.setOnClickListener {
            val title = et_title.text.toString().trim()
            val content = et_content.text.toString().trim()
            if (recipeViewModel.validate(title, content)) {
                recipeViewModel.create(Constants.getToken(this), title, content)
            }
        }
    }

    private fun fill(recipe: Recipe) {
        et_title.setText(recipe.title)
        et_content.setText(recipe.content)
    }

    private fun handleUIState(it: RecipeState) {
        when (it) {
            is RecipeState.IsLoading -> isLoading(it.state)
            is RecipeState.Error -> {
                toast(it.err)
                isLoading(false)
            }
            is RecipeState.ShowToast -> toast(it.message)
            is RecipeState.RecipeValidation -> {
                it.title?.let {
                    setTitleError(it)
                }
                it.content?.let {
                    setContentError(it)
                }
            }
            is RecipeState.Reset -> {
                setTitleError(null)
                setContentError(null)
            }
            is RecipeState.IsSuccess -> {
                when (it.what) {
                    0 -> {
                        toast("Berhasil dibuat")
                        finish()
                    }
                    1 -> {
                        toast("Berhasil diupdate")
                        finish()
                    }
                    2 -> {
                        toast("Berhasil delete")
                        finish()
                    }
                }
            }
        }
    }

    private fun isLoading(state: Boolean) {
        btn_submit.isEnabled = !state
    }

    private fun toast(message: String?) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    private fun setTitleError(err: String?) {
        in_title.error = err
    }

    private fun setContentError(err: String?) {
        in_content.error = err
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (isUpdate()) {
            menuInflater.inflate(R.menu.menu_recipe, menu)
            return true
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete -> {
                recipeViewModel.deleteRecipe(
                    Constants.getToken(this@RecipeActivity),
                    getId().toString()
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
