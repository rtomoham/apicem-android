package com.tomohamat.apicem;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Robert on 3/15/2017.
 */

public class ApicEmFragment extends Fragment {

    private static final String TAG = "ApicEmFragment";

    protected ArrayList<Button> buttons;
    protected View.OnClickListener listener;

    protected TextView mResultTextView;
    protected ProgressDialog progressDialog;
    protected OnFragmentInteractionListener mListener;

    public void disableButtons() {
        setButtonsStatus(false);
    }

    public void enableButtons() {
        setButtonsStatus(true);
    }

    protected void setButtonsStatus(boolean enabled) {
        for (Button button : buttons) {
            button.setEnabled(enabled);
        }
    }

    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    public void showPleaseWait() {
        mResultTextView.setText(getString(R.string.please_wait));
    }

    public void showProgressDialog(boolean visible) {
        if (visible) {
            progressDialog = ProgressDialog.show(this.getContext(), "",
                    getString(R.string.please_wait), true);
            new Thread() {
                public void run() {
                    while (true) {
                        try {
                            sleep(500);
                        } catch (Exception e) {
                        }
                    }
                }
            }.start();
        } else {
            if (null != progressDialog) {
                progressDialog.dismiss();
            }
        }
    }

    public void showResult(String result) {
        mResultTextView.setText(result);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    /*
            if (context instanceof OnFragmentInteractionListener) {
                mListener = (OnFragmentInteractionListener) context;
            } else {
                throw new RuntimeException(context.toString()
                        + " must implement OnFragmentInteractionListener");
            }
    */
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
