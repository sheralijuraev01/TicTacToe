package uz.sher.tictocgame

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer

import uz.sher.tictocgame.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySplashBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_splash)

       object :CountDownTimer(1000,1000){
            override fun onTick(millisUntilFinished: Long) {

            }

           override fun onFinish() {
              startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            }

        }.start()

    }
}