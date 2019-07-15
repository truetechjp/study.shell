package jp.truetech.shell;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.regex.Matcher;

import org.junit.Test;

public class Replace {

    @Test
    public void test() {
        String line, regex, replacement;
        String expected, actual;
        line = "123 456 AAA xxx";
        regex = "AAA";
        replacement = "value of AAA";
        actual = line.replaceAll(regex, replacement);
        expected = "123 456 value of AAA xxx";
        assertThat(actual, is(expected));
    }

    @Test
    public void testSharp() {
        String line, regex, replacement;
        String expected, actual;
        line = "123 456 #AAA xxx";
        regex = "#AAA";
        replacement = "value of AAA";
        actual = line.replaceAll(regex, replacement);
        expected = "123 456 value of AAA xxx";
        assertThat(actual, is(expected));
    }

    @Test
    public void test$() {
        String line, regex, replacement;
        String expected, actual;
        line = "123 456 $AAA xxx";
        regex = "$AAA";
        replacement = "value of AAA";
        actual = line.replaceAll(Matcher.quoteReplacement(regex), replacement);
        expected = "123 456 value of AAA xxx";
        assertThat(actual, is(expected));
    }

    @Test
    public void test$$() {
        String line, regex, replacement;
        String expected, actual;
        line = "123 456 $AAA xxx";
        regex = "\\$AAA";
        replacement = "value of AAA";
        actual = line.replaceAll(regex, replacement);
        expected = "123 456 value of AAA xxx";
        assertThat(actual, is(expected));
    }

}
