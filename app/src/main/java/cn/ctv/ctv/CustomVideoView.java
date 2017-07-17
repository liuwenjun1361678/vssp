package cn.ctv.ctv;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class CustomVideoView extends VideoView {
	public static int WIDTH;
	public static int HEIGHT;
	private Context mContext;

	public CustomVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int width = getDefaultSize(WIDTH, widthMeasureSpec);
		int height = getDefaultSize(HEIGHT, heightMeasureSpec);
		System.out.println("CustomVideoView width:" + width + " height:"
				+ height);
		try {
			Activity activity = (Activity) mContext;

		} catch (Exception e) {
			e.printStackTrace();
			// 说明是ApplicationContext
		}

		setMeasuredDimension(width, height);
	}
}
