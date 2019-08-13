package oelp.mahiti.org.newoepl.utils;

/**
 * Created by sandeep HR on 06/02/19.
 */
public class MyOwnRuntime extends Throwable {
    public MyOwnRuntime(String simpleName, Exception exception) {
        Logger.logE(simpleName, exception.getMessage(), exception);
    }
}
