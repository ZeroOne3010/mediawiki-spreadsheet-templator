package zeroone3010.mediawiki.spreadsheettemplator;

public class Page {
    private final String title;
    private final String content;

    public Page(final String title, final String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
