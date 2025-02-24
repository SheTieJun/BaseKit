package shetj.me.base.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class GradientImageView extends AppCompatImageView {

    private Paint paint;
    private LinearGradient gradient;
    private boolean hasContent = true;

    public GradientImageView(Context context) {
        super(context);
        init();
    }

    public GradientImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GradientImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
    }



    public void setHasContent(boolean hasContent){
        this.hasContent = hasContent;
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (hasContent){
            if (gradient == null) {
                int width = getWidth();
                int height = getHeight();
                int[] colors = {0xFFFFFFFF, 0x00FFFFFF};
                // 位置数组：0f表示顶部，1f表示底部
                float[] positions = {0f, 1f};
                gradient = new LinearGradient(
                        0, 0,                  // 起始点（左上角）
                        0, getHeight(),        // 结束点（左下角）
                        colors, positions,
                        Shader.TileMode.CLAMP
                );
            }

            paint.setShader(gradient);
            canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
        }
    }
}