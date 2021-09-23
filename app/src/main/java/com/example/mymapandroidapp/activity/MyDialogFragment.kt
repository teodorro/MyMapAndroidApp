package com.example.mymapandroidapp.activity

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.mymapandroidapp.R

class MyDialogFragment(
    val items: Array<String>,
    val onClickListener: DialogInterface.OnClickListener
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder
                .setTitle(R.string.all_points)
                .setItems(
                    items,
                    onClickListener
                )
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}