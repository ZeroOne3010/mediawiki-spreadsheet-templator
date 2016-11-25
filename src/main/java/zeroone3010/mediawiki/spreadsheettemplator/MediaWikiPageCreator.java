package zeroone3010.mediawiki.spreadsheettemplator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class MediaWikiPageCreator {
    private static final String PAGENAME = "pagename";

    List<Page> convertDataToPages(final String templateName, final List<Map<String, String>> templates) {
        final List<Page> results = new ArrayList<>();
        for (Map<String, String> template : templates) {
            final String title = createTitle(template);
            final String content = createTemplate(templateName, template);
            results.add(new Page(title, content));
        }
        return results;
    }

    private String createTitle(final Map<String, String> template) {
        String title = getIgnoreCase(template, PAGENAME);
        if (title == null) {
            title = getIgnoreCase(template, "name");
        }
        return title;
    }

    private String getIgnoreCase(final Map<String, String> map, final String key) {
        for (final String string : map.keySet()) {
            if (key.equalsIgnoreCase(string)) {
                return map.get(string);
            }
        }
        return null;
    }

    private String createTemplate(final String name, final Map<String, String> params) {
        final StringBuilder sb = new StringBuilder("{{").append(name);
        if (!params.isEmpty()) {
            sb.append("\n");
        }
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(PAGENAME)) {
                continue;
            }
            sb.append("|").append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
        }
        sb.append("}}");
        return sb.toString();
    }
}
