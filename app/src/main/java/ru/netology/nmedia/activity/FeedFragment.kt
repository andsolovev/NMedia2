package ru.netology.nmedia.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostLoadingStateAdapter
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.PostViewModel
import javax.inject.Inject

@AndroidEntryPoint
class FeedFragment : Fragment() {

    @Inject
    lateinit var appAuth: AppAuth

    private val viewModel: PostViewModel by viewModels()
    val authViewModel: AuthViewModel by viewModels()

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(inflater, container, false)

        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onEdit(post: Post) {
                viewModel.edit(post)
                findNavController().navigate(R.id.action_feedFragment_to_newPostFragment,
                    Bundle().apply {
                        textArg = post.content
                    }
                )
            }

            override fun onLike(post: Post) {
                when (authViewModel.authorized) {
                    true -> {
                        if (post.likedByMe) {
                            viewModel.unlikeById(post.id)
                        } else {
                            viewModel.likeById(post.id)
                        }
                    }
                    false -> signInSnack()
                }
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }

                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
            }

            override fun onAttachment(post: Post) {
                findNavController().navigate(R.id.action_feedFragment_to_attachmentFragment,
                    Bundle().apply {
                        textArg = post.attachment?.url
                    }
                )
            }

        })
        binding.list.adapter = adapter.withLoadStateHeaderAndFooter(
            header = PostLoadingStateAdapter {adapter.retry()},
            footer = PostLoadingStateAdapter {adapter.retry()}
        )

//        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
//            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
//                if (positionStart == 0) {
//                    binding.list.smoothScrollToPosition(0)
//                }
//            }
//        })

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            binding.swipetorefresh.isRefreshing = state.refreshing
        }

        lifecycleScope.launchWhenCreated {
            viewModel.data.collectLatest {
                adapter.submitData(it)
            }
        }

        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest {
                it.refresh is LoadState.Loading
//                        || it.append is LoadState.Loading
//                        || it.prepend is LoadState.Loading
            }
        }

        authViewModel.data.observe(viewLifecycleOwner) {
            adapter.refresh()
        }

        binding.swipetorefresh.setOnRefreshListener {
            adapter.refresh()
            binding.swipetorefresh.isRefreshing = false
        }

        authViewModel.data.observe(viewLifecycleOwner) {
            binding.fab.setOnClickListener {
                when (authViewModel.authorized) {
                    true -> findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
                    false -> signInSnack()
                }
            }
        }

        var menuProvider: MenuProvider? = null

        authViewModel.data.observe(viewLifecycleOwner) {
            menuProvider?.let { requireActivity()::removeMenuProvider }
        }
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_auth, menu)
                menu.setGroupVisible(R.id.authorized, authViewModel.authorized)
                menu.setGroupVisible(R.id.unauthorized, !authViewModel.authorized)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                when (menuItem.itemId) {
                    R.id.logout -> {
                        appAuth.clearAuth()
                        true
                    }

                    R.id.signIn -> {
                        findNavController().navigate(R.id.action_feedFragment_to_signInFragment)
                        true
                    }

                    R.id.signUp -> {
                        findNavController().navigate(R.id.action_feedFragment_to_signUpFragment)
                        true
                    }
                    else -> false
                }
        }.apply {
            menuProvider = this
        }, viewLifecycleOwner)

        authViewModel.data.observe(viewLifecycleOwner) {
            requireActivity().invalidateOptionsMenu()
        }


//        val itemDecoration = CustomItemDecoration(binding.list.context.getDrawable(R.drawable.divider)!!)
//        binding.list.addItemDecoration(itemDecoration)

        return binding.root
    }

    private fun signInSnack() {
        Snackbar.make(requireView(), R.string.sign_in_to_continue, 7000)
            .setAction(
                R.string.sign_in
            ) { findNavController().navigate(R.id.action_feedFragment_to_signInFragment) }
            .show()
    }
}



