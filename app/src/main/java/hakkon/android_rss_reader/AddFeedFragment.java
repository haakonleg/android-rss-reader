package hakkon.android_rss_reader;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by hakkon on 21.03.18.
 */

public class AddFeedFragment extends DialogFragment {
    private EditText urlInput;
    private Button urlBtn;
    private FeedAdded cb;

    public void setFeedAddedCallback(FeedAdded cb) {
        this.cb = cb;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.dialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_feed, container, false);

        this.urlInput = view.findViewById(R.id.urlInput);
        this.urlBtn = view.findViewById(R.id.urlBtn);

        this.urlBtn.setOnClickListener((v) -> {
            String url = this.urlInput.getText().toString();
            if (!url.isEmpty()) {
                cb.onFeedAdded(url);
                this.dismiss();
                this.urlInput.getText().clear();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set dimensions
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 360, getResources().getDisplayMetrics());
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, getResources().getDisplayMetrics());
        getDialog().getWindow().setLayout(width, height);
    }

    public interface FeedAdded {
        public void onFeedAdded(String url);
    }
}
