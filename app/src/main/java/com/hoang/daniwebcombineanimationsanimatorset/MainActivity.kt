package com.hoang.daniwebcombineanimationsanimatorset

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.Path
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout

private const val TAG = "MAIN_ACTIVITY"

class MainActivity : AppCompatActivity() {
    // Size is added to x,y coordinates to make the planet go off screen
    private var planetSize = Pair(0, 0)

    // Location of planet when it is shown on screen
    private var activeXY = Pair(0f, 0f)

    // Location of planet when hidden outside left or right bounds
    private var hiddenLeftXY = Pair(0f, 0f)
    private var hiddenRightXY = Pair(0f, 0f)

    // Control points for quadTo() function
    private var risingControlXY = Pair(0f, 0f)
    private var settingControlXY = Pair(0f, 0f)

    // Simple state for which kind of animation needs to be activated
    private var isNight = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.switch_day_night)

        val container = findViewById<ConstraintLayout>(R.id.container)
        val cloud = findViewById<ImageView>(R.id.cloud)
        val sun = findViewById<ImageView>(R.id.sun)
        val moon = findViewById<ImageView>(R.id.moon)

        // Initiates coordinates after the sun is drawn
        sun.post {
            planetSize = Pair(sun.width, sun.height)
            activeXY = Pair(sun.x, sun.y)
            hiddenLeftXY = Pair(
                0f - planetSize.first,
                activeXY.second + planetSize.second
            )
            hiddenRightXY = Pair(
                activeXY.first * 2 + planetSize.first,
                activeXY.second + planetSize.second
            )
            risingControlXY = Pair(
                activeXY.first - activeXY.first * 0.75f,
                activeXY.second
            )
            settingControlXY = Pair(
                activeXY.first + activeXY.first * 0.75f,
                activeXY.second
            )
        }

        button.setOnClickListener {
            // Animates the background color from night to morning
            val containerAnimator = ValueAnimator.ofArgb(
                if (isNight) Color.BLACK else Color.WHITE,
                if (isNight) Color.WHITE else Color.BLACK
            ).apply {
                addUpdateListener {
                    container.setBackgroundColor(it.animatedValue as Int)
                }
            }

            // Animates the cloud color from night to morning
            val cloudAnimator = ValueAnimator.ofArgb(
                if (isNight) Color.GRAY else Color.BLUE,
                if (isNight) Color.BLUE else Color.GRAY
            ).apply {
                addUpdateListener {
                    cloud.background.setTint(it.animatedValue as Int)
                }
            }

            // Both sun and moon can use this animator
            // Animates the planet setting
            val settingPath = Path().apply {
                moveTo(activeXY.first, activeXY.second)
                quadTo(
                    settingControlXY.first,
                    settingControlXY.second,
                    hiddenRightXY.first,
                    hiddenRightXY.second
                )
            }

            // Both sun and moon can use this animator
            // Animates the planet rising
            val risingPath = Path().apply {
                moveTo(hiddenLeftXY.first, hiddenLeftXY.second)
                quadTo(
                    risingControlXY.first,
                    risingControlXY.second,
                    activeXY.first,
                    activeXY.second
                )
            }

            // Animates the sun rising or setting
            val sunAnimator = ObjectAnimator.ofFloat(
                sun,
                "x",
                "y",
                if (isNight) risingPath else settingPath
            )

            // Animates the moon rising or setting
            val moonAnimator = ObjectAnimator.ofFloat(
                moon,
                "x",
                "y",
                if (isNight) settingPath else risingPath
            )

            AnimatorSet().apply {
                // Play animations separately
                playSequentially(
                    containerAnimator,
                    cloudAnimator,
                    sunAnimator,
                    moonAnimator
                )

                // Play animations together
/*                playTogether(
                    containerAnimator,
                    cloudAnimator,
                    sunAnimator,
                    moonAnimator,
                )*/

                duration = 2000L
                start()
            }

            isNight = !isNight
        }
    }

}