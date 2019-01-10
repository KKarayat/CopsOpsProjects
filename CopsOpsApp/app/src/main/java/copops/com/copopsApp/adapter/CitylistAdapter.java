package copops.com.copopsApp.adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;
import copops.com.copopsApp.R;
import copops.com.copopsApp.interfaceview.IncedentInterface;
import copops.com.copopsApp.pojo.AllLocationAndCityPojo;
import copops.com.copopsApp.pojo.IncidentTypePojo;

public class CitylistAdapter extends RecyclerView.Adapter<CitylistAdapter.ViewHolder> {
 //   private ArrayList<IncidentTypePojo.Data> incidentType;
     ArrayList<String> cityArrayList;
    private Context context;
    IncedentInterface mIncedentInterface;
    String userId;
    public CitylistAdapter(Context context, ArrayList<String> cityArrayList,IncedentInterface mIncedentInterface) {
        this.cityArrayList = cityArrayList;
        this.context = context;
        this.mIncedentInterface = mIncedentInterface;
    }


    @Override
    public CitylistAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.city_item, viewGroup, false);
        return new CitylistAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CitylistAdapter.ViewHolder viewHolder, final int i) {

        viewHolder.cityId.setText(cityArrayList.get(i));

    //    viewHolder.icident_type_name.setText(incidentType.get(i).getIncident_name());
      //  viewHolder.icident_type_desc.setText(incidentType.get(i).getIncident_description());
//        if(incidentType.get(i).getIncident_img_url()!=null){
//            Glide.with(context)
//                    .load(incidentType.get(i).getIncident_img_url())
//                    .apply(new RequestOptions().override(150, 150))
//                    .into(viewHolder.icident_type_img);
//            //       Glide.with(context).load(incidentType.get(i).getIncident_img_url()).into(viewHolder.icident_type_img);
//        }else{
//            Glide.with(context).load(R.mipmap.img_police).into(viewHolder.icident_type_img);
//        }

        viewHolder.cityId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIncedentInterface.clickPosition(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cityArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView cityId;


        public ViewHolder(View view) {
            super(view);
            cityId = (TextView)view.findViewById(R.id.cityId);
        }
    }

    public void filterList(ArrayList<String> filterdNames) {
        this.cityArrayList = filterdNames;
        notifyDataSetChanged();
    }

}