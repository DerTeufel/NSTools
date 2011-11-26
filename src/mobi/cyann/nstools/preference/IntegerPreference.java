/**
 * IntegerPreference.java
 * Nov 26, 2011 9:27:54 AM
 */
package mobi.cyann.nstools.preference;

import mobi.cyann.nstools.R;
import mobi.cyann.nstools.SeekbarDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * @author arif
 *
 */
public class IntegerPreference extends BasePreference implements DialogInterface.OnClickListener {
	private final static String LOG_TAG = "NSTools.IntegerPreference";
	
	protected int value;
	private final SeekbarDialog dialog;
	private final String metrics;
	
	public IntegerPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.mobi_cyann_nstools_preference_IntegerPreference, defStyle, 0);
		int minValue = a.getInt(R.styleable.mobi_cyann_nstools_preference_IntegerPreference_minValue, 0);
		int maxValue = a.getInt(R.styleable.mobi_cyann_nstools_preference_IntegerPreference_maxValue, 100);
		int step = a.getInt(R.styleable.mobi_cyann_nstools_preference_IntegerPreference_step, 1);
		metrics = a.getString(R.styleable.mobi_cyann_nstools_preference_IntegerPreference_metrics);
		a.recycle();
		
		dialog = new SeekbarDialog(context, this, this);
		dialog.setMin(minValue);
		dialog.setMax(maxValue);
		dialog.setStep(step);
		dialog.setTitle(getTitle());
		dialog.setMetrics(metrics);
		
		// initialize value
		value = getValue();
	}

	public IntegerPreference(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public IntegerPreference(Context context) {
		this(context, null);
	}

	@Override
	protected void onBindView(View view) {
		super.onBindView(view);
		// Sync the summary view
        TextView summaryView = (TextView) view.findViewById(android.R.id.summary);
        if (summaryView != null) {
        	if(value < 0) {
        		summaryView.setText(R.string.status_not_available);
        	}else if(metrics != null) {
        		summaryView.setText(value + " " + metrics);
        	}else {
        		summaryView.setText(String.valueOf(value));
        	}
        }
	}

	private void setValue(int newValue) {
		if(value > -1) {
			writeToInterface(String.valueOf(newValue));
			// re-read from interface (to detect error)
			newValue = getValue();
			if(newValue != value) {
				value = newValue;
				persistInt(newValue);
				
				notifyDependencyChange(shouldDisableDependents());
	            notifyChanged();
			}
		}
	}

	private int getValue() {
		int ret = -1;
		String str = readFromInterface();
		try {
			ret = Integer.parseInt(str);
		}catch(NumberFormatException ex) {
			
		}catch(Exception ex) {
			Log.e(LOG_TAG, "str:"+str, ex);
		}
		return ret;
	}
	
	public void reload() {
		setValue(getValue());
	}
	
	@Override
	public boolean isEnabled() {
		return (value > -1) && super.isEnabled();
	}
	
	@Override
	protected void onClick() {
		super.onClick();
	        
		dialog.setValue(value);
		dialog.show();
	}
	
	@Override
	public void onClick(DialogInterface d, int which) {
		//SeekbarDialog d = (SeekbarDialog)dialog;
		if(which == DialogInterface.BUTTON_POSITIVE) {
			int newValue = dialog.getValue();
			if (!callChangeListener(newValue)) {
	            return;
	        }
	        setValue(newValue);
		}
	}
	
	@Override
	public boolean shouldDisableDependents() {
		return (value == -1) || super.shouldDisableDependents();
	}
}