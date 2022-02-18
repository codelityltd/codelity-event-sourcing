package uk.co.codelity.event.sourcing.core.utils.reflection;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class StringUtilsTest {

    @Test
    void shouldReturnEmptyForEmptyArray() {
        String actual = StringUtils.merge(new String[0], ",");
        assertThat(actual, is(""));
    }

    @Test
    void shouldNotAppendDelimiterForSingleString() {
        String actual = StringUtils.merge(new String[] {"one"}, ",");
        assertThat(actual, is("one"));
    }

    @Test
    void shouldAppendDelimiterForMultipleString() {
        String actual = StringUtils.merge(new String[] {"one", "two"}, ",");
        assertThat(actual, is("one,two"));
    }
}