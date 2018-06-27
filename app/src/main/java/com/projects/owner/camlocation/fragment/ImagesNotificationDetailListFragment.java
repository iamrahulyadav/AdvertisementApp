package com.projects.owner.camlocation.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator;
import com.projects.owner.camlocation.R;
import com.projects.owner.camlocation.adapter.CampaignDetailImagesVideosAdapter;
import com.projects.owner.camlocation.adapter.Images_Notification_RecyclerViewAdapter;

/**
 * Created by Sanawal Alvi on 11/3/2017.
 */

public class ImagesNotificationDetailListFragment extends android.support.v4.app.Fragment {

    public static FragmentManager fragmentManager;
    private static RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    public static ImagesNotificationDetailListFragment newInstance()
    {
        return new ImagesNotificationDetailListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.images_notification_detail_list_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.images_notification_recycler_view);
        fragmentManager = getFragmentManager();
        RecyclerView.LayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new MaterialViewPagerHeaderDecorator());
        mAdapter = new Images_Notification_RecyclerViewAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }
}
