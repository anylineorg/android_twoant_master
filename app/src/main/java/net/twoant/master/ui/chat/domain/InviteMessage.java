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
package net.twoant.master.ui.chat.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 邀请消息实体
 */
public class InviteMessage implements Parcelable {
    private String from;
    private long time;
    private String reason;
    /**
     * 远程用户昵称
     */
    private String fromUserNickName;

    /**
     * 头像
     */
    private String avatar;

    private InviteMesageStatus status;
    private String groupId;
    private String groupName;
    private String groupInviter;


    private int id;

    public InviteMessage() {}

    protected InviteMessage(Parcel in) {
        from = in.readString();
        time = in.readLong();
        reason = in.readString();
        fromUserNickName = in.readString();
        avatar = in.readString();
        groupId = in.readString();
        groupName = in.readString();
        groupInviter = in.readString();
        id = in.readInt();
    }

    public static final Creator<InviteMessage> CREATOR = new Creator<InviteMessage>() {
        @Override
        public InviteMessage createFromParcel(Parcel in) {
            return new InviteMessage(in);
        }

        @Override
        public InviteMessage[] newArray(int size) {
            return new InviteMessage[size];
        }
    };

    public String getFrom() {
        return from;
    }

    public String getFromUserNickName() {
        return fromUserNickName;
    }

    public void setFromUserNickName(String fromUserNickName) {
        this.fromUserNickName = fromUserNickName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }


    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public InviteMesageStatus getStatus() {
        return status;
    }

    public void setStatus(InviteMesageStatus status) {
        this.status = status;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setGroupInviter(String inviter) {
        groupInviter = inviter;
    }

    public String getGroupInviter() {
        return groupInviter;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(from);
        dest.writeLong(time);
        dest.writeString(reason);
        dest.writeString(fromUserNickName);
        dest.writeString(avatar);
        dest.writeString(groupId);
        dest.writeString(groupName);
        dest.writeString(groupInviter);
        dest.writeInt(id);
    }


    public enum InviteMesageStatus {

        //==contact
        /**
         * being invited
         */
        BEINVITEED,
        /**
         * being refused
         */
        BEREFUSED,
        /**
         * remote user already agreed
         */
        BEAGREED,

        //==group application
        /**
         * remote user apply to join
         */
        BEAPPLYED,
        /**
         * you have agreed to join
         */
        AGREED,
        /**
         * you refused the join request
         */
        REFUSED,

        //==group invitation
        /**
         * received remote user's invitation
         **/
        GROUPINVITATION,
        /**
         * remote user accept your invitation
         **/
        GROUPINVITATION_ACCEPTED,
        /**
         * remote user declined your invitation
         **/
        GROUPINVITATION_DECLINED
    }

}



