package kr.co.darkkaiser.jv.view;

import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ActionBarListActivity extends ActionBarActivity {

    private ListView listView = null;

    protected ListView getListView() {
        if (this.listView == null) {
            this.listView = (ListView)findViewById(android.R.id.list);
            this.listView.setOnItemClickListener(mOnItemClickListener);
        }

        return this.listView;
    }

    protected void setListAdapter(ListAdapter adapter) {
        getListView().setAdapter(adapter);
    }

    protected ListAdapter getListAdapter() {
        ListAdapter adapter = getListView().getAdapter();
        if (adapter instanceof HeaderViewListAdapter) {
            return ((HeaderViewListAdapter)adapter).getWrappedAdapter();
        } else {
            return adapter;
        }
    }

    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            onListItemClick((ListView)adapterView, view, position, id);
        }
    };

    @SuppressWarnings("UnusedParameters")
    protected void onListItemClick(ListView l, View v, int position, long id) {

    }

}
