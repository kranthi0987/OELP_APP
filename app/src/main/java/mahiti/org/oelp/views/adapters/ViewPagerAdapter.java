package mahiti.org.oelp.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import mahiti.org.oelp.R;
import mahiti.org.oelp.views.activities.AppIntroVideoActivity;


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
        ImageView imageView = view.findViewById(R.id.imageView);
        ImageView ivPlay = view.findViewById(R.id.ivPlay);
        imageView.setImageResource(images[position]);


        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);

        if (position>0){
            ivPlay.setVisibility(View.GONE);
        }

        RelativeLayout cdCardView = view.findViewById(R.id.cdCardView);
        cdCardView.setOnClickListener(view1 -> startVideoActivity());
        return view;

    }

    private void startVideoActivity() {
        Intent i = new Intent(context, AppIntroVideoActivity.class);
        context.startActivity(i);
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);

    }
}

