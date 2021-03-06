package pe.apiconz.android.barcodeexample;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.EnumMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        LinearLayout layout = new LinearLayout(this);
        layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER_VERTICAL);
        layout.setBackgroundColor(WHITE);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        setContentView(layout);

        Intent intent = this.getIntent();
        String barCodeData = intent.getStringExtra(Intent.EXTRA_TEXT);

        //String barCodeData = "7027661000057422855";

        Bitmap bitmap = null;
        ImageView imageView = new ImageView(this);

        bitmap = encodeAsBitmap(barCodeData, BarcodeFormat.CODE_128, 800, 400);
        imageView.setImageBitmap(bitmap);
        layout.addView(imageView);

        //barcode text
        TextView textView = new TextView(this);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setText(barCodeData);

        layout.addView(textView);

    }

    private void setScreenBrightnessMax() {
        WindowManager.LayoutParams settings = getWindow().getAttributes();
        settings.screenBrightness = 1;
        getWindow().setAttributes(settings);
    }

    private void restoreScreenBrightness() {
        WindowManager.LayoutParams settings = getWindow().getAttributes();
        settings.screenBrightness = -1;
        getWindow().setAttributes(settings);
    }


    @Override
    protected void onStart() {
        super.onStart();
        setScreenBrightnessMax();
    }

    @Override
    protected void onPause() {
        super.onPause();
        restoreScreenBrightness();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setScreenBrightnessMax();
    }

    @Override
    protected void onStop() {
        super.onStop();
        restoreScreenBrightness();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        restoreScreenBrightness();
    }

    private Bitmap encodeAsBitmap(String barCodeData, BarcodeFormat format, int imageWidth, int imageHeight) {
        String contentsToEncode = barCodeData;
        if (contentsToEncode == null) {
            return null;
        }

        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropiateEncoding(contentsToEncode);

        if (encoding != null) {
            hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }

        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result = null;
        try {
            result = writer.encode(contentsToEncode, format, imageWidth, imageHeight, hints);
        } catch (IllegalArgumentException e) {
            return null;
        } catch (WriterException e) {
            e.printStackTrace();
        }

        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private String guessAppropiateEncoding(String contentsToEncode) {// Very crude at the moment
        for (int i = 0; i < contentsToEncode.length(); i++) {
            if (contentsToEncode.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
