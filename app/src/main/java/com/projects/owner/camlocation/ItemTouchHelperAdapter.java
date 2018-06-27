package com.projects.owner.camlocation;

/**
 * Created by owner on 23/09/2016.
 */
public interface ItemTouchHelperAdapter {

    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}