package example.naoki.ble_myo;
import java.util.List;

/**
 * Created by naoki on 15/04/16.
 */
public class GestureDetectSendResultAction implements IGestureDetectAction {
    MainActivity activity;

    public GestureDetectSendResultAction(MainActivity mainActivity){
        activity = mainActivity;
    }

    @Override
    public void action(String tag ) {
        if (tag.equalsIgnoreCase("SAVE")){
            activity.setGestureText("Teach Me Another");
            activity.startNopModel();
        } else if (tag.equalsIgnoreCase("SAVE")){
            activity.setGestureText("Detect Ready");
            activity.startNopModel();
        } else {
            activity.setGestureText(tag);
        }


        switch (tag) {
            case "SAVE":
                activity.setGestureText("Teach Me Another");
                activity.startNopModel();
                break;
            case "SAVED":
                activity.setGestureText("Detect Ready");
                activity.startNopModel();
                break;
            default:
                activity.setGestureText(tag);
                break;
        }
    }
}
