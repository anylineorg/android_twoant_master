/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.twoant.master.ui.chat.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeui.adapter.EaseContactAdapter;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.widget.EaseSidebar;
import net.twoant.master.base_app.ChatBaseActivity;
import net.twoant.master.ui.chat.Constant;
import net.twoant.master.ui.chat.app.ChatHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GroupPickContactsActivity extends ChatBaseActivity {
    private final static String EXTRA_GROUP_ID = "groupId";
    public final static String EXTRA_MEMBER_LIST = "newmembers";
    /**
     * if this is a new group
     */
    protected boolean isCreatingNewGroup;
    private PickContactAdapter contactAdapter;
    /**
     * members already in the group
     */
    private List<String> existMembers;

    public static void startActivityForResult(Activity activity, String groupId, int code) {
        Intent intent = new Intent(activity, GroupPickContactsActivity.class);
        intent.putExtra(EXTRA_GROUP_ID, groupId);
        activity.startActivityForResult(intent, code);
    }

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.em_activity_group_pick_contacts;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        initToolbarData();
        String groupId = getIntent().getStringExtra("groupId");
        if (groupId == null) {// create new group
            isCreatingNewGroup = true;
        } else {
            // get members of the group
            EMGroup group = EMClient.getInstance().groupManager().getGroup(groupId);
            existMembers = group.getMembers();
        }
        if (existMembers == null)
            existMembers = new ArrayList<String>();
        // get contact list
        final List<EaseUser> alluserList = new ArrayList<EaseUser>();
        for (EaseUser user : ChatHelper.getContactList().values()) {
            if (!user.getUsername().equals(Constant.NEW_FRIENDS_USERNAME) & !user.getUsername().equals(Constant.GROUP_USERNAME) & !user.getUsername().equals(Constant.CHAT_ROOM) & !user.getUsername().equals(Constant.CHAT_ROBOT))
                alluserList.add(user);
        }
        // sort the list
        Collections.sort(alluserList, new Comparator<EaseUser>() {

            @Override
            public int compare(EaseUser lhs, EaseUser rhs) {
                if (lhs.getInitialLetter().equals(rhs.getInitialLetter())) {
                    return lhs.getNick().compareTo(rhs.getNick());
                } else {
                    if ("#".equals(lhs.getInitialLetter())) {
                        return 1;
                    } else if ("#".equals(rhs.getInitialLetter())) {
                        return -1;
                    }
                    return lhs.getInitialLetter().compareTo(rhs.getInitialLetter());
                }

            }
        });

        ListView listView = (ListView) findViewById(net.twoant.master.R.id.list);
        contactAdapter = new PickContactAdapter(this, net.twoant.master.R.layout.yh_row_contact_with_checkbox, alluserList);
        listView.setAdapter(contactAdapter);
        ((EaseSidebar) findViewById(net.twoant.master.R.id.sidebar)).setListView(listView);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox checkBox = (CheckBox) view.findViewById(net.twoant.master.R.id.checkbox);
                checkBox.toggle();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(net.twoant.master.R.menu.yh_menu_create, menu);
        MenuItem item = menu.findItem(net.twoant.master.R.id.create);
        if (!isCreatingNewGroup && item != null) {
            item.setTitle("完成");
        }
        return true;
    }

    private void initToolbarData() {
        initSimpleToolbarData(this, getString(net.twoant.master.R.string.Select_the_contact), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupPickContactsActivity.this.finish();
            }
        }).setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == net.twoant.master.R.id.create) {
                    List<String> var = getToBeAddMembers();
                    setResult(RESULT_OK, new Intent().putExtra(EXTRA_MEMBER_LIST, var.toArray(new String[var.size()])));
                    finish();
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * get selected members
     *
     * @return
     */
    private ArrayList<String> getToBeAddMembers() {
        ArrayList<String> members = new ArrayList<>();
        int length = contactAdapter.isCheckedArray.length;
        EaseUser item;
        for (int i = 0; i < length; i++) {
            item = contactAdapter.getItem(i);
            if (null != item) {
                String username = item.getUsername();
                if (contactAdapter.isCheckedArray[i] && !existMembers.contains(username)) {
                    members.add(username);
                }
            }
        }

        return members;
    }

    /**
     * adapter
     */
    private class PickContactAdapter extends EaseContactAdapter {

        private boolean[] isCheckedArray;

        public PickContactAdapter(Context context, int resource, List<EaseUser> users) {
            super(context, resource, users);
            isCheckedArray = new boolean[users.size()];
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);

            final String username = getItem(position).getUsername();

            final CheckBox checkBox = (CheckBox) view.findViewById(net.twoant.master.R.id.checkbox);
            ImageView avatarView = (ImageView) view.findViewById(net.twoant.master.R.id.avatar);
            TextView nameView = (TextView) view.findViewById(net.twoant.master.R.id.name);

            if (checkBox != null) {
                if (existMembers != null && existMembers.contains(username)) {
                    checkBox.setButtonDrawable(net.twoant.master.R.drawable.em_checkbox_bg_gray_selector);
                } else {
                    checkBox.setButtonDrawable(net.twoant.master.R.drawable.em_checkbox_bg_selector);
                }

                checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        // check the exist members
                        if (existMembers.contains(username)) {
                            isChecked = true;
                            checkBox.setChecked(true);
                        }
                        isCheckedArray[position] = isChecked;

                    }
                });
                // keep exist members checked
                if (existMembers.contains(username)) {
                    checkBox.setChecked(true);
                    isCheckedArray[position] = true;
                } else {
                    checkBox.setChecked(isCheckedArray[position]);
                }
            }

            return view;
        }
    }
}
