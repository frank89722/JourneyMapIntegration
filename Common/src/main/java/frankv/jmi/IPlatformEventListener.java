package frankv.jmi;

public interface IPlatformEventListener {
    boolean isFirstLogin();
    void setFirstLogin(boolean firstLogin);

    void register();
}
