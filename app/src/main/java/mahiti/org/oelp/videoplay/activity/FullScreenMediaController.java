package mahiti.org.oelp.videoplay.activity;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.MediaController;

public class FullScreenMediaController extends MediaController {

    private ImageButton fullScreen;
    VideoViewActivity mContext;
    static boolean isFull;

    public FullScreenMediaController(Context context, boolean isFullScreen) {
        super(context);
        mContext = (VideoViewActivity) context;
        isFull= isFullScreen;
    }

    @Override
    public void setAnchorView(View view) {

        super.setAnchorView(view);

        //image button for full screen to be added to media controller
        fullScreen = new ImageButton (super.getContext());

        LayoutParams params =
                new LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.RIGHT;
        params.rightMargin = 80;
        addView(fullScreen, params);

        //fullscreen indicator from intent
//        isFullScreen =  ((Activity)getContext()).getIntent().
//                getStringExtra("fullScreenInd");

//        if(isFull){
//            fullScreen.setImageResource(R.drawable.ic_fullscreen_exit);
//        }else{
//            fullScreen.setImageResource(R.drawable.full_screen);
//        }

        //add listener to image button to handle full screen and exit full screen events
        fullScreen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (isFull){
//                    mContext.onFullScreenTouch(false);
//                }else {
//                    mContext.onFullScreenTouch(false);
//                }



//                Intent intent = new Intent(getContext(),VideoViewActivity.class);
//
//                if("y".equals(isFullScreen)){
//                    intent.putExtra("fullScreenInd", "");
//                }else{
//                    intent.putExtra("fullScreenInd", "y");
//                }
//                ((Activity)getContext()).startActivity(intent);
            }
        });
    }

}