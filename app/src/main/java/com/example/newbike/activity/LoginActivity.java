package com.example.newbike.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.newbike.R;
import com.example.newbike.model.User;
import com.example.newbike.util.EditTextUtils;
import com.example.newbike.util.ToastUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

import static com.example.newbike.activity.SignUpActivity.isMobileNO;

public class LoginActivity extends AppCompatActivity {

    //2.使用黄油刀找到对应控件,绑定View
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.et_psw)
    EditText etPsw;
    @BindView(R.id.btn_sign_up)
    Button btnSignUp;
    @BindView(R.id.tv_SignUp)
    TextView tvSignUp;
    @BindView(R.id.cl2)
    Button cl2;
    @BindView(R.id.cl4)
    Button cl4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //1.将黄油刀框架绑定到这个Activity上
        ButterKnife.bind(this);
        EditTextUtils.clearButtonListener(etPhone,cl2);
        EditTextUtils.clearButtonListener(etPsw,cl4);

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
                if (s != null && s.length() == 13) {
                    if (etPhone.isFocused()) {
                        etPhone.clearFocus();
                        etPsw.requestFocus();
                    }
                }
            }
        });

    }

    @OnClick(R.id.btn_sign_up)
    public void onClick() {
        String phone = etPhone.getText().toString().trim();
        String psw = etPsw.getText().toString().trim();
        if (phone.equals("")) {
            ToastUtil.show("手机号码不能为空");
            return;
        }
        if (!isMobileNO(phone)) {
            ToastUtil.show("请输入正确的手机号码");
            return;
        }
        if (psw.equals("")) {
            ToastUtil.show("密码不能为空");
            return;
        }
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereEqualTo("username", etPhone.getText().toString().trim());
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null) {
                    if (list.size() == 0) {
                        ToastUtil.show("该登录用戶不存在");
                    } else {
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
                } else {
                    //查询出错
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
