import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.orion.voxy_test.R
import com.orion.voxy_test.TypingTextView
import com.orion.voxy_test.databinding.FragmentVoiceBinding
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

    private var _binding: FragmentVoiceBinding? = null
    private val binding get() = _binding!!
    private lateinit var typingTextView: TypingTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Speech.init(requireActivity(), requireActivity().packageName)
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
        view.setBackgroundResource(R.drawable.bottom_sheet_background)
        typingTextView = binding.recognisedText
        recognizeSpeech()
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
                    binding.micAnim.pauseAnimation()
                    binding.wave.pauseAnimation()
                    binding.micAnim.isClickable = true
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
}