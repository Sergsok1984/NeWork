package ru.sokolov_diplom.nework.ui.post

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import ru.sokolov_diplom.nework.dto.Post
import ru.sokolov_diplom.nework.viewmodels.AuthViewModel
import ru.sokolov_diplom.nework.viewmodels.PostsViewModel
import ru.sokolov_diplom.nework.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.sokolov_diplom.nework.R
import ru.sokolov_diplom.nework.adapter.LoadingStateAdapter
import ru.sokolov_diplom.nework.adapter.OnPostInteractionListener
import ru.sokolov_diplom.nework.adapter.PostsAdapter
import ru.sokolov_diplom.nework.databinding.FragmentPostsBinding
import ru.sokolov_diplom.nework.ui.user.UserListFragment
import ru.sokolov_diplom.nework.ui.auth.SignOutFragment

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class PostsFragment : Fragment() {

    private val postsViewModel: PostsViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPostsBinding.inflate(inflater, container, false)

        val adapter = PostsAdapter(object : OnPostInteractionListener {

            override fun onEdit(post: Post) {
                postsViewModel.edit(post)
                findNavController().navigate(R.id.action_posts_to_newPostFragment)
            }

            override fun onRemove(post: Post) {
                postsViewModel.removeById(post.id)
            }

            override fun onLike(post: Post) {
                when (authViewModel.authorized) {
                    true -> {
                        when (post.likedByMe) {
                            true -> postsViewModel.unlikeById(post.id)
                            false -> postsViewModel.likeById(post.id)
                        }
                    }

                    false -> unauthorizedAccessAttempt()
                }
            }

            override fun onWatchVideo(post: Post) {
                if (authViewModel.authorized) {
                    try {
                        val uri = Uri.parse(post.attachment?.url)
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.setDataAndType(uri, "video/*")
                        startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, R.string.error_loading, Toast.LENGTH_SHORT).show()
                    }
                } else unauthorizedAccessAttempt()
            }

            override fun onOpenLikers(post: Post) {
                if (authViewModel.authorized) {
                    userViewModel.getUsersIds(post.likeOwnerIds)
                    if (post.likeOwnerIds.isEmpty()) {
                        Toast.makeText(context, R.string.empty_post_likers, Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        UserListFragment().show(childFragmentManager, UserListFragment.TAG)
                    }
                } else unauthorizedAccessAttempt()
            }

            override fun onOpenUserProfile(post: Post) {
                if (authViewModel.authorized) {
                    lifecycleScope.launch {
                        userViewModel.getUserById(post.authorId).join()
                        findNavController().navigate(R.id.action_posts_to_userProfileFragment)
                    }
                } else unauthorizedAccessAttempt()
            }
        })

        val itemAnimator: DefaultItemAnimator = object : DefaultItemAnimator() {
            override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean {
                return true
            }
        }

        binding.postsList.itemAnimator = itemAnimator

        binding.postsList.adapter = adapter.withLoadStateHeaderAndFooter(
            header = LoadingStateAdapter { adapter.retry() },
            footer = LoadingStateAdapter { adapter.retry() },
        )

        binding.postsList.adapter = adapter

        lifecycleScope.launch {
            postsViewModel.data.collect {
                adapter.submitData(it)
            }
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest {
                binding.swipeRefreshLayout.isRefreshing = it.refresh is LoadState.Loading
                        || it.append is LoadState.Loading
                        || it.prepend is LoadState.Loading
            }
        }

        authViewModel.state.observe(viewLifecycleOwner) {
            binding.fab.setOnClickListener {
                when (authViewModel.authorized) {
                    true -> findNavController().navigate(R.id.action_posts_to_newPostFragment)
                    false -> unauthorizedAccessAttempt()
                }
            }
        }

        binding.swipeRefreshLayout.setOnRefreshListener { adapter.refresh() }

        postsViewModel.error.observe(viewLifecycleOwner) {
            Snackbar.make(requireView(), it.message as CharSequence, Snackbar.LENGTH_LONG).show()
        }

        var menuProvider: MenuProvider? = null

        authViewModel.state.observe(viewLifecycleOwner) {
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
                        SignOutFragment().show(childFragmentManager, "logoutDialog")
                        true
                    }

                    R.id.signIn -> {
                        findNavController().navigate(R.id.action_posts_to_signInFragment)
                        true
                    }

                    R.id.signUp -> {
                        findNavController().navigate(R.id.action_posts_to_signUpFragment)
                        true
                    }

                    else -> false
                }
        }.apply {
            menuProvider = this
        }, viewLifecycleOwner)

        authViewModel.state.observe(viewLifecycleOwner) {
            requireActivity().invalidateOptionsMenu()
        }

        return binding.root
    }

    private fun unauthorizedAccessAttempt() {
        Toast.makeText(context, R.string.sign_in_to_continue, Toast.LENGTH_LONG).show()
        findNavController().navigate(R.id.action_posts_to_signInFragment)
    }
}
