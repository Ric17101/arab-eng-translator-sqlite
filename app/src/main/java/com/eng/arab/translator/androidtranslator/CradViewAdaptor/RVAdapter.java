package com.eng.arab.translator.androidtranslator.CradViewAdaptor;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.eng.arab.translator.androidtranslator.R;
import com.eng.arab.translator.androidtranslator.model.HomeButtonsModel;

import java.util.List;

/**
 * Created by keir on 11/1/2016.
 */

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.PersonViewHolder>{

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_home_rv, viewGroup, false);
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {
        personViewHolder.personName.setText(persons.get(i).getTitle());
        personViewHolder.personAge.setText(persons.get(i).getDetails());
        personViewHolder.personPhoto.setImageResource(persons.get(i).getPhotoId());
    }

    @Override
    public int getItemCount() {
        return persons.size();
    }

    public static class PersonViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView personName;
        TextView personAge;
        ImageView personPhoto;

        PersonViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv_translate);
            personName = (TextView)itemView.findViewById(R.id.person_title);
            //personAge = (TextView)itemView.findViewById(R.id.person_details);
            personPhoto = (ImageView)itemView.findViewById(R.id.person_photo);
        }
    }

    private List<HomeButtonsModel> persons;

//    public RVAdapter(List<HomeButtonsModel> persons){
//        this.persons = persons;
//    }

    private Context context;
    public RVAdapter(List<HomeButtonsModel> persons, Context context){
        this.persons = persons;
        this.context = context;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}