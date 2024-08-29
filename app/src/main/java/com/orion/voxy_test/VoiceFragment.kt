import android.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.orion.voxy_test.TypingTextView
import com.orion.voxy_test.data.api.RetrofitBuilder
import com.orion.voxy_test.data.api.VoxyApiHelper
import com.orion.voxy_test.data.utils.ApiResponse
import com.orion.voxy_test.data.utils.ViewModelFactory
import com.orion.voxy_test.databinding.FragmentVoiceBinding
import com.orion.voxy_test.ui.MainViewModel
import net.gotev.speech.GoogleVoiceTypingDisabledException
import net.gotev.speech.Speech
import net.gotev.speech.SpeechDelegate
import net.gotev.speech.SpeechRecognitionNotAvailable


/**
 * A simple [Fragment] subclass.
 * Use the [VoiceFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class VoiceFragment : BottomSheetDialogFragment() {

    private lateinit var viewModel: MainViewModel
    private var _binding: FragmentVoiceBinding? = null
    private val binding get() = _binding!!
    private lateinit var typingTextView: TypingTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Speech.init(requireActivity(), requireActivity().packageName)
        setupViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVoiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setBackgroundResource(com.orion.voxy_test.R.drawable.bottom_sheet_background)
        typingTextView = binding.recognisedText
        recognizeSpeech()
        fetchVoxyResponse()
        binding.micAnim.setOnClickListener {
            recognizeSpeech()
            binding.micAnim.playAnimation()
            binding.wave.playAnimation()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Speech.getInstance().shutdown()
        _binding = null
    }

    private fun recognizeSpeech() {
        binding.micAnim.isClickable = false
        try {
            // you must have android.permission.RECORD_AUDIO granted at this point
            Speech.getInstance().startListening(object : SpeechDelegate {
                override fun onStartOfSpeech() {
                    typingTextView.resetText()
                    Log.i("speech", "speech recognition is now active")
                }

                override fun onSpeechRmsChanged(value: Float) {
                    Log.d("speech", "rms is now: $value")
                }

                override fun onSpeechPartialResults(results: List<String>) {
                    val str = StringBuilder()
                    for (res in results) {
                        str.append(res).append(" ")
                    }
                    typingTextView.appendPartialText(str.toString())

                    Log.i("speech", "partial result: " + str.toString().trim { it <= ' ' })
                }

                override fun onSpeechResult(result: String) {
                    Log.i("speech", "result: $result")
                    typingTextView.finalizeText(result)
                    /* translateText(result)*/
                    viewModel.getVoxyRes(result)
                    binding.micAnim.pauseAnimation()
                    binding.wave.pauseAnimation()
                    binding.micAnim.isClickable = true
                    findResult()
                }
            })
        } catch (exc: SpeechRecognitionNotAvailable) {
            Log.e("speech", "Speech recognition is not available on this device!")
            // You can prompt the user if he wants to install Google App to have
            // speech recognition, and then you can simply call:
            //
            // SpeechUtil.redirectUserToGoogleAppOnPlayStore(this);
            //
            // to redirect the user to the Google App page on Play Store
        } catch (exc: GoogleVoiceTypingDisabledException) {
            Log.e("speech", "Google voice typing must be enabled!")
        }
    }

    override fun dismiss() {
        super.dismiss()
        _binding = null
    }

    private fun findResult() {
        binding.wave.visibility = View.GONE
        binding.micAnim.visibility = View.INVISIBLE
        binding.shimmerLayout1.visibility = View.VISIBLE
        binding.shimmerLayout2.visibility = View.VISIBLE
        binding.shimmerLayout3.visibility = View.VISIBLE
        binding.shimmerLayout1.startShimmer()
        binding.shimmerLayout2.startShimmer()
        binding.shimmerLayout3.startShimmer()
    }

    private fun fetchVoxyResponse() {
        viewModel.data.observeForever {
            when (it) {
                is ApiResponse.Loading -> {}
                is ApiResponse.Success -> {
                    Log.d("speech", "Response in Fragment -> " + it.data.toString())
                    binding.shimmerLayout1.stopShimmer()
                    binding.shimmerLayout2.stopShimmer()
                    binding.shimmerLayout3.stopShimmer()
                    binding.shimmerLayout1.visibility = View.GONE
                    binding.shimmerLayout2.visibility = View.GONE
                    binding.shimmerLayout3.visibility = View.GONE
                    binding.finalText1.visibility = View.VISIBLE
                    binding.finalText1.text = "{\n${it.data?.screenName}\n${it.data?.arguments}\n}"
                }

                is ApiResponse.Error -> {
                    Toast.makeText(requireActivity(), it.exception.toString(), Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    /* private fun translateText(text: String) {
         val languageIdentifier: LanguageIdentifier = LanguageIdentification.getClient()
         languageIdentifier.identifyLanguage(text)
             .addOnSuccessListener { languageCode ->
                 Log.d("sppech","Language -> "+languageCode.substring(0,2))
                 if ("und" != languageCode) {
                     if (languageCode != "en") {
                         translateTextFromLanguage(text, languageCode.substring(0,2))
                     }
                 } else {
                     Toast.makeText(
                         requireActivity(),
                         "Could not identify language",
                         Toast.LENGTH_SHORT
                     )
                         .show()
                 }
             }
             .addOnFailureListener { e ->
                 Toast.makeText(
                     requireActivity(),
                     "Language identification failed",
                     Toast.LENGTH_SHORT
                 ).show()
             }
     }*/

    /*private fun translateTextFromLanguage(text: String, sourceLanguageCode: String) {
        val sourceLanguage = TranslateLanguage.fromLanguageTag(sourceLanguageCode)
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLanguage!!)
            .setTargetLanguage(TranslateLanguage.ENGLISH)
            .build()
        val translator: Translator = Translation.getClient(options)
        translator.downloadModelIfNeeded(DownloadConditions.Builder().build())
            .addOnSuccessListener { unused ->
                translator.translate(text)
                    .addOnSuccessListener { translatedText ->
                        *//*val translatedTextView: TextView =
                            findViewById(R.id.translated_text_view)*//*
                        //binding.recognisedText.text = translatedText
                        Log.d("speech", "Translated Text -> " + translatedText.toString())
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            requireActivity(),
                            "Translation failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    requireActivity(),
                    "Model download failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }*/

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(VoxyApiHelper(RetrofitBuilder.voxyAPI))
        )[MainViewModel::class.java]
    }

}