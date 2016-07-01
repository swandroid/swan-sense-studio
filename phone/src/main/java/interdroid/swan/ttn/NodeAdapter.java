package interdroid.swan.ttn;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.otto.Bus;

import org.joda.time.DateTime;
import org.ttn.android.sdk.domain.node.Node;

import java.text.DateFormat;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import interdroid.swan.R;

/*
 * Copyright 2015 The Things Network
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Created by fabiotiriticco on 20/11/15.
 *
 */

public class NodeAdapter extends RecyclerView.Adapter<NodeAdapter.ViewHolder> {

    Bus mBus;
    List<Node> mNodes;
    DateFormat mDateFormatter = DateFormat.getDateInstance(DateFormat.FULL);

    public NodeAdapter(Bus bus, List<Node> nodes) {
        mBus = bus;
        mNodes = nodes;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.ttn_item_node, parent, false);
        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(ViewHolder vh, final int position) {
        final Node node = mNodes.get(position);

        // node eui
        String nodeEui = node.getNodeEui();
        if (!TextUtils.isEmpty(nodeEui)) {
            vh.mNodeId.setVisibility(View.VISIBLE);
            vh.mNodeId.setText(nodeEui);
        } else {
            vh.mNodeId.setVisibility(View.GONE);
            vh.mNodeId.setText(null);
        }

        // last seen
        DateTime lastSeen = node.getLastSeen();
        if (lastSeen != null) {
            vh.mLastSeen.setVisibility(View.VISIBLE);
            vh.mLastSeen.setText(mDateFormatter.format(lastSeen.toDate()));
        } else {
            vh.mLastSeen.setVisibility(View.GONE);
            vh.mLastSeen.setText(null);
        }

        // last gateway
        String lastGateway = node.getLastGatewayEui();
        if (!TextUtils.isEmpty(lastGateway)) {
            vh.mLastGatewayEui.setVisibility(View.VISIBLE);
            vh.mLastGatewayEui.setText(lastGateway);
        } else {
            vh.mLastGatewayEui.setVisibility(View.GONE);
            vh.mLastGatewayEui.setText(null);
        }

        // packets count
        Integer packetsCount = node.getPacketsCount();
        if (packetsCount != null) {
            vh.mPacketsCount.setVisibility(View.VISIBLE);
            vh.mPacketsCount.setText(String.format("%d", packetsCount));
        } else {
            vh.mPacketsCount.setVisibility(View.GONE);
            vh.mPacketsCount.setText(null);
        }

        // dispatch click event
        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBus.post(new NodeSelectedEvent(node.getNodeEui()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNodes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.node_eui)
        TextView mNodeId;
        @Bind(R.id.node_last_seen)
        TextView mLastSeen;
        @Bind(R.id.node_packets_count)
        TextView mPacketsCount;
        @Bind(R.id.node_last_gateway_eui)
        TextView mLastGatewayEui;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
