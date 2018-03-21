package hakkon.android_rss_reader;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import hakkon.android_rss_reader.feed.FeedItem;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewArticleFragment extends Fragment {
    private String feedName;
    private FeedItem article;

    private TextView titleTxt;
    private TextView footerTxt;
    private WebView articleWebView;

    public ViewArticleFragment() {
        // Required empty public constructor
    }

    public static ViewArticleFragment newInstance(String feedName, FeedItem article) {
        ViewArticleFragment fragment = new ViewArticleFragment();
        Bundle bundle = new Bundle();
        bundle.putString("feed_name", feedName);
        bundle.putParcelable("feed_item", article);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle;
        if (savedInstanceState != null)
            bundle = savedInstanceState;
        else
            bundle = getArguments();

        if (bundle != null) {
            this.feedName = bundle.getString("feed_name");
            this.article = bundle.getParcelable("feed_item");
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("feed_name", this.feedName);
        outState.putParcelable("feed_item", this.article);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_article, container, false);
        this.titleTxt = view.findViewById(R.id.article_title_txt);
        this.footerTxt = view.findViewById(R.id.article_footer_txt);
        this.articleWebView = view.findViewById(R.id.article_web_view);

        this.titleTxt.setText(this.article.getTitle());
        this.footerTxt.setText(this.feedName);

        String articleContents = "";
        if (!this.article.getEncodedContent().isEmpty())
            articleContents = this.article.getEncodedContent();
        else
            articleContents = this.article.getDescription();

        this.articleWebView.loadData(
                getOpenArticleHtml(this.article.getLink()) + articleContents,
                "text/html", null);

        return view;
    }

    private String getOpenArticleHtml(String url) {
        return "<a href=\"" + url + "\">" + "View article in browser</a><br><br>";
    }
}
