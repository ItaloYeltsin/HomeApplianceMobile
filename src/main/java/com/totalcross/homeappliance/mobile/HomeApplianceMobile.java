package com.totalcross.homeappliance.mobile;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import totalcross.io.IOException;
import totalcross.json.JSONObject;
import totalcross.net.HttpStream;
import totalcross.net.URI;
import totalcross.net.ssl.SSLSocketFactory;
import totalcross.sys.Settings;
import totalcross.sys.Time;
import totalcross.sys.Vm;
import totalcross.ui.Button;
import totalcross.ui.ImageControl;
import totalcross.ui.Label;
import totalcross.ui.MainWindow;
import totalcross.ui.Toast;
import totalcross.ui.event.ControlEvent;
import totalcross.ui.event.Event;
import totalcross.ui.event.EventHandler;
import totalcross.ui.font.Font;
import totalcross.ui.image.Image;
import totalcross.ui.image.ImageException;
import totalcross.ui.layout.HBox;

/**
 * @author @gustavopontes
 *         <p>
 *         Climate control sample with Firebase
 */
public class HomeApplianceMobile extends MainWindow {

    // Firebase Secret
    private static final String AUTH_KEY = "9xev12w1d3uGdsBiVjwXQkUov3WfJh7lojO96MB0";

    final String FIREBASE_URL = "https://webinarhomeappliance.firebaseio.com/databases/(default)/documents/commands.json?auth="
            + AUTH_KEY;

    final String B_DAY = "drawable/b_day.png";
    final String B_NIGHT = "drawable/b_night.png";

    final String OFF_DAY = "drawable/off_day.png";
    final String OFF_NIGHT = "drawable/off_night.png";

    final String ON_DAY = "drawable/on_day.png";
    final String ON_NIGHT = "drawable/on_night.png";

    final int ONE_DP = (int) (fmH / 20);

    private Button btnOff;
    private Button btnOn;
    private Button btnMinus;
    private Button btnPlus;

    private ImageControl background;

    private Label lblTemp;

    /*
     * Initial State
     */
    private int temp = 20;
    private boolean power = true;

    static {
        Settings.applicationId = "THAM";
        Settings.appVersion = "1.0.0";
        Settings.iosCFBundleIdentifier = "com.totalcross.homeappliance.mobile";
        Settings.screenDensity = 2.0;
    }

    public HomeApplianceMobile() {
        setUIStyle(Settings.MATERIAL_UI);
    }

    @Override
    public void initUI() {

        try {
            Image imgBackground = new Image(B_DAY).hwScaledFixedAspectRatio((int) (getHeight() * 1.2), true);
            background = new ImageControl(imgBackground);
            background.centerImage = true;
            add(background, LEFT, TOP, FILL, FILL);
        } catch (IOException | ImageException e) {
            e.printStackTrace();
        }

        try {
            Image imgLogo = new Image("drawable/logo_oficial_horizontal_branca.png");
            ImageControl icLogo = new ImageControl(imgLogo);

            icLogo.scaleToFit = true;
            icLogo.transparentBackground = true;
            add(icLogo, LEFT + ONE_DP * 42, TOP + ONE_DP * 42, FILL, SCREENSIZE + 3);
        } catch (IOException | ImageException e) {
            e.printStackTrace();
        }

        ImageControl icTerm = new ImageControl();

        try {
            Image imgTerm = new Image("drawable/term.png").hwScaledFixedAspectRatio((int) (getHeight() / 4), false);
            icTerm.setImage(imgTerm);
            icTerm.centerImage = true;
            icTerm.transparentBackground = true;
            icTerm.scaleToFit = true;
            add(icTerm, CENTER, CENTER, imgTerm.getWidth(), imgTerm.getHeight());
        } catch (IOException | ImageException e) {
            e.printStackTrace();
        }

        try {
            Image imgMinus = new Image("drawable/minus.png").hwScaledFixedAspectRatio(fmH * 3, true);
            btnMinus = new Button(imgMinus);
            btnMinus.setBorder(BORDER_NONE);
            btnMinus.transparentBackground = true;
            add(btnMinus, BEFORE - ONE_DP * 2, CENTER, PREFERRED, PREFERRED, icTerm);

        } catch (IOException | ImageException e) {
            e.printStackTrace();
        }

        try {
            Image imgPlus = new Image("drawable/plus.png").hwScaledFixedAspectRatio(fmH * 3, true);
            btnPlus = new Button(imgPlus);
            btnPlus.setBorder(BORDER_NONE);
            btnPlus.transparentBackground = true;
            add(btnPlus, AFTER + ONE_DP * 2, btnMinus.getY(), PREFERRED, PREFERRED, icTerm);

        } catch (IOException | ImageException e) {
            e.printStackTrace();
        }

        int color = 0x848484;

        lblTemp = new Label("20º");
        lblTemp.align = CENTER;
        lblTemp.setForeColor(color);
        lblTemp.setFont(Font.getFont(Font.MAX_FONT_SIZE - 5));
        lblTemp.transparentBackground = true;
        add(lblTemp, CENTER, CENTER, PREFERRED, PREFERRED);

        Label lblTempClimate = new Label("Climate");
        lblTempClimate.align = CENTER;
        lblTempClimate.setForeColor(color);
        lblTempClimate.setFont(Font.getFont(Font.MIN_FONT_SIZE + 5));
        lblTempClimate.transparentBackground = true;
        add(lblTempClimate, CENTER, AFTER + ONE_DP, PREFERRED, PREFERRED);

        HBox hBox = new HBox(HBox.LAYOUT_STACK_CENTER, HBox.ALIGNMENT_STRETCH);
        hBox.transparentBackground = true;

        try {
            Image imgOn = new Image(ON_DAY).hwScaledFixedAspectRatio(fmH * 6, true);
            btnOn = new Button(imgOn);
            btnOn.setBorder(BORDER_NONE);
            btnOn.transparentBackground = true;
            hBox.add(btnOn);
        } catch (IOException | ImageException e) {
            e.printStackTrace();
        }

        try {
            Image imgOff = new Image(OFF_DAY).hwScaledFixedAspectRatio(fmH * 6, true);
            btnOff = new Button(imgOff);
            btnOff.setBorder(BORDER_NONE);
            btnOff.transparentBackground = true;

            hBox.add(btnOff);
        } catch (IOException | ImageException e) {
            e.printStackTrace();
        }

        add(hBox, LEFT, AFTER + ONE_DP * 90, FILL, PREFERRED, icTerm);

    }

