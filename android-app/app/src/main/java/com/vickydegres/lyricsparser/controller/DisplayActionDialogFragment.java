package com.vickydegres.lyricsparser.controller;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;

import com.vickydegres.lyricsparser.R;

import java.util.Objects;

public class DisplayActionDialogFragment extends DialogFragment {
    DisplayActionDialogListener listener;
    Button mCopy, mEdit, mDelete;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater.
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog.
        // Pass null as the parent view because it's going in the dialog layout.
        View view = inflater.inflate(R.layout.display_action_dialog, null);

        builder.setView(view);

        mCopy = view.findViewById(R.id.display_action_copy_button);
        mEdit = view.findViewById(R.id.display_action_edit_button);
        mDelete = view.findViewById(R.id.display_action_delete_button);

        mCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onCopyClick(DisplayActionDialogFragment.this);
                dismiss();
            }
        });

        mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onEditClick(DisplayActionDialogFragment.this);
                dismiss();
            }
        });

        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onDeleteClick(DisplayActionDialogFragment.this);
            }
        });
                // Add action button
        return builder.create();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface.
        try {
            // Instantiate the NoticeDialogListener so you can send events to
            // the host.
            listener = (DisplayActionDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface. Throw exception.
            throw new ClassCastException(getActivity().toString()
                    + " must implement NoticeDialogListener");
        }

    }

    public interface DisplayActionDialogListener {
        public void onCopyClick(DialogFragment dialog);
        public void onEditClick(DialogFragment dialog);
        public void onDeleteClick(DialogFragment dialog);
    }
}
