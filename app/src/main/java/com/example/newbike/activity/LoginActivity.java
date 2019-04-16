package com.example.newbike.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.newbike.R;
import com.example.newbike.model.User;
import com.example.newbike.util.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.et_psw)
    EditText etPsw;
    @BindView(R.id.btn_sign_up)
    Button btnSignUp;
    @BindView(R.id.tv_SignUp)
    TextView tvSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnSignUp.setBackgroundResource(R.drawable.shape_sign_up_button_pre);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etPhone.getText().toString().trim().equals("")) {
                    btnSignUp.setBackgroundResource(R.drawable.shape_sign_up_button);
                } else {
                    btnSignUp.setBackgroundResource(R.drawable.shape_sign_up_button_pre);
                }
            }
        });

    }

    @OnClick(R.id.btn_sign_up)
    public void onClick() {
        String phone = etPhone.getText().toString().trim();
        String psw = etPsw.getText().toString().trim();
        User user = new User();
        user.setUsername(phone);
        user.setPassword(psw);
        user.login(new SaveListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e == null) {
                    ToastUtil.show("登录成功");
                    etPhone.setText("");
                    etPsw.setText("");
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);//跳转到主界面后，并将栈底的Activity全部都销毁
                    startActivity(intent);
                    LoginActivity.this.finish();
                } else {
                    ToastUtil.show("登录失败，用户账号或密码错误");
                }
            }
        });
    }

    @OnClick(R.id.tv_SignUp)
    public void onSignUpClick() {
        etPhone.setText("");
        etPsw.setText("");
        Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        etPhone.requestFocus();
    }
}
