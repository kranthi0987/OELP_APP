package mahiti.org.oelp.xmpp;

import mahiti.org.oelp.crypto.axolotl.AxolotlService;

public interface OnKeyStatusUpdated {
    public void onKeyStatusUpdated(AxolotlService.FetchStatus report);
}
