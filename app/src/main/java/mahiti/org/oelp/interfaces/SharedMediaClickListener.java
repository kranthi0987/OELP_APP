package mahiti.org.oelp.interfaces;

import mahiti.org.oelp.models.SharedMediaModel;

/**
 * Created by RAJ ARYAN on 14/09/19.
 */
public interface SharedMediaClickListener {

    public void onSharedMediaClick(SharedMediaModel mediaModel, boolean shareGlobally, int position);
}
