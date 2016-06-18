package com.jshvarts.flatstanley;

import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by shvartsy on 6/15/16.
 */
public class LoginPresenterImpl implements LoginPresenter {
    private LoginView loginView;

    public LoginPresenterImpl(LoginView loginView) {
        this.loginView = loginView;
    }

    @Override
    public void login(String username, String password) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            loginView.showValidationError();
        } else {
            if (username.equals("admin") && password.equals("admin")) {
                loginView.loginSuccess();
            } else {
                loginView.loginError();
            }
        }
    }
}
