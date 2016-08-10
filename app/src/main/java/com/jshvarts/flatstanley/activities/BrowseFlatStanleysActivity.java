package com.jshvarts.flatstanley.activities;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.common.base.Preconditions;
import com.jshvarts.flatstanley.Constants;
import com.jshvarts.flatstanley.R;
import com.jshvarts.flatstanley.activities.adapters.MyPicsCursorAdapter;
import com.jshvarts.flatstanley.activities.adapters.SharedByOthersAdapter;
import com.jshvarts.flatstanley.data.MyPicsContract;
import com.jshvarts.flatstanley.data.remote.FlatStanleyRestApiClient;
import com.jshvarts.flatstanley.data.remote.FlatStanleyRetrofitRestApiClient;
import com.jshvarts.flatstanley.model.FlatStanley;
import com.jshvarts.flatstanley.model.FlatStanleyItems;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit.Call;

public class BrowseFlatStanleysActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "BrowseFlatStanleys";
    private static int MY_LOADER_ID = 0;

    @BindView(R.id.progress_bar)
    protected ProgressBar progressBar;

    @BindView(R.id.mine_only_toggle)
    protected ToggleButton toggleButton;

    @BindView(R.id.search_by_caption)
    protected EditText searchByCaptionEditText;

    @BindView(R.id.listView)
    protected ListView listView;

    private Firebase firebase;

    private List<FlatStanley> flatStanleys;

    private SearchAsyncTask searchAsyncTask;

    private SharedByOthersAdapter sharedByOthersAdapter;

    private MyPicsCursorAdapter myPicsCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.browse_flat_stanleys);

        ButterKnife.bind(this);

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    displayPicsCreatedByMe();
                } else {
                    displayPicsCreatedByOthers();
                }
            }
        });

        searchByCaptionEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchByCaptionEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    searchPicsByOthers();

                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchByCaptionEditText.getWindowToken(), 0);

                    handled = true;
                }
                return handled;
            }

        });

        displayPicsCreatedByOthers();
    }

    private void displayPicsCreatedByMe() {
        getLoaderManager().restartLoader(MY_LOADER_ID, null, this);
    }

    private void displayPicsCreatedByOthers() {

        flatStanleys = new ArrayList();

        firebase = new Firebase(Constants.getEntrytUri());
        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                toggleButton.setEnabled(true);

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    FlatStanley flatStanley = dataSnapshot.getValue(FlatStanley.class);
                    flatStanley.setId(dataSnapshot.getKey());
                    flatStanleys.add(flatStanley);
                }

                changeSharedByOthersFlatStanleyAdapterData(flatStanleys);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d(TAG, "The read failed: " + firebaseError.getMessage());
            }
        });
    }

    private void searchPicsByOthers() {
        if (searchAsyncTask != null && searchAsyncTask.getStatus() == AsyncTask.Status.RUNNING) {
            searchAsyncTask.cancel(true);
        }
        searchAsyncTask = new SearchAsyncTask(new FlatStanleyRetrofitRestApiClient());
        searchAsyncTask.execute(searchByCaptionEditText.getText().toString());
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader entered");

        return new CursorLoader(this, MyPicsContract.CONTENT_URI, MyPicsContract.getProjection(), null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d(TAG, "onLoadFinished entered");

        if (cursor.getCount() == 0) {
            Log.d(TAG, "no pics created by current user yet");
            Toast.makeText(BrowseFlatStanleysActivity.this, getText(R.string.no_pics_created_by_user), Toast.LENGTH_SHORT).show();
            return;
        }

        changeMyFlatStanleyPicsAdapterData(cursor);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        Log.d(TAG, "onLoaderReset entered");
        loader = null;
        myPicsCursorAdapter.swapCursor(null);
    }

    private class SearchAsyncTask extends AsyncTask<String, Void, FlatStanleyItems> {

        private FlatStanleyRestApiClient flatStanleyRestApiClient;
        private Exception exception;

        private SearchAsyncTask(FlatStanleyRestApiClient flatStanleyRestApiClient) {
            Preconditions.checkNotNull(flatStanleyRestApiClient);
            this.flatStanleyRestApiClient = flatStanleyRestApiClient;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }

        @Override
        protected FlatStanleyItems doInBackground(String... searchQuery) {
            Preconditions.checkNotNull(searchQuery);
            Call<FlatStanleyItems> call = flatStanleyRestApiClient.pics(encodeQueryParam(searchQuery[0]));
            if (!isCancelled()) {
                try {
                    return call.execute().body();
                } catch (IOException e) {
                    exception = e;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(FlatStanleyItems flatStanleyItems) {
            progressBar.setVisibility(View.GONE);
            if (exception != null) {
                Log.e(TAG, "exception not null");
                return;
            }
            if (flatStanleyItems == null) {
                Log.d(TAG, "flatStanleyItems found is null");
                Toast.makeText(BrowseFlatStanleysActivity.this, getText(R.string.no_data_matched), Toast.LENGTH_SHORT).show();
                return;
            }

            List<FlatStanley> flatStanleyList = new ArrayList<>();
            for (FlatStanley flatStanley : flatStanleyItems.getFlatStanleys()) {
                Log.d(TAG, "path: " + flatStanley.getImageData());
                Log.d(TAG, "caption: " + flatStanley.getCaption());
                Log.d(TAG, "timestamp: " + flatStanley.getTimestamp());
                Log.d(TAG, "id: " + flatStanley.getId());
                flatStanleyList.add(flatStanley);
            }

            changeSharedByOthersFlatStanleyAdapterData(flatStanleyList);
            listView.setVisibility(View.VISIBLE);
        }

        private String encodeQueryParam(String rawQueryParam) {
            StringBuilder stringBuilder = new StringBuilder("\"");
            stringBuilder.append(rawQueryParam);
            stringBuilder.append("\"");

            String queryParam;
            try {
                queryParam = URLEncoder.encode(stringBuilder.toString(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, "unable to encode query param");
                return null;
            }

            Log.d(TAG, "queryParam: " + queryParam);

            return queryParam;
        }
    }

    private void changeSharedByOthersFlatStanleyAdapterData(List<FlatStanley> flatStanleys) {
        sharedByOthersAdapter = new SharedByOthersAdapter(BrowseFlatStanleysActivity.this, flatStanleys);
        listView.setAdapter(sharedByOthersAdapter);
        sharedByOthersAdapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startShareActivity(id, false);
            }
        });
    }

    private void changeMyFlatStanleyPicsAdapterData(Cursor cursor) {
        myPicsCursorAdapter = new MyPicsCursorAdapter(BrowseFlatStanleysActivity.this, cursor);
        listView.setAdapter(myPicsCursorAdapter);
        myPicsCursorAdapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startShareActivity(id, true);
            }
        });
    }

    private void startShareActivity(long id, boolean isLocal) {
        Log.d(TAG, "id clicked: " + id +", isLocal: " + isLocal);
        Intent shareActivityIntent = new Intent(BrowseFlatStanleysActivity.this, ShareExistingFlatStanleyActivity.class);
        shareActivityIntent.putExtra(ShareExistingFlatStanleyActivity.ITEM_ID_EXTRA, id);
        shareActivityIntent.putExtra(ShareExistingFlatStanleyActivity.IS_PIC_LOCAL_EXTRA, isLocal);
        startActivity(shareActivityIntent);
    }
}