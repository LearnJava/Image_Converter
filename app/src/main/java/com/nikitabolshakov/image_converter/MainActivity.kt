package com.nikitabolshakov.image_converter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.nikitabolshakov.image_converter.databinding.ActivityMainBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.ByteArrayOutputStream
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var disp: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bitmap = assetsToBitmap("dog.jpg")
        bitmap?.let {
            binding.bitmapImage.setImageBitmap(bitmap)
        }

        binding.convertStop.setOnClickListener {
            Log.d("RxJava", "Pressed button STOP")
            disp.dispose()
        }

        binding.convertButton.setOnClickListener {
            if (bitmap != null) {
                Log.d("RxJava", "Start converter, pressed start button.")
                disp = bitmap.compress(Bitmap.CompressFormat.PNG)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            println("Starting converter...")
                            binding.compressedImage.setImageBitmap(it)
                            binding.text.text = getString(R.string.text_convert_success)
                        },
                        {
                            Log.e("RxJava", it.stackTraceToString())
                        },

                        {
                            Log.d("RxJava", "Success")
                        }
                    )
            } else {
                Log.e("666", "Error")
            }
        }
    }

    override fun onDestroy() {
        disp.dispose()
        super.onDestroy()
    }

    private fun assetsToBitmap(fileName: String): Bitmap? {
        return try {
            val stream = assets.open(fileName)
            BitmapFactory.decodeStream(stream)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}

fun Bitmap.compress(
    format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
    quality: Int = 100
): @NonNull Flowable<Bitmap> {
    val stream = ByteArrayOutputStream()
    this.compress(format, quality, stream)
    val byteArray = stream.toByteArray()
    return  Flowable.just(BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size))
}