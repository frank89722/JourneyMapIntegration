package frankv.jmi;

public interface PlatformEventListener {
    boolean isFirstLogin();
    void setFirstLogin(boolean firstLogin);

    void register();
}
