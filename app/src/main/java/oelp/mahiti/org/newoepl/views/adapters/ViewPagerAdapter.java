package oelp.mahiti.org.newoepl.views.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import oelp.mahiti.org.newoepl.R;
import oelp.mahiti.org.newoepl.fileandvideodownloader.DownloadClass;
import oelp.mahiti.org.newoepl.fileandvideodownloader.DownloadUtility;
import oelp.mahiti.org.newoepl.fileandvideodownloader.FileModel;
import oelp.mahiti.org.newoepl.services.RetrofitConstant;
import oelp.mahiti.org.newoepl.utils.AppUtils;
import oelp.mahiti.org.newoepl.utils.Constants;
import oelp.mahiti.org.newoepl.utils.Logger;
import oelp.mahiti.org.newoepl.utils.MySharedPref;
import oelp.mahiti.org.newoepl.views.activities.AppIntroVideoActivity;


public class ViewPagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private Integer [] images = {R.drawable.sliderimage1, R.drawable.sliderimage2,R.drawable.sliderimage3};

    public ViewPagerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.custom_layout, null);
        RoundedImageView imageView = view.findViewById(R.id.imageView);
        ImageView ivPlay = view.findViewById(R.id.ivPlay);
        imageView.setImageResource(images[position]);


        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);

        if (position>0){
            ivPlay.setVisibility(View.GONE);
        }

        CardView cdCardView = view.findViewById(R.id.cdCardView);
        cdCardView.setOnClickListener(view1 -> startVideoActivity());
        return view;

    }

    private void startVideoActivity() {
        Intent i = new Intent(context, AppIntroVideoActivity.class);
        context.startActivity(i);
    }

    private void checkVideo() {
        try{
            File file = new File(AppUtils.completePathInSDCard(Constants.VIDEO), AppUtils.getFileName("static/media/2019/08/14/1900125913_U001_V001.mp4"));
            if (!file.exists()){
                downloadIntroVideo();
            }else
                playVideo();
        }catch (Exception ex){
            Logger.logE("",ex.getMessage(), ex );
        }
    }

    private void downloadIntroVideo() {
            FileModel fileModel= new FileModel("ओईएलपी किट", "static/media/2019/08/14/1900125913_U001_V001.mp4", "1111");
            List<FileModel> fileModelList = new ArrayList<>();
            fileModelList.add(fileModel);
            new DownloadClass(Constants.VIDEO, context, RetrofitConstant.BASE_URL, AppUtils.completePathInSDCard(Constants.VIDEO).getAbsolutePath(), fileModelList);
    }

    private void playVideo() {

        try {
            File f = AppUtils.completePathInSDCard(Constants.VIDEO);
            DownloadUtility.playVideo((Activity) context, "static/media/2019/08/14/1900125913_U001_V001.mp4", "ओईएलपी किट",
                    new MySharedPref(context).readInt(Constants.USER_ID, 0), "e7f5738a-4e37-4303-bee2-e0bd9820aab9", "");
        }catch (Exception ex){
            Logger.logE("", ex.getMessage(), ex);
        }

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);

    }
}

