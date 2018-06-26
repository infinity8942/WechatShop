package com.qiushi.wechatshop.model;

import com.qiushi.wechatshop.util.Push;
import com.umeng.analytics.MobclickAgent;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;

public class User extends RealmObject {
    /**
     * @PrimaryKey var id: Long = 0
     * var access_token: String = ""
     * var client_id: String = ""
     * var nick: String = ""
     * var avatar: String = ""
     * var phone: String = ""
     * var isLogin: Boolean = false
     */

    @PrimaryKey
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    private String access_token;
    private String client_id;
    private String nick;
    private String avatar;
    private String phone;
    boolean isLogin;


    private static User current;

    public static void setCurrent(final User current) {
        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<User> result = realm.where(User.class)
                        .equalTo("isLogin", true).findAll();
                for (User user : result) {
                    user.setLogin(false);
                }
                if (current == null) {
                    User.current = null;
                    return;
                }
                MobclickAgent.onProfileSignIn(String.valueOf(current.getId()));
//                current.setPhone(current.getExtra().get("bind"));
                current.setLogin(true);
                realm.copyToRealmOrUpdate(current);
            }
        });
    }

    public static User getCurrent() {
        if (current == null) {
            Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<User> result = realm.where(User.class)
                            .equalTo("isLogin", true).findAll();
                    if (result.size() == 1) {
                        current = result.first();
                    } else if (result.size() > 1) {
                        for (User user : result) {
                            user.setLogin(false);
                        }
                    }
                }
            });
        }
        return current;
    }

    public static User getCurrentFromRealm() {
        final User[] cur = new User[1];
        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<User> result = realm.where(User.class)
                        .equalTo("isLogin", true).findAll();
                if (result.size() == 1) {
                    User user = result.first();
                    cur[0] = user;
                }
            }
        });
        return cur[0];
    }

    public static void logout() {
        if (getCurrent() != null) {
            long id = getCurrent().getId();
            setCurrent(null);

        }

    }

}