    @Override
    public <H extends EventHandler> void onEvent(Event<H> event) {
        super.onEvent(event);
        if (event.type == ControlEvent.PRESSED) {
            if (event.target == btnOff) {
                power(false);
            } else if (event.target == btnOn) {
                power(true);
            } else if (event.target == btnMinus) {
                temp(false);
            } else if (event.target == btnPlus) {
                temp(true);
            }
        }
    }

    /**
     * 
     * <p>
     * This method alternates between on and off according with the received
     * parameter.
     * <p>
     * Sets layout and sends command to firebase.
     *
     * 
     * <p>
     * If on is true, then power on
     * <p>
     * Else if on is false, then power off
     * 
     * @param on
     */
    private void power(boolean on) {
        boolean originalPower = power;
        try {
            power = on;

            transmit();

            if (on) {
                background.setImage(new Image(B_DAY).hwScaledFixedAspectRatio((int) (getHeight() * 1.2), true));
                btnOff.setImage(new Image(OFF_DAY).hwScaledFixedAspectRatio(fmH * 6, true));
                btnOn.setImage(new Image(ON_DAY).hwScaledFixedAspectRatio(fmH * 6, true));
            } else {
                background.setImage(new Image(B_NIGHT).hwScaledFixedAspectRatio((int) (getHeight() * 1.2), true));
                btnOff.setImage(new Image(OFF_NIGHT).hwScaledFixedAspectRatio(fmH * 6, true));
                btnOn.setImage(new Image(ON_NIGHT).hwScaledFixedAspectRatio(fmH * 6, true));
            }

        } catch (Exception e) {
            power = originalPower;
            Toast.show("Houston, we have a problem!", 1500);
            e.printStackTrace();
        }
    }

    /**
     * 
     * <p>
     * This method increases or decrases the temp according with the received
     * parameter.
     * <p>
     * Sets layout and sends command to firebase.
     *
     * 
     * <p>
     * If increase is true, then increase temp
     * <p>
     * Else if increase is false, then decrease temp
     * 
     * @param increase
     */

    private void temp(boolean increase) {
        int originalTemp = temp;
        try {
            if (increase)
                temp = temp < 32 ? temp + 1 : temp;
            else
                temp = temp > 16 ? temp - 1 : temp;

            transmit();

            lblTemp.setText(temp + "º");

        } catch (Exception e) {
            temp = originalTemp;
            Toast.show("Houston, we have a problem!", 1500);
            e.printStackTrace();
        }
    }

    /**
     * Sends temp and power state to firebase.
     * 
     * @throws FirebaseException
     * @throws JacksonUtilityException
     * @throws UnsupportedEncodingException
     */
    private void transmit() throws Exception {
        Map<String, Object> command = new HashMap<String, Object>();
        command.put("power", power);
        command.put("temp", temp);
        command.put("timestamp", new Time().getSQLLong());

        JSONObject json = new JSONObject(command);
        System.out.println(json.toString());

        final HttpStream.Options options = new HttpStream.Options();
        options.readTimeOut = 15000;
        options.socketFactory = new SSLSocketFactory();
        options.writeTimeOut = 15000;
        options.openTimeOut = 5000;
        options.httpType = HttpStream.POST;
        options.writeBytesSize = 4096;
        options.setContentType("application/json; charset=UTF-8");
        options.setCharsetEncoding("UTF-8");

        options.data = json.toString();

        URI uri = new URI(FIREBASE_URL);
        Vm.debug(uri.toString());
        try (final HttpStream hs = new HttpStream(uri, options)) {
            if (!hs.isOk()) {
                throw new Exception("Connection Error!");
            }
        }
    }

}
