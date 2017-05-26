package net.twoant.master.ui.chat.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMConversation.EMSearchDirection;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.util.DateUtils;

import net.twoant.master.base_app.ChatBaseActivity;

import java.util.Date;
import java.util.List;

public class GroupSearchMessageActivity extends ChatBaseActivity implements OnClickListener {
    private ImageButton clearSearch;
    private EditText query;
    private ListView listView;
    private List<EMMessage> messageList;
    private String groupId;
    private TextView searchView;
    private SearchedMessageAdapter messageaAdapter;
    private ProgressDialog pd;

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.em_activity_group_search_message;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        query = (EditText) findViewById(net.twoant.master.R.id.et_query);
        // clear button
        clearSearch = (ImageButton) findViewById(net.twoant.master.R.id.btn_search_clear);
        listView = (ListView) findViewById(net.twoant.master.R.id.listview);
        TextView emptyView = (TextView) findViewById(net.twoant.master.R.id.tv_no_result);
        listView.setEmptyView(emptyView);
        emptyView.setVisibility(View.INVISIBLE);
        TextView cancleView = (TextView) findViewById(net.twoant.master.R.id.tv_cancel);
        searchView = (TextView) findViewById(net.twoant.master.R.id.tv_search);

        groupId = getIntent().getStringExtra("groupId");

        cancleView.setOnClickListener(this);
        searchView.setOnClickListener(this);

        query.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    clearSearch.setVisibility(View.VISIBLE);
                } else {
                    clearSearch.setVisibility(View.INVISIBLE);
                }
                searchView.setVisibility(View.VISIBLE);
                listView.setVisibility(View.INVISIBLE);
                searchView.setText(String.format(getString(net.twoant.master.R.string.search_contanier), s));
            }
        });

        query.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    searchMessages();
                    hideSoftKeyboard();
                    return true;
                }


            return false;
        }
    }

    );
    clearSearch.setOnClickListener(new

    OnClickListener() {
        @Override
        public void onClick (View v){
            query.getText().clear();
            searchView.setText("");
        }
    }

    );

}

    private void searchMessages() {
        pd = new ProgressDialog(this);
        pd.setMessage(getString(net.twoant.master.R.string.searching));
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        new Thread(new Runnable() {
            public void run() {
                EMConversation conversation = EMClient.getInstance().chatManager().getConversation(groupId);
                List<EMMessage> resultList = conversation.searchMsgFromDB(query.getText().toString(), System.currentTimeMillis(), 50, null, EMSearchDirection.UP);
                if (messageList == null) {
                    messageList = resultList;
                } else {
                    messageList.clear();
                    messageList.addAll(resultList);
                }
                onSearchResulted();
            }
        }).start();
    }

    private void onSearchResulted() {
        runOnUiThread(new Runnable() {
            public void run() {
                pd.dismiss();
                searchView.setVisibility(View.INVISIBLE);
                listView.setVisibility(View.VISIBLE);
                if (messageaAdapter == null) {
                    messageaAdapter = new SearchedMessageAdapter(GroupSearchMessageActivity.this, 1, messageList);
                    listView.setAdapter(messageaAdapter);
                } else {
                    messageaAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case net.twoant.master.R.id.tv_cancel:
                finish();
                break;
            case net.twoant.master.R.id.tv_search:
                hideSoftKeyboard();
                searchMessages();
                break;
            default:
                break;
        }
    }

private class SearchedMessageAdapter extends ArrayAdapter<EMMessage> {

    public SearchedMessageAdapter(Context context, int resource, List<EMMessage> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(net.twoant.master.R.layout.em_row_search_message, parent, false);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(net.twoant.master.R.id.name);
            holder.message = (TextView) convertView.findViewById(net.twoant.master.R.id.message);
            holder.time = (TextView) convertView.findViewById(net.twoant.master.R.id.time);
            holder.avatar = (ImageView) convertView.findViewById(net.twoant.master.R.id.avatar);
            convertView.setTag(holder);
        }

        EMMessage message = getItem(position);
        EaseUserUtils.setUserNick(message.getFrom(), holder.name);
        EaseUserUtils.setUserAvatar(getContext(), message.getFrom(), holder.avatar);
        holder.time.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
        holder.message.setText(((EMTextMessageBody) message.getBody()).getMessage());


        return convertView;
    }

}

private static class ViewHolder {
    TextView name;
    TextView message;
    TextView time;
    ImageView avatar;

}

}
