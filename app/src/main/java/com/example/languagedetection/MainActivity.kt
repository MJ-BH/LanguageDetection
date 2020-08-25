package com.example.languagedetection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.languageid.LanguageIdentifier
import java.util.*

class MainActivity : AppCompatActivity() {
    private var inputText: TextInputEditText? =null
    private var outputText : TextView? = null
    var buttonidLanguage : MaterialButton? = null

    private lateinit var languageIdentification : LanguageIdentifier

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        outputText = findViewById(R.id.outputText)

        languageIdentification = LanguageIdentification.getClient()
        // Any new instances of LanguageIdentification needs to be closed appropriately.
        // LanguageIdentification automatically calls close() on the ON_DESTROY lifecycle event,
        // so here we can add our languageIdentification instance as a LifecycleObserver for this
        // activity and have it be closed when this activity is destroyed.
        lifecycle.addObserver(languageIdentification)
        buttonidLanguage =findViewById(R.id.buttonIdLanguage)
        inputText = findViewById(R.id.inputText)
        buttonidLanguage!!.setOnClickListener {
            val input = inputText!!.text?.toString()
            input?.let {
                inputText!!.text?.clear()
                identifyLanguage(it)
            }
        }


    }

    private fun identifyLanguage(inputText: String) {
        languageIdentification.identifyLanguage(inputText)
                .addOnSuccessListener(this) {
     s->  outputText?.append(
                        String.format(
                                Locale.US,
                                "\n%s - %s",
                                inputText,
                                s
                        )
                )
                }
                .addOnFailureListener(this@MainActivity) { e ->
                    Log.e(TAG, "Language identification error", e)
                    Toast.makeText(
                            this@MainActivity, R.string.language_id_error,
                            Toast.LENGTH_SHORT
                    ).show()
                }


    }


    private fun identifyPossibleLanguages(inputText: String) {
        languageIdentification
                .identifyPossibleLanguages(inputText)
                .addOnSuccessListener(this@MainActivity) { identifiedLanguages ->
                    val detectedLanguages = ArrayList<String>(identifiedLanguages.size)
                    for (language in identifiedLanguages) {
                        detectedLanguages.add(
                                String.format(
                                        Locale.US,
                                        "%s (%3f)",
                                        language.languageTag,
                                        language.confidence
                                )
                        )
                    }
                    outputText?.append(
                            String.format(
                                    Locale.US,
                                    "\n%s - [%s]",
                                    inputText,
                                    TextUtils.join(", ", detectedLanguages)
                            )
                    )
                }
                .addOnFailureListener(this@MainActivity) { e ->
                    Log.e(TAG, "Language identification error", e)
                    Toast.makeText(
                            this@MainActivity, R.string.language_id_error,
                            Toast.LENGTH_SHORT
                    ).show()
                }
    }
    companion object {
        private const val TAG = "MainActivity"
    }
}