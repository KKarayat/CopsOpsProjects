package copops.com.copopsApp.fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import copops.com.copopsApp.R;
import copops.com.copopsApp.adapter.CitylistAdapter;
import copops.com.copopsApp.interfaceview.IncedentInterface;
import copops.com.copopsApp.pojo.AllLocationAndCityPojo;
import copops.com.copopsApp.pojo.CityWsieMapShowPojo;
import copops.com.copopsApp.pojo.IncdentSetPojo;
import copops.com.copopsApp.services.ApiUtils;
import copops.com.copopsApp.services.Service;
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
public class PositionOfInteervebtionsFragment extends Fragment implements OnMapReadyCallback, IncedentInterface, View.OnClickListener {
    private GoogleMap map;


    @BindView(R.id.EtcitySearchId)
    EditText EtcitySearchId;
    @BindView(R.id.Rltoolbar)
    RelativeLayout Rltoolbar;

    @BindView(R.id.RLsearch)
    RelativeLayout RLsearch;

    @BindView(R.id.cityList)
    RecyclerView cityList;

    @BindView(R.id.IVimagesgps)
    ImageView IVimagesgps;

    CitylistAdapter mAdapter;
    ArrayList<Double> lat = new ArrayList<>();
    ArrayList<Double> lang = new ArrayList<>();
    ArrayList<String> cityName = new ArrayList<>();
    ArrayList<String> cityId = new ArrayList<>();
    IncedentInterface mIncedentInterface;
    ArrayList<String> filterdNames;
    ProgressDialog progressDialog;
    AllLocationAndCityPojo allLocationAndCityPojo;
    CityWsieMapShowPojo mCityWsieMapShowPojo1;
    LocationManager mLocationManager;
    private boolean isNetworkEnabled;
    private boolean isGpsEnabled;
    double longitude;
    double latitude;
    View mapView;
    ArrayList<String> latlong = new ArrayList<>();
    int p = 0;
    SupportMapFragment mapFragment;
    ArrayList<String> filtercityId = new ArrayList<>();
    BitmapDescriptor icon;



    public PositionOfInteervebtionsFragment(Double latitude, Double longitude ) {

        this.latitude=latitude;
        this.longitude=longitude;

        // Required empty public constructor
    }

    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_position_of_inteervebtions, container, false);
        icon= BitmapDescriptorFactory.fromResource(R.mipmap.img_map_marker_positionofincidents);
        ButterKnife.bind(this, view);
        RLsearch.setOnClickListener(this);
        Rltoolbar.setOnClickListener(this);
        IVimagesgps.setOnClickListener(this);
       // LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);




//        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
//        if (checkPermission() && gpsEnabled()) {
//            if (isNetworkEnabled) {
//                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,
//                        10, mLocationListener);
//            } else {
//                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
//                        10, mLocationListener);
//            }
//        }
//        if (lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null) {
//            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            longitude = location.getLongitude();
//            latitude = location.getLatitude();
//        } else {
//            Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//            longitude = location.getLongitude();
//            latitude = location.getLatitude();
//        }
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("loading...");
        mIncedentInterface = this;

        mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mapView = mapFragment.getView();
        EtcitySearchId.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //   cityList.setVisibility(View.GONE);
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

                try {

                    if (EtcitySearchId.getText().toString().equalsIgnoreCase("")) {
                        cityList.setVisibility(View.GONE);
                        //Utils.showAlert(getActivity().getString(R.string.no_find), getActivity());
                    } else {
                        //  if(cityName.contains(EtcitySearchId.getText().toString())){

                        cityList.setVisibility(View.VISIBLE);
                        filter(s.toString());
                        Editable etext = EtcitySearchId.getText();
                        int position = etext.length();
                        Selection.setSelection(etext, position);


                        // filter(s.toString());
                        // Utils.showAlert(getActivity().getString(R.string.no_find), getActivity());
//                       }else{
//                           cityList.setVisibility(View.GONE);
//                       }


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });
        return view;
    }

    private void initSetCityList() {

        IncdentSetPojo incdentSetPojo = new IncdentSetPojo();
        incdentSetPojo.setIncident_lat(String.valueOf(latitude));
        incdentSetPojo.setIncident_lng(String.valueOf(longitude));

        Log.e("@@@@", EncryptUtils.encrypt(Utils.key, Utils.iv, new Gson().toJson(incdentSetPojo)));
        RequestBody mFile = RequestBody.create(MediaType.parse("text/plain"), EncryptUtils.encrypt(Utils.key, Utils.iv, new Gson().toJson(incdentSetPojo)));

        try {
            if (Utils.checkConnection(getActivity()))
                getMapList(mFile);
            else
                Utils.showAlert(getActivity().getString(R.string.internet_conection), getActivity());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;


        LatLng coordinate = new LatLng(latitude, longitude);
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 11);
        map.moveCamera(CameraUpdateFactory.newLatLng(coordinate));
        map.animateCamera(yourLocation);
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));

        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 30, 100);

        map.getUiSettings().setMapToolbarEnabled(false);
        //  map.getUiSettings().setZoomControlsEnabled( true );

        initSetCityList();

