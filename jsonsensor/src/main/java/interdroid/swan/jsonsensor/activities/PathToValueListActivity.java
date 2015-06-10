package interdroid.swan.jsonsensor.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.liutoapps.android.jsonsensor.R;
import interdroid.swan.jsonsensor.adapters.PathToValueListAdapter;
import interdroid.swan.jsonsensor.pojos.JsonRequestInfo;
import interdroid.swan.jsonsensor.pojos.PathToValue;
import com.melnykov.fab.FloatingActionButton;

/**
 * Created by steven on 04/06/15.
 */
public class PathToValueListActivity extends BaseActivity {

    private static final int REQUEST_CODE_VALUE = 2001;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private PathToValueListAdapter mAdapter;

    private FloatingActionButton mAddButton;

    private JsonRequestInfo mJsonRequestInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);

        getViews();

        mJsonRequestInfo = getIntent().getParcelableExtra(JsonActivity.EXTRA_JSON_REQUEST_INFO);
        mAdapter.setPathToValues(mJsonRequestInfo.pathToValueList);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_path_to_value_list;
    }

    private void getViews() {
        //Main recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.path_to_value_recyclerview);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new PathToValueListAdapter(mOnPathToValueItemClicked);
        mRecyclerView.setAdapter(mAdapter);

        mAddButton = (FloatingActionButton) findViewById(R.id.path_to_value_fab);
        mAddButton.setOnClickListener(mOnAddClickListener);
        mAddButton.attachToRecyclerView(mRecyclerView);
    }

    private PathToValueListAdapter.OnPathToValueClickListener mOnPathToValueItemClicked = new PathToValueListAdapter.OnPathToValueClickListener() {
        @Override
        public void onPathToValueClicked(PathToValue pathToValue) {
            Intent intent = new Intent();
            intent.putExtra(SelectionActivity.REQUEST_EXTRA_RESULT, pathToValue.id);
            setResult(RESULT_OK, intent);
            finish();
        }
    };

    private View.OnClickListener mOnAddClickListener = new View.OnClickListener()

    {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(PathToValueListActivity.this, JsonActivity.class);
            intent.putExtra(JsonActivity.EXTRA_JSON_REQUEST_INFO, mJsonRequestInfo);
            startActivityForResult(intent, REQUEST_CODE_VALUE);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_CODE_VALUE) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }
}
