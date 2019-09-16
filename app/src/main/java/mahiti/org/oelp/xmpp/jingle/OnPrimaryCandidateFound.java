package mahiti.org.oelp.xmpp.jingle;

public interface OnPrimaryCandidateFound {
    void onPrimaryCandidateFound(boolean success, JingleCandidate canditate);
}