//        MarkerOptions mp = new MarkerOptions();
//
//        mp.position(new LatLng(latitude,longitude)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//
//      //  mp.title("my position");
//
//        googleMap.addMarker(mp);
        //  googleMap.moveCamera(center);
        //   googleMap.animateCamera(zoom);

    }

    @SuppressLint("MissingPermission")
    void getCurrentLocation() {
        latlong.clear();
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        for (int i = 0; i < allLocationAndCityPojo.getData().size(); i++) {
            map.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(allLocationAndCityPojo.getData().get(i).getLatitude()), Double.parseDouble(allLocationAndCityPojo.getData().get(i).getLongitude())))
                    .title(allLocationAndCityPojo.getData().get(i).getAddress() + "\n" + allLocationAndCityPojo.getData().get(i).getSub_category_name()).snippet(allLocationAndCityPojo.getData().get(i).getCreated_at()).icon(icon));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(allLocationAndCityPojo.getData().get(i).getLatitude()), Double.parseDouble(allLocationAndCityPojo.getData().get(i).getLongitude())), 10));
            //  p=i;

            latlong.add(allLocationAndCityPojo.getData().get(i).getLatitude() + "," + allLocationAndCityPojo.getData().get(i).getLongitude());

            //  address=allLocationAndCityPojo.getData().get(i).getAddress();
        }


        View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 30, 100);

        map.getUiSettings().setMapToolbarEnabled(false);

        map.setMyLocationEnabled(true);


//        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
//
//            @Override
//            public void onInfoWindowClick(Marker arg0) {
//                // TODO Auto-generated method stub
//         arg0.showInfoWindow();
//
//            }
//        });


        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
