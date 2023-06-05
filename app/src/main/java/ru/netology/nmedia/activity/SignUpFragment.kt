package ru.netology.nmedia.activity

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentSignUpBinding
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.viewmodel.AuthViewModel

class SignUpFragment : Fragment() {
    private val viewModel: AuthViewModel by activityViewModels()

    private val photoPickerContract =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when (it.resultCode) {
                ImagePicker.RESULT_ERROR -> Toast.makeText(
                    requireContext(),
                    "photo_pic_error",
                    Toast.LENGTH_SHORT
                ).show()
                Activity.RESULT_OK -> {
                    val uri = it.data?.data ?: return@registerForActivityResult
                    viewModel.setAvatar(PhotoModel(uri, uri.toFile()))
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSignUpBinding.inflate(
            inflater,
            container,
            false
        )

        binding.pickPhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .provider(ImageProvider.GALLERY)
                .galleryMimeTypes(
                    arrayOf(
                        "image/png",
                        "image/jpeg",
                    )
                )
                .createIntent(photoPickerContract::launch)
        }

        binding.takePhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .provider(ImageProvider.CAMERA)
                .createIntent(photoPickerContract::launch)
        }

        viewModel.avatar.observe(viewLifecycleOwner) {
            if (it?.uri == null) {
                binding.photoContainer.visibility = View.GONE
                return@observe
            }

            binding.photoContainer.visibility = View.VISIBLE
            binding.photo.setImageURI(it.uri)
        }

        binding.removePhoto.setOnClickListener {
            viewModel.clearAvatar()
        }

        binding.signUpButton.setOnClickListener {
            if (binding.name.text.isBlank() || binding.login.text.isBlank() || binding.password.text.isBlank() || binding.passwordConfirmation.text.isBlank()) {
                Toast.makeText(context, R.string.please_fill_in_all_fields, Toast.LENGTH_LONG)
                    .show()
            } else if (binding.password.text.toString() != binding.passwordConfirmation.text.toString()) {
                Toast.makeText(context, R.string.passwords_do_not_match, Toast.LENGTH_LONG).show()
            } else {
                when (viewModel.avatar.value) {
                    null -> viewModel.registerUser(
                        binding.login.text.toString(),
                        binding.password.text.toString(),
                        binding.name.text.toString()
                    )
                    else -> viewModel.avatar.value?.file?.let { file ->
                        viewModel.registerWithPhoto(
                            binding.login.text.toString(),
                            binding.password.text.toString(),
                            binding.name.text.toString(),
                            file
                        )
                    }
                }
            }
        }

        viewModel.data.observe(viewLifecycleOwner) {
            if (it != null) {
                Toast.makeText(
                    context,
                    R.string.you_have_successfully_registered,
                    Toast.LENGTH_LONG
                ).show()
                findNavController().navigateUp()
            }
        }

        return binding.root
    }
}