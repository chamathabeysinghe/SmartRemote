package lk.ac.mrt.cse.smartremotecontroller.models;

/**
 * Created by chamath on 7/9/17.
 */

public class Button {
    private String buttonName;
    private String remoteName;
    private String remoteBrand;
    private String signal;

    public Button(String buttonName, String remoteName, String remoteBrand, String signal) {
        this.buttonName = buttonName;
        this.remoteName = remoteName;
        this.remoteBrand = remoteBrand;
        this.signal = signal;
    }

    public String getButtonName() {
        return buttonName;
    }

    public String getRemoteName() {
        return remoteName;
    }

    public String getRemoteBrand() {
        return remoteBrand;
    }

    public String getSignal() {
        return signal;
    }
}
