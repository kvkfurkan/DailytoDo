package com.furkankavak.dailytodo

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.animation.AnimationUtils
import com.furkankavak.dailytodo.databinding.ActivitySplashScreenBinding

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {

    private lateinit var binding : ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val animation = AnimationUtils.loadAnimation(this, R.anim.animation)

        binding.textView.animation = animation
        binding.textView2.animation = animation

        val timer = object: CountDownTimer(2800, 1000) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                val intent = Intent(this@SplashScreen, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        timer.start()
    }
}