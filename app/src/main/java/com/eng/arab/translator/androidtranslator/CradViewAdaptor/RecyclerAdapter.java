package com.eng.arab.translator.androidtranslator.CradViewAdaptor;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eng.arab.translator.androidtranslator.R;
import com.eng.arab.translator.androidtranslator.model.TranslateModel;

import java.util.List;

public class RecyclerAdapter extends  RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    List<TranslateModel> mTModel;
    Context mContext;
    LayoutInflater mInflater;

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv1,tv2;
        TextView _icon, _id, _arabic, _english, _structure;
        ImageView imageView;

        public ViewHolder(View itemView) {super(itemView);

//        tv1= (TextView) itemView.findViewById(R.id.list_title);
//        tv2= (TextView) itemView.findViewById(R.id.list_desc);
//        imageView= (ImageView) itemView.findViewById(R.id.list_avatar);
//        cardView = (CardView) itemView.findViewById(R.id.user_layout);


//            _icon= (TextView) itemView.findViewById(R.id.icon_lang);
//            _id = (TextView) itemView.findViewById(R.id.txtID);
//            _arabic = (TextView) itemView.findViewById(R.id.txtArabic);
//            _english = (TextView) itemView.findViewById(R.id.txtEnglish);
//            _structure = (TextView) itemView.findViewById(R.id.txtStructure);
        }
    }

    public RecyclerAdapter(Context context, List<TranslateModel> tModel) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mTModel = tModel;
    }

    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.listview_item_row, parent, false);

        // Return a new holder instance
//        ViewHolder viewHolder = new ViewHolder(contactView);


//        View v = mInflater.inflate(R.layout.listview_item_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.ViewHolder holder, int position) {

        // Get the data model based on position
        TranslateModel translate = mTModel.get(position);
        //holder._id.setText(name[position]);
        holder._id.setText(translate._id);
        holder._arabic.setText(translate._arabic);
        holder._english.setText(translate._english);
        holder._structure.setText(translate._type);

        holder._icon.setText(translate._id);

        holder._icon.setOnClickListener(clickListener);
        holder.imageView.setTag(holder);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            RecyclerViewHolder vholder = (RecyclerViewHolder) v.getTag();
            int position = vholder.getPosition();

            Toast.makeText(mContext,"This is position " + position, Toast.LENGTH_LONG ).show();
        }
    };

    @Override
    public int getItemCount() {
        return (null != mTModel ? mTModel.size() : 0);
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }
}
