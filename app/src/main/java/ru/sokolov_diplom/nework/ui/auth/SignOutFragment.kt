package ru.sokolov_diplom.nework.ui.auth

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import ru.sokolov_diplom.nework.authorization.AppAuth
import dagger.hilt.android.AndroidEntryPoint
import ru.sokolov_diplom.nework.R
import javax.inject.Inject

@AndroidEntryPoint
class SignOutFragment : DialogFragment() {

    @Inject
    lateinit var appAuth: AppAuth

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(R.string.are_you_sure)
                .setCancelable(true)
                .setPositiveButton(R.string.logout) { _, _ ->
                    appAuth.removeAuth()
                }
                .setNegativeButton(R.string.cancel) { _, _ ->
                    dismiss()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
