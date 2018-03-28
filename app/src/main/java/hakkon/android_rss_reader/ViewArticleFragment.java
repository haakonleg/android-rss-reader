package hakkon.android_rss_reader;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import hakkon.android_rss_reader.database.FeedItem;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewArticleFragment extends Fragment {
    // CSS to scale images to fit screen
    private static final String WEBVIEW_CSS =
                    "<style>img {dislay: inline; height: auto; max-width: 100%;}" +
                    "p.title {font-size:28px; margin:0px; margin-bottom: 10px;}" +
                    "p.footer {color:#808080; font-size:14px; margin:0px;</style>";

    private FeedItem article;
    private WebView articleWebView;
    private FloatingActionButton openBrowserBtn;

    public ViewArticleFragment() {
        // Required empty public constructor
    }

    public static ViewArticleFragment newInstance(FeedItem article) {
        ViewArticleFragment fragment = new ViewArticleFragment();
        Bundle bundle = new Bundle();
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
            this.article = bundle.getParcelable("feed_item");
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("feed_item", this.article);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_article, container, false);

        // Find elements
        this.articleWebView = view.findViewById(R.id.article_web_view);
        this.openBrowserBtn = view.findViewById(R.id.openBrowserBtn);

        this.articleWebView.setBackgroundColor(getResources().getColor(R.color.windowBackground));

        // Open article in browser
        this.openBrowserBtn.setOnClickListener((v) -> {
            Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(this.article.getLink()));
            startActivity(browser);
        });

        String articleContents = "No contents";
        if (this.article.getEncodedContent() != null)
            articleContents = this.article.getEncodedContent();
        else if (this.article.getDescription() != null)
            articleContents = this.article.getDescription();

        String header = getHeaderHtml(this.article.getTitle(), this.article.getParentTitle(), this.article.getFormattedDate());
        this.articleWebView.loadData(WEBVIEW_CSS + header + articleContents,"text/html", null);

        return view;
    }

    private String getHeaderHtml(String title, String feedName, String date) {
        return "<p class='title'>" + title + "</p>" +
                "<p class='footer'>" + feedName + " / " + date + "</p><br>";
    }
}
