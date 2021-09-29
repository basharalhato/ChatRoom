package dev.bhato.chatroom.ui.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import dev.bhato.chatroom.R
import dev.bhato.chatroom.utils.Constants.IMAGE_PICK_CODE
import dev.bhato.chatroom.viewmodels.ChatroomViewModel

@AndroidEntryPoint
class CreateChatroomDialog : DialogFragment() {

    private val viewModel: ChatroomViewModel by activityViewModels()

    private lateinit var dialogView: View

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = MaterialAlertDialogBuilder(requireContext()).setCancelable(false)
        dialogView = this.layoutInflater.inflate(R.layout.layout_create_chatroom, null)
        builder.setView(dialogView)

        dialogView.findViewById<ImageView>(R.id.ivChatroom).setOnClickListener {
            pickImageFromGallery()
        }

        dialogView.findViewById<TextView>(R.id.tvCancel).setOnClickListener {
            dismiss()
            viewModel.unsetChatroomImage()
        }

        dialogView.findViewById<TextView>(R.id.tvCreate).setOnClickListener {
            val chatroomName = dialogView.findViewById<EditText>(R.id.etChatroom).text.toString()
            if (chatroomName.isNotBlank()) {
                viewModel.createChatroom(chatroomName)
                dismiss()
            }
        }

        return builder.create()
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            data?.data.let { uri ->
                uri?.let {
                    setChatroomImage(it)
                    viewModel.setChatroomImage(it)
                }
            }
        }
    }

    fun setChatroomImage(uri: Uri) {
        Glide.with(requireContext()).load(uri)
            .into(dialogView.findViewById(R.id.ivChatroom))
    }
}