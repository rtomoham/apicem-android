package com.tomohamat.apicem;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.tomohamat.apicem.Model.Host;
import com.tomohamat.apicem.Model.NetworkDevice;
import com.tomohamat.apicem.Model.User;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GeneralFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GeneralFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GeneralFragment extends MyAppFragment {

    private static final String TAG = "GeneralFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Button mGetUsersButton, mGetNetworkDevicesButton, mRequestHostsButton, mRequestLegitReadsButton, mRequestTestButton;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GeneralFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GeneralFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GeneralFragment newInstance(View.OnClickListener listener, String param1, String param2) {
        GeneralFragment fragment = new GeneralFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_general, container, false);

        buttons = new ArrayList<>();

        mGetUsersButton = (Button) rootView.findViewById(R.id.requestUsersButton);
        mGetUsersButton.setOnClickListener(listener);
        buttons.add(mGetUsersButton);
//        mGetNetworkDevicesButton = (Button) rootView.findViewById(R.id.requestNetworkDevicesButton);
//        mGetNetworkDevicesButton.setOnClickListener(listener);
        mRequestHostsButton = (Button) rootView.findViewById(R.id.requestHostsButton);
        mRequestHostsButton.setOnClickListener(listener);
        buttons.add(mRequestHostsButton);
        mRequestLegitReadsButton = (Button) rootView.findViewById(R.id.requestLegitReadsButton);
        mRequestLegitReadsButton.setOnClickListener(listener);
        buttons.add(mRequestLegitReadsButton);
//        mRequestTestButton = (Button) rootView.findViewById(R.id.requestTestButton);
//        mRequestTestButton.setOnClickListener(listener);

        mResultTextView = (TextView) rootView.findViewById(R.id.generalFragmentView);

//        disableButtons();
//        showProgressDialog(true);

        return rootView;
    }

    public void showHosts(ArrayList<Host> hosts) {
        if (null != hosts) {
            String allHosts = "";
            for (Host host : hosts) {
                allHosts += host.getId() + "\n";
            }
            mResultTextView.setText(allHosts);
        }
    }

    public void showLegitReads(ArrayList<String> readCommands) {
        if (null != readCommands) {
            String allCommands = "";
            for (String command : readCommands) {
                allCommands += command + "\n";
            }
            mResultTextView.setText(allCommands);
        }
    }

    public void showNetworkDevices(ArrayList<NetworkDevice> devices) {
        if (null != devices) {
            String allDevices = "";
            for (NetworkDevice device : devices) {
                allDevices += device.getHostname() + "\n";
            }
            mResultTextView.setText(allDevices);
        }
    }

    public void showUsers(ArrayList<User> users) {
        if (null != users) {
            String allUsers = "";
            for (User user : users) {
                allUsers += user.getUsername() + "\n";
            }
            mResultTextView.setText(allUsers);
        }
    }

    public void showWaitForInitialization(final MainTabbedActivity activity, final int i) {
        if (0 == i) {
            progressDialog = ProgressDialog.show(this.getContext(), "",
                    getString(R.string.wait_initializing0), true);
        } else if (1 == i) {
            progressDialog = ProgressDialog.show(this.getContext(), "",
                    getString(R.string.wait_initializing1), true);
        } else {
            activity.startActivitySettings();
        }
        new Thread() {
            public void run() {
                try {
                    // sleep the thread, whatever time you want.
                    sleep(10 * 1000);
                } catch (Exception e) {
                    //
                }
                progressDialog.dismiss();
                showWaitForInitialization(activity, i + 1);
            }
        }.start();
    }

}
