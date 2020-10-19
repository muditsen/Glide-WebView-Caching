package com.mudit.glidewebviewcaching

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.os.Bundle
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webView = WebView(this)
        webView.settings.apply {
            javaScriptEnabled = true
        }

        webView.apply {
            webChromeClient = getChromeClient()
            webViewClient = getClient()
        }
        setContentView(webView)

        webView.loadUrl("https://imgur.com/")
    }

    private fun getChromeClient(): WebChromeClient {
        return object : WebChromeClient() {

        }
    }

    private fun getBitmapInputStream(bitmap:Bitmap,compressFormat: CompressFormat):InputStream{
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(compressFormat, 80, byteArrayOutputStream)
        val bitmapData: ByteArray = byteArrayOutputStream.toByteArray()
        return ByteArrayInputStream(bitmapData)
    }

    private fun getClient(): WebViewClient {
        return object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                return super.shouldInterceptRequest(view, request)
            }

            override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? {
                try{
                    if(url == null){
                        return super.shouldInterceptRequest(view, url as String)
                    }
                    return if(url.toLowerCase(Locale.ROOT).contains(".jpg") || url.toLowerCase(Locale.ROOT).contains(".jpeg")){
                        val bitmap = Glide.with(webView).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).load(url).submit().get()
                        WebResourceResponse("image/jpg", "UTF-8",getBitmapInputStream(bitmap,CompressFormat.JPEG))
                    }else if(url.toLowerCase(Locale.ROOT).contains(".png")){
                        val bitmap = Glide.with(webView).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).load(url).submit().get()
                        WebResourceResponse("image/png", "UTF-8",getBitmapInputStream(bitmap,CompressFormat.PNG))
                    }else if(url.toLowerCase(Locale.ROOT).contains(".webp")){
                        val bitmap = Glide.with(webView).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).load(url).submit().get()
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                            WebResourceResponse("image/webp", "UTF-8",getBitmapInputStream(bitmap,CompressFormat.WEBP_LOSSY))
                        } else {
                            WebResourceResponse("image/webp", "UTF-8",getBitmapInputStream(bitmap,CompressFormat.WEBP))

                        }

                    }else{
                        super.shouldInterceptRequest(view, url)
                    }
                }catch (e:Exception){
                    return super.shouldInterceptRequest(view, url)
                }

            }
        }
    }
}