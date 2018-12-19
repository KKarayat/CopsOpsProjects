package copops.com.copopsApp.fragment;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import copops.com.copopsApp.R;
import copops.com.copopsApp.activity.SplashScreen;
import copops.com.copopsApp.adapter.IncidentTypeAdapter;
import copops.com.copopsApp.interfaceview.IncedentInterface;
import copops.com.copopsApp.pojo.IncidentTypePojo;
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
public class IncidentFragment extends Fragment implements View.OnClickListener,IncedentInterface {

    @BindView(R.id.mRecyclerview)
    RecyclerView mRecyclerview;

    @BindView(R.id.Rltoolbar)
    RelativeLayout Rltoolbar;

    Context mContext;
    ProgressDialog progressDialog;

    IncedentInterface mIncedentInterface;
    IncidentTypePojo incidentTypeResponse;
    String userId;
    AppSession mAppSession;
    public IncidentFragment(String userId) {
   this.userId=userId;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.frag_report_an_incidents, container, false);
        ButterKnife.bind(this, view);
        mIncedentInterface=this;
        mAppSession= mAppSession.getInstance(getActivity());
        onClick();

        mContext = getActivity();

        getIncidentType();
        return view;
    }

    private void onClick() {
        Rltoolbar.setOnClickListener(this);
    }

    public void getIncidentType() {
        try {


            progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage(getActivity().getString(R.string.loading_msg));
            progressDialog.show();

            Service incidentType = ApiUtils.getAPIService();

            RequestBody mFile = RequestBody.create(MediaType.parse("text/plain"), EncryptUtils.encrypt(Utils.key, Utils.iv, new Gson().toJson(Utils.getDeviceId(mContext))));
            Call<IncidentTypePojo> fileUpload = incidentType.incidentType(mFile);
            fileUpload.enqueue(new Callback<IncidentTypePojo>() {
                @Override
                public void onResponse(Call<IncidentTypePojo> call, Response<IncidentTypePojo> response)

                {
                    try {
                        if (response.body() != null) {
                            incidentTypeResponse = response.body();
                            IncidentTypeAdapter mAdapter = new IncidentTypeAdapter(mContext, incidentTypeResponse.getData(), mIncedentInterface);
                            mRecyclerview.setLayoutManager(new LinearLayoutManager(mContext));
                            mRecyclerview.setItemAnimator(new DefaultItemAnimator());
                            mRecyclerview.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                            progressDialog.dismiss();

                        }

                    } catch (Exception e) {
                        progressDialog.dismiss();
                        e.getMessage();
                        Utils.showAlert(e.getMessage(), mContext);
                    }
                }

                @Override
                public void onFailure(Call<IncidentTypePojo> call, Throwable t) {
                    Log.d("TAG", "Error " + t.getMessage());
                    progressDialog.dismiss();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            e.getMessage();
            Utils.showAlert(e.getMessage(), mContext);
        }
        }


    /////All click listners
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.Rltoolbar:
                if (getFragmentManager().getBackStackEntryCount() > 0) {
                    getFragmentManager().popBackStackImmediate();
                }
                break;

        }
    }

    @Override
    public void clickPosition(int pos) {

        if(pos==0){
            Utils.fragmentCall(new IncidentTypeFragment(incidentTypeResponse,pos,userId), getFragmentManager());
            mAppSession.saveData("city","police");
        }else if(pos==1){
            Utils.fragmentCall(new IncidentTypeFragment(incidentTypeResponse,pos,userId), getFragmentManager());
            mAppSession.saveData("city","medical");
        }else{
            mAppSession.saveData("city","city");

            Utils.fragmentCall(new IncidentTypeFragment(incidentTypeResponse,pos,userId), getFragmentManager());
        }


    }

}