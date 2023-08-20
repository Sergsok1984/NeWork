package ru.sokolov_diplom.nework

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.sokolov_diplom.nework.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}
