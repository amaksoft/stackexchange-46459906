package com.example.testrxkotlin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tvMain : TextView? = findViewById(R.id.tvMain)

        getPosts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {response ->
                            tvMain?.text = response.body()?.string()
                        },
                        {
                            error ->
                            error.printStackTrace()
                        }
                )
    }

    fun getPosts(): Observable<Response> {
        val url = "https://jsonplaceholder.typicode.com/posts/1"
        val client = OkHttpClient.Builder()
                .addInterceptor(
                        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
                ).build()
        val request = Request.Builder()
                .url(url)
                .build()

        return Observable.create { em ->
            try {
                val response = client.newCall(request).execute()
                em.onNext(response)
                em.onComplete()
            } catch (err: Throwable) {
                err.printStackTrace()
                em.onError(err)
            }
        }
    }
}
