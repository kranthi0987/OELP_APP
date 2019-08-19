package oelp.mahiti.org.newoepl.utils;

/**
 * Created by RAJ ARYAN on 22/07/19.
 */
public class Action {
    public static final int STATUS_TRUE = 0;
    public static final int STATUS_FALSE = 1;
    public static final int MOVE_TO_CHANGE_MOBILE_ACTIVITY = 2;
    public static final int ON_BACK_PRESSED = 3;
    public static final int RESEND_OTP = 4;
    public static final int VERIFY_OTP = 5;
    public static final int MOVE_TO_NEXT_ACTIVITY = 6;
    private final int mAction;

    public Action(int action) {
        mAction = action;
    }

    public int getValue() {
        return mAction;
    }
}