//marker.getId();


                return false;
            }
        });

        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker marker) {

                LatLng latLng = marker.getPosition();
                View v = null;
                String compare = latLng.latitude + "," + latLng.longitude;

                for (int i = 0; i < latlong.size(); i++) {

                    if (latlong.get(i).equalsIgnoreCase(compare)) {

                        // Getting view from the layout file info_window_layout
                        v = getLayoutInflater().inflate(R.layout.windowlayout, null);

                        TextView tvLat = (TextView) v.findViewById(R.id.tv_lat);
                        TextView tv_lng = (TextView) v.findViewById(R.id.tv_lng);
                        TextView dateId = (TextView) v.findViewById(R.id.dateId);
                        tvLat.setText(allLocationAndCityPojo.getData().get(i).getAddress());
                        tv_lng.setText(allLocationAndCityPojo.getData().get(i).getSub_category_name());
                        dateId.setText(allLocationAndCityPojo.getData().get(i).getCreated_at());
                        //  tvLat.setTextColor(Color.GREEN);
                    }
                }
                return v;
            }

        });
    }

    @SuppressLint("MissingPermission")
    void getCurrentLocationCity() {
        latlong.clear();
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //   latlong.add(allLocationAndCityPojo.getData().get(i).getLatitude()+","+allLocationAndCityPojo.getData().get(i).getLongitude());

        for (int i = 0; i < mCityWsieMapShowPojo1.getData().size(); i++) {
            map.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(mCityWsieMapShowPojo1.getData().get(i).getLatitude()), Double.parseDouble(mCityWsieMapShowPojo1.getData().get(i).getLongitude())))
                    .title(mCityWsieMapShowPojo1.getData().get(i).getAddress() + "       " + mCityWsieMapShowPojo1.getData().get(i).getCreated_at()).icon(
                            icon));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(mCityWsieMapShowPojo1.getData().get(i).getLatitude()), Double.parseDouble(mCityWsieMapShowPojo1.getData().get(i).getLongitude())), 10));

            latlong.add(mCityWsieMapShowPojo1.getData().get(i).getLatitude() + "," + mCityWsieMapShowPojo1.getData().get(i).getLongitude());

        }
        map.setMyLocationEnabled(true);

        View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 30, 100);

        map.getUiSettings().setMapToolbarEnabled(false);

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
//marker.getId();


                return false;
            }
        });

        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker marker) {

                LatLng latLng = marker.getPosition();
                View v = null;
                String compare = latLng.latitude + "," + latLng.longitude;

                for (int i = 0; i < latlong.size(); i++) {

                    if (latlong.get(i).equalsIgnoreCase(compare)) {

                        // Getting view from the layout file info_window_layout
                        v = getLayoutInflater().inflate(R.layout.windowlayout, null);

                        TextView tvLat = (TextView) v.findViewById(R.id.tv_lat);
                        TextView tv_lng = (TextView) v.findViewById(R.id.tv_lng);
                        TextView dateId = (TextView) v.findViewById(R.id.dateId);
                        tvLat.setText(mCityWsieMapShowPojo1.getData().get(i).getAddress());
                        tv_lng.setText(mCityWsieMapShowPojo1.getData().get(i).getSub_category_name());
                        dateId.setText(mCityWsieMapShowPojo1.getData().get(i).getCreated_at());
                    }
                }
                return v;
            }

        });
    }

    private void filter(String text) {
        //new array list that will hold the filtered data
        filterdNames = new ArrayList<>();


        //looping through existing elements
        for (String s : cityName) {
            //if the existing elements contains the search input
            if (s.toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                filterdNames.add(s);
            }

        }

        if(filterdNames.size()>0){
            mAdapter.filterList(filterdNames);
        }else{
            cityList.setVisibility(View.GONE);
        }
        //calling a method of the adapter class and passing the filtered list

    }

    @Override
    public void clickPosition(int pos) {
        filtercityId.clear();
        EtcitySearchId.setText(filterdNames.get(pos));
        for (int i = 0; i < cityName.size(); i++) {
            for (int j = 0; j < filterdNames.size(); j++) {
                if (cityName.get(i).equalsIgnoreCase(filterdNames.get(j))) {
                    filtercityId.add(cityId.get(i));
                }
            }
        }
        Utils.hideKeyboard(getActivity());
        cityList.setVisibility(View.GONE);
    }


    private void getMapList(RequestBody Data) {

        try {
            //   progressDialog.show();
            Service login = ApiUtils.getAPIService();
            Call<AllLocationAndCityPojo> getallLatLong = login.getMapList(Data);
            getallLatLong.enqueue(new Callback<AllLocationAndCityPojo>() {
                @Override
                public void onResponse(Call<AllLocationAndCityPojo> call, Response<AllLocationAndCityPojo> response)

                {
                    try {
                        if (response.body() != null) {
                            allLocationAndCityPojo = response.body();
                            if (allLocationAndCityPojo.getStatus().equals("false")) {
                                //  Utils.showAlert(registrationResponse.getMessage(), getActivity());

                            } else {
                                getCurrentLocation();
                                for (int i = 0; i < allLocationAndCityPojo.getCities().size(); i++) {
                                    cityName.add(allLocationAndCityPojo.getCities().get(i).getCity_name());
                                    cityId.add(allLocationAndCityPojo.getCities().get(i).getId());
                                }
                                mAdapter = new CitylistAdapter(getActivity(), cityName, mIncedentInterface);
                                cityList.setHasFixedSize(true);
                                cityList.setLayoutManager(new LinearLayoutManager(getActivity()));
                                cityList.setItemAnimator(new DefaultItemAnimator());
                                cityList.setAdapter(mAdapter);
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
                public void onFailure(Call<AllLocationAndCityPojo> call, Throwable t) {
                    Log.d("TAG", "Error " + t.getMessage());
                    progressDialog.dismiss();
                    Utils.showAlert(t.getMessage(), getActivity());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.RLsearch:

                if (EtcitySearchId.getText().toString().equalsIgnoreCase("")) {
                    Utils.showAlert(getActivity().getString(R.string.cityName), getActivity());
                    cityList.setVisibility(View.GONE);
                } else if (!cityName.contains(EtcitySearchId.getText().toString())) {
                    Utils.showAlert(getActivity().getString(R.string.no_find), getActivity());
                    cityList.setVisibility(View.GONE);
                } else {
                    map.clear();
                    IncdentSetPojo incdentSetPojo = new IncdentSetPojo();
                    incdentSetPojo.setCity_id(filtercityId.get(0));
                    Log.e("@@@@", EncryptUtils.encrypt(Utils.key, Utils.iv, new Gson().toJson(incdentSetPojo)));
                    RequestBody mFile = RequestBody.create(MediaType.parse("text/plain"), EncryptUtils.encrypt(Utils.key, Utils.iv, new Gson().toJson(incdentSetPojo)));
                    if (Utils.checkConnection(getActivity()))
                        getCityWiseMap(mFile);
                    else
                        Utils.showAlert(getActivity().getString(R.string.internet_conection), getActivity());

                }
                break;

            case R.id.Rltoolbar:
                if (getFragmentManager().getBackStackEntryCount() > 0) {
                    getFragmentManager().popBackStackImmediate();
                }
                break;


            case R.id.IVimagesgps:
                if(map.getMyLocation() != null) { // Check to ensure coordinates aren't null, probably a better way of doing this...
                    LatLng coordinate = new LatLng(latitude, longitude);
                    CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 11);
                    map.moveCamera(CameraUpdateFactory.newLatLng(coordinate));
                    map.animateCamera(yourLocation);
                }
                break;
        }
    }

    public void getCityWiseMap(RequestBody Data) {

        try {


            progressDialog.show();
            Service cityWsieMapShowPojo = ApiUtils.getAPIService();
            Call<CityWsieMapShowPojo> getallLatLong = cityWsieMapShowPojo.getMapListCity(Data);
            getallLatLong.enqueue(new Callback<CityWsieMapShowPojo>() {
                @Override
                public void onResponse(Call<CityWsieMapShowPojo> call, Response<CityWsieMapShowPojo> response)

                {
                    try {
                        if (response.body() != null) {
                            mCityWsieMapShowPojo1 = response.body();
                            if (mCityWsieMapShowPojo1.getStatus().equals("false")) {
                                //  Utils.showAlert(registrationResponse.getMessage(), getActivity());

                            } else {
                                getCurrentLocationCity();
                            }
                            progressDialog.dismiss();

                        } else {
                            progressDialog.dismiss();
                            Utils.showAlert(response.message(), getActivity());
                        }

                    } catch (Exception e) {
                        progressDialog.dismiss();
                        e.getMessage();
                        Utils.showAlert(e.getMessage(), getActivity());
                    }
                }

                @Override
                public void onFailure(Call<CityWsieMapShowPojo> call, Throwable t) {
                    Log.d("TAG", "Error " + t.getMessage());
                    progressDialog.dismiss();
                    Utils.showAlert(t.getMessage(), getActivity());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


//    private final android.location.LocationListener mLocationListener = new android.location.LocationListener() {
//
//        @SuppressLint("MissingPermission")
//        @Override
//        public void onLocationChanged(final Location location) {
//            if (location != null) {
//                // mCurrentLocation = location;
//                latitude = location.getLatitude();
//                longitude = location.getLongitude();
//
//                LatLng coordinate = new LatLng(latitude, longitude);
//                CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 11);
//                map.moveCamera(CameraUpdateFactory.newLatLng(coordinate));
//                map.animateCamera(yourLocation);
//                map.setMyLocationEnabled(true);
//
//                View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
//                RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
//                // position on right bottom
//                rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
//                rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
//                rlp.setMargins(0, 0, 30, 100);
//
//                map.getUiSettings().setMapToolbarEnabled(false);
//              //  map.getUiSettings().setZoomControlsEnabled( true );
//
//                initSetCityList();
//                //  initMapFragment();
//            } else {
//                Toast.makeText(getActivity(), "Location is not available now", Toast.LENGTH_LONG).show();
//            }
//        }
//
//        @Override
//        public void onStatusChanged(String provider, int status, Bundle extras) {
//
//        }
//
//        @Override
//        public void onProviderEnabled(String provider) {
//
//        }
//
//        @Override
//        public void onProviderDisabled(String provider) {
//
//        }
//    };



    private boolean checkPermission() {
        boolean check = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (!check) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return false;
        }
        return true;
//    }
    }

    private boolean gpsEnabled() {
        isGpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGpsEnabled && !isNetworkEnabled) {
            // displayLocationSettingsRequest(getActivity());
//    if (!isGpsEnabled) {
//            Toast.makeText(m_activity, "GPS is not enabled", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}