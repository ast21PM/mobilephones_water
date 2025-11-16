package com.example.mobilephone_water

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    companion object {
        private const val NOTIFICATION_PERMISSION_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val ivLogo = findViewById<ImageView>(R.id.iv_logo)
        val tvTitle = findViewById<TextView>(R.id.tv_splash_title)
        val tvSubtitle = findViewById<TextView>(R.id.tv_splash_subtitle)
        val progressBar = findViewById<ProgressBar>(R.id.progress_bar_splash)
        val tvPercent = findViewById<TextView>(R.id.tv_progress_percent)

        // ✅ АНИМАЦИИ
        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val slideUpAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up)

        ivLogo.startAnimation(fadeInAnimation)
        tvTitle.startAnimation(slideUpAnimation)
        tvSubtitle.startAnimation(slideUpAnimation)

        // ✅ АНИМАЦИЯ ПОЛОСКИ ЗАГРУЗКИ (ЗАМЕДЛЕННО В 1.5 РАЗА)
        lifecycleScope.launch {
            for (i in 0..100 step 5) {
                progressBar.progress = i
                tvPercent.text = "$i%"
                delay(40) // ✅ БЫЛО 30, ТЕПЕРЬ 45 (1.5x)
            }

            // ✅ 100% - готово, ждём 750ms и идём дальше
            delay(650) // ✅ БЫЛО 500, ТЕПЕРЬ 750 (1.5x)

            // ✅ ЗАПРОС РАЗРЕШЕНИЙ
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        this@SplashActivity,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != android.content.pm.PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this@SplashActivity,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        NOTIFICATION_PERMISSION_CODE
                    )
                } else {
                    goToMainActivity()
                }
            } else {
                goToMainActivity()
            }
        }
    }

    private fun goToMainActivity() {
        lifecycleScope.launch {
            delay(4000) // ✅ БЫЛО 3000, ТЕПЕРЬ 4500 (1.5x)
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            goToMainActivity()
        }
    }
}
