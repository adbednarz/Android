package com.example.l6z1

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var retrofit: Retrofit
    private lateinit var spinner: Spinner
    private lateinit var editText: EditText
    private lateinit var textView: TextView
    private lateinit var paths: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.spinner = findViewById(R.id.spinner)
        this.editText = findViewById(R.id.editText)
        this.textView = findViewById(R.id.textView)
        this.paths = resources.getStringArray(R.array.paths)

        // tworzenie spinnera
        ArrayAdapter.createFromResource(
            this,
            R.array.operations,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        // zbudowanie instancji z biblioteki retrofit
        this.retrofit = Retrofit.Builder()
            .baseUrl("https://newton.now.sh/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // funkcja, która wywoła się po naciśnięciu przycisku
    fun onClick(view: View) {
        // po kliknięciu automatycznie schowaj klawiaturę
        val inputManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(view.windowToken, 0)
        // tworzenie API
        val newtonAPI = retrofit.create(INewtonAPI::class.java)
        // strzelanie
        val call = newtonAPI.getResponse(paths[this.spinner.selectedItemPosition],
            java.net.URLEncoder.encode(this.editText.text.toString(), "utf-8"))
        call.enqueue( object : Callback<Result> {
            override fun onFailure(call: Call<Result>, t: Throwable) {
                println(t.message)
            }

            override fun onResponse(call: Call<Result>?, response: Response<Result>?) {
                try {
                    val body = response?.body()
                    textView.text = body!!.result.toString()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
            }
        })
    }
}