package com.example.newbike.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newbike.R;
import com.example.newbike.model.User;
import com.example.newbike.util.ToastUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class SignUpActivity extends AppCompatActivity {

    @BindView(R.id.ll_back)
    LinearLayout llBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.et_psw)
    EditText etPsw;
    @BindView(R.id.btn_sign_up)
    Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);
        ButterKnife.bind(this);
        tvTitle.setText("注册");
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

    @OnClick({R.id.ll_back, R.id.btn_sign_up})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_back:
                finish();
                break;
            case R.id.btn_sign_up:
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
                //这样就可以了
                BmobQuery<User> query = new BmobQuery<>();
                query.addWhereEqualTo("username", etPhone.getText().toString().trim());
                query.findObjects(new FindListener<User>() {
                    @Override
                    public void done(List<User> list, BmobException e) {
                        if (e == null) {
                            if (list.size() != 0) {
                                ToastUtil.show("该注册用户已存在");
                            } else {
                                User user = new User();
                                user.setUsername(phone);
                                user.setPassword(psw);
                                user.setIspayment(false);
                                user.signUp(new SaveListener<User>() {
                                    @Override
                                    public void done(User user, BmobException e) {
                                        if (e == null) {
                                            ToastUtil.show("注册成功");
                                            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                        } else {
                                            ToastUtil.show("注册失败");
                                            Log.e("sssssssssssss", "done: " + e);
                                        }
                                    }
                                });
                            }
                        } else {
                            //查询出错
                        }
                    }
                });
                break;
        }
    }

    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobiles) {
		/*
		移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		联通：130、131、132、152、155、156、166、185、186
		电信：133、153、180、189、（1349卫通）
		总结起来就是第一位必定为1，第二位必定为3或5或6或8，其他位置的可以为0-9
		*/
        String telRegex = "[1][3568]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、6、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles)) return false;
        else return mobiles.matches(telRegex);
    }

}
