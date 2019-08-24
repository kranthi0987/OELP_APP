package mahiti.org.oelp.videoplay.utils;


public class Logger
{
    /**
     * Default constructors
     */
    private Logger(){
        // This is required
    }

    /**
     * function to use in catch block....
     * @param tag
     * @param desc
     * @param e
     */
    public static void logE(String  tag,String desc,Exception e)
    {
        android.util.Log.e(tag,desc,e);
    }

    /**
     *  function to use in debug..
     * @param tag
     * @param desc
     */
    public static void logD(String  tag,String desc)
    {
        android.util.Log.d(tag,""+desc);
    }

    /**
     * function to use in debug..
     * @param tag
     * @param desc
     */
    public static void logV(String  tag,String desc)
    {
        android.util.Log.v(tag,""+desc);
    }

}
