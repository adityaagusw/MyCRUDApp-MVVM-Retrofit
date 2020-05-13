package com.example.crudmvvmretrofit.utils

import android.content.Context

class Constants {

    //companion object sama halnya dengan public static seperti di java
    companion object {
        const val API_ENDPOINT = "https://exampleretrofitmvvm.000webhostapp.com/"

        fun getToken(context: Context): String {
            val pref = context.getSharedPreferences("USER", Context.MODE_PRIVATE)
            val token = pref.getString("TOKEN", "undefined")
            return token!!
        }

        fun setToken(context: Context, token: String) {
            val pref = context.getSharedPreferences("USER", Context.MODE_PRIVATE)
            pref.edit().apply {
                putString("TOKEN", token)
                apply()
            }
        }

        fun clearToken(context: Context){
            val pref = context.getSharedPreferences("USER", Context.MODE_PRIVATE)
            pref.edit().clear().apply()
        }

        fun isValidEmail(email: String) = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        fun isValidPassword(pass: String) = pass.length > 8

    }

}