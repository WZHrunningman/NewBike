package com.example.newbike.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.newbike.R;
import com.example.newbike.activity.LoginActivity;
import com.example.newbike.activity.MainActivity;
import com.example.newbike.activity.SetUpActivity;
import com.example.newbike.activity.TripActivity;
import com.example.newbike.model.Money;
import com.example.newbike.model.User;
import com.example.newbike.util.ToastUtil;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.core.CenterPopupView;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.lxj.xpopup.util.XPopupUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;
import me.iwf.photopicker.PhotoPicker;

import static android.app.Activity.RESULT_OK;

public class MainFragmentTwo extends Fragment {

    @BindView(R.id.profile_image)
    CircleImageView profileImage;
    @BindView(R.id.btn_add_money)
    Button btnAddMoney;
    @BindView(R.id.iv_trip)
    ImageView ivTrip;
    @BindView(R.id.iv_service)
    ImageView ivService;
    Unbinder unbinder;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.ll_my)
    LinearLayout llMy;
    @BindView(R.id.ll_money)
    LinearLayout llMoney;
    @BindView(R.id.ll_trip_and_service)
    LinearLayout llTripAndService;
    @BindView(R.id.ll_no_login)
    LinearLayout llNoLogin;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.tv_balance)
    TextView tvBalance;
    @BindView(R.id.ll_trip)
    LinearLayout llTrip;
    @BindView(R.id.ll_service)
    LinearLayout llService;
    @BindView(R.id.iv_set_up)
    ImageView ivSetUp;
    @BindView(R.id.ll_set_up)
    LinearLayout llSetUp;
    @BindView(R.id.fl_title)
    FrameLayout flTitle;
    @BindView(R.id.iv_deposit)
    ImageView ivDeposit;
    @BindView(R.id.ll_deposit)
    LinearLayout llDeposit;
    @BindView(R.id.tv_refuse)
    TextView tvRefuse;
    @BindView(R.id.tv_pay)
    TextView tvPay;

    private Context context;
    private List<Integer> doubleList;
    private int money = 0;
    private CustomAddMoneyDialog dialog;
    private static final String TAG = "lalala";
    private User user = BmobUser.getCurrentUser(User.class);
    private BasePopupView xPopup;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_two, container, false);
        //返回一个Unbinder值(一个实例)（进行解绑），注意这里的this不能使用getActivity()
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        if (BmobUser.isLogin()) {
            xPopup = new XPopup.Builder(context)
                    .asLoading("正在加载中")
                    .show();
            User user = BmobUser.getCurrentUser(User.class);
            try {
                Glide.with(context).load(user.getAvatar()).into(profileImage);
            } catch (NullPointerException ex) {
                profileImage.setImageResource(R.drawable.avatar);
            }
            if (user.getIspayment()) {
                tvPay.setVisibility(View.VISIBLE);
            }else {
                tvPay.setVisibility(View.GONE);
            }
            requestMoney();
            tvName.setText(user.getUsername());
            btnAddMoney.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog == null) {
                        dialog = new CustomAddMoneyDialog(context);
                    }
                    new XPopup.Builder(context)
                            .asCustom(dialog)
                            .show();
                }
            });
            llSetUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, SetUpActivity.class);
                    startActivity(intent);
                }
            });
            tvRefuse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initView();
                }
            });
            xPopup.dismiss();
        } else {
            llNoLogin.setVisibility(View.VISIBLE);
            llMy.setVisibility(View.GONE);
            llMoney.setVisibility(View.GONE);
            flTitle.setVisibility(View.GONE);
            llTripAndService.setVisibility(View.GONE);
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);//跳转到主界面后，并将栈底的Activity全部都销毁
                    startActivity(intent);
                }
            });
        }

        llTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, TripActivity.class));
            }
        });

        llDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = BmobUser.getCurrentUser(User.class);
                if (user.getIspayment()) {
                    new XPopup.Builder(context)
                            .asConfirm("温馨提示", "您已成功缴纳保证金", new OnConfirmListener() {
                                @Override
                                public void onConfirm() {

                                }
                            })
                            .show();
                    return;
                }
                new XPopup.Builder(context)
                        .asConfirm("缴纳押金须知", "用户需缴纳99元押金方可用车哦", new OnConfirmListener() {
                            @Override
                            public void onConfirm() {
                                user.setIspayment(true);
                                user.update(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null) {
                                            ToastUtil.show("押金缴纳成功");
                                        } else {
                                            ToastUtil.show("押金缴纳失败" + e);
                                        }
                                    }
                                });
                            }
                        })
                        .show();
            }
        });

        llService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:5285286666"));
                startActivity(intent);
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoPicker.builder()
                        .setPhotoCount(1)
                        .setShowCamera(true)
                        .setShowGif(true)
                        .setPreviewEnabled(false)
                        .start((MainActivity) context, MainFragmentTwo.this, PhotoPicker.REQUEST_CODE);
            }
        });
    }

    class CustomAddMoneyDialog extends CenterPopupView {
        private Context context;
        private final TextView tv_twenty = findViewById(R.id.tv_twenty);
        private TextView tv_Fifty = findViewById(R.id.tv_Fifty);
        private TextView tv_one_hundred = findViewById(R.id.tv_one_hundred);
        private Button btn_ok = findViewById(R.id.btn_ok);
        private ImageView iv_cancel = findViewById(R.id.iv_cancel);
        private EditText et_input_money = findViewById(R.id.et_input_money);
        private int i = 0;
        private boolean isOne = false;
        private boolean isTwo = false;
        private boolean isThree = false;

        public CustomAddMoneyDialog(@NonNull Context context) {
            super(context);
            this.context = context;
        }

        // 返回自定义弹窗的布局
        @Override
        protected int getImplLayoutId() {
            return R.layout.custom_popup;
        }

        // 执行初始化操作，比如：findView，设置点击，或者任何你弹窗内的业务逻辑
        @Override
        protected void initPopupContent() {
            super.initPopupContent();
            et_input_money.setText("");
            tv_twenty.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isOne) {
                        i = 1;
                        isOne = true;
                        isTwo = false;
                        isThree = false;
                        tv_twenty.setTextColor(context.getResources().getColor(R.color.white));
                        tv_twenty.setBackgroundResource(R.drawable.shape_sign_up_button_pre);
                    } else {
                        i = 0;
                        isOne = false;
                        isTwo = false;
                        isThree = false;
                        tv_twenty.setTextColor(context.getResources().getColor(R.color.blackThree));
                        tv_twenty.setBackgroundResource(R.drawable.shape_gray);
                    }
                    tv_Fifty.setTextColor(context.getResources().getColor(R.color.blackThree));
                    tv_Fifty.setBackgroundResource(R.drawable.shape_gray);
                    tv_one_hundred.setTextColor(context.getResources().getColor(R.color.blackThree));
                    tv_one_hundred.setBackgroundResource(R.drawable.shape_gray);
                    et_input_money.setText("");
                }
            });
            tv_Fifty.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isTwo) {
                        i = 2;
                        isTwo = true;
                        isOne = false;
                        isThree = false;
                        tv_Fifty.setTextColor(context.getResources().getColor(R.color.white));
                        tv_Fifty.setBackgroundResource(R.drawable.shape_sign_up_button_pre);
                    } else {
                        i = 0;
                        isOne = false;
                        isTwo = false;
                        isThree = false;
                        tv_Fifty.setTextColor(context.getResources().getColor(R.color.blackThree));
                        tv_Fifty.setBackgroundResource(R.drawable.shape_gray);
                    }
                    tv_twenty.setTextColor(context.getResources().getColor(R.color.blackThree));
                    tv_twenty.setBackgroundResource(R.drawable.shape_gray);
                    tv_one_hundred.setTextColor(context.getResources().getColor(R.color.blackThree));
                    tv_one_hundred.setBackgroundResource(R.drawable.shape_gray);
                    et_input_money.setText("");
                }
            });
            tv_one_hundred.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isThree) {
                        i = 3;
                        isThree = true;
                        isOne = false;
                        isTwo = false;
                        tv_one_hundred.setTextColor(context.getResources().getColor(R.color.white));
                        tv_one_hundred.setBackgroundResource(R.drawable.shape_sign_up_button_pre);
                    } else {
                        i = 0;
                        isOne = false;
                        isTwo = false;
                        isThree = false;
                        tv_one_hundred.setTextColor(context.getResources().getColor(R.color.blackThree));
                        tv_one_hundred.setBackgroundResource(R.drawable.shape_gray);
                    }

                    tv_twenty.setTextColor(context.getResources().getColor(R.color.blackThree));
                    tv_twenty.setBackgroundResource(R.drawable.shape_gray);
                    tv_Fifty.setTextColor(context.getResources().getColor(R.color.blackThree));
                    tv_Fifty.setBackgroundResource(R.drawable.shape_gray);
                    et_input_money.setText("");
                }
            });

            iv_cancel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });

            btn_ok.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (i != 0 && !et_input_money.getText().toString().trim().equals("")) {
                        ToastUtil.show("请选择正确的充值金额");
                    }
                    if (i == 0 && isNumeric(et_input_money.getText().toString().trim())) {
                        saveMoney(et_input_money.getText().toString().trim());
                    }
                    if (et_input_money.getText().toString().trim().equals("")) {
                        switch (i) {
                            case 0:
                                ToastUtil.show("请选择充值金额");
                                break;
                            case 1:
                                saveMoney("20");
                                break;
                            case 2:
                                saveMoney("50");
                                break;
                            case 3:
                                saveMoney("100");
                                break;
                        }
                    }
                }
            });
        }

        private void saveMoney(String mon) {
            Money money = new Money();
            money.setMoney(Integer.parseInt(mon));
            money.setUsername(user.getUsername());
            money.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        ToastUtil.show("充值成功");
                        requestMoney();
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    } else {
                        ToastUtil.show("充值失败" + e);
                    }
                }
            });
        }

        //设置弹窗的最大宽度为屏幕的0.85
        @Override
        protected int getMaxWidth() {
            return (int) (XPopupUtils.getWindowWidth(getContext()) * .85f);
        }
    }

    private boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    private void requestMoney() {
        if (!BmobUser.isLogin()) {
            return;
        }
        money = 0;
        doubleList = new ArrayList<>();
        BmobQuery<Money> query = new BmobQuery<>();
        if (user == null) {
            user = BmobUser.getCurrentUser(User.class);
        }
        query.addWhereEqualTo("username", user.getUsername());
        query.findObjects(new FindListener<Money>() {
            @Override
            public void done(List<Money> list, BmobException e) {
                if (e == null) {
                    Log.d(TAG, "done: ----" + list.get(0).getMoney());
                    for (int i = 0; i < list.size(); i++) {
                        doubleList.add(list.get(i).getMoney());
                        Log.d(TAG, "done: do" + doubleList.get(i));
                    }
                    for (Integer d : doubleList) {
                        money += d;
                    }
                    tvBalance.setText(money + "元");
                } else {
                    Log.d(TAG, "done: ---fail" + e);
                }
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            requestMoney();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        llMy.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int h = llMy.getMeasuredHeight();
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) llMoney.getLayoutParams();
                params.setMargins(0, (int) (h * 0.8), 0, 0);
                llMoney.setLayoutParams(params);
                llMy.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: --------------afsadfasd");
        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            Log.d(TAG, "onActivityResult: =============adasd");
            if (data != null) {
                ArrayList<String> photos =
                        data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                xPopup = new XPopup.Builder(context)
                        .asLoading("正在更换头像中")
                        .show();
                Log.d(TAG, "onActivityResult: ===========" + photos + "===========================");
                BmobFile bmobFile = new BmobFile(new File(photos.get(0)));
                bmobFile.uploadblock(new UploadFileListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            Log.d(TAG, "done: ===============111");
                            if (user == null) {
                                user = BmobUser.getCurrentUser(User.class);
                            }
                            user.setAvatar(bmobFile.getFileUrl());
                            user.update(new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        Log.d(TAG, "done: ==========2222" + bmobFile);
                                        try {
                                            Glide.with(context).load(user.getAvatar()).into(profileImage);
                                        } catch (NullPointerException ex) {
                                            profileImage.setImageResource(R.drawable.avatar);
                                        }
                                        xPopup.dismiss();
                                    } else {
                                        ToastUtil.show("头像上传失败" + e);
                                        xPopup.dismiss();

                                    }
                                }
                            });
                        } else {
                            ToastUtil.show("文件上传失败" + e);
                            xPopup.dismiss();
                        }
                    }
                });
            }
        }
    }

    /**
     * onDestroyView中进行解绑操作
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
