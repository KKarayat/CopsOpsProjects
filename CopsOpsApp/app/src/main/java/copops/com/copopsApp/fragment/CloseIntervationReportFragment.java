package copops.com.copopsApp.fragment;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import copops.com.copopsApp.R;
import copops.com.copopsApp.pojo.CommanStatusPojo;
import copops.com.copopsApp.pojo.IncdentSetPojo;
import copops.com.copopsApp.services.ApiUtils;
import copops.com.copopsApp.services.Service;
import copops.com.copopsApp.utils.AppSession;
import copops.com.copopsApp.utils.EncryptUtils;
import copops.com.copopsApp.utils.Utils;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class CloseIntervationReportFragment extends Fragment implements View.OnClickListener, Utils.resetPassInterFace {
    String dateString;
    String address;
    String reference;
    String status;


    @BindView(R.id.Tvdate)
    TextView Tvdate;
    @BindView(R.id.Tvtime)
    TextView Tvtime;
    @BindView(R.id.Tvstate)
    TextView Tvstate;

    @BindView(R.id.TVreferencenumber)
    TextView TVreferencenumber;

    @BindView(R.id.etAddressId)
    EditText etAddressId;
    @BindView(R.id.objectId)
    EditText objectId;

    @BindView(R.id.descId)
    EditText descId;
    @BindView(R.id.Rltoolbar)
    RelativeLayout Rltoolbar;

    @BindView(R.id.Rlintervenue)
    RelativeLayout closeIntervation;

    ProgressDialog progressDialog;
    AppSession mAppSession;
    String insidentId;
    Utils.resetPassInterFace mResetPassInterFace;

    public CloseIntervationReportFragment(String dateString, String address, String reference, String status, String insidentId) {
        // Required empty public constructor

        this.dateString = dateString;
        this.address = address;
        this.reference = reference;
        this.status = status;
        this.insidentId = insidentId;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_close_intervation_report, container, false);

        ButterKnife.bind(this, view);

        mAppSession=mAppSession.getInstance(getActivity());
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("loading...");

        mResetPassInterFace = this;
        initView();

        return view;
    }

    private void initView() {

        closeIntervation.setOnClickListener(this);
        Rltoolbar.setOnClickListener(this);
        etAddressId.setText(address);

        TVreferencenumber.setText(reference);
        //dateString = dateString;
        String[] parts = dateString.split(" ");
        String date = parts[0]; // 004
        String time = parts[1]; // 034556
        Tvdate.setText(date);
        Tvtime.setText(time);
        if (status.equalsIgnoreCase("1")) {
            Tvstate.setText("Pending");
            Tvstate.setTextColor(getResources().getColor(R.color.btntextcolort));
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.Rltoolbar:
                if (getFragmentManager().getBackStackEntryCount() > 0) {
                    getFragmentManager().popBackStackImmediate();
                }
                break;


            case R.id.Rlintervenue:

                if ((descId.getText().toString().trim().equalsIgnoreCase(""))) {
                    Utils.showAlert(getActivity().getString(R.string.des), getActivity());
                } else {
                    if (Utils.checkConnection(getActivity())) {
                        IncdentSetPojo incdentSetPojo = new IncdentSetPojo();
                        incdentSetPojo.setUser_id(mAppSession.getData("id"));
                        incdentSetPojo.setComment(descId.getText().toString().trim());
                        incdentSetPojo.setIncident_id(insidentId);
                        incdentSetPojo.setDevice_id(Utils.getDeviceId(getActivity()));
                        Log.e("@@@@", EncryptUtils.encrypt(Utils.key, Utils.iv, new Gson().toJson(incdentSetPojo)));
                        RequestBody mFile = RequestBody.create(MediaType.parse("text/plain"), EncryptUtils.encrypt(Utils.key, Utils.iv, new Gson().toJson(incdentSetPojo)));
                        getCopsCloseStatus(mFile);
                    } else {
                        Utils.showAlert(getActivity().getString(R.string.internet_conection), getActivity());
                    }
                }

                break;
        }

    }


    private void getCopsCloseStatus(RequestBody Data) {
        progressDialog.show();
        Service operator = ApiUtils.getAPIService();
        Call<CommanStatusPojo> getallLatLong = operator.close(Data);
        getallLatLong.enqueue(new Callback<CommanStatusPojo>() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onResponse(Call<CommanStatusPojo> call, Response<CommanStatusPojo> response)

            {
                try {
                    if (response.body() != null) {
                        CommanStatusPojo commanStatusPojo = response.body();
                        if (commanStatusPojo.getStatus().equals("false")) {
                            Utils.showAlert(commanStatusPojo.getMessage(), getActivity());

                        } else {

                            Utils.showAlertAndClick(commanStatusPojo.getMessage(), getContext(), mResetPassInterFace);

                        }
                        progressDialog.dismiss();

                    } else {
                        Utils.showAlert(response.message(), getActivity());
                    }

                } catch (Exception e) {
                    progressDialog.dismiss();
                    e.getMessage();
                    Utils.showAlert(e.getMessage(), getActivity());
                }
            }

            @Override
            public void onFailure(Call<CommanStatusPojo> call, Throwable t) {
                Log.d("TAG", "Error " + t.getMessage());
                progressDialog.dismiss();
                Utils.showAlert(t.getMessage(), getActivity());
            }
        });
    }

    @Override
    public void onClick(int id) {
        Utils.fragmentCall(new OperatorFragment(), getFragmentManager());
    }
}