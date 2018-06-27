package com.projects.owner.camlocation;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.projects.owner.camlocation.model.ImagesModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by owner on 22/09/2016.
 */
public class FragmentRecylerView extends Fragment {

    private List<ImagesModel> data;


    public FragmentRecylerView() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = new ArrayList<>();
        Bundle b = getArguments();
        if (b != null && b.containsKey(ImagesModel.BRAND_NAME)) {
            data.add(new ImagesModel(b));
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_list_view, container, false);
        RecyclerView recyclerView = (RecyclerView) rootview.findViewById(R.id.recyclerview);
        FragmentRecyclerViewAdapter adapter = new FragmentRecyclerViewAdapter(data, getActivity());

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setRemoveDuration(1000);
        recyclerView.setItemAnimator(itemAnimator);

        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        return rootview;
    }

}
