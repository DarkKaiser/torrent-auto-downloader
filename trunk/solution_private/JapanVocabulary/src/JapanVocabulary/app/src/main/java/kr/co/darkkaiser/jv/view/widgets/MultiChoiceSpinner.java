package kr.co.darkkaiser.jv.view.widgets;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import kr.co.darkkaiser.jv.R;

public class MultiChoiceSpinner extends Spinner implements OnMultiChoiceClickListener {

    private String[] mItems = null;
    private boolean[] mSelection = null;
    private ArrayAdapter<String> mProxyAdapter = null;
    
    public MultiChoiceSpinner(Context context) {
        super(context);

        mProxyAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item);
        super.setAdapter(mProxyAdapter);
    }

    public MultiChoiceSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        mProxyAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item);
        super.setAdapter(mProxyAdapter);
    }

    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        if (mSelection != null && which < mSelection.length) {
            mSelection[which] = isChecked;

            mProxyAdapter.clear();
            mProxyAdapter.add(buildSelectedItemString());
            setSelection(0);
        } else {
            throw new IllegalArgumentException("Argument 'which' is out of bounds.");
        }
    }

    @Override
    public boolean performClick() {
        new AlertDialog.Builder(getContext())
                .setTitle(getResources().getString(R.string.avsl_search_condition_text))
                .setMultiChoiceItems(mItems, mSelection, this)
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();

        return true;
    }
    
    @Override
    public void setAdapter(SpinnerAdapter adapter) {
        throw new RuntimeException("setAdapter is not supported by MultiSelectSpinner.");
    }
    
    public void setItems(String[] items) {
        mItems = items;
        mSelection = new boolean[mItems.length];
        Arrays.fill(mSelection, false);
    }
    
    public void setItems(List<String> items) {
        mItems = items.toArray(new String[items.size()]);
        mSelection = new boolean[mItems.length];
        Arrays.fill(mSelection, false);
    }

    public void setSelection(String[] selection) {
        for (String sel : selection) {
            for (int j = 0; j < mItems.length; ++j) {
                if (mItems[j].equals(sel))
                    mSelection[j] = true;
            }
        }
    }
    
    public void setSelection(List<String> selection) {
        for (String sel : selection) {
            for (int j = 0; j < mItems.length; ++j) {
                if (mItems[j].equals(sel))
                    mSelection[j] = true;
            }
        }
    }
    
    public void setSelection(int[] selectedIndicies) {
        for (int index : selectedIndicies) {
            if (index >= 0 && index < mSelection.length)
                mSelection[index] = true;
            else
                throw new IllegalArgumentException("Index " + index + " is out of bounds.");
        }
    }
    
    public List<String> getSelectedStrings() {
        List<String> selection = new LinkedList<String>();
        for (int i = 0; i < mItems.length; ++i) {
            if (mSelection[i]) selection.add(mItems[i]);
        }

        return selection;
    }
    
    public List<Integer> getSelectedIndicies() {
        List<Integer> selection = new LinkedList<Integer>();
        for (int i = 0; i < mItems.length; ++i) {
            if (mSelection[i])
                selection.add(i);
        }

        return selection;
    }
    
    private String buildSelectedItemString() {
        boolean foundOne = false;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < mItems.length; ++i) {
            if (mSelection[i]) {
                if (foundOne) sb.append(", ");
                foundOne = true;
                
                sb.append(mItems[i]);
            }
        }
        
        return sb.toString();
    }
}
