package br.unisinos.hefestos.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import br.unisinos.hefestos.R;
import br.unisinos.hefestos.pojo.Resource;
import br.unisinos.hefestos.utility.Utility;

public class ResourceAdapter extends BaseAdapter {

    private static final String LOG_TAG = ResourceAdapter.class.getSimpleName();
    private List<Resource> mResources;

    private Context mContext;

    public ResourceAdapter(List<Resource> mResources, Context mContext) {
        this.mResources = mResources;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        if(mResources == null || mResources.isEmpty()){
            return 0;
        }
        return mResources.size();
    }

    @Override
    public Object getItem(int position) {
        if(mResources == null || mResources.isEmpty()){
            return null;
        }
        return mResources.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.resource_row, parent, false);

            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        Resource resource = (Resource)getItem(position);

        viewHolder.resourceNameTextView.setText(resource.getName());
        viewHolder.resourceDescriptionTextView.setText(resource.getDescription());
        if(resource.getDistance() == null){
            viewHolder.resourceDistanceTextView.setVisibility(View.INVISIBLE);
        }else {
            viewHolder.resourceDistanceTextView.setText(String.valueOf(resource.getDistance()));
        }
        viewHolder.resourceIconImageView.setImageResource(Utility.getResourceIconId(resource.getType()));

        return convertView;
    }

    public static class ViewHolder {
        public final TextView resourceNameTextView;
        public final TextView resourceDescriptionTextView;
        public final TextView resourceDistanceTextView;
        public final ImageView resourceIconImageView;

        public ViewHolder(View view) {
            resourceNameTextView = (TextView) view.findViewById(R.id.recursoNome);
            resourceDescriptionTextView = (TextView) view.findViewById(R.id.recursoDesc);
            resourceDistanceTextView = (TextView) view.findViewById(R.id.recursoDist);
            resourceIconImageView= (ImageView) view.findViewById(R.id.recursoIcon);
        }
    }
}
