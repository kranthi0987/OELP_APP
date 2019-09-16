package mahiti.org.oelp.ui;

public interface UiInformableCallback<T> extends UiCallback<T> {
    void inform(String text);
}