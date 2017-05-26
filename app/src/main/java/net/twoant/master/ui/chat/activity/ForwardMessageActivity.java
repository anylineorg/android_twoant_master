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

import android.os.Bundle;

import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.widget.EaseAlertDialog;

/**
 * 转发消息
 */
public class ForwardMessageActivity extends PickContactNoCheckboxActivity {
    private EaseUser selectUser;
    private String forward_msg_id;

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        super.subOnCreate(savedInstanceState);
        forward_msg_id = getIntent().getStringExtra("forward_msg_id");
    }

    @Override
    protected void onListItemClick(int position) {
        selectUser = contactAdapter.getItem(position);
        new EaseAlertDialog(this, null, getString(net.twoant.master.R.string.confirm_forward_to, selectUser.getNick()), null, new EaseAlertDialog.AlertDialogUser() {
            @Override
            public void onResult(boolean confirmed, Bundle bundle) {
                if (confirmed) {
                    if (selectUser == null)
                        return;
                    try {
                        ChatActivity.activityInstance.finish();
                    } catch (Exception e) {
                    }
                    ChatActivity.startActivityRelay(ForwardMessageActivity.this, forward_msg_id, selectUser.getUsername());
                    finish();
                }
            }
        }, true).show();
    }

}
