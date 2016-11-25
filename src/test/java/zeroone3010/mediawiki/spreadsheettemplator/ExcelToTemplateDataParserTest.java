package zeroone3010.mediawiki.spreadsheettemplator;

import org.junit.Test;

import java.io.File;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ExcelToTemplateDataParserTest {
    @Test
    public void parseExcelFile_should_return_correct_result() {
        final ClassLoader classLoader = getClass().getClassLoader();
        final File file = new File(classLoader.getResource("planets.xls").getFile());
        final TemplateDataCollection result = new ExcelToTemplateDataParser().parseExcelFile(file);

        assertThat(result.getTemplateName(), is("Planet"));
        assertThat(result.getData().size(), is(2));

        final Map<String, String> earth = result.getData().get(0);
        assertThat(earth.size(), is(4));
        assertThat(earth.get("pagename"), is("Planet Earth"));
        assertThat(earth.get("name"), is("Earth"));
        assertThat(earth.get("mass"), is("5.94x10^24 kg"));
        assertThat(earth.get("radius"), is("6371.0 km"));

        final Map<String, String> mars = result.getData().get(1);
        assertThat(mars.size(), is(4));
        assertThat(mars.get("pagename"), is("Mars"));
        assertThat(mars.get("name"), is("Mars"));
        assertThat(mars.get("mass"), is("6.42x10^23 kg"));
        assertThat(mars.get("radius"), is("3389.6 km"));
    }
}
