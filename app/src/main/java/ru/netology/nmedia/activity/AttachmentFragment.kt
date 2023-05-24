package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.databinding.FragmentAttachmentBinding
import ru.netology.nmedia.util.StringArg

class AttachmentFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAttachmentBinding.inflate(
            inflater,
            container,
            false
        )

        Glide.with(binding.attachmentShow)
            .load("${BuildConfig.BASE_URL}/media/${arguments?.textArg}")
            .timeout(6_000)
            .into(binding.attachmentShow)

        return binding.root
    }
}


















