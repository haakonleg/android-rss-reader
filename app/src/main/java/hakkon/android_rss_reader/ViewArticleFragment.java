package hakkon.android_rss_reader;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
            "<style>body{color:#000000; background-color:#FAFAFA;}" +
                    "img{dislay:inline; height:auto; max-width:100%;}" +
                    "p.title{font-size:28px;padding:0px;margin:0px;margin-bottom:10px;}" +
                    "p.footer{color:#808080;font-size:14px;margin:0px;</style>";

    private static final String WEBVIEW_CSS_DARK =
                    "<style>body{color:#E0E0E0; background-color:#000000;}" +
                    "img{dislay:inline; height:auto; max-width:100%;}" +
                    "p.title{font-size:28px;padding:0px;margin:0px;margin-bottom:10px;}" +
                    "p.footer{color:#808080;font-size:14px;margin:0px;</style>";

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

        String header = getHeaderHtml(
                this.article.getTitle(),
                this.article.getParentTitle(),
                this.article.getAuthor(),
                this.article.getFormattedDate());

        // Get CSS for theme
        String theme = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString("selected_theme", getString(R.string.pref_theme_default));
        String css = WEBVIEW_CSS;
        if (theme.equals("dark"))
            css = WEBVIEW_CSS_DARK;

        this.articleWebView.loadData(css + header + articleContents,"text/html", null);

        return view;
    }

    private String getHeaderHtml(String title, String feedName, String author, String date) {
        StringBuilder builder = new StringBuilder();
        builder.append("<p class='title'>").append(title).append("</p>");

        if (author != null)
            builder.append("<p class='footer'>").append(author).append("</p>");

        builder.append("<p class='footer'>").append(feedName).append(" / ").append(date).append("</p><br>");

        return builder.toString();
    }
}
