package com.mayada1994.paint.activities

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mayada1994.paint.R
import com.mayada1994.paint.databinding.ActivityMainBinding
import com.mayada1994.paint.databinding.DialogStrokeWidthPickerBinding

@SuppressLint("UseCompatTextViewDrawableApis")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var currentColor: Int = R.color.black

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            txtSelectedColor.run {
                compoundDrawableTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this@MainActivity, R.color.black))
                setOnClickListener { showColorPicker() }
            }
            txtStrokeWidthValue.run {
                text = paintView.getStrokeWidth().toInt().toString()
                setOnClickListener { showStrokeWidthPicker() }
            }
        }
    }

    private fun showColorPicker() {
        ColorPickerDialogBuilder
            .with(this)
            .setTitle("Choose color")
            .initialColor(currentColor)
            .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
            .density(12)
            .setPositiveButton(getString(R.string.ok)) { _, selectedColor, _ ->
                currentColor = selectedColor
                binding.txtSelectedColor.compoundDrawableTintList = ColorStateList.valueOf(selectedColor)
                binding.paintView.changeColor(selectedColor)
            }
            .setNegativeButton(getString(R.string.cancel)) { _, _ -> }
            .build()
            .show()
    }

    private fun showStrokeWidthPicker() {
        val dialogView = DialogStrokeWidthPickerBinding.inflate(layoutInflater)
        with(dialogView) {
            sliderStrokeWidth.value = binding.paintView.getStrokeWidth()
            MaterialAlertDialogBuilder(this@MainActivity)
                .setPositiveButton(R.string.ok) { _, _ ->
                    binding.paintView.changeStrokeWidth(sliderStrokeWidth.value)
                    binding.txtStrokeWidthValue.text =
                        (binding.paintView.getStrokeWidth()).toInt().toString()
                }
                .setNegativeButton(R.string.cancel, null)
                .setView(dialogView.root)
                .create()
                .show()
        }
    }

}