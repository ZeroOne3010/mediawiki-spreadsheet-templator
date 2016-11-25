package zeroone3010.mediawiki.spreadsheettemplator;

import java.util.Collections;
import java.util.List;
import java.util.Map;

class TemplateDataCollection {
    private final String templateName;
    private final List<Map<String, String>> data;

    public TemplateDataCollection(final String templateName, final List<Map<String, String>> data) {
        this.templateName = templateName;
        this.data = Collections.unmodifiableList(data);
    }

    public List<Map<String, String>> getData() {
        return data;
    }

    public String getTemplateName() {
        return templateName;
    }
}
