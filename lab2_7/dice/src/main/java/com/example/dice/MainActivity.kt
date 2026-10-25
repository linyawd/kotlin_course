package com.example.dice

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.dice.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var imageViews: Array<ImageView>

    private val viewModel: DiceViewModel by viewModels()

    private val drawables = arrayOf(
        R.drawable.die_1, R.drawable.die_2,
        R.drawable.die_3, R.drawable.die_4,
        R.drawable.die_5, R.drawable.die_6
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding for view object references
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageViews = arrayOf(   // Views: dice
            binding.die1,
            binding.die2,
            binding.die3,
            binding.die4,
            binding.die5
        )

        binding.rollButton.setOnClickListener {
            rollTheDice()
        }

        setupObservers()
    }

    private fun setupObservers() {
        viewModel.diceValues.observe(this) { diceList ->
            if (diceList.size >= imageViews.size) {
                for (i in imageViews.indices) {
                    val diceValue = diceList[i]
                    val drawableIndex = (diceValue - 1).coerceIn(0, 5)
                    imageViews[i].setImageResource(drawables[drawableIndex])
                }
            }
        }

        viewModel.isRolling.observe(this) { isRolling ->
            binding.rollButton.isEnabled = !isRolling
        }
    }

    private fun rollTheDice() {
        viewModel.rollDice()
    }
}