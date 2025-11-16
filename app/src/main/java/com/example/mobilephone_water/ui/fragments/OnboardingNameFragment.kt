package com.example.mobilephone_water.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.mobilephone_water.OnboardingActivity
import com.example.mobilephone_water.R

class OnboardingNameFragment : Fragment() {

    private lateinit var etName: EditText
    private var selectedName = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_onboarding_name, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etName = view.findViewById(R.id.et_name)

        etName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                selectedName = etName.text.toString().trim()
                (activity as? OnboardingActivity)?.selectedName = selectedName
            }
        }

        // Обновляем при каждом нажатии
        etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                selectedName = s.toString().trim()
                (activity as? OnboardingActivity)?.selectedName = selectedName
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }
}
