package com.meiqia.ue.ec.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.meiqia.meiqiasdk.activity.MQPhotoPickerActivity;
import com.meiqia.ue.ec.R;
import com.meiqia.ue.ec.util.QiniuUtil;
import com.meiqia.ue.ec.util.SPUtil;
import com.meiqia.ue.ec.util.StorageUtil;
import com.meiqia.ue.ec.util.SweetAlertDialogUtil;
import com.qiniu.storage.model.FileInfo;
import com.trello.rxlifecycle.FragmentEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/3/18 上午12:39
 * 描述:
 */
public class ProfileFragment extends BaseFragment {
    private static final int REQUEST_CODE_CHOOSE_PHOTO = 1;
    private static final int REQUEST_CODE_CROP = 2;

    private CircleImageView mAvatarCiv;

    private TextInputLayout mIdTil;
    private TextInputEditText mIdTiet;

    private TextInputLayout mNicknameTil;
    private TextInputEditText mNicknameTiet;

    private TextInputLayout mTelTil;
    private TextInputEditText mTelTiet;

    private File mNewAvatarFile;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_profile);

        mAvatarCiv = getViewById(R.id.civ_profile_avatar);

        mIdTil = getViewById(R.id.til_profile_id);
        mIdTiet = getViewById(R.id.tiet_profile_id);

        mNicknameTil = getViewById(R.id.til_profile_nickname);
        mNicknameTiet = getViewById(R.id.tiet_profile_nickname);

        mTelTil = getViewById(R.id.til_profile_tel);
        mTelTiet = getViewById(R.id.tiet_profile_tel);
    }

    @Override
    protected void setListener() {
        mAvatarCiv.setOnClickListener(this);

        setOnClickListener(R.id.btn_profile_done);

        mTelTiet.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    submitCustomInfo();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        loadAvatar();

        mIdTiet.setText(SPUtil.getCustomId());
        mNicknameTiet.setText(SPUtil.getNickname());
        mTelTiet.setText(SPUtil.getTel());
    }

    private void loadAvatar() {
        String localAvatar = SPUtil.getLocalAvatar();
        if (!TextUtils.isEmpty(localAvatar) && new File(localAvatar).exists()) {
            Glide.with(this).load("file://" + localAvatar).placeholder(R.mipmap.holder_avatar).error(R.mipmap.holder_avatar).dontAnimate().into(mAvatarCiv);
        } else {
            // 加载头像并保存到本地
            Glide.with(this).load(SPUtil.getAvatar()).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onStart() {
                    mAvatarCiv.setImageResource(R.mipmap.holder_avatar);
                }

                @Override
                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                    mAvatarCiv.setImageResource(R.mipmap.holder_avatar);
                }

                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    mAvatarCiv.setImageBitmap(resource);
                    saveAvatarToLocal(resource);
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.civ_profile_avatar) {
            startActivityForResult(MQPhotoPickerActivity.newIntent(mActivity, null, 1, null, getString(R.string.mq_confirm)), REQUEST_CODE_CHOOSE_PHOTO);
        } else if (v.getId() == R.id.btn_profile_done) {
            submitCustomInfo();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            switch (requestCode) {
                case REQUEST_CODE_CHOOSE_PHOTO:
                    String avatarPath = MQPhotoPickerActivity.getSelectedImages(data).get(0);
                    mNewAvatarFile = new File(StorageUtil.getImageDir(), "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".png");
                    startActivityForResult(createCropIntent(Uri.fromFile(new File(avatarPath)), mNewAvatarFile), REQUEST_CODE_CROP);
                    break;
                case REQUEST_CODE_CROP:
                    Glide.with(this).load("file://" + mNewAvatarFile.getAbsolutePath()).placeholder(R.mipmap.holder_avatar).error(R.mipmap.holder_avatar).dontAnimate().into(mAvatarCiv);

                    uploadAvatarToQiniu();
                    break;
            }
        }
    }

    private Intent createCropIntent(Uri uri, File file) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("return-data", false);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        intent.putExtra("noFaceDetection", true);
        return intent;
    }

    private void submitCustomInfo() {
        final String id = mIdTiet.getText().toString().trim();
        final String nickname = mNicknameTiet.getText().toString().trim();
        final String tel = mTelTiet.getText().toString().trim();

        boolean flag = true;
        if (TextUtils.isEmpty(nickname)) {
            mNicknameTil.setError("昵称不能为空!");
            flag = false;
        } else {
            mNicknameTil.setErrorEnabled(false);
        }

        if (TextUtils.isEmpty(tel)) {
            mTelTil.setError("手机号不能为空!");
            flag = false;
        } else {
            mTelTil.setErrorEnabled(false);
        }

        if (flag) {
            SPUtil.setCustomId(id);
            SPUtil.setNickname(nickname);
            SPUtil.setTel(tel);

            SweetAlertDialogUtil.showSuccess(mActivity, "保存自定义信息成功", "将在下一次打开对话时生效");
        }
    }

    private void uploadAvatarToQiniu() {
        Observable.create(new Observable.OnSubscribe<FileInfo>() {
            @Override
            public void call(Subscriber<? super FileInfo> subscriber) {
                try {
                    subscriber.onNext(QiniuUtil.uploadFile(mNewAvatarFile.getAbsolutePath(), "avatar"));
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        }).compose(this.<FileInfo>bindUntilEvent(FragmentEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mActivity.showLoadingDialog(R.string.uploading_avatar);
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<FileInfo>() {
                    @Override
                    public void call(FileInfo fileInfo) {
                        mActivity.dismissLoadingDialog();

                        SPUtil.setAvatar(QiniuUtil.QN_PATH + fileInfo.key);

                        SweetAlertDialogUtil.showSuccess(mActivity, "保存头像成功", "将在下一次打开对话时生效");

                        // 删除之前本地缓存的头像
                        String localAvatar = SPUtil.getLocalAvatar();
                        if (!TextUtils.isEmpty(localAvatar) && new File(localAvatar).exists()) {
                            new File(localAvatar).delete();
                        }
                        // 通知图库更新
                        mActivity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(mNewAvatarFile)));
                        SPUtil.setLocalAvatar(mNewAvatarFile.getAbsolutePath());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mActivity.dismissLoadingDialog();

                        if (mNewAvatarFile != null && mNewAvatarFile.exists()) {
                            mNewAvatarFile.delete();
                        }

                        loadAvatar();

                        SweetAlertDialogUtil.showWarning(mActivity, "保存头像失败", throwable.getMessage());
                    }
                });
    }

    private void saveAvatarToLocal(final Bitmap bitmap) {
        Observable.create(new Observable.OnSubscribe<File>() {
            @Override
            public void call(Subscriber<? super File> subscriber) {
                FileOutputStream fos = null;
                File file = null;
                try {
                    file = new File(StorageUtil.getImageDir(), "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".png");
                    fos = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();

                    subscriber.onNext(file);
                    subscriber.onCompleted();
                } catch (IOException e) {
                    if (file != null && file.exists()) {
                        file.delete();
                    }
                    subscriber.onError(e);
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                        }
                    }
                }
            }
        }).compose(this.<File>bindUntilEvent(FragmentEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<File>() {
                    @Override
                    public void call(File avatarFile) {
                        // 通知图库更新
                        mActivity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(avatarFile)));
                        SPUtil.setLocalAvatar(avatarFile.getAbsolutePath());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                    }
                });
    }
}
