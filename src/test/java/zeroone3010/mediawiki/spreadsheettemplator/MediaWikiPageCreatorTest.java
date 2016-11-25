package zeroone3010.mediawiki.spreadsheettemplator;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MediaWikiPageCreatorTest {
    @Test
    public void convertDataToPages_should_return_correct_result() {
        final List<Map<String, String>> templates = new ArrayList<>();
        templates.add(new HashMap<String, String>() {{
            put("pagename", "First");
            put("foo", "BAR");
        }});
        templates.add(new HashMap<String, String>() {{
            put("pagename", "The Other One");
            put("boom", "bang");
            put("zork", "mid");
        }});
        final List<Page> pages = new MediaWikiPageCreator().convertDataToPages("Foo", templates);

        assertThat(pages.size(), is(2));
        assertThat(pages.get(0).getTitle(), is("First"));
        assertThat(pages.get(0).getContent(), is("{{Foo\n|foo=BAR\n}}"));

        assertThat(pages.get(1).getTitle(), is("The Other One"));
        assertThat(pages.get(1).getContent(), is("{{Foo\n|boom=bang\n|zork=mid\n}}"));
    }
}