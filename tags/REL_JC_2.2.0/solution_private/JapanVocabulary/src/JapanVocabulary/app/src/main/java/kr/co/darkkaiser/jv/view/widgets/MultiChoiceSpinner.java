package kr.co.darkkaiser.jv.view.widgets;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import kr.co.darkkaiser.jv.R;

// @@@@@
public class MultiChoiceSpinner extends Spinner implements OnMultiChoiceClickListener {

    private String[] items = null;
    private boolean[] selection = null;
    private ArrayAdapter<String> proxyAdapter = null;

    public MultiChoiceSpinner(Context context) {
        super(context);

        this.proxyAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item);
        super.setAdapter(this.proxyAdapter);
    }

    public MultiChoiceSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.proxyAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item);
        super.setAdapter(this.proxyAdapter);
    }

    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        if (this.selection != null && which < this.selection.length) {
            this.selection[which] = isChecked;

            this.proxyAdapter.clear();
            this.proxyAdapter.add(buildSelectedItemString());
            setSelection(0);
        } else {
            throw new IllegalArgumentException("Argument 'which' is out of bounds.");
        }
    }

    @Override
    public boolean performClick() {
        new AlertDialog.Builder(getContext())
                .setTitle(getResources().getString(R.string.avsl_search_condition_select_text))
                .setMultiChoiceItems(this.items, this.selection, this)
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
        this.items = items;
        this.selection = new boolean[this.items.length];
        Arrays.fill(this.selection, false);
    }
    
    public void setItems(List<String> items) {
        this.items = items.toArray(new String[items.size()]);
        this.selection = new boolean[this.items.length];
        Arrays.fill(this.selection, false);
    }

    public void setSelection(String[] selection) {
        for (String sel : selection) {
            for (int j = 0; j < this.items.length; ++j) {
                if (this.items[j].equals(sel))
                    this.selection[j] = true;
            }
        }

        this.proxyAdapter.clear();
        this.proxyAdapter.add(buildSelectedItemString());
        setSelection(0);
    }
    
    public void setSelection(List<String> selection) {
        for (String sel : selection) {
            for (int j = 0; j < this.items.length; ++j) {
                if (this.items[j].equals(sel))
                    this.selection[j] = true;
            }
        }

        this.proxyAdapter.clear();
        this.proxyAdapter.add(buildSelectedItemString());
        setSelection(0);
    }
    
    public void setSelection(int[] selectedIndicies) {
        for (int index : selectedIndicies) {
            if (index >= 0 && index < this.selection.length)
                this.selection[index] = true;
            else
                throw new IllegalArgumentException("Index " + index + " is out of bounds.");
        }

        this.proxyAdapter.clear();
        this.proxyAdapter.add(buildSelectedItemString());
        setSelection(0);
    }

    public void setSelection(ArrayList<Integer> selectedIndicies) {
        for (int index : selectedIndicies) {
            if (index >= 0 && index < this.selection.length)
                this.selection[index] = true;
            else
                throw new IllegalArgumentException("Index " + index + " is out of bounds.");
        }

        this.proxyAdapter.clear();
        this.proxyAdapter.add(buildSelectedItemString());
        setSelection(0);
    }

    public List<String> getSelectedStrings() {
        List<String> selection = new LinkedList<String>();
        for (int i = 0; i < this.items.length; ++i) {
            if (this.selection[i])
                selection.add(this.items[i]);
        }

        return selection;
    }
    
    public List<Integer> getSelectedIndicies() {
        List<Integer> selection = new LinkedList<Integer>();
        for (int i = 0; i < this.items.length; ++i) {
            if (this.selection[i])
                selection.add(i);
        }

        return selection;
    }
    
    private String buildSelectedItemString() {
        boolean foundOne = false;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < this.items.length; ++i) {
            if (this.selection[i]) {
                if (foundOne)
                    sb.append(", ");

                foundOne = true;
                
                sb.append(this.items[i]);
            }
        }

        return sb.toString();
    }

}
