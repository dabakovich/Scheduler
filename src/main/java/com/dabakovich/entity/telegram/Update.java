package com.dabakovich.entity.telegram;

/**
 * Created by dabak on 17.08.2017, 14:51.
 */
public class Update {

    private int update_id;

    private Message message;

    public int getUpdate_id() {
        return update_id;
    }

    public void setUpdate_id(int update_id) {
        this.update_id = update_id;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Update{" +
                "update_id=" + update_id +
                ", message=" + message +
                '}';
    }
}
